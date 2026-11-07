package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import infra.JdbcValidation;
import model.vo.Contacto;

public class ContactoDAO implements GenericoDAO<Contacto, Long> {

    @Override
    public void salvar(Contacto objecto) {
        throw new IllegalArgumentException("idPessoa e obrigatorio para salvar um contacto.");
    }

    public void salvarComPessoa(Contacto objecto, Long idPessoa) {
        String sql = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getContacto());
            stmt.setLong(2, JdbcValidation.requireId(idPessoa, "idPessoa"));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdContacto(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar contacto: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM contacto WHERE id_contacto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar contacto: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Contacto objecto) {
        String sql = "UPDATE contacto SET contacto = ? WHERE id_contacto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getContacto());
            stmt.setLong(2, objecto.getIdContacto());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar contacto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Contacto> listarTodos() {
        String sql = "SELECT id_contacto, contacto FROM contacto ORDER BY id_contacto";
        List<Contacto> lista = new ArrayList<Contacto>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Contacto c = new Contacto();
                c.setIdContacto(rs.getLong("id_contacto"));
                c.setContacto(rs.getString("contacto"));
                lista.add(c);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar contactos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Contacto buscarPorId(Long id) {
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_contacto = ?";
        Contacto c = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar contacto por ID: " + e.getMessage(), e);
        }
        return c;
    }

    public List<Contacto> buscarPorPessoa(Long idPessoa) {
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_pessoa = ?";
        List<Contacto> lista = new ArrayList<Contacto>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPessoa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                    lista.add(c);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar contactos por pessoa: " + e.getMessage(), e);
        }
        return lista;
    }

}
