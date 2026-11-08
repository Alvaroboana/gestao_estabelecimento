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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import model.vo.ItemVendido;
import model.vo.Venda;

public final class GeradorFaturaPDF {
    private static final BaseColor COR_TEXTO = new BaseColor(0x1B, 0x20, 0x30);
    private static final BaseColor COR_SUB = new BaseColor(0x5A, 0x64, 0x78);
    private static final BaseColor COR_DOURADO = new BaseColor(0xF5, 0xB6, 0x38);
    private static final BaseColor COR_BORDA = new BaseColor(0xDD, 0xE2, 0xEA);
    private static final BaseColor COR_ZEBRA = new BaseColor(0xF7, 0xF8, 0xFA);

    private GeradorFaturaPDF() {
    }

    public static void gerar(Venda venda, double valorRecebido, double troco, File destino)
            throws IOException, DocumentException {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula.");
        }
        Document document = new Document(PageSize.A4, 36f, 36f, 32f, 32f);
        try (FileOutputStream fos = new FileOutputStream(destino)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataStr = venda.getData() != null ? venda.getData().format(dtf) : "-";
            String idStr = venda.getIdVenda() != null ? String.valueOf(venda.getIdVenda()) : "-";
            String clienteNome = (venda.getCliente() != null && venda.getCliente().getNome() != null)
                    ? venda.getCliente().getNome()
                    : "Consumidor Final";
            String operadorNome = (venda.getFuncionario() != null && venda.getFuncionario().getNome() != null)
                    ? venda.getFuncionario().getNome()
                    : "-";
            String estadoStr = venda.getEstado() != null ? venda.getEstado().name() : "CONCLUIDA";

            PdfPTable header = new PdfPTable(new float[] {1.3f, 1f});
            header.setWidthPercentage(100);
            header.setSpacingAfter(14f);

            PdfPCell esq = new PdfPCell();
            esq.setBorder(Rectangle.BOTTOM);
            esq.setBorderWidthBottom(2.5f);
            esq.setBorderColorBottom(COR_DOURADO);
            esq.setPaddingBottom(10f);
            esq.addElement(new Paragraph("MERCEARIA KUMBEZA", fonte(16, Font.BOLD, COR_TEXTO)));
            esq.addElement(new Paragraph("Maputo · Moçambique | Gestão de Estabelecimento", fonte(9, Font.NORMAL, COR_SUB)));

            PdfPCell dir = new PdfPCell();
            dir.setBorder(Rectangle.BOTTOM);
            dir.setBorderWidthBottom(2.5f);
            dir.setBorderColorBottom(COR_DOURADO);
            dir.setPaddingBottom(10f);
            dir.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph pFat = new Paragraph("FATURA / RECIBO Nº #" + idStr, fonte(13, Font.BOLD, COR_TEXTO));
            pFat.setAlignment(Element.ALIGN_RIGHT);
            Paragraph pData = new Paragraph("Data: " + dataStr + "  |  Estado: " + estadoStr, fonte(9, Font.NORMAL, COR_SUB));
            pData.setAlignment(Element.ALIGN_RIGHT);
            dir.addElement(pFat);
            dir.addElement(pData);

            header.addCell(esq);
            header.addCell(dir);
            document.add(header);

            PdfPTable meta = new PdfPTable(new float[] {1f, 1f});
            meta.setWidthPercentage(100);
            meta.setSpacingAfter(14f);
            meta.addCell(celulaInfo("Cliente", clienteNome));
            meta.addCell(celulaInfo("Operador / Caixa", operadorNome));
            document.add(meta);

            PdfPTable tabela = new PdfPTable(new float[] {3.2f, 0.9f, 1.3f, 1.3f, 1.5f});
            tabela.setWidthPercentage(100);
            tabela.setSpacingAfter(14f);

            tabela.addCell(celulaCabecalho("Produto / Descrição"));
            tabela.addCell(celulaCabecalho("Qtd"));
            tabela.addCell(celulaCabecalho("Preço Unit."));
            tabela.addCell(celulaCabecalho("Desconto"));
            tabela.addCell(celulaCabecalho("Subtotal"));

            double totalBruto = 0.0;
            double totalDescontos = 0.0;
            int idx = 0;
            if (venda.getItemVendido() != null) {
                for (ItemVendido item : venda.getItemVendido()) {
                    if (item == null) continue;
                    String prod = (item.getProduto() != null && item.getProduto().getNome() != null)
                            ? item.getProduto().getNome()
                            : "Produto";
                    int qtd = item.getQuantidade() != null ? item.getQuantidade() : 1;
                    double preco = item.getPreco() != null ? item.getPreco() : 0.0;
                    double desc = item.getDesconto() != null ? item.getDesconto() : 0.0;
                    double bruto = preco * qtd;
                    double sub = Math.max(0.0, bruto - desc);
                    totalBruto += bruto;
                    totalDescontos += desc;

                    boolean zebra = (idx++ % 2 == 1);
                    tabela.addCell(celulaDado(prod, Element.ALIGN_LEFT, zebra));
                    tabela.addCell(celulaDado(String.valueOf(qtd), Element.ALIGN_CENTER, zebra));
                    tabela.addCell(celulaDado(formatarMt(preco), Element.ALIGN_RIGHT, zebra));
                    tabela.addCell(celulaDado(formatarMt(desc), Element.ALIGN_RIGHT, zebra));
                    tabela.addCell(celulaDado(formatarMt(sub), Element.ALIGN_RIGHT, zebra));
                }
            }
            document.add(tabela);

            double totalLiquido = venda.getTotal() != null ? venda.getTotal() : Math.max(0.0, totalBruto - totalDescontos);
            double recebidoEfetivo = valorRecebido > 0 ? valorRecebido : totalLiquido;
            double trocoEfetivo = Math.max(0.0, troco > 0 ? troco : (recebidoEfetivo - totalLiquido));

            PdfPTable resumo = new PdfPTable(new float[] {2.5f, 1.2f});
            resumo.setWidthPercentage(55);
            resumo.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumo.setSpacingAfter(20f);

            adicionarLinhaResumo(resumo, "Subtotal Bruto:", formatarMt(totalBruto), false);
            adicionarLinhaResumo(resumo, "Total de Descontos:", "- " + formatarMt(totalDescontos), false);
            adicionarLinhaResumo(resumo, "TOTAL LÍQUIDO A PAGAR:", formatarMt(totalLiquido), true);
            adicionarLinhaResumo(resumo, "Valor Recebido:", formatarMt(recebidoEfetivo), false);
            adicionarLinhaResumo(resumo, "Troco:", formatarMt(trocoEfetivo), false);
            document.add(resumo);

            Paragraph rodape = new Paragraph(
                    "Obrigado pela sua preferência! Documento processado eletronicamente — Mercearia Kumbeza.",
                    fonte(8.5f, Font.ITALIC, COR_SUB)
            );
            rodape.setAlignment(Element.ALIGN_CENTER);
            document.add(rodape);

            document.close();
        }
    }

    private static PdfPCell celulaInfo(String rotulo, String valor) {
        PdfPCell c = new PdfPCell();
        c.setBorderColor(COR_BORDA);
        c.setPadding(8f);
        c.addElement(new Paragraph(rotulo, fonte(8.5f, Font.BOLD, COR_SUB)));
        c.addElement(new Paragraph(valor != null ? valor : "-", fonte(11f, Font.BOLD, COR_TEXTO)));
        return c;
    }

    private static PdfPCell celulaCabecalho(String texto) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fonte(9.5f, Font.BOLD, COR_TEXTO)));
        c.setBackgroundColor(COR_ZEBRA);
        c.setBorderColor(COR_BORDA);
        c.setPadding(7f);
        return c;
    }

    private static PdfPCell celulaDado(String texto, int alinhamento, boolean zebra) {
        PdfPCell c = new PdfPCell(new Phrase(texto, fonte(9.5f, Font.NORMAL, COR_TEXTO)));
        if (zebra) {
            c.setBackgroundColor(COR_ZEBRA);
        }
        c.setHorizontalAlignment(alinhamento);
        c.setBorderColor(COR_BORDA);
        c.setPadding(6.5f);
        return c;
    }

    private static void adicionarLinhaResumo(PdfPTable t, String rotulo, String valor, boolean destaque) {
        Font f = destaque ? fonte(11f, Font.BOLD, COR_TEXTO) : fonte(9.5f, Font.NORMAL, COR_TEXTO);
        PdfPCell c1 = new PdfPCell(new Phrase(rotulo, f));
        c1.setBorderColor(COR_BORDA);
        c1.setPadding(6f);
        PdfPCell c2 = new PdfPCell(new Phrase(valor, f));
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setBorderColor(COR_BORDA);
        c2.setPadding(6f);
        t.addCell(c1);
        t.addCell(c2);
    }

    private static String formatarMt(double valor) {
        return String.format(Locale.GERMANY, "%,.2f MT", valor);
    }

    private static Font fonte(float tamanho, int estilo, BaseColor cor) {
        return FontFactory.getFont(FontFactory.HELVETICA, tamanho, estilo, cor);
    }
}
