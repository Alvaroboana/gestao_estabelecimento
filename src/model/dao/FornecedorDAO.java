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
import model.vo.Fornecedor;

public class FornecedorDAO implements GenericoDAO<Fornecedor, Long> {

    @Override
    public void salvar(Fornecedor objecto) {
        String sqlPessoa = "INSERT INTO pessoa (nome) VALUES (?)";
        String sqlForn = "INSERT INTO fornecedor (id_pessoa) VALUES (?)";
        String sqlContacto = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";

        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            Long idPessoa = null;
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                stmtP.setString(1, objecto.getNome());
                stmtP.executeUpdate();
                try (ResultSet rs = stmtP.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPessoa = rs.getLong(1);
                        objecto.setIdPessoa(idPessoa);
                    }
                }
            }

            try (PreparedStatement stmtF = conn.prepareStatement(sqlForn, Statement.RETURN_GENERATED_KEYS)) {
                stmtF.setLong(1, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                stmtF.executeUpdate();
                try (ResultSet rs = stmtF.getGeneratedKeys()) {
                    if (rs.next()) {
                        objecto.setIdFornecedor(rs.getLong(1));
                    }
                }
            }

            if (objecto.getContacto() != null && !objecto.getContacto().isEmpty()) {
                try (PreparedStatement stmtC = conn.prepareStatement(sqlContacto, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < objecto.getContacto().size(); i++) {
                        Contacto c = objecto.getContacto().get(i);
                        if (c != null && c.getContacto() != null) {
                            stmtC.setString(1, c.getContacto());
                            stmtC.setLong(2, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                            stmtC.executeUpdate();
                            try (ResultSet rs = stmtC.getGeneratedKeys()) {
                                if (rs.next()) {
                                    c.setIdContacto(rs.getLong(1));
                                }
                            }
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao salvar fornecedor.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = (SELECT id_pessoa FROM fornecedor WHERE id_fornecedor = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar fornecedor: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Fornecedor objecto) {
        String sql = "UPDATE pessoa SET nome = ? WHERE id_pessoa = (SELECT id_pessoa FROM fornecedor WHERE id_fornecedor = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdFornecedor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Fornecedor> listarTodos() {
        String sql = "SELECT f.id_fornecedor, p.id_pessoa, p.nome " +
                     "FROM fornecedor f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "ORDER BY p.nome";
        List<Fornecedor> lista = new ArrayList<Fornecedor>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setIdFornecedor(rs.getLong("id_fornecedor"));
                f.setIdPessoa(rs.getLong("id_pessoa"));
                f.setNome(rs.getString("nome"));
                carregarContactos(conn, f);
                lista.add(f);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar fornecedores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Fornecedor buscarPorId(Long id) {
        String sql = "SELECT f.id_fornecedor, p.id_pessoa, p.nome " +
                     "FROM fornecedor f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "WHERE f.id_fornecedor = ?";
        Fornecedor f = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    f = new Fornecedor();
                    f.setIdFornecedor(rs.getLong("id_fornecedor"));
                    f.setIdPessoa(rs.getLong("id_pessoa"));
                    f.setNome(rs.getString("nome"));
                    carregarContactos(conn, f);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar fornecedor por ID: " + e.getMessage(), e);
        }
        return f;
    }

    private void carregarContactos(Connection conn, Fornecedor fornecedor) {
        if (fornecedor == null || fornecedor.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_pessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getIdPessoa());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Contacto> lista = new ArrayList<Contacto>();
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                    lista.add(c);
                }
                fornecedor.setContacto(lista);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar contactos do fornecedor.", e);
        }
    }

}
