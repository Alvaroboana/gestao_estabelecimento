package testes;

import controller.RelatorioController;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import view.RelatoriosPanel;
import view.RelatoriosPanel.AbaRelatorio;

public class TesteExportacaoRelatorios {

    public static void main(String[] args) {
        System.out.println("========== Iniciando Teste de Exportacao e Relatorios ==========");
        testarObterTabelaAtivaPorAba();
        testarExportacaoCsvController();
        testarExportacaoCsvPainel();
        System.out.println("========== Todos os testes passaram com sucesso! ==========");
    }

    private static void testarObterTabelaAtivaPorAba() {
        System.out.println("[Teste] Selecao de abas e obterTabelaAtiva...");
        RelatoriosPanel panel = new RelatoriosPanel(AbaRelatorio.COMPLETO);

        assertNotNull(panel.obterTabelaAtiva(), "Tabela ativa em COMPLETO nao deve ser nula");
        assertEquals("Completo", panel.getAbaAtiva(), "Aba ativa deve ser Completo");

        panel.selecionarAba("Vendas");
        assertEquals(panel.getTabelaVendas(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaVendas");

        panel.selecionarAba("Estoque baixo");
        assertEquals(panel.getTabelaEstoqueBaixo(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaEstoqueBaixo");

        panel.selecionarAba("Mais vendidos");
        assertEquals(panel.getTabelaMaisVendidos(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaMaisVendidos");

        panel.selecionarAba("Menos vendidos");
        assertEquals(panel.getTabelaMenosVendidos(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaMenosVendidos");

        panel.selecionarAba("Reembolsos");
        assertEquals(panel.getTabelaReembolsos(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaReembolsos");

        panel.selecionarAba("Auditoria");
        assertEquals(panel.getTabelaAuditoria(), panel.obterTabelaAtiva(), "Tabela ativa deve ser tabelaAuditoria");

        assertNotNull(panel.getBotaoImprimir(), "Botao Imprimir deve existir");
        assertEquals(1, panel.getBotaoImprimir().getActionListeners().length, "Botao Imprimir deve ter ActionListener registrado");

        assertNotNull(panel.getBotaoExportarSimples(), "Botao Exportar CSV deve existir");
        assertEquals(1, panel.getBotaoExportarSimples().getActionListeners().length, "Botao Exportar CSV deve ter ActionListener registrado");

        System.out.println("[OK] Teste de abas concluido com sucesso.");
    }

    private static void testarExportacaoCsvController() {
        System.out.println("[Teste] Exportacao CSV via RelatorioController...");
        RelatorioController controller = new RelatorioController();

        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{
                        {"1", "Pão Francês", "10", "5,00 MT"},
                        {"2", "Leite; Longa Vida", "5", "85,00 MT"},
                        {"3", "Água mineral \"Pure\"", "20", "40,00 MT"}
                },
                new String[]{"ID", "Produto", "Qtd", "Preço"}
        );
        JTable table = new JTable(model);

        File tempFile = null;
        try {
            tempFile = File.createTempFile("teste_csv_", ".csv");
            tempFile.deleteOnExit();

            controller.exportarCSV(table, tempFile);

            assertTrue(tempFile.exists(), "Ficheiro temporario deve existir");
            assertTrue(tempFile.length() > 0, "Ficheiro temporario nao deve estar vazio");

            String conteudo = lerArquivoUtf8(tempFile);

            assertTrue(conteudo.startsWith("\uFEFF"), "Ficheiro deve iniciar com Byte Order Mark (BOM) UTF-8");

            assertTrue(conteudo.contains("ID;Produto;Qtd;Preço"), "Cabecalho com ponto e virgula esperado");
            assertTrue(conteudo.contains("1;Pão Francês;10;5,00 MT"), "Linha 1 com acentuacao esperada");
            assertTrue(conteudo.contains("\"Leite; Longa Vida\""), "Campo contendo ponto e virgula deve estar entre aspas");
            assertTrue(conteudo.contains("\"Água mineral \"\"Pure\"\"\""), "Aspas duplas devem ser escapadas com aspas duplas adicionais");

            System.out.println("[OK] Exportacao CSV via RelatorioController validada.");
        } catch (Exception e) {
            throw new RuntimeException("Falha no teste de exportacao CSV do controller: " + e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private static void testarExportacaoCsvPainel() {
        System.out.println("[Teste] Exportacao CSV programatica via RelatoriosPanel...");
        RelatoriosPanel panel = new RelatoriosPanel(AbaRelatorio.MAIS_VENDIDOS);
        DefaultTableModel m = (DefaultTableModel) panel.getTabelaMaisVendidos().getModel();
        if (m.getRowCount() == 0) {
            m.addRow(new Object[] {"1", "Pão", "10", "50,00 MT"});
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("teste_painel_mais_vendidos_", ".csv");
            tempFile.deleteOnExit();

            panel.exportarCsv(tempFile);

            assertTrue(tempFile.exists(), "Ficheiro gerado pelo painel deve existir");
            String conteudo = lerArquivoUtf8(tempFile);

            assertTrue(conteudo.startsWith("\uFEFF"), "BOM UTF-8 esperado");
            assertTrue(conteudo.contains("#;Produto;Qtd vendida;Receita"), "Cabecalho esperado de Mais Vendidos");
            assertTrue(conteudo.contains("Pão"), "Dados esperados de Mais Vendidos");

            System.out.println("[OK] Exportacao CSV via RelatoriosPanel validada.");
        } catch (Exception e) {
            throw new RuntimeException("Falha no teste de exportacao CSV do painel: " + e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private static String lerArquivoUtf8(File arquivo) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8)) {
            char[] buf = new char[1024];
            int read;
            while ((read = isr.read(buf)) != -1) {
                sb.append(buf, 0, read);
            }
        }
        return sb.toString();
    }

    private static void assertEquals(Object esperado, Object atual, String msg) {
        if (esperado == null && atual == null) return;
        if (esperado != null && esperado.equals(atual)) return;
        throw new AssertionError(msg + " - Esperado: " + esperado + ", Atual: " + atual);
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) {
            throw new AssertionError(msg);
        }
    }

    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) {
            throw new AssertionError(msg);
        }
    }
}
