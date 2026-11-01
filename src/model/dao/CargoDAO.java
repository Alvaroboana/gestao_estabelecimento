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
import model.vo.Cargo;

public class CargoDAO implements GenericoDAO<Cargo, Long> {

    @Override
    public void salvar(Cargo objecto) {
        String sql = "INSERT INTO cargo (nome, salario_base) VALUES (?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());
            double sal = 0.0;
            if (objecto.getSalarioBase() != null) {
                sal = objecto.getSalarioBase();
            }
            stmt.setDouble(2, sal);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdCargo(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar cargo: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM cargo WHERE id_cargo = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar cargo: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Cargo objecto) {
        String sql = "UPDATE cargo SET nome = ?, salario_base = ? WHERE id_cargo = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            double sal = 0.0;
            if (objecto.getSalarioBase() != null) {
                sal = objecto.getSalarioBase();
            }
            stmt.setDouble(2, sal);
            stmt.setLong(3, objecto.getIdCargo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar cargo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cargo> listarTodos() {
        String sql = "SELECT id_cargo, nome, salario_base FROM cargo ORDER BY nome";
        List<Cargo> cargos = new ArrayList<Cargo>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cargo cargo = new Cargo();
                cargo.setIdCargo(rs.getLong("id_cargo"));
                cargo.setNome(rs.getString("nome"));
                cargo.setSalarioBase(rs.getDouble("salario_base"));
                cargos.add(cargo);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar cargos: " + e.getMessage(), e);
        }
        return cargos;
    }

    @Override
    public Cargo buscarPorId(Long id) {
        String sql = "SELECT id_cargo, nome, salario_base FROM cargo WHERE id_cargo = ?";
        Cargo cargo = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cargo = new Cargo();
                    cargo.setIdCargo(rs.getLong("id_cargo"));
                    cargo.setNome(rs.getString("nome"));
                    cargo.setSalarioBase(rs.getDouble("salario_base"));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar cargo por ID: " + e.getMessage(), e);
        }
        return cargo;
    }

}
