package model.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import infra.DBException;
import model.vo.Bairro;
import model.vo.Cargo;
import model.vo.Cidade;
import model.vo.Contacto;
import model.vo.Endereco;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;

public class FuncionarioDAO implements GenericoDAO<Funcionario, Long> {

    @Override
    public void salvar(Funcionario objecto) {
        String sqlPessoa = "INSERT INTO pessoa (nome) VALUES (?)";
        String sqlFunc = "INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlContacto = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";

        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            Long idPessoa = null;
            try (PreparedStatement stmtPessoa = conn.prepareStatement(sqlPessoa, Statement.RETURN_GENERATED_KEYS)) {
                stmtPessoa.setString(1, objecto.getNome());
                stmtPessoa.executeUpdate();
                try (ResultSet rs = stmtPessoa.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPessoa = rs.getLong(1);
                        objecto.setIdPessoa(idPessoa);
                    }
                }
            }

            try (PreparedStatement stmtFunc = conn.prepareStatement(sqlFunc, Statement.RETURN_GENERATED_KEYS)) {
                stmtFunc.setLong(1, infra.JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                stmtFunc.setString(2, objecto.getBi());
                stmtFunc.setDate(3, Date.valueOf(objecto.getDataNascimento()));
                stmtFunc.setDate(4, Date.valueOf(objecto.getDataInicio()));

                String estadoNome = "ATIVO";
                if (objecto.getEstado() != null) {
                    estadoNome = objecto.getEstado().name();
                }
                stmtFunc.setString(5, estadoNome);

                stmtFunc.setLong(6, infra.JdbcValidation.requireId(
                        objecto.getCargo() == null ? null : objecto.getCargo().getIdCargo(),
                        "cargo.idCargo"));

                stmtFunc.executeUpdate();
                try (ResultSet rs = stmtFunc.getGeneratedKeys()) {
                    if (rs.next()) {
                        objecto.setIdFuncionario(rs.getLong(1));
                    }
                }
            }

            if (objecto.getContacto() != null && !objecto.getContacto().isEmpty()) {
                try (PreparedStatement stmtContacto = conn.prepareStatement(sqlContacto, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < objecto.getContacto().size(); i++) {
                        Contacto c = objecto.getContacto().get(i);
                        if (c != null && c.getContacto() != null) {
                            stmtContacto.setString(1, c.getContacto());
                            stmtContacto.setLong(2, infra.JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                            stmtContacto.executeUpdate();
                            try (ResultSet rs = stmtContacto.getGeneratedKeys()) {
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
                try (PreparedStatement stmtEnd = conn.prepareStatement(sqlEnd, Statement.RETURN_GENERATED_KEYS)) {
                    stmtEnd.setString(1, objecto.getEndereco().getQuarteirao());
                    stmtEnd.setString(2, objecto.getEndereco().getRua());
                    stmtEnd.setString(3, objecto.getEndereco().getNumeroCasa());
                    stmtEnd.setLong(4, objecto.getEndereco().getBairro().getIdBairro());
                    stmtEnd.setLong(5, infra.JdbcValidation.requireId(idPessoa, "pessoa.idPessoa"));
                    stmtEnd.executeUpdate();
                    try (ResultSet rs = stmtEnd.getGeneratedKeys()) {
                        if (rs.next()) {
                            objecto.getEndereco().setIdEndereco(rs.getLong(1));
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao salvar funcionario.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM pessoa WHERE id_pessoa = (SELECT id_pessoa FROM funcionario WHERE id_funcionario = ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Erro ao eliminar funcionario: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizar(Funcionario objecto) {
        String sqlPessoa = "UPDATE pessoa SET nome = ? WHERE id_pessoa = (SELECT id_pessoa FROM funcionario WHERE id_funcionario = ?)";
        String sqlFunc = "UPDATE funcionario SET bi = ?, data_nascimento = ?, data_inicio = ?, estado = ?, id_cargo = ? WHERE id_funcionario = ?";
        Connection conn = null;
        try (Connection resource = ConexaoDB.getConnection()) {
            conn = resource;
            conn.setAutoCommit(false);

            try (PreparedStatement stmtP = conn.prepareStatement(sqlPessoa)) {
                stmtP.setString(1, objecto.getNome());
                stmtP.setLong(2, objecto.getIdFuncionario());
                stmtP.executeUpdate();
            }

            try (PreparedStatement stmtF = conn.prepareStatement(sqlFunc)) {
                stmtF.setString(1, objecto.getBi());
                stmtF.setDate(2, Date.valueOf(objecto.getDataNascimento()));
                stmtF.setDate(3, Date.valueOf(objecto.getDataInicio()));

                String estadoNome = "ATIVO";
                if (objecto.getEstado() != null) {
                    estadoNome = objecto.getEstado().name();
                }
                stmtF.setString(4, estadoNome);

                stmtF.setLong(5, infra.JdbcValidation.requireId(
                        objecto.getCargo() == null ? null : objecto.getCargo().getIdCargo(),
                        "cargo.idCargo"));
                stmtF.setLong(6, objecto.getIdFuncionario());

                stmtF.executeUpdate();
            }

            Long idPessoa = objecto.getIdPessoa();
            if (idPessoa == null) {
                try (PreparedStatement stP = conn.prepareStatement("SELECT id_pessoa FROM funcionario WHERE id_funcionario = ?")) {
                    stP.setLong(1, objecto.getIdFuncionario());
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
                try (PreparedStatement stInsE = conn.prepareStatement(sqlEnd, Statement.RETURN_GENERATED_KEYS)) {
                    stInsE.setString(1, objecto.getEndereco().getQuarteirao());
                    stInsE.setString(2, objecto.getEndereco().getRua());
                    stInsE.setString(3, objecto.getEndereco().getNumeroCasa());
                    stInsE.setLong(4, objecto.getEndereco().getBairro().getIdBairro());
                    stInsE.setLong(5, idPessoa);
                    stInsE.executeUpdate();
                    try (ResultSet rs = stInsE.getGeneratedKeys()) {
                        if (rs.next()) {
                            objecto.getEndereco().setIdEndereco(rs.getLong(1));
                        }
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            DBException failure = new DBException("Erro ao atualizar funcionario.", e);
            ConexaoDB.rollback(conn, failure);
            throw failure;
        }
    }

    @Override
    public List<Funcionario> listarTodos() {
        String sql = "SELECT f.id_funcionario, f.bi, f.data_nascimento, f.data_inicio, f.estado, " +
                     "p.id_pessoa, p.nome, c.id_cargo, c.nome AS nome_cargo, c.salario_base " +
                     "FROM funcionario f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "INNER JOIN cargo c ON f.id_cargo = c.id_cargo " +
                     "ORDER BY p.nome";
        List<Funcionario> lista = new ArrayList<Funcionario>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Funcionario f = extrairFuncionario(rs);
                carregarContactos(conn, f);
                carregarEndereco(conn, f);
                lista.add(f);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao listar funcionarios: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Funcionario buscarPorId(Long id) {
        String sql = "SELECT f.id_funcionario, f.bi, f.data_nascimento, f.data_inicio, f.estado, " +
                     "p.id_pessoa, p.nome, c.id_cargo, c.nome AS nome_cargo, c.salario_base " +
                     "FROM funcionario f " +
                     "INNER JOIN pessoa p ON f.id_pessoa = p.id_pessoa " +
                     "INNER JOIN cargo c ON f.id_cargo = c.id_cargo " +
                     "WHERE f.id_funcionario = ?";
        Funcionario f = null;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    f = extrairFuncionario(rs);
                    carregarContactos(conn, f);
                    carregarEndereco(conn, f);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao buscar funcionario por ID: " + e.getMessage(), e);
        }
        return f;
    }

    private Funcionario extrairFuncionario(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setIdFuncionario(rs.getLong("id_funcionario"));
        f.setIdPessoa(rs.getLong("id_pessoa"));
        f.setNome(rs.getString("nome"));
        f.setBi(rs.getString("bi"));

        Date nasc = rs.getDate("data_nascimento");
        if (nasc != null) {
            f.setDataNascimento(nasc.toLocalDate());
        }

        Date ini = rs.getDate("data_inicio");
        if (ini != null) {
            f.setDataInicio(ini.toLocalDate());
        }

        String est = rs.getString("estado");
        if (est != null) {
            f.setEstado(EstadoFuncionario.valueOf(est));
        }

        Cargo cargo = new Cargo();
        cargo.setIdCargo(rs.getLong("id_cargo"));
        cargo.setNome(rs.getString("nome_cargo"));
        cargo.setSalarioBase(rs.getDouble("salario_base"));
        f.setCargo(cargo);

        return f;
    }

    private void carregarContactos(Connection conn, Funcionario funcionario) {
        if (funcionario == null || funcionario.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT id_contacto, contacto FROM contacto WHERE id_pessoa = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, funcionario.getIdPessoa());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Contacto> lista = new ArrayList<Contacto>();
                while (rs.next()) {
                    Contacto c = new Contacto();
                    c.setIdContacto(rs.getLong("id_contacto"));
                    c.setContacto(rs.getString("contacto"));
                    lista.add(c);
                }
                funcionario.setContacto(lista);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao carregar contactos do funcionario.", e);
        }
    }

    private void carregarEndereco(Connection conn, Funcionario funcionario) throws SQLException {
        if (funcionario == null || funcionario.getIdPessoa() == null) {
            return;
        }
        String sql = "SELECT e.id_endereco, e.quarteirao, e.rua, e.numero_casa, "
                + "b.id_bairro, b.nome AS nome_bairro, c.id_cidade, c.nome AS nome_cidade "
                + "FROM endereco e "
                + "LEFT JOIN bairro b ON e.id_bairro = b.id_bairro "
                + "LEFT JOIN cidade c ON b.id_cidade = c.id_cidade "
                + "WHERE e.id_pessoa = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, funcionario.getIdPessoa());
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
                    funcionario.setEndereco(end);
                }
            }
        }
    }

}
