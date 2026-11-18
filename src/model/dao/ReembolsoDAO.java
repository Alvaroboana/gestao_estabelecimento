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
import model.exception.ReembolsoInvalidoException;
import infra.DBException;
import infra.JdbcValidation;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Reembolso;

public class ReembolsoDAO implements GenericoDAO<Reembolso, Long> {

    @Override
    public void salvar(Reembolso objecto) {
        salvarReembolsoEReporEstoque(objecto, new EstoqueDAO());
    }

    private void inserir(Connection conn, Reembolso objecto) throws SQLException {
        String sql = "INSERT INTO reembolso (quantidade, valor, motivo, data, id_item, id_funcionario) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        }
    }

    public void salvarReembolsoEReporEstoque(Reembolso reembolso, EstoqueDAO estoqueDAO) {
        Long anterior = reembolso.getIdReembolso();
        try {
            Transacao.executar("Erro ao processar reembolso.", conn -> {
                long idItem = reembolso.getItem().getIdItem();
                long idVenda;
                try (PreparedStatement st = conn.prepareStatement("SELECT id_venda FROM item_vendido WHERE id_item = ?")) {
                    st.setLong(1, idItem);
                    try (ResultSet rs = st.executeQuery()) {
                        if (!rs.next()) throw new ReembolsoInvalidoException("O item já não existe.");
                        idVenda = rs.getLong(1);
                    }
                }
                try (PreparedStatement st = conn.prepareStatement("SELECT estado FROM venda WHERE id_venda = ? FOR UPDATE")) {
                    st.setLong(1, idVenda);
                    try (ResultSet rs = st.executeQuery()) {
                        if (!rs.next() || !"CONCLUIDA".equals(rs.getString(1)))
                            throw new ReembolsoInvalidoException("A venda não está disponível para reembolso.");
                    }
                }
                int vendido; java.math.BigDecimal preco, desconto;
                try (PreparedStatement st = conn.prepareStatement("SELECT quantidade, preco, desconto FROM item_vendido WHERE id_item = ? FOR UPDATE")) {
                    st.setLong(1, idItem);
                    try (ResultSet rs = st.executeQuery()) {
                        if (!rs.next()) throw new ReembolsoInvalidoException("Item inexistente.");
                        vendido = rs.getInt(1); preco = rs.getBigDecimal(2); desconto = rs.getBigDecimal(3);
                    }
                }
                int devolvido = 0; java.math.BigDecimal valorDevolvido = java.math.BigDecimal.ZERO;
                try (PreparedStatement st = conn.prepareStatement("SELECT quantidade, valor FROM reembolso WHERE id_item = ? FOR UPDATE")) {
                    st.setLong(1, idItem);
                    try (ResultSet rs = st.executeQuery()) { while (rs.next()) { devolvido += rs.getInt(1); valorDevolvido = valorDevolvido.add(rs.getBigDecimal(2)); } }
                }
                int quantidade = reembolso.getQuantidade();
                if (quantidade <= 0 || quantidade > vendido - devolvido)
                    throw new ReembolsoInvalidoException("A quantidade excede o saldo actual do item. Actualize a venda.");
                java.math.BigDecimal total = preco.multiply(java.math.BigDecimal.valueOf(vendido)).subtract(desconto).max(java.math.BigDecimal.ZERO);
                java.math.BigDecimal valor = quantidade == vendido - devolvido ? total.subtract(valorDevolvido)
                        : total.multiply(java.math.BigDecimal.valueOf(quantidade)).divide(java.math.BigDecimal.valueOf(vendido), 2, java.math.RoundingMode.HALF_UP);
                reembolso.setValor(valor.max(java.math.BigDecimal.ZERO).doubleValue());
                inserir(conn, reembolso);
                distribuirDevolucao(conn, idItem, quantidade, reembolso.isReporEstoque());
                try (PreparedStatement st = conn.prepareStatement("INSERT INTO reembolso_destino (id_reembolso, reposto) VALUES (?, ?)")) {
                    st.setLong(1, reembolso.getIdReembolso()); st.setBoolean(2, reembolso.isReporEstoque()); st.executeUpdate();
                }
                try (PreparedStatement st = conn.prepareStatement(
                        "UPDATE venda SET estado = 'REEMBOLSADA' WHERE id_venda = ? AND "
                        + "(SELECT SUM(quantidade) FROM item_vendido WHERE id_venda = ?) = "
                        + "(SELECT COALESCE(SUM(r.quantidade),0) FROM reembolso r JOIN item_vendido i ON i.id_item = r.id_item WHERE i.id_venda = ?)")) {
                    st.setLong(1, idVenda); st.setLong(2, idVenda); st.setLong(3, idVenda); st.executeUpdate();
                }
                return null;
            });
        } catch (RuntimeException e) { reembolso.setIdReembolso(anterior); throw e; }
    }

    private void distribuirDevolucao(Connection conn, long idItem, int quantidade, boolean repor) throws SQLException {
        int restante = quantidade;
        try (PreparedStatement st = conn.prepareStatement(
                "SELECT il.id_estoque, il.quantidade - il.quantidade_devolvida AS saldo, e.data_validade "
                + "FROM item_lote il JOIN estoque e ON e.id_estoque = il.id_estoque "
                + "WHERE il.id_item = ? ORDER BY il.id_estoque FOR UPDATE")) {
            st.setLong(1, idItem);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next() && restante > 0) {
                    int devolver = Math.min(restante, rs.getInt("saldo"));
                    if (devolver <= 0) continue;
                    long lote = rs.getLong("id_estoque");
                    try (PreparedStatement upd = conn.prepareStatement("UPDATE item_lote SET quantidade_devolvida = quantidade_devolvida + ? WHERE id_item = ? AND id_estoque = ?")) {
                        upd.setInt(1, devolver); upd.setLong(2, idItem); upd.setLong(3, lote); upd.executeUpdate();
                    }
                    if (repor) {
                        java.sql.Date validade = rs.getDate("data_validade");
                        if (validade != null && validade.toLocalDate().isBefore(java.time.LocalDate.now()))
                            throw new ReembolsoInvalidoException("O lote original expirou. Registe a devolução sem reposição ao stock.");
                        try (PreparedStatement upd = conn.prepareStatement("UPDATE estoque SET quantidade = quantidade + ? WHERE id_estoque = ?")) {
                            upd.setInt(1, devolver); upd.setLong(2, lote); upd.executeUpdate();
                        }
                    }
                    restante -= devolver;
                }
            }
        }
        if (repor && restante > 0)
            throw new ReembolsoInvalidoException("Esta venda antiga não tem os lotes de origem registados. Faça o reembolso sem reposição automática e confira o stock fisicamente.");
    }
    @Override
    public void eliminar(Long id) {
        throw new ReembolsoInvalidoException("Reembolsos registados não podem ser alterados ou eliminados, para preservar o stock e o histórico.");
    }

    @Override
    public void atualizar(Reembolso objecto) {
        throw new ReembolsoInvalidoException("Reembolsos registados não podem ser alterados ou eliminados, para preservar o stock e o histórico.");
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
