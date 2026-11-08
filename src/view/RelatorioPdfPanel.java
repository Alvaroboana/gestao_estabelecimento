package view;

import controller.RelatorioController;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import model.vo.SessaoUtilizador;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import util.AsyncTask;
import util.GeradorRelatorioPDF;

import model.vo.Utilizador;

public class RelatorioPdfPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    private static final int LARGURA_PAGINA = 794;
    private static final int ALTURA_PAGINA = 1123;

    private static final Color PG_BG = Color.WHITE;
    private static final Color PG_TEXT = new Color(0x1B, 0x20, 0x30);
    private static final Color PG_SUB = new Color(0x5A, 0x64, 0x78);
    private static final Color PG_BRAND = new Color(0x6B, 0x4F, 0x00);
    private static final Color PG_GOLD = new Color(0xF5, 0xB6, 0x38);
    private static final Color PG_KPI_BORDER = new Color(0xDD, 0xE2, 0xEA);
    private static final Color PG_TD_BORDER = new Color(0xE6, 0xE9, 0xF0);
    private static final Color PG_ZEBRA = new Color(0xF5, 0xF6, 0xF9);
    private static final Color PG_TOT_BG = new Color(0xFF, 0xF8, 0xE1);

    private static final Color CH_Y_BG = new Color(0xFF, 0xF3, 0xCD);
    private static final Color CH_Y_FG = new Color(0x6B, 0x4F, 0x00);
    private static final Color CH_G_BG = new Color(0xD9, 0xF2, 0xE3);
    private static final Color CH_G_FG = new Color(0x0F, 0x5B, 0x34);
    private static final Color CH_R_BG = new Color(0xFB, 0xDA, 0xDA);
    private static final Color CH_R_FG = new Color(0x8F, 0x1A, 0x1A);
    private static final Color CH_B_BG = new Color(0xDC, 0xE9, 0xFB);
    private static final Color CH_B_FG = new Color(0x1A, 0x4A, 0x93);

    private static final Color PAINEL_BG = new Color(0xF2, 0xF4, 0xF8);
    private static final Color PAINEL_TITULO_FG = new Color(0x1B, 0x20, 0x30);
    private static final Color BOTAO_VOLTAR_BG = Color.WHITE;
    private static final Color BOTAO_VOLTAR_FG = new Color(0x1B, 0x20, 0x30);
    private static final Color BOTAO_VOLTAR_BORDA = new Color(0xDD, 0xE2, 0xEA);

    private JLabel labelSubCabecalho;
    private JLabel labelKpiVendasPeriodo;
    private JLabel labelKpiTotalConcluido;
    private JLabel labelKpiTotalReembolsado;
    private JLabel labelKpiEstoqueBaixo;
    private JLabel labelKpiAExpirar;
    private DefaultTableModel modeloVendasEstado;
    private DefaultTableModel modeloMaisVendidos;
    private DefaultTableModel modeloMenosVendidos;
    private DefaultTableModel modeloEstoqueBaixo;
    private DefaultTableModel modeloReembolsos;
    private DefaultTableModel modeloAuditoria;
    private JPanel barraTopo;
    private JPanel wrapperCentral;
    private JScrollPane scrollPagina;
    private final RelatorioController relatorioController = new RelatorioController();

    public RelatorioPdfPanel() {
        this(true);
    }

    public RelatorioPdfPanel(boolean exibirBarraExportacao) {
        putClientProperty("temaFixoClaro", Boolean.TRUE);
        setLayout(new BorderLayout(0, 12));
        setBackground(PAINEL_BG);
        setBorder(new EmptyBorder(16, 28, 16, 28));

        if (exibirBarraExportacao) {
            this.barraTopo = criarBarraTopo();
            add(this.barraTopo, BorderLayout.NORTH);
        }

        JPanel paginaA4 = criarPaginaA4();

        this.wrapperCentral = new JPanel(new GridBagLayout());
        this.wrapperCentral.setBackground(PAINEL_BG);
        this.wrapperCentral.putClientProperty("temaFixoClaro", Boolean.TRUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        this.wrapperCentral.add(paginaA4, gbc);

        this.scrollPagina = new JScrollPane(this.wrapperCentral);
        this.scrollPagina.setBorder(BorderFactory.createEmptyBorder());
        this.scrollPagina.setBackground(PAINEL_BG);
        this.scrollPagina.getViewport().setBackground(PAINEL_BG);
        this.scrollPagina.getVerticalScrollBar().setUnitIncrement(18);
        this.scrollPagina.putClientProperty("temaFixoClaro", Boolean.TRUE);
        add(this.scrollPagina, BorderLayout.CENTER);
        recarregarDados();
    }

    private JPanel criarBarraTopo() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setOpaque(false);
        barra.putClientProperty("temaFixoClaro", Boolean.TRUE);

        JLabel titulo = new JLabel("Pré-visualização — Relatório Completo (PDF)");
        titulo.setFont(UiStyle.fonte(18, Font.BOLD));
        titulo.setForeground(PAINEL_TITULO_FG);
        barra.add(titulo, BorderLayout.WEST);

        JButton botaoVoltar = new JButton("Voltar aos relatórios");
        botaoVoltar.setFont(UiStyle.fonteSemiBold(13));
        botaoVoltar.setBackground(BOTAO_VOLTAR_BG);
        botaoVoltar.setForeground(BOTAO_VOLTAR_FG);
        botaoVoltar.setIcon(UiStyle.icone(MaterialDesignA.ARROW_LEFT, 15, BOTAO_VOLTAR_FG));
        botaoVoltar.setIconTextGap(6);
        botaoVoltar.setFocusPainted(false);
        botaoVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoVoltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BOTAO_VOLTAR_BORDA, 1, true),
                new EmptyBorder(9, 16, 9, 16)
        ));
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
        botaoExportar.setIcon(UiStyle.icone(MaterialDesignF.FILE_PDF_BOX, 15, UiStyle.ACCENT_TEXT));
        botaoExportar.setIconTextGap(6);
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

    public JPanel criarPaginaA4() {
        JPanel pg = new JPanel(new BorderLayout());
        pg.setBackground(PG_BG);
        pg.putClientProperty("temaFixoClaro", Boolean.TRUE);
        pg.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDD, 0xE2, 0xEA), 1),
                new EmptyBorder(40, 44, 26, 44)
        ));
        Dimension dimA4 = new Dimension(LARGURA_PAGINA, ALTURA_PAGINA);
        pg.setPreferredSize(dimA4);
        pg.setMinimumSize(dimA4);
        pg.setMaximumSize(dimA4);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        int larguraConteudo = LARGURA_PAGINA - 88;

        JPanel hd = criarCabecalhoPagina(larguraConteudo);
        corpo.add(hd);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel kp = criarLinhaKpis(larguraConteudo);
        corpo.add(kp);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        modeloVendasEstado = criarModelo(new String[] {"Estado", "Qtd", "Total"});
        modeloMaisVendidos = criarModelo(new String[] {"#", "Produto", "Qtd"});
        JPanel grade1 = new JPanel(new GridLayout(1, 2, 14, 0));
        grade1.setOpaque(false);
        grade1.add(criarSecaoTabela("Vendas por estado", modeloVendasEstado, true, true));
        grade1.add(criarSecaoTabela("Mais vendidos", modeloMaisVendidos, false, false));
        fixarDimensaoBloco(grade1, larguraConteudo, 178);
        corpo.add(grade1);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        modeloMenosVendidos = criarModelo(new String[] {"#", "Produto", "Qtd"});
        modeloEstoqueBaixo = criarModelo(new String[] {"Produto", "Actual", "Mínimo"});
        JPanel grade2 = new JPanel(new GridLayout(1, 2, 14, 0));
        grade2.setOpaque(false);
        grade2.add(criarSecaoTabela("Menos vendidos", modeloMenosVendidos, false, false));
        grade2.add(criarSecaoTabela("Estoque baixo", modeloEstoqueBaixo, false, false));
        fixarDimensaoBloco(grade2, larguraConteudo, 154);
        corpo.add(grade2);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        modeloReembolsos = criarModelo(new String[] {"Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"});
        JPanel secaoReembolsos = criarSecaoTabela("Reembolsos", modeloReembolsos, false, true);
        fixarDimensaoBloco(secaoReembolsos, larguraConteudo, 130);
        corpo.add(secaoReembolsos);
        corpo.add(Box.createRigidArea(new Dimension(0, 14)));

        modeloAuditoria = criarModelo(new String[] {"Dia", "Tabela afectada", "Operação", "Registos"});
        JPanel secaoAuditoria = criarSecaoTabela("Resumo de auditoria", modeloAuditoria, false, false);
        fixarDimensaoBloco(secaoAuditoria, larguraConteudo, 154);
        corpo.add(secaoAuditoria);

        pg.add(corpo, BorderLayout.NORTH);
        pg.add(criarRodapePagina(larguraConteudo), BorderLayout.SOUTH);
        return pg;
    }

    private DefaultTableModel criarModelo(String[] colunas) {
        return new DefaultTableModel(new Object[0][colunas.length], colunas) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

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

        JLabel labelMarca = new JLabel("ESTABELECIMENTO");
        labelMarca.setFont(UiStyle.fonte(12, Font.BOLD));
        labelMarca.setForeground(PG_BRAND);

        JLabel labelTitulo = new JLabel("Relatório Completo");
        labelTitulo.setFont(UiStyle.fonte(24, Font.BOLD));
        labelTitulo.setForeground(PG_TEXT);
        labelTitulo.setBorder(new EmptyBorder(2, 0, 0, 0));

        esq.add(labelMarca);
        esq.add(labelTitulo);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String hojeStr = LocalDate.now().format(dtf);
        String inicioStr = LocalDate.now().minusDays(30).format(dtf);
        labelSubCabecalho = new JLabel(
                "<html><div style='text-align:right;line-height:1.45'>Período: " + inicioStr + " a " + hojeStr
                        + "<br>Gerado em " + hojeStr + " por admin</div></html>",
                SwingConstants.RIGHT
        );
        labelSubCabecalho.setFont(UiStyle.fonte(11, Font.PLAIN));
        labelSubCabecalho.setForeground(PG_SUB);
        labelSubCabecalho.setVerticalAlignment(SwingConstants.BOTTOM);

        hd.add(esq, BorderLayout.WEST);
        hd.add(labelSubCabecalho, BorderLayout.EAST);
        fixarDimensaoBloco(hd, largura, 62);
        return hd;
    }

    private JPanel criarLinhaKpis(int largura) {
        JPanel kp = new JPanel(new GridLayout(1, 5, 8, 0));
        kp.setOpaque(false);
        labelKpiVendasPeriodo = new JLabel("0");
        labelKpiTotalConcluido = new JLabel("0,00 MT");
        labelKpiTotalReembolsado = new JLabel("0,00 MT");
        labelKpiEstoqueBaixo = new JLabel("0 produtos");
        labelKpiAExpirar = new JLabel("0 lotes");

        kp.add(criarCardKpi("Vendas no período", labelKpiVendasPeriodo));
        kp.add(criarCardKpi("Total concluído", labelKpiTotalConcluido));
        kp.add(criarCardKpi("Total reembolsado", labelKpiTotalReembolsado));
        kp.add(criarCardKpi("Estoque baixo", labelKpiEstoqueBaixo));
        kp.add(criarCardKpi("A expirar (7 dias)", labelKpiAExpirar));
        fixarDimensaoBloco(kp, largura, 56);
        return kp;
    }

    private JPanel criarCardKpi(String rotulo, JLabel bold) {
        PainelKpiArredondado card = new PainelKpiArredondado();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel small = new JLabel(rotulo);
        small.setFont(UiStyle.fonte(10, Font.PLAIN));
        small.setForeground(PG_SUB);

        bold.setFont(UiStyle.fonte(17, Font.BOLD));
        bold.setForeground(PG_TEXT);
        bold.setBorder(new EmptyBorder(2, 0, 0, 0));

        card.add(small);
        card.add(bold);
        return card;
    }

    private JPanel criarSecaoTabela(
            String tituloSecao,
            DefaultTableModel modelo,
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
            JFrame frame = new JFrame("Relatório PDF — Estabelecimento");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(920, 820);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(PAINEL_BG);
            frame.setContentPane(new RelatorioPdfPanel(true));
            frame.setVisible(true);
        });
    }

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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String hojeStr = LocalDate.now().format(dtf);
        String inicioStr = LocalDate.now().minusDays(30).format(dtf);
        String userStr = (SessaoUtilizador.getUtilizador() != null && SessaoUtilizador.getUtilizador().getUsername() != null)
                ? SessaoUtilizador.getUtilizador().getUsername() : "admin";
        if (labelSubCabecalho != null) {
            labelSubCabecalho.setText("<html><div style='text-align:right;line-height:1.45'>Período: "
                    + inicioStr + " a " + hojeStr + "<br>Gerado em " + hojeStr + " por " + userStr + "</div></html>");
        }

        final LocalDateTime inicio = LocalDate.now().minusDays(30).atStartOfDay();
        final LocalDateTime fim = LocalDateTime.now().plusDays(1);
        AsyncTask.executar(
            () -> {
                Map<String, Integer> kpis = relatorioController.obterIndicadoresDashboard();
                List<Map<String, Object>> resumoEstado = relatorioController.obterResumoVendasPorEstadoRelatorio(inicio, fim);
                List<Map<String, Object>> maisVend = relatorioController.obterProdutosMaisVendidosDashboard();
                List<Map<String, Object>> estBaixo = relatorioController.obterEstoqueBaixoDashboard();
                List<Map<String, Object>> reemb = relatorioController.obterReembolsosPeriodoRelatorio(inicio, fim);
                List<Map<String, Object>> audit = relatorioController.obterResumoAuditoriaRelatorio(inicio, fim);
                return new Object[] { kpis, resumoEstado, maisVend, estBaixo, reemb, audit };
            },
            res -> {
                Object[] arr = (Object[]) res;
                @SuppressWarnings("unchecked")
                Map<String, Integer> kpis = (Map<String, Integer>) arr[0];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> resumoEstado = (List<Map<String, Object>>) arr[1];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> maisVend = (List<Map<String, Object>>) arr[2];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> estBaixo = (List<Map<String, Object>>) arr[3];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> reemb = (List<Map<String, Object>>) arr[4];
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> audit = (List<Map<String, Object>>) arr[5];

                int totalQtdVendas = 0;
                double somaTotalVendas = 0.0;
                double totalConcluido = 0.0;
                if (modeloVendasEstado != null) {
                    modeloVendasEstado.setRowCount(0);
                    if (resumoEstado != null) {
                        for (Map<String, Object> m : resumoEstado) {
                            String est = String.valueOf(m.getOrDefault("estado", ""));
                            int q = ((Number) m.getOrDefault("quantidade", 0)).intValue();
                            double t = ((Number) m.getOrDefault("total", 0.0)).doubleValue();
                            totalQtdVendas += q;
                            somaTotalVendas += t;
                            if ("CONCLUIDA".equalsIgnoreCase(est)) {
                                totalConcluido += t;
                            }
                            modeloVendasEstado.addRow(new Object[] {est, String.valueOf(q), String.format(Locale.GERMANY, "%,.2f MT", t)});
                        }
                        if (!resumoEstado.isEmpty()) {
                            modeloVendasEstado.addRow(new Object[] {"Total", String.valueOf(totalQtdVendas), String.format(Locale.GERMANY, "%,.2f MT", somaTotalVendas)});
                        }
                    }
                }

                if (modeloMaisVendidos != null) {
                    modeloMaisVendidos.setRowCount(0);
                    if (maisVend != null) {
                        for (int i = 0; i < Math.min(4, maisVend.size()); i++) {
                            Map<String, Object> m = maisVend.get(i);
                            modeloMaisVendidos.addRow(new Object[] {
                                String.valueOf(i + 1),
                                String.valueOf(m.getOrDefault("nome_produto", "-")),
                                String.valueOf(m.getOrDefault("quantidade_vendida", 0))
                            });
                        }
                    }
                }

                if (modeloMenosVendidos != null) {
                    modeloMenosVendidos.setRowCount(0);
                    if (maisVend != null) {
                        List<Map<String, Object>> menos = new ArrayList<>(maisVend);
                        Collections.sort(menos, Comparator.comparingDouble(o -> {
                            try { return Double.parseDouble(String.valueOf(o.getOrDefault("quantidade_vendida", 0))); }
                            catch (Exception e) { return 0.0; }
                        }));
                        for (int i = 0; i < Math.min(4, menos.size()); i++) {
                            Map<String, Object> m = menos.get(i);
                            modeloMenosVendidos.addRow(new Object[] {
                                String.valueOf(i + 1),
                                String.valueOf(m.getOrDefault("nome_produto", "-")),
                                String.valueOf(m.getOrDefault("quantidade_vendida", 0))
                            });
                        }
                    }
                }

                if (modeloEstoqueBaixo != null) {
                    modeloEstoqueBaixo.setRowCount(0);
                    if (estBaixo != null) {
                        for (int i = 0; i < Math.min(4, estBaixo.size()); i++) {
                            Map<String, Object> m = estBaixo.get(i);
                            modeloEstoqueBaixo.addRow(new Object[] {
                                String.valueOf(m.getOrDefault("nome_produto", "-")),
                                String.valueOf(m.getOrDefault("quantidade_atual", 0)),
                                String.valueOf(m.getOrDefault("quantidade_minima", 0))
                            });
                        }
                    }
                }

                double totalReembValor = 0.0;
                int totalReembQtd = 0;
                if (modeloReembolsos != null) {
                    modeloReembolsos.setRowCount(0);
                    if (reemb != null) {
                        for (int i = 0; i < Math.min(4, reemb.size()); i++) {
                            Map<String, Object> r = reemb.get(i);
                            String d = String.valueOf(r.getOrDefault("data_reembolso", "-"));
                            if (d.length() >= 10) d = d.substring(0, 10);
                            int q = ((Number) r.getOrDefault("quantidade", 0)).intValue();
                            double v = ((Number) r.getOrDefault("valor", 0.0)).doubleValue();
                            totalReembQtd += q;
                            totalReembValor += v;
                            modeloReembolsos.addRow(new Object[] {
                                d,
                                String.valueOf(r.getOrDefault("id_venda", "-")),
                                String.valueOf(r.getOrDefault("nome_produto", "-")),
                                String.valueOf(q),
                                String.format(Locale.GERMANY, "%,.2f MT", v),
                                String.valueOf(r.getOrDefault("motivo", "-")),
                                String.valueOf(r.getOrDefault("nome_funcionario", "-"))
                            });
                        }
                        if (!reemb.isEmpty()) {
                            modeloReembolsos.addRow(new Object[] {
                                "Total", "", "", String.valueOf(totalReembQtd),
                                String.format(Locale.GERMANY, "%,.2f MT", totalReembValor), "", ""
                            });
                        }
                    }
                }

                if (modeloAuditoria != null) {
                    modeloAuditoria.setRowCount(0);
                    if (audit != null) {
                        for (int i = 0; i < Math.min(4, audit.size()); i++) {
                            Map<String, Object> a = audit.get(i);
                            modeloAuditoria.addRow(new Object[] {
                                String.valueOf(a.getOrDefault("dia", "-")),
                                String.valueOf(a.getOrDefault("tabela_afetada", "-")),
                                String.valueOf(a.getOrDefault("operacao", "-")),
                                String.valueOf(a.getOrDefault("quantidade", 0))
                            });
                        }
                    }
                }

                if (labelKpiVendasPeriodo != null) labelKpiVendasPeriodo.setText(String.valueOf(totalQtdVendas));
                if (labelKpiTotalConcluido != null) labelKpiTotalConcluido.setText(String.format(Locale.GERMANY, "%,.2f MT", totalConcluido));
                if (labelKpiTotalReembolsado != null) labelKpiTotalReembolsado.setText(String.format(Locale.GERMANY, "%,.2f MT", totalReembValor));
                if (labelKpiEstoqueBaixo != null) labelKpiEstoqueBaixo.setText((estBaixo != null ? estBaixo.size() : 0) + " produtos");
                if (labelKpiAExpirar != null) {
                    int exp = (kpis != null) ? kpis.getOrDefault("lotesAExpirar", 0) : 0;
                    labelKpiAExpirar.setText(exp + " lotes");
                }
            },
            erro -> {}
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
    }

    @Override
    public void aplicarTema() {
        setBackground(PAINEL_BG);
        if (wrapperCentral != null) {
            wrapperCentral.setBackground(PAINEL_BG);
        }
        if (scrollPagina != null && scrollPagina.getViewport() != null) {
            scrollPagina.getViewport().setBackground(PAINEL_BG);
            scrollPagina.setBackground(PAINEL_BG);
        }
        repaint();
    }
}
