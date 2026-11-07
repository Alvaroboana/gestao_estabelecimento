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
import model.vo.Bairro;
import model.vo.Cidade;
import model.vo.Endereco;

public class EnderecoDAO implements GenericoDAO<Endereco, Long> {

    @Override
    public void salvar(Endereco objecto) {
        throw new IllegalArgumentException("idPessoa e obrigatorio para salvar um endereco.");
    }

    public void salvarComPessoa(Endereco objecto, Long idPessoa) {
        String sql = "INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getQuarteirao());
            stmt.setString(2, objecto.getRua());
            stmt.setString(3, objecto.getNumeroCasa());
            stmt.setLong(4, JdbcValidation.requireId(
                    objecto.getBairro() == null ? null : objecto.getBairro().getIdBairro(),
                    "bairro.idBairro"));
            stmt.setLong(5, JdbcValidation.requireId(idPessoa, "idPessoa"));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdEndereco(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar endereco: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM endereco WHERE id_endereco = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar endereco: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Endereco objecto) {
        String sql = "UPDATE endereco SET quarteirao = ?, rua = ?, numero_casa = ?, id_bairro = ? WHERE id_endereco = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getQuarteirao());
            stmt.setString(2, objecto.getRua());
            stmt.setString(3, objecto.getNumeroCasa());
            stmt.setLong(4, JdbcValidation.requireId(
                    objecto.getBairro() == null ? null : objecto.getBairro().getIdBairro(),
                    "bairro.idBairro"));
            stmt.setLong(5, objecto.getIdEndereco());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar endereco: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Endereco> listarTodos() {
        String sql = "SELECT e.id_endereco, e.quarteirao, e.rua, e.numero_casa, " +
                     "b.id_bairro, b.nome AS nome_bairro, c.id_cidade, c.nome AS nome_cidade " +
                     "FROM endereco e " +
                     "LEFT JOIN bairro b ON e.id_bairro = b.id_bairro " +
                     "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade";
        List<Endereco> lista = new ArrayList<Endereco>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Endereco end = extrairEndereco(rs);
                lista.add(end);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar enderecos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Endereco buscarPorId(Long id) {
        String sql = "SELECT e.id_endereco, e.quarteirao, e.rua, e.numero_casa, " +
                     "b.id_bairro, b.nome AS nome_bairro, c.id_cidade, c.nome AS nome_cidade " +
                     "FROM endereco e " +
                     "LEFT JOIN bairro b ON e.id_bairro = b.id_bairro " +
                     "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade " +
                     "WHERE e.id_endereco = ?";
        Endereco end = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    end = extrairEndereco(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar endereco por ID: " + e.getMessage(), e);
        }
        return end;
    }

    public Endereco buscarPorPessoa(Long idPessoa) {
        String sql = "SELECT e.id_endereco, e.quarteirao, e.rua, e.numero_casa, " +
                     "b.id_bairro, b.nome AS nome_bairro, c.id_cidade, c.nome AS nome_cidade " +
                     "FROM endereco e " +
                     "LEFT JOIN bairro b ON e.id_bairro = b.id_bairro " +
                     "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade " +
                     "WHERE e.id_pessoa = ?";
        Endereco end = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPessoa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    end = extrairEndereco(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar endereco por pessoa: " + e.getMessage(), e);
        }
        return end;
    }

    private Endereco extrairEndereco(ResultSet rs) throws SQLException {
        Endereco end = new Endereco();
        end.setIdEndereco(rs.getLong("id_endereco"));
        end.setQuarteirao(rs.getString("quarteirao"));
        end.setRua(rs.getString("rua"));
        end.setNumeroCasa(rs.getString("numero_casa"));

        Bairro b = new Bairro();
        b.setIdBairro(rs.getLong("id_bairro"));
        b.setNome(rs.getString("nome_bairro"));

        Cidade c = new Cidade();
        c.setIdCidade(rs.getLong("id_cidade"));
        c.setNome(rs.getString("nome_cidade"));
        b.setCidade(c);

        end.setBairro(b);
        return end;
    }

}
