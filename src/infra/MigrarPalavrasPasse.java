package infra;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;


public final class MigrarPalavrasPasse {
    public static void main(String[] args) {
        if (args.length != 1 || !"--converter-legadas".equals(args[0]))
            throw new IllegalArgumentException("Use --converter-legadas após guardar uma cópia de segurança.");
        int total = Transacao.executar("Erro ao migrar palavras-passe.", conn -> {
            var ids = new ArrayList<Long>();
            var senhas = new ArrayList<String>();
            try (PreparedStatement st = conn.prepareStatement("SELECT id_utilizador, password_hash FROM utilizador FOR UPDATE"); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    String senha = rs.getString(2);
                    if (senha != null && !senha.matches("\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}")) {
                        ids.add(rs.getLong(1)); senhas.add(senha);
                    }
                }
            }
            try (PreparedStatement st = conn.prepareStatement("UPDATE utilizador SET password_hash = ?, troca_senha_obrigatoria = TRUE WHERE id_utilizador = ?")) {
                for (int i = 0; i < ids.size(); i++) {
                    st.setString(1, BCrypt.hashpw(senhas.get(i), BCrypt.gensalt())); st.setLong(2, ids.get(i)); st.executeUpdate();
                }
            }
            return ids.size();
        });
        System.out.println("Credenciais antigas convertidas para BCrypt: " + total);
    }
}
