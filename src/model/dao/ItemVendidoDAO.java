package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Venda;

public class ItemVendidoDAO implements GenericoDAO<ItemVendido, Long> {

    @Override
    public void salvar(ItemVendido objecto) {
        String sql = "INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, objecto.getQuantidade() != null ? objecto.getQuantidade() : 1);
            stmt.setDouble(2, objecto.getPreco() != null ? objecto.getPreco() : 0.0);
            stmt.setDouble(3, objecto.getDesconto() != null ? objecto.getDesconto() : 0.0);

            long idVenda = 0L;
            if (objecto.getVenda() != null && objecto.getVenda().getIdVenda() != null) {
                idVenda = objecto.getVenda().getIdVenda();
            }
            stmt.setLong(4, idVenda);

            long idProduto = 0L;
            if (objecto.getProduto() != null && objecto.getProduto().getIdProduto() != null) {
                idProduto = objecto.getProduto().getIdProduto();
            }
            stmt.setLong(5, idProduto);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdItem(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar item vendido: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM item_vendido WHERE id_item = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar item vendido: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(ItemVendido objecto) {
        String sql = "UPDATE item_vendido SET quantidade = ?, preco = ?, desconto = ?, id_venda = ?, id_produto = ? WHERE id_item = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, objecto.getQuantidade() != null ? objecto.getQuantidade() : 1);
            stmt.setDouble(2, objecto.getPreco() != null ? objecto.getPreco() : 0.0);
            stmt.setDouble(3, objecto.getDesconto() != null ? objecto.getDesconto() : 0.0);

            long idVenda = 0L;
            if (objecto.getVenda() != null && objecto.getVenda().getIdVenda() != null) {
                idVenda = objecto.getVenda().getIdVenda();
            }
            stmt.setLong(4, idVenda);

            long idProduto = 0L;
            if (objecto.getProduto() != null && objecto.getProduto().getIdProduto() != null) {
                idProduto = objecto.getProduto().getIdProduto();
            }
            stmt.setLong(5, idProduto);
            stmt.setLong(6, objecto.getIdItem());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar item vendido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ItemVendido> listarTodos() {
        String sql = "SELECT iv.id_item, iv.quantidade, iv.preco, iv.desconto, " +
                     "v.id_venda, v.data AS data_venda, " +
                     "p.id_produto, p.nome AS nome_produto, p.preco AS preco_produto " +
                     "FROM item_vendido iv " +
                     "INNER JOIN venda v ON iv.id_venda = v.id_venda " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "ORDER BY iv.id_item DESC";
        List<ItemVendido> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairItemVendido(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar itens vendidos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public ItemVendido buscarPorId(Long id) {
        String sql = "SELECT iv.id_item, iv.quantidade, iv.preco, iv.desconto, " +
                     "v.id_venda, v.data AS data_venda, " +
                     "p.id_produto, p.nome AS nome_produto, p.preco AS preco_produto " +
                     "FROM item_vendido iv " +
                     "INNER JOIN venda v ON iv.id_venda = v.id_venda " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "WHERE iv.id_item = ?";
        ItemVendido item = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item = extrairItemVendido(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar item vendido por ID: " + e.getMessage(), e);
        }
        return item;
    }

    public List<ItemVendido> buscarPorVenda(Long idVenda) {
        String sql = "SELECT iv.id_item, iv.quantidade, iv.preco, iv.desconto, " +
                     "v.id_venda, v.data AS data_venda, " +
                     "p.id_produto, p.nome AS nome_produto, p.preco AS preco_produto " +
                     "FROM item_vendido iv " +
                     "INNER JOIN venda v ON iv.id_venda = v.id_venda " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "WHERE iv.id_venda = ? " +
                     "ORDER BY iv.id_item ASC";
        List<ItemVendido> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVenda);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairItemVendido(rs));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar itens por venda: " + e.getMessage(), e);
        }
        return lista;
    }

    private ItemVendido extrairItemVendido(ResultSet rs) throws SQLException {
        ItemVendido item = new ItemVendido();
        item.setIdItem(rs.getLong("id_item"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPreco(rs.getDouble("preco"));
        item.setDesconto(rs.getDouble("desconto"));

        Venda venda = new Venda();
        venda.setIdVenda(rs.getLong("id_venda"));
        Timestamp ts = rs.getTimestamp("data_venda");
        if (ts != null) {
            venda.setData(ts.toLocalDateTime());
        }
        item.setVenda(venda);

        Produto produto = new Produto();
        produto.setIdProduto(rs.getLong("id_produto"));
        produto.setNome(rs.getString("nome_produto"));
        produto.setPreco(rs.getDouble("preco_produto"));
        item.setProduto(produto);

        return item;
    }
}
