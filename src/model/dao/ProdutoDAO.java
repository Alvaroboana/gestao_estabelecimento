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
import model.vo.Categoria;
import model.vo.EstadoProduto;
import model.vo.Produto;

public class ProdutoDAO implements GenericoDAO<Produto, Long> {

    @Override
    public void salvar(Produto objecto) {
        String sql = "INSERT INTO produto (nome, preco, descricao, unidade, estado, quantidade_minima, id_categoria) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, objecto.getNome());

            double preco = 0.0;
            if (objecto.getPreco() != null) {
                preco = objecto.getPreco();
            }
            stmt.setDouble(2, preco);
            stmt.setString(3, objecto.getDescricao());
            stmt.setString(4, objecto.getUnidade());

            String estadoNome = "ATIVO";
            if (objecto.getEstado() != null) {
                estadoNome = objecto.getEstado().name();
            }
            stmt.setString(5, estadoNome);

            int qtdMin = 0;
            if (objecto.getQuantidadeMinima() != null) {
                qtdMin = objecto.getQuantidadeMinima();
            }
            stmt.setInt(6, qtdMin);

            long idCat = 0L;
            if (objecto.getCategoria() != null && objecto.getCategoria().getIdCategoria() != null) {
                idCat = objecto.getCategoria().getIdCategoria();
            }
            stmt.setLong(7, idCat);

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    objecto.setIdProduto(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM produto WHERE id_produto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Produto objecto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, descricao = ?, unidade = ?, " +
                     "estado = ?, quantidade_minima = ?, id_categoria = ? WHERE id_produto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, objecto.getNome());

            double preco = 0.0;
            if (objecto.getPreco() != null) {
                preco = objecto.getPreco();
            }
            stmt.setDouble(2, preco);
            stmt.setString(3, objecto.getDescricao());
            stmt.setString(4, objecto.getUnidade());

            String estadoNome = "ATIVO";
            if (objecto.getEstado() != null) {
                estadoNome = objecto.getEstado().name();
            }
            stmt.setString(5, estadoNome);

            int qtdMin = 0;
            if (objecto.getQuantidadeMinima() != null) {
                qtdMin = objecto.getQuantidadeMinima();
            }
            stmt.setInt(6, qtdMin);

            long idCat = 0L;
            if (objecto.getCategoria() != null && objecto.getCategoria().getIdCategoria() != null) {
                idCat = objecto.getCategoria().getIdCategoria();
            }
            stmt.setLong(7, idCat);
            stmt.setLong(8, objecto.getIdProduto());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> listarTodos() {
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.descricao, p.unidade, p.estado, " +
                     "p.quantidade_minima, c.id_categoria, c.nome AS nome_categoria " +
                     "FROM produto p " +
                     "INNER JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "ORDER BY p.nome";
        List<Produto> lista = new ArrayList<Produto>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairProduto(rs));
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Produto buscarPorId(Long id) {
        String sql = "SELECT p.id_produto, p.nome, p.preco, p.descricao, p.unidade, p.estado, " +
                     "p.quantidade_minima, c.id_categoria, c.nome AS nome_categoria " +
                     "FROM produto p " +
                     "INNER JOIN categoria c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.id_produto = ?";
        Produto prod = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prod = extrairProduto(rs);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar produto por ID: " + e.getMessage(), e);
        }
        return prod;
    }

    private Produto extrairProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setIdProduto(rs.getLong("id_produto"));
        p.setNome(rs.getString("nome"));
        p.setPreco(rs.getDouble("preco"));
        p.setDescricao(rs.getString("descricao"));
        p.setUnidade(rs.getString("unidade"));

        String est = rs.getString("estado");
        if (est != null) {
            p.setEstado(EstadoProduto.valueOf(est));
        }
        p.setQuantidadeMinima(rs.getInt("quantidade_minima"));

        Categoria cat = new Categoria();
        cat.setIdCategoria(rs.getLong("id_categoria"));
        cat.setNome(rs.getString("nome_categoria"));
        p.setCategoria(cat);

        return p;
    }

}
