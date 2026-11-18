package infra;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

/** Actualização explícita de bases existentes. Nunca executa os dados iniciais. */
public final class MigracoesDB {
    private MigracoesDB() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1 || !(args[0].equals("--estado") || args[0].equals("--aplicar"))) {
            throw new IllegalArgumentException("Utilize --estado ou --aplicar. Faça uma cópia de segurança antes de aplicar.");
        }
        boolean aplicar = args[0].equals("--aplicar");
        Path pasta = Path.of(System.getProperty("gestao.migrations.dir", "docker/migrations"));
        List<Path> ficheiros;
        try (var lista = Files.list(pasta)) {
            ficheiros = lista.filter(p -> p.getFileName().toString().matches("[0-9]{3}_[a-z0-9_]+\\.sql")).sorted().toList();
        }
        if (ficheiros.isEmpty()) throw new IllegalStateException("Não foram encontradas migrações em " + pasta);
        try (Connection conn = ConexaoDB.getConnection()) {
            String lock = "gestao_migracoes_" + conn.getCatalog();
            try (PreparedStatement st = conn.prepareStatement("SELECT GET_LOCK(?, 10)")) {
                st.setString(1, lock);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) != 1) throw new IllegalStateException("Outra actualização está em curso.");
                }
            }
            try {
                Map<String, String> aplicadas = new HashMap<>();
                boolean existe;
                try (ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), null, "schema_migrations", new String[]{"TABLE"})) {
                    existe = rs.next();
                }
                if (existe) {
                    try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT versao, checksum FROM schema_migrations")) {
                        while (rs.next()) aplicadas.put(rs.getString(1), rs.getString(2));
                    }
                }
                Map<String, String> checksums = new LinkedHashMap<>();
                Set<String> versoes = new HashSet<>();
                for (Path f : ficheiros) {
                    String nome = f.getFileName().toString();
                    if (!versoes.add(nome.substring(0, 3))) throw new IllegalStateException("Versão duplicada: " + nome);
                    String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readString(f, StandardCharsets.UTF_8).replace("\r\n", "\n").getBytes(StandardCharsets.UTF_8)));
                    checksums.put(nome, hash);
                    if (aplicadas.containsKey(nome) && !hash.equals(aplicadas.get(nome))) {
                        throw new IllegalStateException("Migração já aplicada foi alterada: " + nome);
                    }
                }
                if (!checksums.keySet().containsAll(aplicadas.keySet())) throw new IllegalStateException("Faltam ficheiros de migrações já aplicadas.");
                if (aplicar && !existe) {
                    try (Statement st = conn.createStatement()) {
                        st.execute("CREATE TABLE schema_migrations (versao VARCHAR(200) PRIMARY KEY, checksum CHAR(64) NOT NULL, aplicada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
                    }
                }
                for (Path f : ficheiros) {
                    String nome = f.getFileName().toString();
                    if (aplicadas.containsKey(nome)) {
                        System.out.println("Aplicada: " + nome);
                        continue;
                    }
                    if (!aplicar) { System.out.println("Pendente: " + nome); continue; }
                    // DDL MySQL faz commit implícito. Os scripts são idempotentes para permitir retomar uma falha.
                    for (String sql : separarComandos(Files.readString(f, StandardCharsets.UTF_8))) {
                        try (Statement st = conn.createStatement()) { st.execute(sql); }
                    }
                    try (PreparedStatement st = conn.prepareStatement("INSERT INTO schema_migrations(versao,checksum) VALUES (?,?)")) {
                        st.setString(1, nome); st.setString(2, checksums.get(nome)); st.executeUpdate();
                    }
                    System.out.println("Concluída: " + nome);
                }
            } finally {
                try (PreparedStatement st = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                    st.setString(1, lock); st.execute();
                }
            }
        }
    }

    public static List<String> separarComandos(String texto) {
        List<String> comandos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        char aspas = 0;
        boolean linha = false, bloco = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i), proximo = i + 1 < texto.length() ? texto.charAt(i + 1) : 0;
            if (linha) { if (c == '\n') { linha = false; actual.append('\n'); } continue; }
            if (bloco) { if (c == '*' && proximo == '/') { bloco = false; i++; actual.append(' '); } continue; }
            if (aspas != 0) {
                actual.append(c);
                if (c == '\\' && proximo != 0) { actual.append(proximo); i++; }
                else if (c == aspas) {
                    if (proximo == aspas) { actual.append(proximo); i++; }
                    else aspas = 0;
                }
            } else if (c == '-' && proximo == '-' && (i + 2 == texto.length() || Character.isWhitespace(texto.charAt(i + 2)))) {
                linha = true; i++;
            } else if (c == '#') linha = true;
            else if (c == '/' && proximo == '*') { bloco = true; i++; }
            else if (c == '\'' || c == '"' || c == '`') { aspas = c; actual.append(c); }
            else if (c == ';') { adicionar(comandos, actual); }
            else actual.append(c);
        }
        if (aspas != 0 || bloco) throw new IllegalArgumentException("SQL incompleto: aspas ou comentário não fechado.");
        adicionar(comandos, actual);
        return comandos;
    }

    private static void adicionar(List<String> comandos, StringBuilder actual) {
        String sql = actual.toString().strip(); actual.setLength(0);
        if (sql.isEmpty()) return;
        if (sql.matches("(?is)^(USE|DELIMITER)\\b.*")) throw new IllegalArgumentException("USE e DELIMITER não são permitidos nas migrações.");
        comandos.add(sql);
    }
}
