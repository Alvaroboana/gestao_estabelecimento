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
import model.vo.Cidade;

public class CidadeDAO implements GenericoDAO<Cidade, Long> {

    @Override
    public void salvar(Cidade objecto) {
        String sql = "INSERT INTO cidade (nome) VALUES (?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdCidade(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar cidade: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM cidade WHERE id_cidade = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar cidade: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cidade objecto) {
        String sql = "UPDATE cidade SET nome = ? WHERE id_cidade = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar cidade: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cidade> listarTodos() {
        String sql = "SELECT id_cidade, nome FROM cidade ORDER BY nome";
        List<Cidade> cidades = new ArrayList<Cidade>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cidade cidade = new Cidade();
                cidade.setIdCidade(rs.getLong("id_cidade"));
                cidade.setNome(rs.getString("nome"));
                cidades.add(cidade);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar cidades: " + e.getMessage(), e);
        }
        return cidades;
    }

    @Override
    public Cidade buscarPorId(Long id) {
        String sql = "SELECT id_cidade, nome FROM cidade WHERE id_cidade = ?";
        Cidade cidade = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cidade = new Cidade();
                    cidade.setIdCidade(rs.getLong("id_cidade"));
                    cidade.setNome(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar cidade por ID: " + e.getMessage(), e);
        }
        return cidade;
    }

}
