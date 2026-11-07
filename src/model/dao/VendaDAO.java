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
import model.vo.EstadoFuncionario;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Reembolso;
import model.vo.Venda;

public class VendaDAO implements GenericoDAO<Venda, Long> {

    @Override
    public void salvar(Venda objecto) {
        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);
            salvar(conn, objecto);
            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao salvar venda.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    public void salvar(Connection conn, Venda objecto) throws SQLException {
        String sqlVenda = "INSERT INTO venda (data, total, estado, id_cliente, id_funcionario) VALUES (?, ?, ?, ?, ?)";
        String sqlItem = "INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto) VALUES (?, ?, ?, ?, ?)";

        Long idVenda = null;
        try (PreparedStatement stmtV = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
            if (objecto.getData() != null) {
                stmtV.setTimestamp(1, Timestamp.valueOf(objecto.getData()));
            } else {
                stmtV.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            }

            double tot = objecto.getTotal() != null ? objecto.getTotal() : 0.0;
            stmtV.setDouble(2, tot);

            String estNome = objecto.getEstado() != null ? objecto.getEstado().name() : "PENDENTE";
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
                for (ItemVendido item : objecto.getItemVendido()) {
                    if (item != null) {
                        int qtd = item.getQuantidade() != null ? item.getQuantidade() : 1;
                        stmtI.setInt(1, qtd);
                        stmtI.setDouble(2, item.getPreco() != null ? item.getPreco() : 0.0);
                        stmtI.setDouble(3, item.getDesconto() != null ? item.getDesconto() : 0.0);
                        stmtI.setLong(4, JdbcValidation.requireId(idVenda, "venda.idVenda"));
                        stmtI.setLong(5, JdbcValidation.requireId(
                                item.getProduto() == null ? null : item.getProduto().getIdProduto(),
                                "produto.idProduto"));
                        stmtI.executeUpdate();
                        try (ResultSet rs = stmtI.getGeneratedKeys()) {
                            if (rs.next()) {
                                item.setIdItem(rs.getLong(1));
                                item.setVenda(objecto);
                            }
                        }
                    }
                }
            }
        }
    }

    public void concluirVendaAtomicamente(Venda venda, EstoqueDAO estoqueDAO) {
        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            if (venda.getIdVenda() == null || venda.getIdVenda() <= 0) {
                salvar(conn, venda);
            } else {
                atualizar(conn, venda);
                if (venda.getItemVendido() == null || venda.getItemVendido().isEmpty()) {
                    carregarItens(conn, venda);
                }
            }

            if (venda.getItemVendido() != null) {
                for (ItemVendido item : venda.getItemVendido()) {
                    if (item != null && item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                        int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
                        if (qtd > 0) {
                            estoqueDAO.baixarEstoqueFEFO(conn, item.getProduto().getIdProduto(), qtd);
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao concluir venda: " + e.getMessage(), e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        } catch (RuntimeException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
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
        try (Connection conn = ConexaoDB.getConnection()) {
            atualizar(conn, objecto);
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar venda: " + e.getMessage(), e);
        }
    }

    public void atualizar(Connection conn, Venda objecto) throws SQLException {
        String sql = "UPDATE venda SET total = ?, estado = ? WHERE id_venda = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            double tot = objecto.getTotal() != null ? objecto.getTotal() : 0.0;
            stmt.setDouble(1, tot);

            String estNome = objecto.getEstado() != null ? objecto.getEstado().name() : "PENDENTE";
            stmt.setString(2, estNome);
            stmt.setLong(3, objecto.getIdVenda());

            stmt.executeUpdate();
        }
    }

    @Override
    public List<Venda> listarTodos() {
        String sql = "SELECT v.id_venda, v.data, v.total, v.estado, " +
                     "c.id_cliente, pc.nome AS nome_cliente, " +
                     "f.id_funcionario, pf.nome AS nome_funcionario, f.estado AS estado_funcionario " +
                     "FROM venda v " +
                     "INNER JOIN cliente c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN pessoa pc ON c.id_pessoa = pc.id_pessoa " +
                     "INNER JOIN funcionario f ON v.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pf ON f.id_pessoa = pf.id_pessoa " +
                     "ORDER BY v.data DESC, v.id_venda DESC";
        List<Venda> lista = new ArrayList<Venda>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Venda v = extrairVenda(rs);
                carregarItens(conn, v);
                lista.add(v);
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
                     "f.id_funcionario, pf.nome AS nome_funcionario, f.estado AS estado_funcionario " +
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
        try {
            String estFunc = rs.getString("estado_funcionario");
            if (estFunc != null) {
                func.setEstado(EstadoFuncionario.valueOf(estFunc));
            } else {
                func.setEstado(EstadoFuncionario.ATIVO);
            }
        } catch (Exception ignored) {
            func.setEstado(EstadoFuncionario.ATIVO);
        }
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

                    carregarReembolsosDoItem(conn, item);
                    itens.add(item);
                }
                venda.setItemVendido(itens);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar itens da venda.", e);
        }
    }

    private void carregarReembolsosDoItem(Connection conn, ItemVendido item) throws SQLException {
        if (item == null || item.getIdItem() == null) {
            return;
        }
        String sql = "SELECT id_reembolso, quantidade, valor, motivo, data FROM reembolso WHERE id_item = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, item.getIdItem());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Reembolso> reembolsos = new ArrayList<Reembolso>();
                while (rs.next()) {
                    Reembolso r = new Reembolso();
                    r.setIdReembolso(rs.getLong("id_reembolso"));
                    r.setQuantidade(rs.getInt("quantidade"));
                    r.setValor(rs.getDouble("valor"));
                    r.setMotivo(rs.getString("motivo"));
                    Timestamp ts = rs.getTimestamp("data");
                    if (ts != null) {
                        r.setData(ts.toLocalDateTime());
                    }
                    r.setItem(item);
                    reembolsos.add(r);
                }
                item.setReembolso(reembolsos);
            }
        }
    }

}
