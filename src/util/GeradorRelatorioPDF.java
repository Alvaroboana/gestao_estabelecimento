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

/**
 * Gerador do "Relatório Completo" em PDF utilizando iText 5 (lib/itextpdf-5.5.13.4.jar),
 * replicando as cores, tabelas e estrutura de RelatorioPDF.dc.html.
 */
public final class GeradorRelatorioPDF {
    private static final BaseColor COR_TEXTO = new BaseColor(0x1B, 0x20, 0x30);       // #1B2030
    private static final BaseColor COR_SUB = new BaseColor(0x5A, 0x64, 0x78);         // #5A6478
    private static final BaseColor COR_MARCA = new BaseColor(0x6B, 0x4F, 0x00);       // #6B4F00
    private static final BaseColor COR_DOURADO = new BaseColor(0xF5, 0xB6, 0x38);     // #F5B638
    private static final BaseColor COR_BORDA_KPI = new BaseColor(0xDD, 0xE2, 0xEA);   // #DDE2EA
    private static final BaseColor COR_BORDA_TD = new BaseColor(0xE6, 0xE9, 0xF0);    // #E6E9F0
    private static final BaseColor COR_ZEBRA = new BaseColor(0xF5, 0xF6, 0xF9);       // #F5F6F9
    private static final BaseColor COR_TOTAL_BG = new BaseColor(0xFF, 0xF8, 0xE1);    // #FFF8E1

    private GeradorRelatorioPDF() {
    }

    public static void gerar(File destino) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 32f, 32f, 28f, 28f);
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            adicionarCabecalho(document);
            adicionarLinhaKpis(document);
            adicionarGradeVendasEMaisVendidos(document);
            adicionarGradeMenosVendidosEEstoqueBaixo(document);
            adicionarTabelaReembolsos(document);
            adicionarTabelaAuditoria(document);
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

        Paragraph sub = new Paragraph(
                "Período: 01/11/2026 a 03/11/2026\nGerado em 03/11/2026 por admin",
                fonte(8.5f, Font.NORMAL, COR_SUB)
        );
        sub.setAlignment(Element.ALIGN_RIGHT);
        dir.addElement(sub);

        header.addCell(esq);
        header.addCell(dir);
        doc.add(header);
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
