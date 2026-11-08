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
<<<<<<< HEAD
            Connection conn = DriverManager.getConnection(
                    DatabaseConfig.url(),
                    DatabaseConfig.username(),
                    DatabaseConfig.password());

            Long idUtilizador = SessaoUtilizador.getIdUtilizador();
            if (idUtilizador != null && idUtilizador > 0) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET @id_utilizador_atual = " + idUtilizador);
                } catch (SQLException ignore) {
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

=======
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new DBException("Erro ao conectar o banco de dados: " + e.getMessage(), e);
        }
    }

    public static void closeConnection() {
        // Mantido para compatibilidade de chamadas legadas
    }

    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DBException("Erro ao fechar o Statement.", e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement pst) {
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                throw new DBException("Erro ao fechar o PreparedStatement.", e);
            }
        }
    }

    public static void closeresultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DBException("Erro ao fecha o resuntSet.", e);
            }
        }
    }
>>>>>>> 17e879f68eff4d35866ab34526ac56e032df40a5
}
