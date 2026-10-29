package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import model.vo.Categoria;

public class CategoriaDAO implements GenericoDAO<Categoria, Long>{

    @Override
    public void salvar(Categoria objecto) {
        String sql = "INSERT INTO categoria (nomeCategoria) VALUES (?)";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdCategoria(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM categoria WHERE idCategoria = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Categoria objecto) {
        String sql = "UPDATE categoria SET nomeCategoria = ? WHERE idCategoria = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCategoria());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Categoria> listarTodos() {
        String sql = "SELECT * FROM categoria";
        List<Categoria> categorias = new ArrayList<>();
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getLong("idCategoria"));
                categoria.setNome(rs.getString("nomeCategoria"));
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    @Override
    public Categoria buscarPorId(Long id) {
        String sql = "SELECT * FROM categoria WHERE idCategoria = ?";
        Categoria categoria = null;
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    categoria = new Categoria();
                    categoria.setIdCategoria(rs.getLong("idCategoria"));
                    categoria.setNome(rs.getString("nomeCategoria"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoria;
    }
    
}
