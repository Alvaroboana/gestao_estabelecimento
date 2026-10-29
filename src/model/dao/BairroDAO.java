package model.dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import model.vo.Bairro;

public class BairroDAO implements GenericoDAO<Bairro, Long> {

    @Override
    public void salvar(Bairro object) throws SQLException {
        String sql = "INSERT INTO bairro (nomeBairro,idCidade) VALUES (?,?)";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, object.getNome());
            stmt.setLong(2, object.getCidade() != null ? object.getCidade().getIdCidade() : 0L);
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    object.setIdBairro(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM bairro WHERE idBairro = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizar(Bairro objecto) throws SQLException {
        String sql = "UPDATE bairro SET nomeBairro = ?, idCidade = ? WHERE idBairro = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getCidade() != null ? objecto.getCidade().getIdCidade() : 0L);
            stmt.setLong(3, objecto.getIdBairro());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Bairro> listarTodos() {
        String sql = "SELECT * FROM bairro";
        List<Bairro> bairros = new ArrayList<>();
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Bairro bairro = new Bairro();
                bairro.setIdBairro(rs.getLong("idBairro"));
                bairro.setNome(rs.getString("nomeBairro"));
                bairros.add(bairro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bairros;
        

    }

    @Override
    public Bairro buscarPorId(Long id) {
        String sql = "SELECT * FROM bairro WHERE idBairro = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    Bairro bairro = new Bairro();
                    bairro.setIdBairro(rs.getLong("idBairro"));
                    bairro.setNome(rs.getString("nomeBairro"));
                    return bairro;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
