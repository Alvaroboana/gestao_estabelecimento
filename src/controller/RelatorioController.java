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

/**
 * Controller responsável pela emissão de relatórios analíticos, auditoria,
 * indicadores de desempenho, impressão e exportação em formato PDF e CSV.
 */
public class RelatorioController {

    private final RelatorioBO relatorioBO;

    public RelatorioController() {
        this(new RelatorioBO());
    }

    public RelatorioController(RelatorioBO relatorioBO) {
        ValidadorObjeto.validarNaoNulo(relatorioBO, "RelatorioBO");
        this.relatorioBO = relatorioBO;
    }

    /**
     * Consulta os indicadores estatísticos consolidados para o Dashboard.
     *
     * @return Mapa contendo métricas chave (vendas hoje, estoque baixo, etc.)
     */
    public Map<String, Integer> consultarIndicadoresDashboard() {
        return relatorioBO.obterIndicadoresDashboard();
    }

    /**
     * Consulta o relatório de vendas ocorridas dentro de um intervalo de datas.
     *
     * @param utilizador Utilizador com permissão para visualização de relatórios
     * @param inicio     Data e hora inicial do período
     * @param fim        Data e hora final do período
     * @return Lista com os registos de vendas do período
     */
    public List<Map<String, Object>> consultarVendasPeriodo(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.vendasPeriodo(utilizador, inicio, fim);
    }

    /**
     * Consulta os produtos com estoque abaixo do nível mínimo de segurança.
     *
     * @param utilizador Utilizador com permissão
     * @return Lista de produtos com estoque baixo
     */
    public List<Map<String, Object>> consultarEstoqueBaixo(Utilizador utilizador) {
        return relatorioBO.estoqueBaixo(utilizador);
    }

    /**
     * Consulta a listagem dos produtos com maior volume de vendas registradas.
     *
     * @param utilizador Utilizador com permissão
     * @return Lista ordenada decrescente por quantidade vendida
     */
    public List<Map<String, Object>> consultarProdutosMaisVendidos(Utilizador utilizador) {
        return relatorioBO.produtosMaisVendidos(utilizador);
    }

    /**
     * Consulta a listagem dos produtos menos vendidos no estabelecimento.
     *
     * @param utilizador Utilizador com permissão
     * @return Lista ordenada crescente por quantidade vendida
     */
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

    /**
     * Consulta o relatório de reembolsos efetuados no período especificado.
     *
     * @param utilizador Utilizador com permissão
     * @param inicio     Data inicial
     * @param fim        Data final
     * @return Lista de reembolsos processados
     */
    public List<Map<String, Object>> consultarReembolsos(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.reembolsosPeriodo(utilizador, inicio, fim);
    }

    /**
     * Consulta os registos detalhados de auditoria do sistema com base em filtros.
     *
     * @param utilizador Utilizador com perfil ADMIN ou GERENTE
     * @param tabela     Nome da tabela auditada (opcional)
     * @param idRegistro Identificador do registo auditado (opcional)
     * @param inicio     Data inicial (opcional)
     * @param fim        Data final (opcional)
     * @param operacao   Tipo de operação (INSERT, UPDATE, DELETE) (opcional)
     * @return Lista de registos de auditoria correspondentes
     */
    public List<Map<String, Object>> consultarAuditoria(
            Utilizador utilizador, String tabela, Long idRegistro,
            LocalDateTime inicio, LocalDateTime fim, String operacao) {
        return relatorioBO.consultarAuditoria(utilizador, tabela, idRegistro, inicio, fim, operacao);
    }

    /**
     * Consulta o resumo agrupado de auditoria por dia e operação.
     *
     * @param utilizador Utilizador com permissão
     * @param inicio     Data inicial
     * @param fim        Data final
     * @return Lista de resumos de auditoria
     */
    public List<Map<String, Object>> resumoAuditoria(Utilizador utilizador, LocalDateTime inicio, LocalDateTime fim) {
        return relatorioBO.resumoAuditoria(utilizador, inicio, fim);
    }

    /**
     * Obtém o valor total de vendas realizadas por um funcionário no mês corrente.
     *
     * @param idFuncionario Identificador do funcionário
     * @return Total acumulado em vendas
     */
    public double obterTotalVendasFuncionarioMes(Long idFuncionario) {
        return relatorioBO.obterTotalVendasFuncionarioMes(idFuncionario);
    }

    /**
     * Exporta o relatório completo institucional em formato PDF.
     *
     * @param destino Arquivo de destino (.pdf)
     * @throws IOException       Em caso de falha de I/O na escrita do ficheiro
     * @throws DocumentException Em caso de erro na geração da estrutura do PDF
     */
    public void exportarPDF(File destino) throws IOException, DocumentException {
        GeradorRelatorioPDF.gerar(destino);
    }

    /**
     * Exporta os dados exibidos numa JTable para ficheiro CSV utilizando ponto e vírgula (;)
     * como delimitador de campos e codificação UTF-8 com BOM (Byte Order Mark), garantindo
     * perfeita compatibilidade com Microsoft Excel e outros leitores de planilhas.
     *
     * @param tabela  Tabela Swing com os dados a serem gravados
     * @param destino Ficheiro de destino (.csv)
     * @throws IOException Em caso de falha de escrita no disco
     */
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

    /**
     * Exporta cabeçalhos e linhas para ficheiro CSV estruturado com separador ';' e UTF-8 com BOM.
     *
     * @param colunas Nomes das colunas
     * @param linhas  Lista de registos contendo os valores
     * @param destino Ficheiro de destino (.csv)
     * @throws IOException Em caso de erro na gravação do ficheiro
     */
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
            // Byte Order Mark (BOM) UTF-8 para garantir que softwares como o MS Excel
            // identifiquem a codificação e exibam caracteres acentuados corretamente
            writer.write('\uFEFF');

            // Cabeçalho
            for (int col = 0; col < colunas.size(); col++) {
                if (col > 0) {
                    writer.write(';');
                }
                writer.write(formatarCampoCsv(colunas.get(col)));
            }
            writer.write("\r\n");

            // Linhas
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

    /**
     * Formata um campo textual segundo as diretrizes RFC 4180 de CSV,
     * envolvendo entre aspas caso possua delimitadores (;), quebras de linha ou aspas duplas.
     *
     * @param valor Valor original do campo
     * @return String formatada para CSV
     */
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

    /**
     * Imprime os dados de uma JTable utilizando a API de impressão do Swing (JTable.print),
     * configurando ajuste de largura à página (FIT_WIDTH), cabeçalho institucional e numeração de rodapé.
     *
     * @param tabela          Tabela do relatório a ser impressa
     * @param tituloRelatorio Nome ou descrição para o cabeçalho do documento impresso
     * @return true se o trabalho de impressão foi concluído, false se foi cancelado
     * @throws PrinterException Em caso de erro na comunicação com a impressora
     */
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
