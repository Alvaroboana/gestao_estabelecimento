package infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoDB {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/gestao?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "gestao";
    private static final String SENHA = "gestao";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USUARIO, SENHA);
            }
            return connection;
        } catch (SQLException e) {
            throw new DBException("Erro ao conectar o banco de dados: " + e.getMessage(), e);
        } catch (ClassNotFoundException a) {
            throw new DBException("Driver JDBC do MySQL não encontrado", a);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DBException("Erro ao fechar a conexao com o banco de dados.", e);
            }
        }
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
}
