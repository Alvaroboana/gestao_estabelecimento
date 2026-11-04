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
import model.vo.Cliente;
import model.vo.Contacto;

public class ClienteDAO implements GenericoDAO<Cliente, Long> {

    @Override
    public void salvar(Cliente objecto) {
        String sqlPessoa = "INSERT INTO pessoa (nome) VALUES (?)";
        String sqlCliente = "INSERT INTO cliente (id_pessoa) VALUES (?)";
        String sqlContacto = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";

        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            Long idPessoa = null;
            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                stmtPessoa.setString(1, objecto.getNome());
                stmtPessoa.executeUpdate();
                try (ResultSet rs = stmtPessoa.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPessoa = rs.getLong(1);
                        objecto.setIdPessoa(idPessoa);
                    }
                }
            }

            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS)) {
                stmtCliente.setLong(1, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                stmtCliente.executeUpdate();
                try (ResultSet rs = stmtCliente.getGeneratedKeys()) {
                    if (rs.next()) {
                        objecto.setIdCliente(rs.getLong(1));
                    }
                }
            }

            if (objecto.getContacto() != null && !objecto.getContacto().isEmpty()) {
                try (PreparedStatement stmtContacto = conn.prepareStatement(sqlContacto, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < objecto.getContacto().size(); i++) {
                        Contacto c = objecto.getContacto().get(i);
                        if (c != null && c.getContacto() != null) {
                            stmtContacto.setString(1, c.getContacto());
                            stmtContacto.setLong(2, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                            stmtContacto.executeUpdate();
                            try (ResultSet rs = stmtContacto.getGeneratedKeys()) {
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
            DBException failure = new DBException("Erro ao salvar cliente.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = (SELECT id_pessoa FROM cliente WHERE id_cliente = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cliente objecto) {
        String sql = "UPDATE pessoa SET nome = ? WHERE id_pessoa = (SELECT id_pessoa FROM cliente WHERE id_cliente = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCliente());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT c.id_cliente, p.id_pessoa, p.nome " +
                     "FROM cliente c " +
                     "INNER JOIN pessoa p ON c.id_pessoa = p.id_pessoa " +
                     "ORDER BY p.nome";
        List<Cliente> clientes = new ArrayList<Cliente>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getLong("id_cliente"));
                cliente.setIdPessoa(rs.getLong("id_pessoa"));
                cliente.setNome(rs.getString("nome"));
                carregarContactos(conn, cliente);
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return clientes;
    }

    @Override
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT c.id_cliente, p.id_pessoa, p.nome " +
                     "FROM cliente c " +
                     "INNER JOIN pessoa p ON c.id_pessoa = p.id_pessoa " +
                     "WHERE c.id_cliente = ?";
        Cliente cliente = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setIdCliente(rs.getLong("id_cliente"));
                    cliente.setIdPessoa(rs.getLong("id_pessoa"));
                    cliente.setNome(rs.getString("nome"));
                    carregarContactos(conn, cliente);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar cliente por ID: " + e.getMessage(), e);
        }
        return cliente;
    }

    private void carregarContactos(Connection conn, Cliente cliente) {
        if (cliente == null || cliente.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_pessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cliente.getIdPessoa());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Contacto> lista = new ArrayList<Contacto>();
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                    lista.add(c);
                }
                cliente.setContacto(lista);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar contactos do cliente.", e);
        }
    }

}
