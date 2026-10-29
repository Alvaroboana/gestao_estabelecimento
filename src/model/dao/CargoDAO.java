package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import infra.ConexaoDB;
import model.vo.Cargo;

public class CargoDAO implements GenericoDAO<Cargo, Long>{

    @Override
    public void salvar(Cargo objecto) {
        String sql = "INSERT INTO cargo (nomeCargo) VALUES (?)";
        try(Connection conn = ConexaoDB.getConnection();    
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdCargo(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException {
        String sql = "DELETE FROM cargo WHERE idCargo = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Cargo objecto) {
        String sql = "UPDATE cargo SET nomeCargo = ? WHERE idCargo = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setLong(2, objecto.getIdCargo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cargo> listarTodos() {
        String sql = "SELECT * FROM cargo";
        List<Cargo> cargos = new ArrayList<>();
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Cargo cargo = new Cargo();
                cargo.setIdCargo(rs.getLong("idCargo"));
                cargo.setNome(rs.getString("nomeCargo"));
                cargos.add(cargo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cargos;
    }

    @Override
    public Cargo buscarPorId(Long id) {
        String sql = "SELECT * FROM cargo WHERE idCargo = ?";
        Cargo cargo = null;
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    cargo = new Cargo();
                    cargo.setIdCargo(rs.getLong("idCargo"));
                    cargo.setNome(rs.getString("nomeCargo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cargo;
    }
    
}
