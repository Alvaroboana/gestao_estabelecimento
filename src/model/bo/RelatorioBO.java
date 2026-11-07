package model.bo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import model.dao.RelatorioDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.vo.Utilizador;
import util.ValidadorObjeto;

public class RelatorioBO {
    private final RelatorioDAO dao;

    public RelatorioBO() {
        this(new RelatorioDAO());
    }

    public RelatorioBO(RelatorioDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de relatorio");
        this.dao = dao;
    }

    public List<Map<String, Object>> consultarAuditoria(
            Utilizador utilizador, String tabela, Long idRegistro,
            LocalDateTime inicio, LocalDateTime fim, String operacao) {
        exigirPermissao(utilizador);
        validarIntervalo(inicio, fim);
        if (idRegistro != null && idRegistro <= 0) {
            throw new DadoInvalidoException("idRegistro deve ser positivo.");
        }
        return dao.consultarAuditoria(tabela, idRegistro, inicio, fim, operacao);
    }

    public List<Map<String, Object>> resumoAuditoria(
            Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        exigirPermissao(utilizador);
        validarIntervalo(inicio, fim);
        return dao.resumoAuditoria(inicio, fim);
    }

    public List<Map<String, Object>> vendasPeriodo(
            Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        exigirPermissao(utilizador);
        validarIntervaloObrigatorio(inicio, fim);
        return dao.vendasPeriodo(inicio, fim);
    }

    public List<Map<String, Object>> estoqueBaixo(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.estoqueBaixo();
    }

    public List<Map<String, Object>> produtosMaisVendidos(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.produtosMaisVendidos();
    }

    public List<Map<String, Object>> reembolsosPeriodo(
            Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        exigirPermissao(utilizador);
        validarIntervaloObrigatorio(inicio, fim);
        return dao.reembolsosPeriodo(inicio, fim);
    }

    public List<Map<String, Object>> resumoVendasPorEstado(
            Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        exigirPermissao(utilizador);
        validarIntervalo(inicio, fim);
        return dao.resumoVendasPorEstado(inicio, fim);
    }

    public List<Map<String, Object>> itensPorVenda(Utilizador utilizador, Long idVenda) {
        exigirPermissao(utilizador);
        if (idVenda == null || idVenda <= 0) {
            throw new DadoInvalidoException("idVenda deve ser positivo.");
        }
        return dao.itensPorVenda(idVenda);
    }

    public Map<String, Integer> obterIndicadoresDashboard() {
        return dao.obterIndicadoresDashboard();
    }

    public List<Map<String, Object>> obterEstoqueBaixoDashboard() {
        return dao.estoqueBaixo();
    }

    public List<Map<String, Object>> obterProdutosMaisVendidosDashboard() {
        return dao.produtosMaisVendidos();
    }

    public List<Map<String, Object>> vendasPorFuncionario(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.vendasPorFuncionario();
    }

    public List<Map<String, Object>> resumoVendasDiario(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.resumoVendasDiario();
    }

    public List<Map<String, Object>> lotesProximosValidade(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.lotesProximosValidade();
    }

    public List<Map<String, Object>> lotesExpirados(Utilizador utilizador) {
        exigirPermissao(utilizador);
        return dao.lotesExpirados();
    }

    public List<Map<String, Object>> obterResumoVendasPorEstadoRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        validarIntervalo(inicio, fim);
        return dao.resumoVendasPorEstado(inicio, fim);
    }

    public List<Map<String, Object>> obterReembolsosPeriodoRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        validarIntervaloObrigatorio(inicio, fim);
        return dao.reembolsosPeriodo(inicio, fim);
    }

    public List<Map<String, Object>> obterResumoAuditoriaRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        validarIntervalo(inicio, fim);
        return dao.resumoAuditoria(inicio, fim);
    }

    public double obterTotalVendasFuncionarioMes(Long idFuncionario) {
        return dao.obterTotalVendasFuncionarioMes(idFuncionario);
    }

    private void exigirPermissao(Utilizador utilizador) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (!utilizador.podeVerRelatorios()) {
            throw new AcessoNegadoException("O utilizador nao pode consultar relatorios.");
        }
    }

    private void validarIntervaloObrigatorio(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new DadoInvalidoException("O periodo inicial e final sao obrigatorios.");
        }
        validarIntervalo(inicio, fim);
    }

    private void validarIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio != null && fim != null && !fim.isAfter(inicio)) {
            throw new DadoInvalidoException("O fim do periodo deve ser posterior ao inicio.");
        }
    }
}
