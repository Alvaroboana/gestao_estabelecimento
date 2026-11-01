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
import model.vo.Categoria;

public class CategoriaDAO implements GenericoDAO<Categoria, Long> {

    @Override
    public void salvar(Categoria objecto) {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdCategoria(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Categoria objecto) {
        String sql = "UPDATE categoria SET nome = ? WHERE id_categoria = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCategoria());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Categoria> listarTodos() {
        String sql = "SELECT id_categoria, nome FROM categoria ORDER BY nome";
        List<Categoria> lista = new ArrayList<Categoria>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getLong("id_categoria"));
                cat.setNome(rs.getString("nome"));
                lista.add(cat);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar categorias: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Categoria buscarPorId(Long id) {
        String sql = "SELECT id_categoria, nome FROM categoria WHERE id_categoria = ?";
        Categoria cat = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cat = new Categoria();
                    cat.setIdCategoria(rs.getLong("id_categoria"));
                    cat.setNome(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar categoria por ID: " + e.getMessage(), e);
        }
        return cat;
    }

}
