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
        indicadores.put("lotesExpirados", 0);

        try (Connection conn = ConexaoDB.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM venda WHERE DATE(data) = CURDATE() AND estado != 'ANULADA'")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("vendasHoje", rs.getInt(1));
                }
            }
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM venda WHERE estado = 'PENDENTE'")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("vendasPendentes", rs.getInt(1));
                }
            }
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM vw_estoque_baixo")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("estoqueBaixo", rs.getInt(1));
                }
            }
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM vw_lotes_proximos_validade WHERE dias_para_validade <= 7 AND dias_para_validade >= 0")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("lotesAExpirar", rs.getInt(1));
                }
            }
            try (PreparedStatement st = conn.prepareStatement("SELECT COUNT(*) FROM estoque WHERE quantidade > 0 AND data_validade IS NOT NULL AND data_validade < CURRENT_DATE")) {
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) indicadores.put("lotesExpirados", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
        }
        return indicadores;
    }

    public List<Map<String, Object>> vendasPorFuncionario() {
        return consultarSemParametros(
                "SELECT id_funcionario, nome_funcionario, quantidade_vendas, total_vendido "
                        + "FROM vw_vendas_por_funcionario "
                        + "ORDER BY total_vendido DESC, nome_funcionario ASC",
                "vendas por funcionario");
    }

    public List<Map<String, Object>> resumoVendasDiario() {
        return consultarSemParametros(
                "SELECT dia, quantidade_vendas, total_vendido, quantidade_itens "
                        + "FROM vw_resumo_vendas_diario "
                        + "ORDER BY dia DESC",
                "resumo diario de vendas");
    }

    public List<Map<String, Object>> lotesProximosValidade() {
        return consultarSemParametros(
                "SELECT id_estoque, id_produto, nome_produto, quantidade, data_entrada, "
                        + "data_validade, dias_para_validade, id_funcionario "
                        + "FROM vw_lotes_proximos_validade "
                        + "ORDER BY dias_para_validade ASC, id_estoque ASC",
                "lotes proximos da validade");
    }

    public List<Map<String, Object>> lotesExpirados() {
        return consultarSemParametros(
                "SELECT e.id_estoque, e.id_produto, p.nome AS nome_produto, e.quantidade, "
                        + "e.data_entrada, e.data_validade, DATEDIFF(e.data_validade, CURRENT_DATE) AS dias_para_validade, "
                        + "e.id_funcionario, pf.nome AS nome_funcionario "
                        + "FROM estoque e "
                        + "INNER JOIN produto p ON p.id_produto = e.id_produto "
                        + "INNER JOIN funcionario f ON f.id_funcionario = e.id_funcionario "
                        + "INNER JOIN pessoa pf ON pf.id_pessoa = f.id_pessoa "
                        + "WHERE e.quantidade > 0 AND e.data_validade IS NOT NULL AND e.data_validade < CURRENT_DATE "
                        + "ORDER BY e.data_validade ASC",
                "lotes expirados");
    }

    public List<Map<String, Object>> resumoVendasPorEstado(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT estado, COUNT(*) AS quantidade, COALESCE(SUM(total), 0.0) AS total "
                + "FROM venda "
                + "WHERE (? IS NULL OR data >= ?) "
                + "AND (? IS NULL OR data < ?) "
                + "GROUP BY estado "
                + "ORDER BY FIELD(estado, 'PENDENTE', 'CONCLUIDA', 'ANULADA', 'REEMBOLSADA')";
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
            throw new DBException("Erro ao consultar resumo de vendas por estado.", e);
        }
    }

    public List<Map<String, Object>> itensPorVenda(Long idVenda) {
        String sql = "SELECT id_item, id_venda, id_produto, nome_produto, nome_categoria, "
                + "quantidade, preco, desconto, subtotal "
                + "FROM vw_itens_vendidos_detalhados "
                + "WHERE id_venda = ? "
                + "ORDER BY id_item ASC";
        try (Connection conn = ConexaoDB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idVenda);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new DBException("Erro ao consultar itens da venda.", e);
        }
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
