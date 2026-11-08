package util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.dao.RelatorioDAO;
import model.vo.SessaoUtilizador;

public final class GeradorRelatorioPDF {
    private static final BaseColor COR_TEXTO = new BaseColor(0x1B, 0x20, 0x30);
    private static final BaseColor COR_SUB = new BaseColor(0x5A, 0x64, 0x78);
    private static final BaseColor COR_MARCA = new BaseColor(0x6B, 0x4F, 0x00);
    private static final BaseColor COR_DOURADO = new BaseColor(0xF5, 0xB6, 0x38);
    private static final BaseColor COR_BORDA_KPI = new BaseColor(0xDD, 0xE2, 0xEA);
    private static final BaseColor COR_BORDA_TD = new BaseColor(0xE6, 0xE9, 0xF0);
    private static final BaseColor COR_ZEBRA = new BaseColor(0xF5, 0xF6, 0xF9);
    private static final BaseColor COR_TOTAL_BG = new BaseColor(0xFF, 0xF8, 0xE1);

    private GeradorRelatorioPDF() {
    }

    public static void gerar(File destino) throws IOException, DocumentException {
        RelatorioDAO dao = new RelatorioDAO();
        Map<String, Integer> kpis = null;
        List<Map<String, Object>> resumoVendasEstado = null;
        List<Map<String, Object>> maisVendidos = null;
        List<Map<String, Object>> estoqueBaixo = null;
        List<Map<String, Object>> reembolsos = null;
        List<Map<String, Object>> auditoria = null;
        try {
            LocalDateTime inicio = LocalDateTime.now().minusDays(30);
            LocalDateTime fim = LocalDateTime.now().plusDays(1);
            kpis = dao.obterIndicadoresDashboard();
            resumoVendasEstado = dao.resumoVendasPorEstado(inicio, fim);
            maisVendidos = dao.produtosMaisVendidos();
            estoqueBaixo = dao.estoqueBaixo();
            reembolsos = dao.reembolsosPeriodo(inicio, fim);
            auditoria = dao.resumoAuditoria(inicio, fim);
        } catch (Exception ignored) {
        }

        Document document = new Document(PageSize.A4, 32f, 32f, 28f, 28f);
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            adicionarCabecalho(document);
            adicionarLinhaKpisDinamica(document, kpis, resumoVendasEstado, reembolsos);
            adicionarGradeVendasEMaisVendidosDinamica(document, resumoVendasEstado, maisVendidos);
            adicionarGradeMenosVendidosEEstoqueBaixoDinamica(document, maisVendidos, estoqueBaixo);
            adicionarTabelaReembolsosDinamica(document, reembolsos);
            adicionarTabelaAuditoriaDinamica(document, auditoria);
            adicionarRodape(document);

            document.close();
        }
    }

    private static void adicionarCabecalho(Document doc) throws DocumentException {
        PdfPTable header = new PdfPTable(new float[] {1.2f, 1f});
        header.setWidthPercentage(100);
        header.setSpacingAfter(10f);

        PdfPCell esq = new PdfPCell();
        esq.setBorder(Rectangle.BOTTOM);
        esq.setBorderWidthBottom(2.5f);
        esq.setBorderColorBottom(COR_DOURADO);
        esq.setPaddingBottom(8f);

        Paragraph marca = new Paragraph("ESTABELECIMENTO", fonte(9, Font.BOLD, COR_MARCA));
        Paragraph titulo = new Paragraph("Relatório Completo", fonte(18, Font.BOLD, COR_TEXTO));
        esq.addElement(marca);
        esq.addElement(titulo);

        PdfPCell dir = new PdfPCell();
        dir.setBorder(Rectangle.BOTTOM);
        dir.setBorderWidthBottom(2.5f);
        dir.setBorderColorBottom(COR_DOURADO);
        dir.setPaddingBottom(8f);
        dir.setVerticalAlignment(Element.ALIGN_BOTTOM);

        String usuarioAtual = (SessaoUtilizador.getUtilizador() != null)
                ? SessaoUtilizador.getUtilizador().getUsername()
                : "admin";
        String agora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph sub = new Paragraph(
                "Período: Últimos 30 dias\nGerado em " + agora + " por " + usuarioAtual,
                fonte(8.5f, Font.NORMAL, COR_SUB)
        );
        sub.setAlignment(Element.ALIGN_RIGHT);
        dir.addElement(sub);

        header.addCell(esq);
        header.addCell(dir);
        doc.add(header);
    }

    private static void adicionarLinhaKpisDinamica(
            Document doc,
            Map<String, Integer> kpis,
            List<Map<String, Object>> resumoVendasEstado,
            List<Map<String, Object>> reembolsos) throws DocumentException {
        int totalVendasPeriodo = 0;
        double totalConcluido = 0.0;
        if (resumoVendasEstado != null) {
            for (Map<String, Object> m : resumoVendasEstado) {
                int q = ((Number) m.getOrDefault("quantidade", 0)).intValue();
                double t = ((Number) m.getOrDefault("total", 0.0)).doubleValue();
                totalVendasPeriodo += q;
                if ("CONCLUIDA".equalsIgnoreCase(String.valueOf(m.getOrDefault("estado", "")))) {
                    totalConcluido += t;
                }
            }
        }
        double totalReembolsado = 0.0;
        if (reembolsos != null) {
            for (Map<String, Object> r : reembolsos) {
                totalReembolsado += ((Number) r.getOrDefault("valor", 0.0)).doubleValue();
            }
        }

        PdfPTable grid = new PdfPTable(5);
        grid.setWidthPercentage(100);
        grid.setSpacingAfter(10f);

        int estBaixoCount = kpis != null ? kpis.getOrDefault("estoqueBaixo", 0) : 0;
        int expCount = kpis != null ? kpis.getOrDefault("lotesAExpirar", 0) : 0;
        String[][] dados = {
            {"Vendas no período", String.valueOf(totalVendasPeriodo)},
            {"Total concluído", String.format(Locale.GERMANY, "%,.2f MT", totalConcluido)},
            {"Total reembolsado", String.format(Locale.GERMANY, "%,.2f MT", totalReembolsado)},
            {"Estoque baixo", estBaixoCount + " produtos"},
            {"A expirar (7 dias)", expCount + " lotes"}
        };

        for (String[] item : dados) {
            PdfPCell cell = new PdfPCell();
            cell.setBorderColor(COR_BORDA_KPI);
            cell.setPadding(6f);
            cell.addElement(new Paragraph(item[0], fonte(7.5f, Font.NORMAL, COR_SUB)));
            cell.addElement(new Paragraph(item[1], fonte(12.5f, Font.BOLD, COR_TEXTO)));
            grid.addCell(cell);
        }
        doc.add(grid);
    }

    private static void adicionarGradeVendasEMaisVendidosDinamica(
            Document doc,
            List<Map<String, Object>> resumoVendasEstado,
            List<Map<String, Object>> maisVendidos) throws DocumentException {
        PdfPTable grade = new PdfPTable(new float[] {1f, 0.04f, 1f});
        grade.setWidthPercentage(100);
        grade.setSpacingAfter(10f);

        int nEstado = resumoVendasEstado != null ? resumoVendasEstado.size() : 0;
        String[][] linhasVendas = new String[nEstado][3];
        int somaQtd = 0;
        double somaVal = 0.0;
        for (int i = 0; i < nEstado; i++) {
            Map<String, Object> m = resumoVendasEstado.get(i);
            int q = ((Number) m.getOrDefault("quantidade", 0)).intValue();
            double t = ((Number) m.getOrDefault("total", 0.0)).doubleValue();
            somaQtd += q;
            somaVal += t;
            linhasVendas[i][0] = String.valueOf(m.getOrDefault("estado", "-"));
            linhasVendas[i][1] = String.valueOf(q);
            linhasVendas[i][2] = String.format(Locale.GERMANY, "%,.2f MT", t);
        }
        PdfPTable tVendas = criarTabelaSecao(
                new String[] {"Estado", "Qtd", "Total"},
                linhasVendas,
                new String[] {"Total", String.valueOf(somaQtd), String.format(Locale.GERMANY, "%,.2f MT", somaVal)}
        );

        int limit = maisVendidos != null ? Math.min(maisVendidos.size(), 4) : 0;
        String[][] linhasMais = new String[limit][3];
        for (int i = 0; i < limit; i++) {
            Map<String, Object> m = maisVendidos.get(i);
            linhasMais[i][0] = String.valueOf(i + 1);
            linhasMais[i][1] = String.valueOf(m.getOrDefault("nome_produto", "-"));
            linhasMais[i][2] = String.valueOf(m.getOrDefault("quantidade_vendida", "0"));
        }

        PdfPTable tMaisVendidos = criarTabelaSecao(
                new String[] {"#", "Produto", "Qtd"},
                linhasMais,
                null
        );

        grade.addCell(envolverSecao("Vendas por estado", tVendas));
        grade.addCell(celulaEspacadora());
        grade.addCell(envolverSecao("Mais vendidos", tMaisVendidos));
        doc.add(grade);
    }

    private static void adicionarGradeMenosVendidosEEstoqueBaixoDinamica(
            Document doc,
            List<Map<String, Object>> maisVendidos,
            List<Map<String, Object>> estoqueBaixo) throws DocumentException {
        PdfPTable grade = new PdfPTable(new float[] {1f, 0.04f, 1f});
        grade.setWidthPercentage(100);
        grade.setSpacingAfter(10f);

        List<Map<String, Object>> menos = new ArrayList<>();
        if (maisVendidos != null) {
            menos.addAll(maisVendidos);
            Collections.sort(menos, Comparator.comparingDouble(o -> {
                try { return Double.parseDouble(String.valueOf(o.getOrDefault("quantidade_vendida", 0))); }
                catch (Exception e) { return 0.0; }
            }));
        }
        int limitMenos = Math.min(menos.size(), 4);
        String[][] linhasMenos = new String[limitMenos][3];
        for (int i = 0; i < limitMenos; i++) {
            Map<String, Object> m = menos.get(i);
            linhasMenos[i][0] = String.valueOf(i + 1);
            linhasMenos[i][1] = String.valueOf(m.getOrDefault("nome_produto", "-"));
            linhasMenos[i][2] = String.valueOf(m.getOrDefault("quantidade_vendida", "0"));
        }
        PdfPTable tMenosVendidos = criarTabelaSecao(
                new String[] {"#", "Produto", "Qtd"},
                linhasMenos,
                null
        );

        int limit = estoqueBaixo != null ? Math.min(estoqueBaixo.size(), 4) : 0;
        String[][] linhasEstoque = new String[limit][3];
        for (int i = 0; i < limit; i++) {
            Map<String, Object> m = estoqueBaixo.get(i);
            linhasEstoque[i][0] = String.valueOf(m.getOrDefault("nome_produto", "-"));
            linhasEstoque[i][1] = String.valueOf(m.getOrDefault("quantidade_atual", "0"));
            linhasEstoque[i][2] = String.valueOf(m.getOrDefault("quantidade_minima", "0"));
        }

        PdfPTable tEstoqueBaixo = criarTabelaSecao(
                new String[] {"Produto", "Actual", "Mínimo"},
                linhasEstoque,
                null
        );

        grade.addCell(envolverSecao("Menos vendidos", tMenosVendidos));
        grade.addCell(celulaEspacadora());
        grade.addCell(envolverSecao("Estoque baixo", tEstoqueBaixo));
        doc.add(grade);
    }

    private static void adicionarTabelaReembolsosDinamica(Document doc, List<Map<String, Object>> reembolsos) throws DocumentException {
        int limit = reembolsos != null ? Math.min(reembolsos.size(), 6) : 0;
        String[][] linhas = new String[limit][7];
        double totalValor = 0.0;
        int totalQtd = 0;
        for (int i = 0; i < limit; i++) {
            Map<String, Object> r = reembolsos.get(i);
            linhas[i][0] = String.valueOf(r.getOrDefault("data_reembolso", "-"));
            linhas[i][1] = String.valueOf(r.getOrDefault("id_venda", "-"));
            linhas[i][2] = String.valueOf(r.getOrDefault("nome_produto", "-"));
            int q = ((Number) r.getOrDefault("quantidade", 0)).intValue();
            totalQtd += q;
            linhas[i][3] = String.valueOf(q);
            double v = ((Number) r.getOrDefault("valor", 0.0)).doubleValue();
            totalValor += v;
            linhas[i][4] = String.format("%.2f MT", v);
            linhas[i][5] = String.valueOf(r.getOrDefault("motivo", "-"));
            linhas[i][6] = String.valueOf(r.getOrDefault("nome_funcionario", "-"));
        }

        PdfPTable tReembolsos = criarTabelaSecao(
                new String[] {"Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"},
                linhas,
                new String[] {"Total", "", "", String.valueOf(totalQtd), String.format("%.2f MT", totalValor), "", ""}
        );
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(10f);
        wrapper.addCell(envolverSecao("Reembolsos", tReembolsos));
        doc.add(wrapper);
    }

    private static void adicionarTabelaAuditoriaDinamica(Document doc, List<Map<String, Object>> auditoria) throws DocumentException {
        int limit = auditoria != null ? Math.min(auditoria.size(), 5) : 0;
        String[][] linhas = new String[limit][4];
        for (int i = 0; i < limit; i++) {
            Map<String, Object> a = auditoria.get(i);
            linhas[i][0] = String.valueOf(a.getOrDefault("dia", "-"));
            linhas[i][1] = String.valueOf(a.getOrDefault("tabela_afetada", "-"));
            linhas[i][2] = String.valueOf(a.getOrDefault("operacao", "-"));
            linhas[i][3] = String.valueOf(a.getOrDefault("quantidade", "0"));
        }

        PdfPTable tAuditoria = criarTabelaSecao(
                new String[] {"Dia", "Tabela afectada", "Operação", "Registos"},
                linhas,
                null
        );
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(14f);
        wrapper.addCell(envolverSecao("Resumo de auditoria", tAuditoria));
        doc.add(wrapper);
    }

    private static void adicionarLinhaKpis(Document doc) throws DocumentException {
        PdfPTable kpis = new PdfPTable(5);
        kpis.setWidthPercentage(100);
        kpis.setSpacingAfter(10f);

        String[][] dados = {
            {"Vendas no período", "4"},
            {"Total concluído", "840,00 MT"},
            {"Total reembolsado", "105,00 MT"},
            {"Estoque baixo", "4 produtos"},
            {"A expirar (7 dias)", "5 produtos"}
        };

        for (String[] item : dados) {
            PdfPCell cell = new PdfPCell();
            cell.setBorderColor(COR_BORDA_KPI);
            cell.setPadding(6f);
            cell.addElement(new Paragraph(item[0], fonte(7.5f, Font.NORMAL, COR_SUB)));
            cell.addElement(new Paragraph(item[1], fonte(12.5f, Font.BOLD, COR_TEXTO)));
            kpis.addCell(cell);
        }
        doc.add(kpis);
    }

    private static void adicionarGradeVendasEMaisVendidos(Document doc) throws DocumentException {
        PdfPTable grade = new PdfPTable(new float[] {1f, 0.04f, 1f});
        grade.setWidthPercentage(100);
        grade.setSpacingAfter(10f);

        PdfPTable tVendas = criarTabelaSecao(
                new String[] {"Estado", "Qtd", "Total"},
                new String[][] {
                    {"PENDENTE", "1", "1.250,00 MT"},
                    {"CONCLUÍDA", "1", "840,00 MT"},
                    {"ANULADA", "1", "300,00 MT"},
                    {"REEMBOLSADA", "1", "2.100,00 MT"}
                },
                new String[] {"Total", "4", "4.490,00 MT"}
        );

        PdfPTable tMaisVendidos = criarTabelaSecao(
                new String[] {"#", "Produto", "Qtd"},
                new String[][] {
                    {"1", "Pão", "320"},
                    {"2", "Leite 1L", "210"},
                    {"3", "Água 1,5L", "180"},
                    {"4", "Arroz 25kg", "35"}
                },
                null
        );

        grade.addCell(envolverSecao("Vendas por estado", tVendas));
        grade.addCell(celulaEspacadora());
        grade.addCell(envolverSecao("Mais vendidos", tMaisVendidos));
        doc.add(grade);
    }

    private static void adicionarGradeMenosVendidosEEstoqueBaixo(Document doc) throws DocumentException {
        PdfPTable grade = new PdfPTable(new float[] {1f, 0.04f, 1f});
        grade.setWidthPercentage(100);
        grade.setSpacingAfter(10f);

        PdfPTable tMenosVendidos = criarTabelaSecao(
                new String[] {"#", "Produto", "Qtd"},
                new String[][] {
                    {"1", "Biscoito", "0"},
                    {"2", "Óleo 5L", "4"},
                    {"3", "Sal 1kg", "5"},
                    {"4", "Açúcar 1kg", "7"}
                },
                null
        );

        PdfPTable tEstoqueBaixo = criarTabelaSecao(
                new String[] {"Produto", "Actual", "Mínimo"},
                new String[][] {
                    {"Arroz 25kg", "4", "10"},
                    {"Óleo 5L", "2", "8"},
                    {"Açúcar 1kg", "6", "15"},
                    {"Sal 1kg", "1", "5"}
                },
                null
        );

        grade.addCell(envolverSecao("Menos vendidos", tMenosVendidos));
        grade.addCell(celulaEspacadora());
        grade.addCell(envolverSecao("Estoque baixo", tEstoqueBaixo));
        doc.add(grade);
    }

    private static void adicionarTabelaReembolsos(Document doc) throws DocumentException {
        PdfPTable tReembolsos = criarTabelaSecao(
                new String[] {"Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"},
                new String[][] {
                    {"03/11/2026", "1039", "Leite 1L", "1", "85,00 MT", "Embalagem danificada", "Sara"},
                    {"02/11/2026", "1039", "Pão", "4", "20,00 MT", "Fora do prazo", "Sara"}
                },
                new String[] {"Total", "", "", "5", "105,00 MT", "", ""}
        );
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(10f);
        wrapper.addCell(envolverSecao("Reembolsos", tReembolsos));
        doc.add(wrapper);
    }

    private static void adicionarTabelaAuditoria(Document doc) throws DocumentException {
        PdfPTable tAuditoria = criarTabelaSecao(
                new String[] {"Dia", "Tabela afectada", "Operação", "Registos"},
                new String[][] {
                    {"03/11/2026", "Vendas", "Registo criado", "12"},
                    {"03/11/2026", "Estoque", "Registo alterado", "8"},
                    {"02/11/2026", "Produtos", "Registo alterado", "3"},
                    {"02/11/2026", "Reembolsos", "Registo criado", "2"}
                },
                null
        );
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(14f);
        wrapper.addCell(envolverSecao("Resumo de auditoria", tAuditoria));
        doc.add(wrapper);
    }

    private static void adicionarRodape(Document doc) throws DocumentException {
        PdfPTable footer = new PdfPTable(2);
        footer.setWidthPercentage(100);

        PdfPCell esq = new PdfPCell(new Phrase("Estabelecimento · Relatório confidencial", fonte(8, Font.NORMAL, COR_SUB)));
        esq.setBorder(Rectangle.TOP);
        esq.setBorderColorTop(COR_BORDA_KPI);
        esq.setPaddingTop(5f);

        PdfPCell dir = new PdfPCell(new Phrase("Página 1 de 1", fonte(8, Font.NORMAL, COR_SUB)));
        dir.setBorder(Rectangle.TOP);
        dir.setBorderColorTop(COR_BORDA_KPI);
        dir.setPaddingTop(5f);
        dir.setHorizontalAlignment(Element.ALIGN_RIGHT);

        footer.addCell(esq);
        footer.addCell(dir);
        doc.add(footer);
    }

    private static PdfPCell envolverSecao(String tituloSecao, PdfPTable tabela) {
        PdfPCell container = new PdfPCell();
        container.setBorder(Rectangle.NO_BORDER);
        container.setPadding(0);

        PdfPTable barraTitulo = new PdfPTable(1);
        barraTitulo.setWidthPercentage(100);
        barraTitulo.setSpacingAfter(4f);

        PdfPCell cellTitulo = new PdfPCell(new Phrase(tituloSecao, fonte(9.5f, Font.BOLD, COR_TEXTO)));
        cellTitulo.setBorder(Rectangle.LEFT);
        cellTitulo.setBorderWidthLeft(3f);
        cellTitulo.setBorderColorLeft(COR_DOURADO);
        cellTitulo.setPaddingLeft(6f);
        cellTitulo.setPaddingTop(1f);
        cellTitulo.setPaddingBottom(2f);
        barraTitulo.addCell(cellTitulo);

        container.addElement(barraTitulo);
        container.addElement(tabela);
        return container;
    }

    private static PdfPTable criarTabelaSecao(String[] colunas, String[][] linhas, String[] linhaTotal) {
        PdfPTable table = new PdfPTable(colunas.length);
        table.setWidthPercentage(100);

        for (String col : colunas) {
            PdfPCell th = new PdfPCell(new Phrase(col, fonte(8f, Font.BOLD, BaseColor.WHITE)));
            th.setBackgroundColor(COR_TEXTO);
            th.setBorder(Rectangle.NO_BORDER);
            th.setPadding(4.5f);
            table.addCell(th);
        }

        for (int r = 0; r < linhas.length; r++) {
            boolean par = (r % 2 == 1);
            for (String valor : linhas[r]) {
                PdfPCell td = new PdfPCell(new Phrase(valor, fonte(8.5f, Font.NORMAL, COR_TEXTO)));
                td.setBackgroundColor(par ? COR_ZEBRA : BaseColor.WHITE);
                td.setBorder(Rectangle.BOTTOM);
                td.setBorderColorBottom(COR_BORDA_TD);
                td.setPadding(4.5f);
                table.addCell(td);
            }
        }

        if (linhaTotal != null) {
            for (String valor : linhaTotal) {
                PdfPCell tot = new PdfPCell(new Phrase(valor, fonte(8.5f, Font.BOLD, COR_TEXTO)));
                tot.setBackgroundColor(COR_TOTAL_BG);
                tot.setBorder(Rectangle.BOTTOM);
                tot.setBorderColorBottom(COR_BORDA_TD);
                tot.setPadding(4.5f);
                table.addCell(tot);
            }
        }

        return table;
    }

    private static PdfPCell celulaEspacadora() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private static Font fonte(float tamanho, int estilo, BaseColor cor) {
        return FontFactory.getFont(FontFactory.HELVETICA, tamanho, estilo, cor);
    }
}
