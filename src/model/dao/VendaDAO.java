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
import model.vo.Cliente;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Venda;

public class VendaDAO implements GenericoDAO<Venda, Long> {

    @Override
    public void salvar(Venda objecto) {
        String sqlVenda = "INSERT INTO venda (data, total, estado, id_cliente, id_funcionario) VALUES (?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            Long idVenda = null;
            try (PreparedStatement stmtV = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                if (objecto.getData() != null) {
                    stmtV.setTimestamp(1, Timestamp.valueOf(objecto.getData()));
                } else {
                    stmtV.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                }

                double tot = 0.0;
                if (objecto.getTotal() != null) {
                    tot = objecto.getTotal();
                }
                stmtV.setDouble(2, tot);

                String estNome = "PENDENTE";
                if (objecto.getEstado() != null) {
                    estNome = objecto.getEstado().name();
                }
                stmtV.setString(3, estNome);

                stmtV.setLong(4, JdbcValidation.requireId(
                        objecto.getCliente() == null ? null : objecto.getCliente().getIdCliente(),
                        "cliente.idCliente"));
                stmtV.setLong(5, JdbcValidation.requireId(
                        objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                        "funcionario.idFuncionario"));

                stmtV.executeUpdate();
                try (ResultSet rs = stmtV.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVenda = rs.getLong(1);
                        objecto.setIdVenda(idVenda);
                    }
                }
            }

            if (objecto.getItemVendido() != null && !objecto.getItemVendido().isEmpty()) {
                try (PreparedStatement stmtI = conn.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < objecto.getItemVendido().size(); i++) {
                        ItemVendido item = objecto.getItemVendido().get(i);
                        if (item != null) {
                            int qtd = 1;
                            if (item.getQuantidade() != null) {
                                qtd = item.getQuantidade();
                            }
                            stmtI.setInt(1, qtd);

                            double preco = 0.0;
                            if (item.getPreco() != null) {
                                preco = item.getPreco();
                            }
                            stmtI.setDouble(2, preco);

                            double desc = 0.0;
                            if (item.getDesconto() != null) {
                                desc = item.getDesconto();
                            }
                            stmtI.setDouble(3, desc);

                            stmtI.setLong(4, JdbcValidation.requireId(idVenda, "venda.idVenda"));

                            stmtI.setLong(5, JdbcValidation.requireId(
                                    item.getProduto() == null ? null : item.getProduto().getIdProduto(),
                                    "produto.idProduto"));

                            stmtI.executeUpdate();
                            try (ResultSet rs = stmtI.getGeneratedKeys()) {
                                if (rs.next()) {
                                    item.setIdItem(rs.getLong(1));
                                }
                            }
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao salvar venda.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM venda WHERE id_venda = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar venda: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Venda objecto) {
        String sql = "UPDATE venda SET total = ?, estado = ? WHERE id_venda = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            double tot = 0.0;
            if (objecto.getTotal() != null) {
                tot = objecto.getTotal();
            }
            stmt.setDouble(1, tot);

            String estNome = "PENDENTE";
            if (objecto.getEstado() != null) {
                estNome = objecto.getEstado().name();
            }
            stmt.setString(2, estNome);
            stmt.setLong(3, objecto.getIdVenda());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar venda: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Venda> listarTodos() {
        String sql = "SELECT v.id_venda, v.data, v.total, v.estado, " +
                     "c.id_cliente, pc.nome AS nome_cliente, " +
                     "f.id_funcionario, pf.nome AS nome_funcionario " +
                     "FROM venda v " +
                     "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN pessoa pc ON c.id_pessoa = pc.id_pessoa " +
                     "INNER JOIN funcionario f ON v.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pf ON f.id_pessoa = pf.id_pessoa " +
                     "ORDER BY v.data DESC";
        List<Venda> lista = new ArrayList<Venda>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairVenda(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar vendas: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Venda buscarPorId(Long id) {
        String sql = "SELECT v.id_venda, v.data, v.total, v.estado, " +
                     "c.id_cliente, pc.nome AS nome_cliente, " +
                     "f.id_funcionario, pf.nome AS nome_funcionario " +
                     "FROM venda v " +
                     "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN pessoa pc ON c.id_pessoa = pc.id_pessoa " +
                     "INNER JOIN funcionario f ON v.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pf ON f.id_pessoa = pf.id_pessoa " +
                     "WHERE v.id_venda = ?";
        Venda v = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    v = extrairVenda(rs);
                    carregarItens(conn, v);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar venda por ID: " + e.getMessage(), e);
        }
        return v;
    }

    private Venda extrairVenda(ResultSet rs) throws SQLException {
        Venda v = new Venda();
        v.setIdVenda(rs.getLong("id_venda"));

        Timestamp ts = rs.getTimestamp("data");
        if (ts != null) {
            v.setData(ts.toLocalDateTime());
        }
        v.setTotal(rs.getDouble("total"));

        String est = rs.getString("estado");
        if (est != null) {
            v.setEstado(EstadoVenda.valueOf(est));
        }

        Cliente cli = new Cliente();
        cli.setIdCliente(rs.getLong("id_cliente"));
        cli.setNome(rs.getString("nome_cliente"));
        v.setCliente(cli);

        Funcionario func = new Funcionario();
        func.setIdFuncionario(rs.getLong("id_funcionario"));
        func.setNome(rs.getString("nome_funcionario"));
        v.setFuncionario(func);

        return v;
    }

    private void carregarItens(Connection conn, Venda venda) {
        if (venda == null || venda.getIdVenda() == null) {
            return;
        }
        String sql = "SELECT iv.id_item, iv.quantidade, iv.preco, iv.desconto, " +
                     "p.id_produto, p.nome AS nome_produto " +
                     "FROM item_vendido iv " +
                     "INNER JOIN produto p ON iv.id_produto = p.id_produto " +
                     "WHERE iv.id_venda = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, venda.getIdVenda());
            try (ResultSet rs = stmt.executeQuery()) {
                List<ItemVendido> itens = new ArrayList<ItemVendido>();
                while (rs.next()) {
                    ItemVendido item = new ItemVendido();
                    item.setIdItem(rs.getLong("id_item"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPreco(rs.getDouble("preco"));
                    item.setDesconto(rs.getDouble("desconto"));
                    item.setVenda(venda);

                    Produto prod = new Produto();
                    prod.setIdProduto(rs.getLong("id_produto"));
                    prod.setNome(rs.getString("nome_produto"));
                    item.setProduto(prod);

                    itens.add(item);
                }
                venda.setItemVendido(itens);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar itens da venda.", e);
        }
    }

}
