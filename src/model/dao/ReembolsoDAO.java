package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import infra.JdbcValidation;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Reembolso;

public class ReembolsoDAO implements GenericoDAO<Reembolso, Long> {

    @Override
    public void salvar(Reembolso objecto) {
        String sql = "INSERT INTO reembolso (quantidade, valor, motivo, data, id_item, id_funcionario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, objecto.getQuantidade() != null ? objecto.getQuantidade() : 1);
            stmt.setDouble(2, objecto.getValor() != null ? objecto.getValor() : 0.0);
            stmt.setString(3, objecto.getMotivo());

            if (objecto.getData() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(objecto.getData()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            stmt.setLong(5, JdbcValidation.requireId(
                    objecto.getItem() == null ? null : objecto.getItem().getIdItem(),
                    "item.idItem"));
            stmt.setLong(6, JdbcValidation.requireId(
                    objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                    "funcionario.idFuncionario"));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdReembolso(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar reembolso: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM reembolso WHERE id_reembolso = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar reembolso: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Reembolso objecto) {
        String sql = "UPDATE reembolso SET quantidade = ?, valor = ?, motivo = ?, data = ?, id_item = ?, id_funcionario = ? " +
                     "WHERE id_reembolso = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objecto.getQuantidade() != null ? objecto.getQuantidade() : 1);
            stmt.setDouble(2, objecto.getValor() != null ? objecto.getValor() : 0.0);
            stmt.setString(3, objecto.getMotivo());

            if (objecto.getData() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(objecto.getData()));
            } else {
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            }

            stmt.setLong(5, JdbcValidation.requireId(
                    objecto.getItem() == null ? null : objecto.getItem().getIdItem(),
                    "item.idItem"));
            stmt.setLong(6, JdbcValidation.requireId(
                    objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                    "funcionario.idFuncionario"));
            stmt.setLong(7, objecto.getIdReembolso());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar reembolso: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Reembolso> listarTodos() {
        String sql = "SELECT r.id_reembolso, r.quantidade, r.valor, r.motivo, r.data, " +
                     "iv.id_item, iv.quantidade AS item_quantidade, iv.preco AS item_preco, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM reembolso r " +
                     "INNER JOIN item_vendido iv ON r.id_item = iv.id_item " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON r.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "ORDER BY r.data DESC";
        List<Reembolso> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairReembolso(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar reembolsos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Reembolso buscarPorId(Long id) {
        String sql = "SELECT r.id_reembolso, r.quantidade, r.valor, r.motivo, r.data, " +
                     "iv.id_item, iv.quantidade AS item_quantidade, iv.preco AS item_preco, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM reembolso r " +
                     "INNER JOIN item_vendido iv ON r.id_item = iv.id_item " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON r.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE r.id_reembolso = ?";
        Reembolso reembolso = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reembolso = extrairReembolso(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar reembolso por ID: " + e.getMessage(), e);
        }
        return reembolso;
    }

    public List<Reembolso> buscarPorItem(Long idItem) {
        String sql = "SELECT r.id_reembolso, r.quantidade, r.valor, r.motivo, r.data, " +
                     "iv.id_item, iv.quantidade AS item_quantidade, iv.preco AS item_preco, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM reembolso r " +
                     "INNER JOIN item_vendido iv ON r.id_item = iv.id_item " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON r.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE r.id_item = ? " +
                     "ORDER BY r.data DESC";
        List<Reembolso> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idItem);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairReembolso(rs));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar reembolsos por item: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Reembolso> buscarPorFuncionario(Long idFuncionario) {
        String sql = "SELECT r.id_reembolso, r.quantidade, r.valor, r.motivo, r.data, " +
                     "iv.id_item, iv.quantidade AS item_quantidade, iv.preco AS item_preco, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM reembolso r " +
                     "INNER JOIN item_vendido iv ON r.id_item = iv.id_item " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON r.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE r.id_funcionario = ? " +
                     "ORDER BY r.data DESC";
        List<Reembolso> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairReembolso(rs));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar reembolsos por funcionário: " + e.getMessage(), e);
        }
        return lista;
    }

    private Reembolso extrairReembolso(ResultSet rs) throws SQLException {
        Reembolso r = new Reembolso();
        r.setIdReembolso(rs.getLong("id_reembolso"));
        r.setQuantidade(rs.getInt("quantidade"));
        r.setValor(rs.getDouble("valor"));
        r.setMotivo(rs.getString("motivo"));

        Timestamp ts = rs.getTimestamp("data");
        if (ts != null) {
            r.setData(ts.toLocalDateTime());
        }

        ItemVendido item = new ItemVendido();
        item.setIdItem(rs.getLong("id_item"));
        item.setQuantidade(rs.getInt("item_quantidade"));
        item.setPreco(rs.getDouble("item_preco"));

        Produto prod = new Produto();
        prod.setIdProduto(rs.getLong("id_produto"));
        prod.setNome(rs.getString("nome_produto"));
        item.setProduto(prod);

        r.setItem(item);

        Funcionario func = new Funcionario();
        func.setIdFuncionario(rs.getLong("id_funcionario"));
        func.setNome(rs.getString("nome_funcionario"));
        r.setFuncionario(func);

        return r;
    }
}
