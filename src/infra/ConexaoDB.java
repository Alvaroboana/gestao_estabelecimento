package infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import model.vo.SessaoUtilizador;

public class ConexaoDB {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new DBException("Driver JDBC do MySQL não encontrado", e);
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(
                    DatabaseConfig.url(),
                    DatabaseConfig.username(),
                    DatabaseConfig.password());

            Long idUtilizador = SessaoUtilizador.getIdUtilizador();
            if (idUtilizador != null && idUtilizador > 0) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET @id_utilizador_atual = " + idUtilizador);
                } catch (SQLException ignore) {
                    // Trata silenciosamente se a variável de sessão não for suportada
                }
            }

            return conn;
        } catch (SQLException e) {
            throw new DBException("Erro ao conectar o banco de dados.", e);
        }
    }

    public static void rollback(Connection connection, DBException failure) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            failure.addSuppressed(rollbackFailure);
        }
    }

}
