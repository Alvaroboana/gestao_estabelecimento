package controller;

import com.itextpdf.text.DocumentException;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import model.bo.RelatorioBO;
import model.vo.Utilizador;
import util.GeradorRelatorioPDF;
import util.ValidadorObjeto;

public class RelatorioController {

    private final RelatorioBO relatorioBO;

    public RelatorioController() {
        this(new RelatorioBO());
    }

    public RelatorioController(RelatorioBO relatorioBO) {
        ValidadorObjeto.validarNaoNulo(relatorioBO, "RelatorioBO");
        this.relatorioBO = relatorioBO;
    }

    public Map<String, Integer> consultarIndicadoresDashboard() {
        return relatorioBO.obterIndicadoresDashboard();
    }

    public List<Map<String, Object>> consultarVendasPeriodo(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.vendasPeriodo(utilizador, inicio, fim);
    }

    public List<Map<String, Object>> consultarEstoqueBaixo(Utilizador utilizador) {
        return relatorioBO.estoqueBaixo(utilizador);
    }

    public List<Map<String, Object>> consultarProdutosMaisVendidos(Utilizador utilizador) {
        return relatorioBO.produtosMaisVendidos(utilizador);
    }

    public List<Map<String, Object>> consultarProdutosMenosVendidos(Utilizador utilizador) {
        List<Map<String, Object>> lista = relatorioBO.produtosMaisVendidos(utilizador);
        if (lista != null) {
            lista = new ArrayList<>(lista);
            lista.sort(Comparator.comparingDouble(o -> {
                try {
                    return Double.parseDouble(String.valueOf(o.getOrDefault("quantidade_vendida", 0)));
                } catch (Exception e) {
                    return 0.0;
                }
            }));
        }
        return lista;
    }

    public List<Map<String, Object>> consultarReembolsos(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.reembolsosPeriodo(utilizador, inicio, fim);
    }

    public List<Map<String, Object>> consultarAuditoria(
            Utilizador utilizador, String tabela, Long idRegistro,
            LocalDateTime inicio, LocalDateTime fim, String operacao) {
        return relatorioBO.consultarAuditoria(utilizador, tabela, idRegistro, inicio, fim, operacao);
    }

    public List<Map<String, Object>> resumoAuditoria(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.resumoAuditoria(utilizador, inicio, fim);
    }

    public double obterTotalVendasFuncionarioMes(Long idFuncionario) {
        return relatorioBO.obterTotalVendasFuncionarioMes(idFuncionario);
    }

    public List<Map<String, Object>> consultarItensPorVenda(Utilizador utilizador, Long idVenda) {
        return relatorioBO.itensPorVenda(utilizador, idVenda);
    }

    public List<Map<String, Object>> consultarResumoVendasPorEstado(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.resumoVendasPorEstado(utilizador, inicio, fim);
    }

