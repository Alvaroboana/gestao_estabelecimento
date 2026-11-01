package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import model.vo.Estoque;
import model.vo.Funcionario;
import model.vo.Produto;

public class EstoqueDAO implements GenericoDAO<Estoque, Long> {

    @Override
    public void salvar(Estoque objecto) {
        String sql = "INSERT INTO estoque (quantidade, data_entrada, data_validade, id_produto, id_funcionario) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int qtd = 0;
            if (objecto.getQuantidade() != null) {
                qtd = objecto.getQuantidade();
            }
            stmt.setInt(1, qtd);

            if (objecto.getDataEntrada() != null) {
                stmt.setDate(2, Date.valueOf(objecto.getDataEntrada()));
            } else {
                stmt.setDate(2, Date.valueOf(LocalDate.now()));
            }

            if (objecto.getDataValidade() != null) {
                stmt.setDate(3, Date.valueOf(objecto.getDataValidade()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            long idProd = 0L;
            if (objecto.getProduto() != null && objecto.getProduto().getIdProduto() != null) {
                idProd = objecto.getProduto().getIdProduto();
            }
            stmt.setLong(4, idProd);

            long idFunc = 0L;
            if (objecto.getFuncionario() != null && objecto.getFuncionario().getIdFuncionario() != null) {
                idFunc = objecto.getFuncionario().getIdFuncionario();
            }
            stmt.setLong(5, idFunc);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdEstoque(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar estoque: " + e.getMessage(), e);
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
        String sql = "UPDATE estoque SET quantidade = ?, data_validade = ? WHERE id_estoque = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int qtd = 0;
            if (objecto.getQuantidade() != null) {
                qtd = objecto.getQuantidade();
            }
            stmt.setInt(1, qtd);

            if (objecto.getDataValidade() != null) {
                stmt.setDate(2, Date.valueOf(objecto.getDataValidade()));
            } else {
                stmt.setNull(2, java.sql.Types.DATE);
            }
            stmt.setLong(3, objecto.getIdEstoque());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar estoque: " + e.getMessage(), e);
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
        String sql = "SELECT e.id_estoque, e.quantidade, e.data_entrada, e.data_validade, " +
                     "p.id_produto, p.nome AS nome_produto, " +
                     "f.id_funcionario, pes.nome AS nome_funcionario " +
                     "FROM estoque e " +
                     "INNER JOIN produto p ON e.id_produto = p.id_produto " +
                     "INNER JOIN funcionario f ON e.id_funcionario = f.id_funcionario " +
                     "INNER JOIN pessoa pes ON f.id_pessoa = pes.id_pessoa " +
                     "WHERE e.id_produto = ? AND e.quantidade > 0 " +
                     "ORDER BY e.data_validade ASC";
        List<Estoque> lista = new ArrayList<Estoque>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairEstoque(rs));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar estoques por produto: " + e.getMessage(), e);
        }
        return lista;
    }

    private Estoque extrairEstoque(ResultSet rs) throws SQLException {
        Estoque est = new Estoque();
        est.setIdEstoque(rs.getLong("id_estoque"));
        est.setQuantidade(rs.getInt("quantidade"));

        Date ent = rs.getDate("data_entrada");
        if (ent != null) {
            est.setDataEntrada(ent.toLocalDate());
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
