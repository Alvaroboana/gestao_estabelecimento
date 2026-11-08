package testes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import infra.ConexaoDB;

public class testeConexao {
    public static void main(String[] args) {
        Connection conn = ConexaoDB.getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT DATABASE(), COUNT(*) FROM information_schema.tables WHERE table_schema = 'gestao'")) {
            rs.next();
            System.out.println("Ligado à base: " + rs.getString(1) + " | tabelas: " + rs.getInt(2));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConexaoDB.closeConnection();
        }
    }
}