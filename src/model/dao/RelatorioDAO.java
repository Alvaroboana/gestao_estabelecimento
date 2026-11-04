package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import infra.ConexaoDB;
import infra.DBException;

public class RelatorioDAO {

    public List<Map<String, Object>> consultarAuditoria(
            String tabela, Long idRegistro, LocalDateTime inicio, LocalDateTime fim, String operacao) {
        String sql = "SELECT id_auditoria, tabela_afetada, id_registro, operacao, "
                + "dados_antes, dados_depois, id_utilizador, username, data_operacao "
                + "FROM vw_auditoria_detalhada "
                + "WHERE (? IS NULL OR tabela_afetada = ?) "
                + "AND (? IS NULL OR id_registro = ?) "
                + "AND (? IS NULL OR data_operacao >= ?) "
                + "AND (? IS NULL OR data_operacao < ?) "
                + "AND (? IS NULL OR operacao = ?) "
                + "ORDER BY data_operacao DESC, id_auditoria DESC";
        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tabela);
            stmt.setString(2, tabela);
            setNullableLong(stmt, 3, idRegistro);
            setNullableLong(stmt, 4, idRegistro);
            setNullableTimestamp(stmt, 5, inicio);
            setNullableTimestamp(stmt, 6, inicio);
            setNullableTimestamp(stmt, 7, fim);
            setNullableTimestamp(stmt, 8, fim);
            stmt.setString(9, operacao);
            stmt.setString(10, operacao);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao consultar auditoria.", e);
        }
    }

    public List<Map<String, Object>> resumoAuditoria(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT dia, tabela_afetada, operacao, quantidade "
                + "FROM vw_resumo_auditoria "
                + "WHERE (? IS NULL OR dia >= DATE(?)) "
                + "AND (? IS NULL OR dia < DATE(?)) "
                + "ORDER BY dia DESC, tabela_afetada, operacao";
        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            setNullableTimestamp(stmt, 1, inicio);
            setNullableTimestamp(stmt, 2, inicio);
            setNullableTimestamp(stmt, 3, fim);
            setNullableTimestamp(stmt, 4, fim);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao consultar resumo de auditoria.", e);
        }
    }

    public List<Map<String, Object>> vendasPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT id_venda, data_venda, total, estado, id_cliente, nome_cliente, "
                + "id_funcionario, nome_funcionario "
                + "FROM vw_vendas_detalhadas "
                + "WHERE data_venda >= ? AND data_venda < ? "
                + "ORDER BY data_venda DESC";
        return consultarIntervalo(sql, inicio, fim, "vendas");
    }

    public List<Map<String, Object>> estoqueBaixo() {
        return consultarSemParametros(
                "SELECT id_produto, nome_produto, quantidade_minima, quantidade_atual "
                        + "FROM vw_estoque_baixo ORDER BY nome_produto",
                "estoque baixo");
    }

    public List<Map<String, Object>> produtosMaisVendidos() {
        return consultarSemParametros(
                "SELECT id_produto, nome_produto, id_categoria, nome_categoria, "
                        + "quantidade_vendida, valor_vendido "
                        + "FROM vw_produtos_mais_vendidos "
                        + "ORDER BY quantidade_vendida DESC, nome_produto",
                "produtos mais vendidos");
    }

    public List<Map<String, Object>> reembolsosPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT id_reembolso, quantidade, valor, motivo, data_reembolso, id_item, "
                + "id_venda, id_produto, nome_produto, id_funcionario, nome_funcionario "
                + "FROM vw_reembolsos_detalhados "
                + "WHERE data_reembolso >= ? AND data_reembolso < ? "
                + "ORDER BY data_reembolso DESC";
        return consultarIntervalo(sql, inicio, fim, "reembolsos");
    }

    public Map<String, Integer> obterIndicadoresDashboard() {
        Map<String, Integer> indicadores = new HashMap<>();
        indicadores.put("vendasHoje", 0);
        indicadores.put("vendasPendentes", 0);
        indicadores.put("estoqueBaixo", 0);
        indicadores.put("lotesAExpirar", 0);

        try (Connection conn = ConexaoDB.getConnection()) {
            // Vendas de hoje
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM venda WHERE DATE(data) = CURDATE() AND estado != 'ANULADA'")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("vendasHoje", rs.getInt(1));
                }
            }
            // Vendas pendentes
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM venda WHERE estado = 'PENDENTE'")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("vendasPendentes", rs.getInt(1));
                }
            }
            // Estoque baixo
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM vw_estoque_baixo")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("estoqueBaixo", rs.getInt(1));
                }
            }
            // Lotes a expirar (7 dias)
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM vw_lotes_proximos_validade WHERE dias_para_vencer <= 7 AND dias_para_vencer >= 0")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("lotesAExpirar", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            // Retorna zeros graciosamente caso a tabela não exista ou o banco esteja offline
        }
        return indicadores;
    }

    public double obterTotalVendasFuncionarioMes(Long idFuncionario) {
        if (idFuncionario == null || idFuncionario <= 0) {
            return 0.0;
        }

        LocalDate hoje = LocalDate.now();
        String sql = "SELECT COALESCE(SUM(total), 0.0) FROM venda "
                + "WHERE id_funcionario = ? AND estado = 'CONCLUIDA' "
                + "AND YEAR(data) = ? AND MONTH(data) = ?";

        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idFuncionario);
            stmt.setInt(2, hoje.getYear());
            stmt.setInt(3, hoje.getMonthValue());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            // Em caso de falha de conexão, retorna 0.0
        }
        return 0.0;
    }

    private List<Map<String, Object>> consultarIntervalo(
            String sql, LocalDateTime inicio, LocalDateTime fim, String descricao) {
        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao consultar relatorio de " + descricao + ".", e);
        }
    }

    private List<Map<String, Object>> consultarSemParametros(String sql, String descricao) {
        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            return mapear(rs);
        } catch (SQLException e) {
            throw new DBException("Erro ao consultar relatorio de " + descricao + ".", e);
        }
    }

    private List<Map<String, Object>> mapear(ResultSet rs) throws SQLException {
        List<Map<String, Object>> resultados = new ArrayList<Map<String, Object>>();
        int colunas = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Map<String, Object> linha = new LinkedHashMap<String, Object>();
            for (int i = 1; i <= colunas; i++) {
                linha.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
            }
            resultados.add(linha);
        }
        return resultados;
    }

    private void setNullableLong(PreparedStatement stmt, int index, Long value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(index, value);
        }
    }

    private void setNullableTimestamp(PreparedStatement stmt, int index, LocalDateTime value)
            throws SQLException {
        if (value == null) {
            stmt.setNull(index, java.sql.Types.TIMESTAMP);
        } else {
            stmt.setTimestamp(index, Timestamp.valueOf(value));
        }
    }
}
