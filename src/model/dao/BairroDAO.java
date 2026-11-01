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
import model.vo.Bairro;
import model.vo.Cidade;

public class BairroDAO implements GenericoDAO<Bairro, Long> {

    @Override
    public void salvar(Bairro objecto) {
        String sql = "INSERT INTO bairro (nome, id_cidade) VALUES (?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());
            long idCid = 0L;
            if (objecto.getCidade() != null && objecto.getCidade().getIdCidade() != null) {
                idCid = objecto.getCidade().getIdCidade();
            }
            stmt.setLong(2, idCid);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdBairro(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar bairro: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM bairro WHERE id_bairro = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar bairro: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Bairro objecto) {
        String sql = "UPDATE bairro SET nome = ?, id_cidade = ? WHERE id_bairro = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());
            long idCid = 0L;
            if (objecto.getCidade() != null && objecto.getCidade().getIdCidade() != null) {
                idCid = objecto.getCidade().getIdCidade();
            }
            stmt.setLong(2, idCid);
            stmt.setLong(3, objecto.getIdBairro());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar bairro: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Bairro> listarTodos() {
        String sql = "SELECT b.id_bairro, b.nome, b.id_cidade, c.nome AS nome_cidade " +
                     "FROM bairro b " +
                     "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade " +
                     "ORDER BY b.nome";
        List<Bairro> bairros = new ArrayList<Bairro>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Bairro bairro = new Bairro();
                bairro.setIdBairro(rs.getLong("id_bairro"));
                bairro.setNome(rs.getString("nome"));

                Cidade cidade = new Cidade();
                cidade.setIdCidade(rs.getLong("id_cidade"));
                cidade.setNome(rs.getString("nome_cidade"));
                bairro.setCidade(cidade);

                bairros.add(bairro);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar bairros: " + e.getMessage(), e);
        }
        return bairros;
    }

    @Override
    public Bairro buscarPorId(Long id) {
        String sql = "SELECT b.id_bairro, b.nome, b.id_cidade, c.nome AS nome_cidade " +
                     "FROM bairro b " +
                     "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade " +
                     "WHERE b.id_bairro = ?";
        Bairro bairro = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bairro = new Bairro();
                    bairro.setIdBairro(rs.getLong("id_bairro"));
                    bairro.setNome(rs.getString("nome"));

                    Cidade cidade = new Cidade();
                    cidade.setIdCidade(rs.getLong("id_cidade"));
                    cidade.setNome(rs.getString("nome_cidade"));
                    bairro.setCidade(cidade);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar bairro por ID: " + e.getMessage(), e);
        }
        return bairro;
    }

}
