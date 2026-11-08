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
import model.vo.Contacto;
import model.vo.Endereco;
import model.vo.Fornecedor;
import model.vo.Produto;

public class FornecedorDAO implements GenericoDAO<Fornecedor, Long> {

    @Override
    public void salvar(Fornecedor objecto) {
        String sqlPessoa = "INSERT INTO pessoa (nome) VALUES (?)";
        String sqlForn = "INSERT INTO fornecedor (id_pessoa) VALUES (?)";
        String sqlContacto = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";

        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            Long idPessoa = null;
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                stmtP.setString(1, objecto.getNome());
                stmtP.executeUpdate();
                try (ResultSet rs = stmtP.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPessoa = rs.getLong(1);
                        objecto.setIdPessoa(idPessoa);
                    }
                }
            }

            try (PreparedStatement stmtF = conn.prepareStatement(sqlForn, Statement.RETURN_GENERATED_KEYS)) {
                stmtF.setLong(1, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                stmtF.executeUpdate();
                try (ResultSet rs = stmtF.getGeneratedKeys()) {
                    if (rs.next()) {
                        objecto.setIdFornecedor(rs.getLong(1));
                    }
                }
            }

            if (objecto.getContacto() != null && !objecto.getContacto().isEmpty()) {
                try (PreparedStatement stmtC = conn.prepareStatement(sqlContacto, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < objecto.getContacto().size(); i++) {
                        Contacto c = objecto.getContacto().get(i);
                        if (c != null && c.getContacto() != null) {
                            stmtC.setString(1, c.getContacto());
                            stmtC.setLong(2, JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                            stmtC.executeUpdate();
                            try (ResultSet rs = stmtC.getGeneratedKeys()) {
                                if (rs.next()) {
                                    c.setIdContacto(rs.getLong(1));
                                }
                            }
                        }
                    }
                }
            }

            if (objecto.getEndereco() != null && objecto.getEndereco().getBairro() != null
                    && objecto.getEndereco().getBairro().getIdBairro() != null) {
                String sqlEnd = "INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmtE = conn.prepareStatement(sqlEnd, Statement.RETURN_GENERATED_KEYS)) {
                    stmtE.setString(1, objecto.getEndereco().getQuarteirao());
                    stmtE.setString(2, objecto.getEndereco().getRua());
                    stmtE.setString(3, objecto.getEndereco().getNumeroCasa());
                    stmtE.setLong(4, objecto.getEndereco().getBairro().getIdBairro());
                    stmtE.setLong(5, idPessoa);
                    stmtE.executeUpdate();
                    try (ResultSet rs = stmtE.getGeneratedKeys()) {
                        if (rs.next()) {
                            objecto.getEndereco().setIdEndereco(rs.getLong(1));
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao salvar fornecedor.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = (SELECT id_pessoa FROM fornecedor WHERE id_fornecedor = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar fornecedor: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Fornecedor objecto) {
        String sql = "UPDATE pessoa SET nome = ? WHERE id_pessoa = (SELECT id_pessoa FROM fornecedor WHERE id_fornecedor = ?)";
        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, objecto.getNome());
                stmt.setLong(2, objecto.getIdFornecedor());
                stmt.executeUpdate();
            }

            Long idPessoa = objecto.getIdPessoa();
            if (idPessoa == null) {
                try (PreparedStatement stP = conn.prepareStatement("SELECT id_pessoa FROM fornecedor WHERE id_fornecedor = ?")) {
                    stP.setLong(1, objecto.getIdFornecedor());
                    try (ResultSet rs = stP.executeQuery()) {
                        if (rs.next()) {
                            idPessoa = rs.getLong(1);
                        }
                    }
                }
            }
            if (idPessoa != null && objecto.getContacto() != null && !objecto.getContacto().isEmpty()) {
                try (PreparedStatement stDel = conn.prepareStatement("DELETE FROM contacto WHERE id_pessoa = ?")) {
                    stDel.setLong(1, idPessoa);
                    stDel.executeUpdate();
                }
                try (PreparedStatement stIns = conn.prepareStatement("INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)")) {
                    for (Contacto c : objecto.getContacto()) {
                        if (c != null && c.getContacto() != null && !c.getContacto().trim().isEmpty()) {
                            stIns.setString(1, c.getContacto().trim());
                            stIns.setLong(2, idPessoa);
                            stIns.executeUpdate();
                        }
                    }
                }
            }
            if (idPessoa != null && objecto.getEndereco() != null && objecto.getEndereco().getBairro() != null
                    && objecto.getEndereco().getBairro().getIdBairro() != null) {
                try (PreparedStatement stDelE = conn.prepareStatement("DELETE FROM endereco WHERE id_pessoa = ?")) {
                    stDelE.setLong(1, idPessoa);
                    stDelE.executeUpdate();
                }
                String sqlEnd = "INSERT INTO endereco (quarteirao, rua, numero_casa, id_bairro, id_pessoa) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stInsE = conn.prepareStatement(sqlEnd)) {
                    stInsE.setString(1, objecto.getEndereco().getQuarteirao());
                    stInsE.setString(2, objecto.getEndereco().getRua());
                    stInsE.setString(3, objecto.getEndereco().getNumeroCasa());
                    stInsE.setLong(4, objecto.getEndereco().getBairro().getIdBairro());
                    stInsE.setLong(5, idPessoa);
                    stInsE.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
            ConexaoDB.rollback(conn, failure);
            throw new DBException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Fornecedor> listarTodos() {
        String sql = "SELECT f.id_fornecedor, p.id_pessoa, p.nome " +
                     "FROM fornecedor f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "ORDER BY p.nome";
        List<Fornecedor> lista = new ArrayList<Fornecedor>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setIdFornecedor(rs.getLong("id_fornecedor"));
                f.setIdPessoa(rs.getLong("id_pessoa"));
                f.setNome(rs.getString("nome"));
                carregarContactos(conn, f);
                carregarEndereco(conn, f);
                carregarProdutosFornecidos(conn, f);
                lista.add(f);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar fornecedores: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Fornecedor buscarPorId(Long id) {
        String sql = "SELECT f.id_fornecedor, p.id_pessoa, p.nome " +
                     "FROM fornecedor f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "WHERE f.id_fornecedor = ?";
        Fornecedor f = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    f = new Fornecedor();
                    f.setIdFornecedor(rs.getLong("id_fornecedor"));
                    f.setIdPessoa(rs.getLong("id_pessoa"));
                    f.setNome(rs.getString("nome"));
                    carregarContactos(conn, f);
                    carregarEndereco(conn, f);
                    carregarProdutosFornecidos(conn, f);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar fornecedor por ID: " + e.getMessage(), e);
        }
        return f;
    }

    private void carregarContactos(Connection conn, Fornecedor fornecedor) {
        if (fornecedor == null || fornecedor.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_pessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getIdPessoa());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Contacto> lista = new ArrayList<Contacto>();
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                    lista.add(c);
                }
                fornecedor.setContacto(lista);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar contactos do fornecedor.", e);
        }
    }

    private void carregarEndereco(Connection conn, Fornecedor fornecedor) throws SQLException {
        if (fornecedor == null || fornecedor.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT e.id_endereco, e.quarteirao, e.rua, e.numero_casa, "
                + "b.id_bairro, b.nome AS nome_bairro, c.id_cidade, c.nome AS nome_cidade "
                + "FROM endereco e "
                + "LEFT JOIN bairro b ON e.id_bairro = b.id_bairro "
                + "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade "
                + "WHERE e.id_pessoa = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getIdPessoa());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
                    fornecedor.setEndereco(end);
                }
            }
        }
    }

    private void carregarProdutosFornecidos(Connection conn, Fornecedor fornecedor) throws SQLException {
        if (fornecedor == null || fornecedor.getIdFornecedor() == null) {
            return;
        }
        String sql = "SELECT p.id_produto, p.nome, p.preco FROM produto p "
                + "INNER JOIN fornecedor_produto fp ON p.id_produto = fp.id_produto "
                + "WHERE fp.id_fornecedor = ? ORDER BY p.nome";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, fornecedor.getIdFornecedor());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Produto> prods = new ArrayList<Produto>();
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setIdProduto(rs.getLong("id_produto"));
                    p.setNome(rs.getString("nome"));
                    p.setPreco(rs.getDouble("preco"));
                    prods.add(p);
                }
                fornecedor.setProduto(prods);
            }
        }
    }

}