    public List<Map<String, Object>> obterResumoVendasPorEstadoRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.obterResumoVendasPorEstadoRelatorio(inicio, fim);
    }

    public List<Map<String, Object>> obterProdutosMaisVendidosRelatorio() {
        return relatorioBO.obterProdutosMaisVendidosDashboard();
    }

    public List<Map<String, Object>> obterEstoqueBaixoRelatorio() {
        return relatorioBO.obterEstoqueBaixoDashboard();
    }

    public List<Map<String, Object>> obterReembolsosPeriodoRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.obterReembolsosPeriodoRelatorio(inicio, fim);
    }

    public List<Map<String, Object>> obterResumoAuditoriaRelatorio(LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.obterResumoAuditoriaRelatorio(inicio, fim);
    }

    public Map<String, Integer> obterIndicadoresDashboard() {
        return relatorioBO.obterIndicadoresDashboard();
    }

    public List<Map<String, Object>> obterProdutosMaisVendidosDashboard() {
        return relatorioBO.obterProdutosMaisVendidosDashboard();
    }

    public List<Map<String, Object>> obterEstoqueBaixoDashboard() {
        return relatorioBO.obterEstoqueBaixoDashboard();
    }

    public List<Map<String, Object>> itensPorVenda(Utilizador utilizador, Long idVenda) {
        return relatorioBO.itensPorVenda(utilizador, idVenda);
    }

    public List<Map<String, Object>> estoqueBaixo(Utilizador utilizador) {
        return relatorioBO.estoqueBaixo(utilizador);
    }

    public List<Map<String, Object>> produtosMaisVendidos(Utilizador utilizador) {
        return relatorioBO.produtosMaisVendidos(utilizador);
    }

    public List<Map<String, Object>> reembolsosPeriodo(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.reembolsosPeriodo(utilizador, inicio, fim);
    }

    public List<Map<String, Object>> vendasPeriodo(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.vendasPeriodo(utilizador, inicio, fim);
    }

    public List<Map<String, Object>> resumoVendasPorEstado(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.resumoVendasPorEstado(utilizador, inicio, fim);
    }

    public void exportarPDF(File destino) throws IOException, DocumentException {
        GeradorRelatorioPDF.gerar(destino);
    }

    public void exportarCSV(JTable tabela, File destino) throws IOException {
        ValidadorObjeto.validarNaoNulo(tabela, "Tabela");
        ValidadorObjeto.validarNaoNulo(destino, "Arquivo de destino");

        List<String> colunas = new ArrayList<>();
        for (int c = 0; c < tabela.getColumnCount(); c++) {
            colunas.add(tabela.getColumnName(c));
        }

        List<List<Object>> linhas = new ArrayList<>();
        for (int r = 0; r < tabela.getRowCount(); r++) {
            List<Object> linha = new ArrayList<>();
            for (int c = 0; c < tabela.getColumnCount(); c++) {
                linha.add(tabela.getValueAt(r, c));
            }
            linhas.add(linha);
        }

        exportarCSV(colunas, linhas, destino);
    }

    public void exportarCSV(List<String> colunas, List<List<Object>> linhas, File destino) throws IOException {
        ValidadorObjeto.validarNaoNulo(colunas, "Colunas");
        ValidadorObjeto.validarNaoNulo(linhas, "Linhas");
        ValidadorObjeto.validarNaoNulo(destino, "Arquivo de destino");

        File pastaPai = destino.getParentFile();
        if (pastaPai != null && !pastaPai.exists()) {
            pastaPai.mkdirs();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(destino), StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');

            for (int col = 0; col < colunas.size(); col++) {
                if (col > 0) {
                    writer.write(';');
                }
                writer.write(formatarCampoCsv(colunas.get(col)));
            }
            writer.write("\r\n");

            for (List<Object> linha : linhas) {
                for (int col = 0; col < linha.size(); col++) {
                    if (col > 0) {
                        writer.write(';');
                    }
                    Object val = col < linha.size() ? linha.get(col) : "";
                    writer.write(formatarCampoCsv(val != null ? val.toString() : ""));
                }
                writer.write("\r\n");
            }

            writer.flush();
        }
    }

    private String formatarCampoCsv(String valor) {
        if (valor == null) {
            return "";
        }
        boolean precisaAspas = valor.contains(";") || valor.contains("\"")
                || valor.contains("\n") || valor.contains("\r");
        String formatado = valor.replace("\"", "\"\"");
        if (precisaAspas) {
            return "\"" + formatado + "\"";
        }
        return formatado;
    }

    public boolean imprimirTabela(JTable tabela, String tituloRelatorio) throws PrinterException {
        ValidadorObjeto.validarNaoNulo(tabela, "Tabela");
        String titulo = (tituloRelatorio != null && !tituloRelatorio.trim().isEmpty())
                ? tituloRelatorio.trim()
                : "Relatório";
        MessageFormat header = new MessageFormat("Estabelecimento Comercial — " + titulo);
        MessageFormat footer = new MessageFormat("Página {0}");
        return tabela.print(JTable.PrintMode.FIT_WIDTH, header, footer);
    }
}
