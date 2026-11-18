package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import infra.JdbcValidation;
import model.vo.Estoque;
import model.vo.Funcionario;
import model.vo.Produto;

public class EstoqueDAO implements GenericoDAO<Estoque, Long> {

    @Override
    public void salvar(Estoque objecto) {
        try (Connection conn = ConexaoDB.getConnection()) { salvar(conn, objecto); }
        catch (SQLException e) { throw new DBException("Erro ao salvar estoque.", e); }
    }

    public void salvar(Connection conn, Estoque objecto) throws SQLException {
        String sql = "INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int qtd = 0;
            if (objecto.getQuantidade() != null) {
                qtd = objecto.getQuantidade();
            }
            stmt.setInt(1, qtd);

            if (objecto.getDataEntrada() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(objecto.getDataEntrada().atStartOfDay()));
            } else {
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDate.now().atStartOfDay()));
            }

            if (objecto.getDataValidade() != null) {
                stmt.setDate(3, Date.valueOf(objecto.getDataValidade()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setLong(4, JdbcValidation.requireId(
                    objecto.getProduto() == null ? null : objecto.getProduto().getIdProduto(),
                    "produto.idProduto"));
            stmt.setLong(5, JdbcValidation.requireId(
                    objecto.getFuncionario() == null ? null : objecto.getFuncionario().getIdFuncionario(),
                    "funcionario.idFuncionario"));

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdEstoque(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM estoque WHERE id_estoque = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar estoque: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Estoque objecto) {
        try (Connection conn = ConexaoDB.getConnection()) {
            atualizar(conn, objecto);
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar estoque: " + e.getMessage(), e);
        }
    }

    public void atualizar(Connection conn, Estoque objecto) throws SQLException {
        String sql = "UPDATE estoque SET quantidade = ?, data_validade = ?, id_produto = COALESCE(?, id_produto) WHERE id_estoque = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int qtd = objecto.getQuantidade() != null ? objecto.getQuantidade() : 0;
            stmt.setInt(1, qtd);

            if (objecto.getDataValidade() != null) {
                stmt.setDate(2, Date.valueOf(objecto.getDataValidade()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }

            if (objecto.getProduto() != null && objecto.getProduto().getIdProduto() != null) {
                stmt.setLong(3, objecto.getProduto().getIdProduto());
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            stmt.setLong(4, objecto.getIdEstoque());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Estoque> listarTodos() {
        String sql = "SELECT e.id_estoque, e.quantidade, e.data_entrada, e.data_validade, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM estoque e " +
                     "INNER JOIN produto p ON e.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON e.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "ORDER BY e.data_entrada DESC";
        List<Estoque> lista = new ArrayList<Estoque>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairEstoque(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar estoques: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Estoque buscarPorId(Long id) {
        String sql = "SELECT e.id_estoque, e.quantidade, e.data_entrada, e.data_validade, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM estoque e " +
                     "INNER JOIN produto p ON e.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON e.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE e.id_estoque = ?";
        Estoque est = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    est = extrairEstoque(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar estoque por ID: " + e.getMessage(), e);
        }
        return est;
    }

    public List<Estoque> buscarPorProduto(Long idProduto) {
        try (Connection conn = ConexaoDB.getConnection()) {
            return buscarPorProduto(conn, idProduto);
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar estoques por produto: " + e.getMessage(), e);
        }
    }

    public List<Estoque> buscarPorProduto(Connection conn, Long idProduto) throws SQLException {
        String sql = "SELECT e.id_estoque, e.quantidade, e.data_entrada, e.data_validade, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM estoque e " +
                     "INNER JOIN produto p ON e.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON e.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE e.id_produto = ? AND e.quantidade > 0 " +
                     "AND (e.data_validade IS NULL OR e.data_validade >= CURRENT_DATE) " +
                     "ORDER BY CASE WHEN e.data_validade IS NULL THEN 1 ELSE 0 END, e.data_validade ASC, e.id_estoque ASC";
        List<Estoque> lista = new ArrayList<Estoque>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairEstoque(rs));
                }
            }
        }
        return lista;
    }

    public void baixarEstoqueFEFO(Connection conn, Long idProduto, int quantidadeDesejada) throws SQLException {
        baixarEstoqueFEFO(conn, idProduto, quantidadeDesejada, null);
    }

    public void baixarEstoqueFEFO(Connection conn, Long idProduto, int quantidadeDesejada, Long idItem) throws SQLException {
        if (quantidadeDesejada <= 0) {
            return;
        }
        try (PreparedStatement lock = conn.prepareStatement("SELECT estado FROM produto WHERE id_produto = ? FOR UPDATE")) {
            lock.setLong(1, idProduto);
            try (ResultSet rs = lock.executeQuery()) {
                if (!rs.next() || !"ATIVO".equals(rs.getString(1))) throw new model.exception.VendaInvalidaException("Produto indisponível.");
            }
        }
        List<Estoque> lotesValidos = new ArrayList<>();
        try (PreparedStatement lock = conn.prepareStatement(
                "SELECT id_estoque, quantidade FROM estoque WHERE id_produto = ? AND quantidade > 0 "
                + "AND (data_validade IS NULL OR data_validade >= CURRENT_DATE) "
                + "ORDER BY data_validade IS NULL, data_validade, id_estoque FOR UPDATE")) {
            lock.setLong(1, idProduto);
            try (ResultSet rs = lock.executeQuery()) {
                while (rs.next()) { Estoque e = new Estoque(); e.setIdEstoque(rs.getLong(1)); e.setQuantidade(rs.getInt(2)); lotesValidos.add(e); }
            }
        }
        int totalDisponivel = 0;
        for (Estoque l : lotesValidos) {
            totalDisponivel += (l.getQuantidade() != null ? l.getQuantidade() : 0);
        }
        if (totalDisponivel < quantidadeDesejada) {
            throw new model.exception.EstoqueInsuficienteException(
                    "Estoque válido insuficiente para o produto #" + idProduto
                            + " (disponível: " + totalDisponivel + ", solicitado: " + quantidadeDesejada + ").");
        }
        int restante = quantidadeDesejada;
        for (Estoque lote : lotesValidos) {
            if (restante <= 0) {
                break;
            }
            int qtdLote = lote.getQuantidade() != null ? lote.getQuantidade() : 0;
            int retirada = Math.min(qtdLote, restante);
            try (PreparedStatement st = conn.prepareStatement("UPDATE estoque SET quantidade = quantidade - ? WHERE id_estoque = ? AND quantidade >= ?")) {
                st.setInt(1, retirada); st.setLong(2, lote.getIdEstoque()); st.setInt(3, retirada);
                if (st.executeUpdate() != 1) throw new model.exception.EstoqueInsuficienteException("O stock foi alterado por outra operação.");
            }
            if (idItem != null) try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO item_lote (id_item, id_estoque, quantidade) VALUES (?, ?, ?)")) {
                st.setLong(1, idItem); st.setLong(2, lote.getIdEstoque()); st.setInt(3, retirada); st.executeUpdate();
            }
            restante -= retirada;
        }
    }

    public void reporEstoque(Connection conn, Long idProduto, int quantidade, Long idFuncionario) throws SQLException {
        throw new model.exception.DadoInvalidoException("A reposição exige a identificação do lote original.");
    }

    private Estoque extrairEstoque(ResultSet rs) throws SQLException {
        Estoque est = new Estoque();
        est.setIdEstoque(rs.getLong("id_estoque"));
        est.setQuantidade(rs.getInt("quantidade"));

        Timestamp ent = rs.getTimestamp("data_entrada");
        if (ent != null) {
            est.setDataEntrada(ent.toLocalDateTime().toLocalDate());
        }

        Date val = rs.getDate("data_validade");
        if (val != null) {
            est.setDataValidade(val.toLocalDate());
        }

        Produto prod = new Produto();
        prod.setIdProduto(rs.getLong("id_produto"));
        prod.setNome(rs.getString("nome_produto"));
        est.setProduto(prod);

        Funcionario func = new Funcionario();
        func.setIdFuncionario(rs.getLong("id_funcionario"));
        func.setNome(rs.getString("nome_funcionario"));
        est.setFuncionario(func);

        return est;
    }

}
