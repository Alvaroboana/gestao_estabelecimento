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
import model.vo.Cargo;
import model.vo.Contacto;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;

public class FuncionarioDAO implements GenericoDAO<Funcionario, Long> {

    @Override
    public void salvar(Funcionario objecto) {
        String sqlPessoa = "INSERT INTO pessoa (nome) VALUES (?)";
        String sqlFunc = "INSERT INTO funcionario (id_pessoa, bi, data_nascimento, data_inicio, estado, id_cargo) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlContacto = "INSERT INTO contacto (contacto, id_pessoa) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = ConexaoDB.getConnection();
            conn.setAutoCommit(false);

            long idPessoa = 0L;
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
                stmtFunc.setLong(1, idPessoa);
                stmtFunc.setString(2, objecto.getBi());
                stmtFunc.setDate(3, Date.valueOf(objecto.getDataNascimento()));
                stmtFunc.setDate(4, Date.valueOf(objecto.getDataInicio()));

                String estadoNome = "ATIVO";
                if (objecto.getEstado() != null) {
                    estadoNome = objecto.getEstado().name();
                }
                stmtFunc.setString(5, estadoNome);

                long idCargo = 0L;
                if (objecto.getCargo() != null && objecto.getCargo().getIdCargo() != null) {
                    idCargo = objecto.getCargo().getIdCargo();
                }
                stmtFunc.setLong(6, idCargo);

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
                            stmtContacto.setLong(2, idPessoa);
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

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new DBException("Erro ao salvar funcionario: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                }
            }
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
        try {
            conn = ConexaoDB.getConnection();
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

                long idCargo = 0L;
                if (objecto.getCargo() != null && objecto.getCargo().getIdCargo() != null) {
                    idCargo = objecto.getCargo().getIdCargo();
                }
                stmtF.setLong(5, idCargo);
                stmtF.setLong(6, objecto.getIdFuncionario());

                stmtF.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new DBException("Erro ao atualizar funcionario: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                }
            }
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
        }
    }

}
