package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import util.GeradorRelatorioPDF;

import model.vo.Utilizador;

/**
 * Visualização em Java Swing fiel ao design HTML "Relatório completo em PDF (exemplo)" (RelatorioPDF.dc.html),
 * com suporte a exportação real para PDF via iText 5 (lib/itextpdf-5.5.13.4.jar).
 */
public class RelatorioPdfPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    // Dimensões e cores exatas de RelatorioPDF.dc.html
    private static final int LARGURA_PAGINA = 794;
    private static final int ALTURA_PAGINA = 1123;

    private static final Color PG_BG = Color.WHITE;                         // #FFFFFF
    private static final Color PG_TEXT = new Color(0x1B, 0x20, 0x30);       // #1B2030 (body / th background)
    private static final Color PG_SUB = new Color(0x5A, 0x64, 0x78);        // #5A6478 (.sub / .kp small / .ft)
    private static final Color PG_BRAND = new Color(0x6B, 0x4F, 0x00);      // #6B4F00 (.br / .ch.y color)
    private static final Color PG_GOLD = new Color(0xF5, 0xB6, 0x38);       // #F5B638 (.hd border / h2 border-left)
    private static final Color PG_KPI_BORDER = new Color(0xDD, 0xE2, 0xEA); // #DDE2EA (.kp div border / .ft border)
    private static final Color PG_TD_BORDER = new Color(0xE6, 0xE9, 0xF0);  // #E6E9F0 (td border-bottom)
    private static final Color PG_ZEBRA = new Color(0xF5, 0xF6, 0xF9);      // #F5F6F9 (tr:nth-child(even) td)
    private static final Color PG_TOT_BG = new Color(0xFF, 0xF8, 0xE1);     // #FFF8E1 (.tot td)

    // Chips do relatório PDF (.ch)
    private static final Color CH_Y_BG = new Color(0xFF, 0xF3, 0xCD);       // #FFF3CD
    private static final Color CH_Y_FG = new Color(0x6B, 0x4F, 0x00);       // #6B4F00
    private static final Color CH_G_BG = new Color(0xD9, 0xF2, 0xE3);       // #D9F2E3
    private static final Color CH_G_FG = new Color(0x0F, 0x5B, 0x34);       // #0F5B34
    private static final Color CH_R_BG = new Color(0xFB, 0xDA, 0xDA);       // #FBDADA
    private static final Color CH_R_FG = new Color(0x8F, 0x1A, 0x1A);       // #8F1A1A
    private static final Color CH_B_BG = new Color(0xDC, 0xE9, 0xFB);       // #DCE9FB
    private static final Color CH_B_FG = new Color(0x1A, 0x4A, 0x93);       // #1A4A93

    public RelatorioPdfPanel() {
        this(true);
    }

    public RelatorioPdfPanel(boolean exibirBarraExportacao) {
        UiStyle.instalarTemaEscuro();
        setLayout(new BorderLayout(0, 12));
        setBackground(UiStyle.BACKGROUND);
        setBorder(new EmptyBorder(16, 28, 16, 28));

        if (exibirBarraExportacao) {
            add(criarBarraTopo(), BorderLayout.NORTH);
        }

        JPanel paginaA4 = criarPaginaA4();

        JPanel wrapperCentral = new JPanel(new GridBagLayout());
        wrapperCentral.setBackground(UiStyle.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        wrapperCentral.add(paginaA4, gbc);

        JScrollPane scroll = new JScrollPane(wrapperCentral);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UiStyle.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarBarraTopo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);

        JLabel titulo = new JLabel("Pré-visualização — Relatório Completo (PDF)");
        UiStyle.aplicarTexto(titulo, 18, true);
        barra.add(titulo, BorderLayout.WEST);

        JButton botaoVoltar = new JButton("← Voltar aos relatórios");
        botaoVoltar.setFont(UiStyle.fonteSemiBold(13));
        botaoVoltar.setBackground(UiStyle.SECONDARY_BG);
        botaoVoltar.setForeground(UiStyle.TEXT);
        botaoVoltar.setFocusPainted(false);
        botaoVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoVoltar.setBorder(new EmptyBorder(9, 16, 9, 16));
        botaoVoltar.addActionListener(e -> {
            Component root = SwingUtilities.getWindowAncestor(this);
            if (root instanceof MainFrame) {
                ((MainFrame) root).mostrarPainel(MainFrame.PAINEL_RELATORIOS);
            }
        });

        JButton botaoExportar = new JButton("Exportar PDF");
        botaoExportar.setFont(UiStyle.fonteSemiBold(13));
        botaoExportar.setBackground(UiStyle.ACCENT);
        botaoExportar.setForeground(UiStyle.ACCENT_TEXT);
        botaoExportar.setFocusPainted(false);
        botaoExportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoExportar.setBorder(new EmptyBorder(9, 16, 9, 16));
        botaoExportar.addActionListener(e -> exportarArquivoPdf());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(botaoVoltar);
        direita.add(botaoExportar);
        barra.add(direita, BorderLayout.EAST);
        return barra;
    }

    /**
     * .pg { width:794px; height:1123px; background:#fff; padding:40px 44px;
     *       display:flex; flex-direction:column; gap:14px; position:relative }
     */
    public JPanel criarPaginaA4() {
        JPanel pg = new JPanel(new BorderLayout());
        pg.setBackground(PG_BG);
        pg.setBorder(new EmptyBorder(40, 44, 26, 44));
        Dimension dimA4 = new Dimension(LARGURA_PAGINA, ALTURA_PAGINA);
        pg.setPreferredSize(dimA4);
        pg.setMinimumSize(dimA4);
        pg.setMaximumSize(dimA4);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        int larguraConteudo = LARGURA_PAGINA - 88; // 706px (794 - 44*2)

        // 1. Cabeçalho (.hd)
        JPanel hd = criarCabecalhoPagina(larguraConteudo);
        corpo.add(hd);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        // 2. KPIs (.kp)
        JPanel kp = criarLinhaKpis(larguraConteudo);
        corpo.add(kp);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        // 3. Primeira grade .g2: Vendas por estado | Mais vendidos
        JPanel grade1 = new JPanel(new GridLayout(1, 2, 14, 0));
        grade1.setOpaque(false);
        grade1.add(criarSecaoTabela(
                "Vendas por estado",
                new String[] {"Estado", "Qtd", "Total"},
                new Object[][] {
                    {"PENDENTE", "1", "1.250,00 MT"},
                    {"CONCLUÍDA", "1", "840,00 MT"},
                    {"ANULADA", "1", "300,00 MT"},
                    {"REEMBOLSADA", "1", "2.100,00 MT"},
                    {"Total", "4", "4.490,00 MT"}
                },
                true,
                true
        ));
        grade1.add(criarSecaoTabela(
                "Mais vendidos",
                new String[] {"#", "Produto", "Qtd"},
                new Object[][] {
                    {"1", "Pão", "320"},
                    {"2", "Leite 1L", "210"},
                    {"3", "Água 1,5L", "180"},
                    {"4", "Arroz 25kg", "35"}
                },
                false,
                false
        ));
        fixarDimensaoBloco(grade1, larguraConteudo, 178);
        corpo.add(grade1);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        // 4. Segunda grade .g2: Menos vendidos | Estoque baixo
        JPanel grade2 = new JPanel(new GridLayout(1, 2, 14, 0));
        grade2.setOpaque(false);
        grade2.add(criarSecaoTabela(
                "Menos vendidos",
                new String[] {"#", "Produto", "Qtd"},
                new Object[][] {
                    {"1", "Biscoito", "0"},
                    {"2", "Óleo 5L", "4"},
                    {"3", "Sal 1kg", "5"},
                    {"4", "Açúcar 1kg", "7"}
                },
                false,
                false
        ));
        grade2.add(criarSecaoTabela(
                "Estoque baixo",
                new String[] {"Produto", "Actual", "Mínimo"},
                new Object[][] {
                    {"Arroz 25kg", "4", "10"},
                    {"Óleo 5L", "2", "8"},
                    {"Açúcar 1kg", "6", "15"},
                    {"Sal 1kg", "1", "5"}
                },
                false,
                false
        ));
        fixarDimensaoBloco(grade2, larguraConteudo, 154);
        corpo.add(grade2);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        // 5. Seção Reembolsos
        JPanel secaoReembolsos = criarSecaoTabela(
                "Reembolsos",
                new String[] {"Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"},
                new Object[][] {
                    {"03/11/2026", "1039", "Leite 1L", "1", "85,00 MT", "Embalagem danificada", "Sara"},
                    {"02/11/2026", "1039", "Pão", "4", "20,00 MT", "Fora do prazo", "Sara"},
                    {"Total", "", "", "5", "105,00 MT", "", ""}
                },
                false,
                true
        );
        fixarDimensaoBloco(secaoReembolsos, larguraConteudo, 130);
        corpo.add(secaoReembolsos);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        // 6. Seção Resumo de auditoria
        JPanel secaoAuditoria = criarSecaoTabela(
                "Resumo de auditoria",
                new String[] {"Dia", "Tabela afectada", "Operação", "Registos"},
                new Object[][] {
                    {"03/11/2026", "Vendas", "Registo criado", "12"},
                    {"03/11/2026", "Estoque", "Registo alterado", "8"},
                    {"02/11/2026", "Produtos", "Registo alterado", "3"},
                    {"02/11/2026", "Reembolsos", "Registo criado", "2"}
                },
                false,
                false
        );
        fixarDimensaoBloco(secaoAuditoria, larguraConteudo, 154);
        corpo.add(secaoAuditoria);

        pg.add(corpo, BorderLayout.NORTH);
        pg.add(criarRodapePagina(larguraConteudo), BorderLayout.SOUTH);
        return pg;
    }

    /**
     * .hd { display:flex; justify-content:space-between; align-items:flex-end;
     *       border-bottom:3px solid #F5B638; padding-bottom:10px }
     */
    private JPanel criarCabecalhoPagina(int largura) {
        JPanel hd = new JPanel(new BorderLayout());
        hd.setOpaque(false);
        hd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, PG_GOLD),
                new EmptyBorder(0, 0, 10, 0)
        ));

        JPanel esq = new JPanel();
        esq.setOpaque(false);
        esq.setLayout(new BoxLayout(esq, BoxLayout.Y_AXIS));

        // .br { font-weight:700; font-size:12px; letter-spacing:1px; color:#6B4F00 }
        JLabel labelMarca = new JLabel("ESTABELECIMENTO");
        labelMarca.setFont(UiStyle.fonte(12, Font.BOLD));
        labelMarca.setForeground(PG_BRAND);

        // .tt { font-size:24px; font-weight:700; margin-top:2px }
        JLabel labelTitulo = new JLabel("Relatório Completo");
        labelTitulo.setFont(UiStyle.fonte(24, Font.BOLD));
        labelTitulo.setForeground(PG_TEXT);
        labelTitulo.setBorder(new EmptyBorder(2, 0, 0, 0));

        esq.add(labelMarca);
        esq.add(labelTitulo);

        // .sub { color:#5A6478; font-size:11px; text-align:right; line-height:1.5 }
        JLabel sub = new JLabel(
                "<html><div style='text-align:right;line-height:1.45'>Período: 01/11/2026 a 03/11/2026<br>Gerado em 03/11/2026 por admin</div></html>",
                SwingConstants.RIGHT
        );
        sub.setFont(UiStyle.fonte(11, Font.PLAIN));
        sub.setForeground(PG_SUB);
        sub.setVerticalAlignment(SwingConstants.BOTTOM);

        hd.add(esq, BorderLayout.WEST);
        hd.add(sub, BorderLayout.EAST);
        fixarDimensaoBloco(hd, largura, 62);
        return hd;
    }

    /**
     * .kp { display:flex; gap:8px }
     * .kp div { flex:1; border:1px solid #DDE2EA; border-radius:6px; padding:8px 10px }
     * .kp small { color:#5A6478; font-size:10px }
     * .kp b { display:block; font-size:17px; margin-top:2px }
     */
    private JPanel criarLinhaKpis(int largura) {
        JPanel kp = new JPanel(new GridLayout(1, 5, 8, 0));
        kp.setOpaque(false);
        kp.add(criarCardKpi("Vendas no período", "4"));
        kp.add(criarCardKpi("Total concluído", "840,00 MT"));
        kp.add(criarCardKpi("Total reembolsado", "105,00 MT"));
        kp.add(criarCardKpi("Estoque baixo", "4 produtos"));
        kp.add(criarCardKpi("A expirar (7 dias)", "5 produtos"));
        fixarDimensaoBloco(kp, largura, 56);
        return kp;
    }

    private JPanel criarCardKpi(String rotulo, String valor) {
        PainelKpiArredondado card = new PainelKpiArredondado();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel small = new JLabel(rotulo);
        small.setFont(UiStyle.fonte(10, Font.PLAIN));
        small.setForeground(PG_SUB);

        JLabel bold = new JLabel(valor);
        bold.setFont(UiStyle.fonte(17, Font.BOLD));
        bold.setForeground(PG_TEXT);
        bold.setBorder(new EmptyBorder(2, 0, 0, 0));

        card.add(small);
        card.add(bold);
        return card;
    }

    /**
     * h2 { font-size:12px; margin:0 0 5px; padding-left:7px; border-left:4px solid #F5B638 }
     */
    private JPanel criarSecaoTabela(
            String tituloSecao,
            String[] colunas,
            Object[][] dados,
            boolean primeiraColunaChipEstado,
            boolean ultimaLinhaTotal) {
        JPanel secao = new JPanel(new BorderLayout(0, 5));
        secao.setOpaque(false);

        JLabel h2 = new JLabel(tituloSecao);
        h2.setFont(UiStyle.fonte(12, Font.BOLD));
        h2.setForeground(PG_TEXT);
        h2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, PG_GOLD),
                new EmptyBorder(0, 7, 0, 0)
        ));
        secao.add(h2, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(UiStyle.fonte(11, Font.PLAIN));
        tabela.setBackground(PG_BG);
        tabela.setForeground(PG_TEXT);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setGridColor(PG_TD_BORDER);
        tabela.setIntercellSpacing(new Dimension(0, 1));
        tabela.setRowSelectionAllowed(false);

        JTableHeader header = tabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 24));
        header.setDefaultRenderer(new RenderizadorThPdf());

        RenderizadorTdPdf renderizadorTd = new RenderizadorTdPdf(primeiraColunaChipEstado, ultimaLinhaTotal);
        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorTd);
        }

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.add(header, BorderLayout.NORTH);
        tabelaWrapper.add(tabela, BorderLayout.CENTER);

        secao.add(tabelaWrapper, BorderLayout.CENTER);
        return secao;
    }

    /**
     * .ft { display:flex; justify-content:space-between; color:#5A6478; font-size:10px;
     *       border-top:1px solid #DDE2EA; padding-top:6px }
     */
    private JPanel criarRodapePagina(int largura) {
        JPanel ft = new JPanel(new BorderLayout());
        ft.setOpaque(false);
        ft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, PG_KPI_BORDER),
                new EmptyBorder(6, 0, 0, 0)
        ));

        JLabel esq = new JLabel("Estabelecimento · Relatório confidencial");
        esq.setFont(UiStyle.fonte(10, Font.PLAIN));
        esq.setForeground(PG_SUB);

        JLabel dir = new JLabel("Página 1 de 1");
        dir.setFont(UiStyle.fonte(10, Font.PLAIN));
        dir.setForeground(PG_SUB);

        ft.add(esq, BorderLayout.WEST);
        ft.add(dir, BorderLayout.EAST);
        fixarDimensaoBloco(ft, largura, 22);
        return ft;
    }

    private void fixarDimensaoBloco(JComponent comp, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void exportarArquivoPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Relatório Completo em PDF");
        chooser.setSelectedFile(new File("RelatorioCompleto.pdf"));
        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File destino = chooser.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".pdf")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
            }
            try {
                GeradorRelatorioPDF.gerar(destino);
                JOptionPane.showMessageDialog(
                        this,
                        "Relatório PDF gerado com sucesso em:\n" + destino.getAbsolutePath(),
                        "PDF Exportado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível gerar o PDF: " + ex.getMessage(),
                        "Erro ao exportar PDF",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public static void abrirJanelaIsolada() {
        SwingUtilities.invokeLater(() -> {
            UiStyle.instalarTemaEscuro();
            JFrame frame = new JFrame("Relatório PDF — Estabelecimento");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(920, 820);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new RelatorioPdfPanel(true));
            frame.setVisible(true);
        });
    }

    /**
     * .kp div { border:1px solid #DDE2EA; border-radius:6px; padding:8px 10px }
     */
    private static final class PainelKpiArredondado extends JPanel {
        private static final long serialVersionUID = 1L;

        PainelKpiArredondado() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(PG_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 12, 12));
                g2.setColor(PG_KPI_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, 12, 12));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * th { background:#1B2030; color:#fff; text-align:left; padding:5px 7px; font-size:10px }
     */
    private static final class RenderizadorThPdf extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(true);
            label.setBackground(PG_TEXT);
            label.setForeground(Color.WHITE);
            label.setFont(UiStyle.fonte(10, Font.BOLD));
            label.setBorder(new EmptyBorder(5, 7, 5, 7));
            return label;
        }
    }

    /**
     * td { padding:5px 7px; border-bottom:1px solid #E6E9F0 }
     * tr:nth-child(even) td { background:#F5F6F9 }
     * .tot td { font-weight:700; background:#FFF8E1!important }
     */
    private static final class RenderizadorTdPdf extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        private final boolean primeiraColunaChip;
        private final boolean ultimaLinhaTotal;

        RenderizadorTdPdf(boolean primeiraColunaChip, boolean ultimaLinhaTotal) {
            this.primeiraColunaChip = primeiraColunaChip;
            this.ultimaLinhaTotal = ultimaLinhaTotal;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";
            boolean isTotalRow = ultimaLinhaTotal && (row == table.getRowCount() - 1);
            // Em CSS, tr:nth-child(even) considera o <tr> do cabeçalho como 1º filho,
            // logo a 1ª linha de dados (row 0) é o 2º filho (even -> #F5F6F9)
            Color corFundoLinha = isTotalRow
                    ? PG_TOT_BG
                    : ((row % 2 == 0) ? PG_ZEBRA : PG_BG);

            if (primeiraColunaChip && column == 0 && !isTotalRow) {
                Color bgChip;
                Color fgChip;
                if ("PENDENTE".equalsIgnoreCase(texto)) {
                    bgChip = CH_Y_BG;
                    fgChip = CH_Y_FG;
                } else if ("CONCLUÍDA".equalsIgnoreCase(texto) || "CONCLUIDA".equalsIgnoreCase(texto)) {
                    bgChip = CH_G_BG;
                    fgChip = CH_G_FG;
                } else if ("ANULADA".equalsIgnoreCase(texto)) {
                    bgChip = CH_R_BG;
                    fgChip = CH_R_FG;
                } else {
                    bgChip = CH_B_BG;
                    fgChip = CH_B_FG;
                }

                JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                container.setOpaque(true);
                container.setBackground(corFundoLinha);
                container.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, PG_TD_BORDER),
                        new EmptyBorder(4, 7, 4, 7)
                ));
                container.add(new ChipPdf(texto, bgChip, fgChip));
                return container;
            }

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(true);
            label.setBackground(corFundoLinha);
            label.setForeground(PG_TEXT);
            label.setFont(UiStyle.fonte(11, isTotalRow ? Font.BOLD : Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, PG_TD_BORDER),
                    new EmptyBorder(5, 7, 5, 7)
            ));
            return label;
        }
    }

    /**
     * .ch { padding:1px 7px; border-radius:99px; font-size:9px; font-weight:700 }
     */
    private static final class ChipPdf extends JLabel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;

        ChipPdf(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonte(9, Font.BOLD));
            setForeground(corTexto);
            setBorder(new EmptyBorder(1, 7, 1, 7));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    @Override
    public void recarregarDados() {
        // Pré-visualização atualizada
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        // Respeita regras de visualização
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        repaint();
    }
}
