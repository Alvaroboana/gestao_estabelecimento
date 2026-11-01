package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import model.vo.Cidade;

public class CidadeDAO implements GenericoDAO<Cidade, Long> {

    @Override
    public void salvar(Cidade objecto) {
        String sql = "INSERT INTO cidade (nomeCidade) VALUES (?)";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdCidade(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM cidade WHERE idCidade = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Cidade objecto) {
        String sql = "UPDATE cidade SET nomeCidade = ? WHERE idCidade = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cidade> listarTodos() {
        String sql = "SELECT * FROM cidade";
        List<Cidade> cidades = new ArrayList<>();
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Cidade cidade = new Cidade();
                cidade.setIdCidade(rs.getLong("idCidade"));
                cidade.setNome(rs.getString("nomeCidade"));
                cidades.add(cidade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cidades;
    }

    @Override
    public Cidade buscarPorId(Long id) {
        String sql = "SELECT * FROM cidade WHERE idCidade = ?";
        Cidade cidade = null;
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    cidade = new Cidade();
                    cidade.setIdCidade(rs.getLong("idCidade"));
                    cidade.setNome(rs.getString("nomeCidade"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cidade;
    }
    
}
