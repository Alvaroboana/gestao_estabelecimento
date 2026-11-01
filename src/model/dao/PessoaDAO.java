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
import model.vo.Cliente;
import model.vo.Pessoa;


// Acho que não vamos usar esse DAO mas por precausão vamos la deixar 😁

public class PessoaDAO implements GenericoDAO<Pessoa, Long> {

    @Override
    public void salvar(Pessoa objecto) {
        String sql = "INSERT INTO pessoa (nome) VALUES (?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdPessoa(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar pessoa: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar pessoa: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Pessoa objecto) {
        String sql = "UPDATE pessoa SET nome = ? WHERE id_pessoa = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdPessoa());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar pessoa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pessoa> listarTodos() {
        String sql = "SELECT id_pessoa, nome FROM pessoa ORDER BY nome";
        List<Pessoa> lista = new ArrayList<Pessoa>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cliente p = new Cliente();
                p.setIdPessoa(rs.getLong("id_pessoa"));
                p.setNome(rs.getString("nome"));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar pessoas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Pessoa buscarPorId(Long id) {
        String sql = "SELECT id_pessoa, nome FROM pessoa WHERE id_pessoa = ?";
        Pessoa p = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdPessoa(rs.getLong("id_pessoa"));
                    c.setNome(rs.getString("nome"));
                    p = c;
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar pessoa por ID: " + e.getMessage(), e);
        }
        return p;
    }

}

