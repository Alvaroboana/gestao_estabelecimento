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
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;

public class UtilizadorDAO implements GenericoDAO<Utilizador, Long> {

    @Override
    public void salvar(Utilizador objecto) {
        String sql = "INSERT INTO utilizador (username, password_hash, perfil, id_funcionario) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getUsername());
            stmt.setString(2, objecto.getPasswordHash());

            String perfilNome = "VENDEDOR";
            if (objecto.getPerfil() != null) {
                perfilNome = objecto.getPerfil().name();
            }
            stmt.setString(3, perfilNome);

            stmt.setLong(4, JdbcValidation.requireId(
                    objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                    "funcionario.idFuncionario"));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdUtilizador(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar utilizador: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM utilizador WHERE id_utilizador = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar utilizador: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Utilizador objecto) {
        String sql = "UPDATE utilizador SET username = ?, password_hash = ?, perfil = ?, id_funcionario = ? WHERE id_utilizador = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getUsername());
            stmt.setString(2, objecto.getPasswordHash());

            String perfilNome = "VENDEDOR";
            if (objecto.getPerfil() != null) {
                perfilNome = objecto.getPerfil().name();
            }
            stmt.setString(3, perfilNome);

            stmt.setLong(4, JdbcValidation.requireId(
                    objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                    "funcionario.idFuncionario"));
            stmt.setLong(5, objecto.getIdUtilizador());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar utilizador: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Utilizador> listarTodos() {
        String sql = "SELECT u.id_utilizador, u.username, u.password_hash, u.perfil, " +
                     "f.id_funcionario, f.bi, f.estado, p.id_pessoa, p.nome " +
                     "FROM utilizador u " +
                     "INNER JOIN funcionario f ON u.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "ORDER BY u.username";
        List<Utilizador> lista = new ArrayList<Utilizador>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairUtilizador(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar utilizadores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Utilizador buscarPorId(Long id) {
        String sql = "SELECT u.id_utilizador, u.username, u.password_hash, u.perfil, " +
                     "f.id_funcionario, f.bi, f.estado, p.id_pessoa, p.nome " +
                     "FROM utilizador u " +
                     "INNER JOIN funcionario f ON u.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "WHERE u.id_utilizador = ?";
        Utilizador u = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    u = extrairUtilizador(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar utilizador por ID: " + e.getMessage(), e);
        }
        return u;
    }

    public Utilizador buscarPorUsername(String username) {
        String sql = "SELECT u.id_utilizador, u.username, u.password_hash, u.perfil, " +
                     "f.id_funcionario, f.bi, f.estado, p.id_pessoa, p.nome " +
                     "FROM utilizador u " +
                     "INNER JOIN funcionario f ON u.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "WHERE u.username = ?";
        Utilizador u = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    u = extrairUtilizador(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar utilizador por username: " + e.getMessage(), e);
        }
        return u;
    }

    private Utilizador extrairUtilizador(ResultSet rs) throws SQLException {
        Utilizador u = new Utilizador();
        u.setIdUtilizador(rs.getLong("id_utilizador"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));

        String perf = rs.getString("perfil");
        if (perf != null) {
            u.setPerfil(Perfil.valueOf(perf));
        }

        Funcionario f = new Funcionario();
        f.setIdFuncionario(rs.getLong("id_funcionario"));
        f.setIdPessoa(rs.getLong("id_pessoa"));
        f.setNome(rs.getString("nome"));
        f.setBi(rs.getString("bi"));

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            f.setEstado(EstadoFuncionario.valueOf(estadoStr));
        }

        u.setFuncionario(f);

        return u;
    }

}
