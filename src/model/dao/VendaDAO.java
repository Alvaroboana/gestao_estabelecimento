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
import infra.Transacao;
import model.exception.VendaInvalidaException;
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
        if (objecto.getEstado() != null && objecto.getEstado() != EstadoVenda.PENDENTE)
            throw new VendaInvalidaException("Use a operação de conclusão para finalizar a venda.");
        Long anterior = objecto.getIdVenda();
        try {
            Transacao.executar("Erro ao salvar venda.", conn -> { salvar(conn, objecto); return null; });
        } catch (RuntimeException e) { objecto.setIdVenda(anterior); limparIdsItens(objecto); throw e; }
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
        if (venda.getEstado() != EstadoVenda.CONCLUIDA)
            throw new VendaInvalidaException("A conclusão deve passar pela camada de negócio.");
        Long anterior = venda.getIdVenda();
        try {
            Transacao.executar("Erro ao concluir venda.", conn -> {
                venda.validarPagamento();
                if (venda.getIdVenda() != null) exigirPendente(conn, venda.getIdVenda());
                java.util.List<ItemVendido> itens = new ArrayList<>(venda.getItemVendido());
                itens.sort(java.util.Comparator.comparing(i -> i.getProduto().getIdProduto()));
                // Bloquear antes de inserir os itens evita promover simultaneamente
                // os bloqueios partilhados das chaves estrangeiras dos produtos.
                for (ItemVendido item : itens) {
                    try (PreparedStatement st = conn.prepareStatement("SELECT estado FROM produto WHERE id_produto = ? FOR UPDATE")) {
                        st.setLong(1, item.getProduto().getIdProduto());
                        try (ResultSet rs = st.executeQuery()) {
                            if (!rs.next() || !"ATIVO".equals(rs.getString(1)))
                                throw new VendaInvalidaException("O produto deixou de estar disponível.");
                        }
                    }
                }
                if (venda.getIdVenda() == null) salvar(conn, venda);
                else atualizar(conn, venda);
                for (ItemVendido item : itens)
                    estoqueDAO.baixarEstoqueFEFO(conn, item.getProduto().getIdProduto(), item.getQuantidade(), item.getIdItem());
                try (PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO pagamento_venda (id_venda, valor_recebido, troco) VALUES (?, ?, ?)")) {
                    st.setLong(1, venda.getIdVenda());
                    st.setDouble(2, venda.getValorRecebido()); st.setDouble(3, venda.getTroco()); st.executeUpdate();
                }
                return null;
            });
        } catch (RuntimeException e) { venda.setIdVenda(anterior); limparIdsItens(venda); throw e; }
    }

    private void limparIdsItens(Venda venda) {
        if (venda.getItemVendido() != null) for (ItemVendido item : venda.getItemVendido()) item.setIdItem(null);
    }

    private void exigirPendente(Connection conn, Long id) throws SQLException {
        try (PreparedStatement st = conn.prepareStatement("SELECT estado FROM venda WHERE id_venda = ? FOR UPDATE")) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next() || !"PENDENTE".equals(rs.getString(1)))
                    throw new VendaInvalidaException("A venda já foi concluída, anulada ou deixou de existir. Actualize a lista.");
            }
        }
    }

    @Override
    public void eliminar(Long id) {
        Transacao.executar("Erro ao eliminar rascunho.", conn -> {
            exigirPendente(conn, id);
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM venda WHERE id_venda = ?")) {
                st.setLong(1, id); st.executeUpdate();
            }
            return null;
        });
    }

    @Override
    public void atualizar(Venda objecto) {
        if (objecto.getEstado() != EstadoVenda.PENDENTE && objecto.getEstado() != EstadoVenda.ANULADA)
            throw new VendaInvalidaException("Uma venda concluída não pode ser editada.");
        Transacao.executar("Erro ao actualizar venda.", conn -> { atualizar(conn, objecto); return null; });
    }

    public void atualizar(Connection conn, Venda objecto) throws SQLException {
        exigirPendente(conn, objecto.getIdVenda());
        if (objecto.getEstado() == EstadoVenda.ANULADA) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE venda SET estado = 'ANULADA' WHERE id_venda = ?")) {
                st.setLong(1, objecto.getIdVenda()); st.executeUpdate();
            }
            return;
        }
        try (PreparedStatement st = conn.prepareStatement(
                "UPDATE venda SET total = ?, estado = ?, id_cliente = ?, id_funcionario = ? WHERE id_venda = ?")) {
            st.setDouble(1, objecto.calcularTotal()); st.setString(2, objecto.getEstado().name());
            st.setLong(3, objecto.getCliente().getIdCliente()); st.setLong(4, objecto.getFuncionario().getIdFuncionario());
            st.setLong(5, objecto.getIdVenda()); st.executeUpdate();
        }
        try (PreparedStatement st = conn.prepareStatement("DELETE FROM item_vendido WHERE id_venda = ?")) {
            st.setLong(1, objecto.getIdVenda()); st.executeUpdate();
        }
        try (PreparedStatement st = conn.prepareStatement(
                "INSERT INTO item_vendido (quantidade, preco, desconto, id_venda, id_produto) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            for (ItemVendido item : objecto.getItemVendido()) {
                st.setInt(1, item.getQuantidade()); st.setDouble(2, item.getPreco());
                st.setDouble(3, item.getDesconto() == null ? 0 : item.getDesconto());
                st.setLong(4, objecto.getIdVenda()); st.setLong(5, item.getProduto().getIdProduto()); st.executeUpdate();
                try (ResultSet keys = st.getGeneratedKeys()) { if (keys.next()) item.setIdItem(keys.getLong(1)); }
                item.setVenda(objecto);
            }
        }
    }

    @Override
    public List<Venda> listarTodos() {
        String sql = "SELECT v.id_venda, v.data, v.total, v.estado, pag.valor_recebido, pag.troco, " +
                     "c.id_cliente, pc.nome AS nome_cliente, " +
                     "f.id_funcionario, pf.nome AS nome_funcionario, f.estado AS estado_funcionario " +
                     "FROM venda v LEFT JOIN pagamento_venda pag ON pag.id_venda = v.id_venda " +
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
        String sql = "SELECT v.id_venda, v.data, v.total, v.estado, pag.valor_recebido, pag.troco, " +
                     "c.id_cliente, pc.nome AS nome_cliente, " +
                     "f.id_funcionario, pf.nome AS nome_funcionario, f.estado AS estado_funcionario " +
                     "FROM venda v LEFT JOIN pagamento_venda pag ON pag.id_venda = v.id_venda " +
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
        v.setValorRecebido((Double) rs.getObject("valor_recebido", Double.class));
        v.setTroco((Double) rs.getObject("troco", Double.class));

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
