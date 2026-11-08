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
=======
<<<<<<< HEAD
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
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
<<<<<<< HEAD
=======
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
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
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

<<<<<<< HEAD
=======
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
>>>>>>> da6ec68d8ffae3fa0348004e14812c148af00b49
}
