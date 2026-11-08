package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import controller.RelatorioController;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;
import util.AsyncTask;

public class RelatoriosPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum AbaRelatorio {
        COMPLETO("Completo"),
        VENDAS("Vendas"),
        ESTOQUE_BAIXO("Estoque baixo"),
        MAIS_VENDIDOS("Mais vendidos"),
        MENOS_VENDIDOS("Menos vendidos"),
        REEMBOLSOS("Reembolsos"),
        AUDITORIA("Auditoria");

        private final String rotulo;

        AbaRelatorio(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    private static final String CARD_COMPLETO = "COMPLETO";
    private static final String CARD_VENDAS = "VENDAS";
    private static final String CARD_ESTOQUE_BAIXO = "ESTOQUE_BAIXO";
    private static final String CARD_MAIS_VENDIDOS = "MAIS_VENDIDOS";
    private static final String CARD_MENOS_VENDIDOS = "MENOS_VENDIDOS";
    private static final String CARD_REEMBOLSOS = "REEMBOLSOS";
    private static final String CARD_AUDITORIA = "AUDITORIA";

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_DETALHE_VENDA = 420;

    private static final String[] ABAS_RELATORIO = {
        "Completo",
        "Vendas",
        "Estoque baixo",
        "Mais vendidos",
        "Menos vendidos",
        "Reembolsos",
        "Auditoria"
    };

    private static final String[] COLUNAS_MAIS_VENDIDOS = {
        "#", "Produto", "Qtd vendida", "Receita"
    };

    private static final String[] COLUNAS_MENOS_VENDIDOS = {
        "#", "Produto", "Qtd vendida", "Receita"
    };

    private static final String[] COLUNAS_ESTOQUE_BAIXO = {
        "Produto", "Estoque actual", "Mínimo", "Em falta"
    };

    private static final String[] COLUNAS_REEMBOLSOS = {
        "Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"
    };

    private static final String[] COLUNAS_AUDITORIA = {
        "Dia", "Tabela afectada", "Operação", "Registos"
    };

    private static final String[] COLUNAS_VENDAS = {
        "ID", "Data", "Cliente", "Funcionário", "Total", "Estado"
    };

    private static final String[] COLUNAS_ITENS_VENDA_DETALHE = {
        "Item", "Qtd", "Preço", "Subtotal"
    };

    private final List<BotaoAbaPilula> botoesAbas = new ArrayList<>();
    private final CampoDataArredondado campoDataDe;
    private final CampoDataArredondado campoDataAte;
    private final JButton botaoGerarRelatorio;
    private final JButton botaoExportarPdf;
    private final JButton botaoImprimir;
    private final JButton botaoGerarSimples;
    private final JButton botaoExportarSimples;
    private final JButton botaoFecharDetalheVenda;
    private final JPanel containerBotoesFiltro;
    private final CardLayout layoutCorpo;
    private final JPanel containerCorpo;
    private final JTable tabelaVendas;
    private final JTable tabelaItensVendaDetalhe;
    private final JPanel wrapperDireitaVenda;
    private final JPanel cardDetalheVenda;
    private JLabel labelDetalheVendaTitulo;
    private JLabel labelDetalheVendaCliente;
    private ChipEstado chipDetalheVendaEstado;
    private JLabel labelDetalheVendaFuncionario;
    private JLabel labelDetalheVendaTotal;
    private final JTable tabelaEstoqueBaixo;
    private final JTable tabelaMaisVendidos;
    private final JTable tabelaMenosVendidos;
    private final JTable tabelaReembolsos;
    private final JTable tabelaAuditoria;
    private JLabel labelKpiVendasPeriodo;
    private JLabel labelKpiTotalConcluido;
    private JLabel labelKpiTotalReembolsado;
    private JLabel labelKpiEstoqueBaixo;
    private JLabel labelKpiProdutosExpirar;
    private JLabel labelKpiLotesExpirados;
    private DefaultTableModel modeloMiniVendasEstado;
    private DefaultTableModel modeloMiniMaisVendidos;
    private DefaultTableModel modeloMiniMenosVendidos;
    private DefaultTableModel modeloMiniEstoqueBaixo;
    private DefaultTableModel modeloMiniReembolsos;
    private DefaultTableModel modeloMiniAuditoria;
    private final RelatorioController relatorioController;
    private boolean detalheVendaVisivel = true;
    private String abaAtiva = "Completo";

    public RelatoriosPanel() {
        this(AbaRelatorio.COMPLETO);
    }

    public RelatoriosPanel(AbaRelatorio abaInicial) {
        UiStyle.instalarTemaEscuro();
        this.relatorioController = new RelatorioController();
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        DateTimeFormatter dtfPadrao = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.campoDataDe = new CampoDataArredondado("De: " + LocalDate.now().minusDays(30).format(dtfPadrao));
        this.campoDataAte = new CampoDataArredondado("Até: " + LocalDate.now().format(dtfPadrao));
        this.botaoGerarRelatorio = new BotaoToolbarArredondado(
                "Gerar relatório completo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoGerarRelatorio.setIcon(UiStyle.icone(MaterialDesignR.REFRESH, 15, UiStyle.ACCENT_TEXT));
        this.botaoGerarRelatorio.setIconTextGap(6);
        this.botaoGerarRelatorio.addActionListener(e -> carregarRelatorioAtual());
        this.botaoExportarPdf = criarBotaoExportarPdf();
        this.botaoImprimir = new BotaoToolbarArredondado(
                "Imprimir", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoImprimir.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PRINTER, 15, () -> UiStyle.TEXT));
        this.botaoImprimir.setIconTextGap(6);
        this.botaoImprimir.addActionListener(e -> imprimirRelatorioAtivo());
        this.botaoGerarSimples = new BotaoToolbarArredondado(
                "Gerar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoGerarSimples.setIcon(UiStyle.icone(MaterialDesignR.REFRESH, 15, UiStyle.ACCENT_TEXT));
        this.botaoGerarSimples.setIconTextGap(6);
        this.botaoGerarSimples.addActionListener(e -> carregarRelatorioAtual());
        this.botaoExportarSimples = new BotaoToolbarArredondado(
                "Exportar CSV", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoExportarSimples.setIcon(UiStyle.iconeDinamico(MaterialDesignF.FILE_DELIMITED, 15, () -> UiStyle.TEXT));
        this.botaoExportarSimples.setIconTextGap(6);
        this.botaoExportarSimples.addActionListener(e -> exportarCsvRelatorioAtivo());
        this.botaoFecharDetalheVenda = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoFecharDetalheVenda.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        this.botaoFecharDetalheVenda.setIconTextGap(6);
        this.botaoFecharDetalheVenda.addActionListener(e -> mostrarDetalheVenda(false));
        this.containerBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesFiltro.setOpaque(false);

        this.tabelaVendas = criarTabelaVendas();
        this.tabelaItensVendaDetalhe = criarTabelaItensVendaDetalhe();
        this.wrapperDireitaVenda = new JPanel(new BorderLayout());
        this.wrapperDireitaVenda.setOpaque(false);
        this.cardDetalheVenda = criarCardDetalheVenda();
        this.tabelaEstoqueBaixo = criarTabelaRanking(new Object[0][COLUNAS_ESTOQUE_BAIXO.length], COLUNAS_ESTOQUE_BAIXO);
        this.tabelaMaisVendidos = criarTabelaRanking(new Object[0][COLUNAS_MAIS_VENDIDOS.length], COLUNAS_MAIS_VENDIDOS);
        this.tabelaMenosVendidos = criarTabelaRanking(new Object[0][COLUNAS_MENOS_VENDIDOS.length], COLUNAS_MENOS_VENDIDOS);
        this.tabelaReembolsos = criarTabelaRanking(new Object[0][COLUNAS_REEMBOLSOS.length], COLUNAS_REEMBOLSOS);
        this.tabelaAuditoria = criarTabelaRanking(new Object[0][COLUNAS_AUDITORIA.length], COLUNAS_AUDITORIA);
        this.layoutCorpo = new CardLayout();
        this.containerCorpo = new JPanel(this.layoutCorpo);
        this.containerCorpo.setOpaque(false);
        this.containerCorpo.add(criarCorpoRelatorioCompleto(), CARD_COMPLETO);
        this.containerCorpo.add(criarCorpoVendas(), CARD_VENDAS);
        this.containerCorpo.add(criarCardTabelaRanking(this.tabelaEstoqueBaixo), CARD_ESTOQUE_BAIXO);
        this.containerCorpo.add(criarCardTabelaRanking(this.tabelaMaisVendidos), CARD_MAIS_VENDIDOS);
        this.containerCorpo.add(criarCardTabelaRanking(this.tabelaMenosVendidos), CARD_MENOS_VENDIDOS);
        this.containerCorpo.add(criarCardTabelaRanking(this.tabelaReembolsos), CARD_REEMBOLSOS);
        this.containerCorpo.add(criarCardTabelaRanking(this.tabelaAuditoria), CARD_AUDITORIA);

        add(criarTopo(), BorderLayout.NORTH);
        add(this.containerCorpo, BorderLayout.CENTER);

        mostrarDetalheVenda(true);
        AbaRelatorio inicial = abaInicial != null ? abaInicial : AbaRelatorio.COMPLETO;
        selecionarAba(inicial.getRotulo());
        carregarRelatorioAtual();
    }

    public String getAbaAtiva() {
        return abaAtiva;
    }

    public JTextField getCampoDataDe() {
        return campoDataDe;
    }

    public JTextField getCampoDataAte() {
        return campoDataAte;
    }

    public JButton getBotaoGerarRelatorio() {
        return botaoGerarRelatorio;
    }

    public JButton getBotaoExportarPdf() {
        return botaoExportarPdf;
    }

    public JButton getBotaoImprimir() {
        return botaoImprimir;
    }

    public JButton getBotaoGerarSimples() {
        return botaoGerarSimples;
    }

    public JButton getBotaoExportarSimples() {
        return botaoExportarSimples;
    }

    public JButton getBotaoFecharDetalheVenda() {
        return botaoFecharDetalheVenda;
    }

    public JTable getTabelaVendas() {
        return tabelaVendas;
    }

    public JTable getTabelaItensVendaDetalhe() {
        return tabelaItensVendaDetalhe;
    }

    public JPanel getCardDetalheVenda() {
        return cardDetalheVenda;
    }

    public boolean isDetalheVendaVisivel() {
        return detalheVendaVisivel;
    }

    public JTable getTabelaEstoqueBaixo() {
        return tabelaEstoqueBaixo;
    }

    public JTable getTabelaMaisVendidos() {
        return tabelaMaisVendidos;
    }

    public JTable getTabelaMenosVendidos() {
        return tabelaMenosVendidos;
    }

    public JTable getTabelaReembolsos() {
        return tabelaReembolsos;
    }

    public JTable getTabelaAuditoria() {
        return tabelaAuditoria;
    }

    public RelatorioController getRelatorioController() {
        return relatorioController;
    }

    public JTable obterTabelaAtiva() {
        if ("Vendas".equalsIgnoreCase(abaAtiva)) {
            return tabelaVendas;
        } else if ("Estoque baixo".equalsIgnoreCase(abaAtiva)) {
            return tabelaEstoqueBaixo;
        } else if ("Mais vendidos".equalsIgnoreCase(abaAtiva)) {
            return tabelaMaisVendidos;
        } else if ("Menos vendidos".equalsIgnoreCase(abaAtiva)) {
            return tabelaMenosVendidos;
        } else if ("Reembolsos".equalsIgnoreCase(abaAtiva)) {
            return tabelaReembolsos;
        } else if ("Auditoria".equalsIgnoreCase(abaAtiva)) {
            return tabelaAuditoria;
        } else {
            return tabelaVendas;
        }
    }

    public String obterTituloRelatorioAtivo() {
        if ("Vendas".equalsIgnoreCase(abaAtiva)) {
            return "Vendas";
        } else if ("Estoque baixo".equalsIgnoreCase(abaAtiva)) {
            return "Estoque Baixo";
        } else if ("Mais vendidos".equalsIgnoreCase(abaAtiva)) {
            return "Produtos Mais Vendidos";
        } else if ("Menos vendidos".equalsIgnoreCase(abaAtiva)) {
            return "Produtos Menos Vendidos";
        } else if ("Reembolsos".equalsIgnoreCase(abaAtiva)) {
            return "Reembolsos";
        } else if ("Auditoria".equalsIgnoreCase(abaAtiva)) {
            return "Auditoria";
        } else {
            return "Relatório Completo — Vendas";
        }
    }

    public void imprimirRelatorioAtivo() {
        JTable tabela = obterTabelaAtiva();
        if (tabela == null || tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não existem dados disponíveis na tabela para impressão.",
                    "Aviso de Impressão",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            boolean impresso = relatorioController.imprimirTabela(tabela, obterTituloRelatorioAtivo());
            if (impresso) {
                JOptionPane.showMessageDialog(
                        this,
                        "Relatório \"" + obterTituloRelatorioAtivo() + "\" enviado para impressão com sucesso.",
                        "Impressão",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível imprimir o relatório: " + ex.getMessage(),
                    "Erro de Impressão",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Acesso ao serviço de impressão bloqueado pelo sistema de segurança.",
                    "Permissão Negada",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Falha durante a impressão: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void exportarCsvRelatorioAtivo() {
        JTable tabela = obterTabelaAtiva();
        if (tabela == null || tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não existem dados disponíveis na tabela para exportação.",
                    "Aviso de Exportação",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar " + obterTituloRelatorioAtivo() + " em CSV");
        String nomeSugerido = "relatorio_" + normalizarNomeArquivo(abaAtiva) + "_" + LocalDate.now() + ".csv";
        chooser.setSelectedFile(new File(nomeSugerido));
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Ficheiros CSV (*.csv)", "csv");
        chooser.setFileFilter(filtro);

        int resultado = chooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File destino = chooser.getSelectedFile();
            if (!destino.getName().toLowerCase().endsWith(".csv")) {
                destino = new File(destino.getParentFile(), destino.getName() + ".csv");
            }

            if (destino.exists()) {
                int confirmacao = JOptionPane.showConfirmDialog(
                        this,
                        "O ficheiro já existe:\n" + destino.getName() + "\nDeseja substituí-lo?",
                        "Substituir Ficheiro",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirmacao != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                relatorioController.exportarCSV(tabela, destino);
                JOptionPane.showMessageDialog(
                        this,
                        "Relatório exportado com sucesso em:\n" + destino.getAbsolutePath(),
                        "Exportação Concluída",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Erro ao gravar o ficheiro CSV: " + ex.getMessage(),
                        "Erro de Gravação",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Falha inesperada ao exportar: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public void exportarCsv(File destino) throws IOException {
        JTable tabela = obterTabelaAtiva();
        if (tabela == null) {
            throw new IllegalStateException("Nenhuma tabela ativa disponível.");
        }
        relatorioController.exportarCSV(tabela, destino);
    }

    private String normalizarNomeArquivo(String nome) {
        if (nome == null) {
            return "relatorio";
        }
        return nome.toLowerCase()
                .replace(" ", "_")
                .replace("ã", "a")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ç", "c");
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Relatórios");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.add(titulo);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel barraAbas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                int larguraDisp = getWidth();
                if (larguraDisp <= 0 && getParent() != null) {
                    larguraDisp = getParent().getWidth();
                }
                if (larguraDisp <= 0) {
                    return super.getPreferredSize();
                }
                int maxW = Math.max(200, larguraDisp);
                int hgap = 8;
                int vgap = 6;
                int x = 0;
                int y = vgap;
                int rowH = 0;
                for (Component c : getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = c.getPreferredSize();
                    if (x > 0 && x + hgap + d.width > maxW) {
                        y += rowH + vgap;
                        x = 0;
                        rowH = 0;
                    }
                    if (x > 0) x += hgap;
                    x += d.width;
                    rowH = Math.max(rowH, d.height);
                }
                y += rowH + vgap;
                return new Dimension(larguraDisp, Math.max(34, y));
            }
        };
        barraAbas.setOpaque(false);
        barraAbas.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraAbas.setBorder(new EmptyBorder(-6, -8, -6, 0));

        for (int i = 0; i < ABAS_RELATORIO.length; i++) {
            String nomeAba = ABAS_RELATORIO[i];
            BotaoAbaPilula botaoAba = new BotaoAbaPilula(nomeAba, "Completo".equals(nomeAba));
            botaoAba.addActionListener(e -> selecionarAba(nomeAba));
            botoesAbas.add(botaoAba);
            barraAbas.add(botaoAba);
        }
        topo.add(barraAbas);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel barraFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                int larguraDisp = getWidth();
                if (larguraDisp <= 0 && getParent() != null) {
                    larguraDisp = getParent().getWidth();
                }
                if (larguraDisp <= 0) {
                    return super.getPreferredSize();
                }
                int maxW = Math.max(200, larguraDisp);
                int hgap = 8;
                int vgap = 6;
                int x = 0;
                int y = vgap;
                int rowH = 0;
                for (Component c : getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = c.getPreferredSize();
                    if (x > 0 && x + hgap + d.width > maxW) {
                        y += rowH + vgap;
                        x = 0;
                        rowH = 0;
                    }
                    if (x > 0) x += hgap;
                    x += d.width;
                    rowH = Math.max(rowH, d.height);
                }
                y += rowH + vgap;
                return new Dimension(larguraDisp, Math.max(ALTURA_INPUT, y));
            }
        };
        barraFiltros.setOpaque(false);
        barraFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraFiltros.setBorder(new EmptyBorder(-6, -8, -6, 0));

        Dimension dimInput = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        campoDataDe.setPreferredSize(dimInput);
        campoDataDe.setMinimumSize(dimInput);
        campoDataAte.setPreferredSize(dimInput);
        campoDataAte.setMinimumSize(dimInput);

        barraFiltros.add(campoDataDe);
        barraFiltros.add(campoDataAte);
        barraFiltros.add(containerBotoesFiltro);

        topo.add(barraFiltros);
        return topo;
    }

    public final void selecionarAba(AbaRelatorio aba) {
        if (aba != null) {
            selecionarAba(aba.getRotulo());
        }
    }

    public final void selecionarAba(String nomeAba) {
        if (!"Completo".equalsIgnoreCase(nomeAba)
                && !"Vendas".equalsIgnoreCase(nomeAba)
                && !"Estoque baixo".equalsIgnoreCase(nomeAba)
                && !"Mais vendidos".equalsIgnoreCase(nomeAba)
                && !"Menos vendidos".equalsIgnoreCase(nomeAba)
                && !"Reembolsos".equalsIgnoreCase(nomeAba)
                && !"Auditoria".equalsIgnoreCase(nomeAba)) {
            JOptionPane.showMessageDialog(
                    this,
                    "A sub-aba \"" + nomeAba + "\" está reservada para a próxima etapa.",
                    "Módulo reservado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if ("Vendas".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Vendas";
        } else if ("Estoque baixo".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Estoque baixo";
        } else if ("Mais vendidos".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Mais vendidos";
        } else if ("Menos vendidos".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Menos vendidos";
        } else if ("Reembolsos".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Reembolsos";
        } else if ("Auditoria".equalsIgnoreCase(nomeAba)) {
            this.abaAtiva = "Auditoria";
        } else {
            this.abaAtiva = "Completo";
        }

        for (BotaoAbaPilula botao : botoesAbas) {
            botao.setAtiva(botao.getText().equalsIgnoreCase(this.abaAtiva));
        }

        containerBotoesFiltro.removeAll();
        if ("Vendas".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_VENDAS);
        } else if ("Estoque baixo".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_ESTOQUE_BAIXO);
        } else if ("Mais vendidos".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_MAIS_VENDIDOS);
        } else if ("Menos vendidos".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_MENOS_VENDIDOS);
        } else if ("Reembolsos".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_REEMBOLSOS);
        } else if ("Auditoria".equalsIgnoreCase(this.abaAtiva)) {
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_AUDITORIA);
        } else {
            containerBotoesFiltro.add(botaoGerarRelatorio);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarPdf);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_COMPLETO);
        }

        carregarRelatorioAtual();
        revalidate();
        repaint();
    }

    public final void mostrarDetalheVenda(boolean visivel) {
        this.detalheVendaVisivel = visivel;
        wrapperDireitaVenda.removeAll();
        if (visivel) {
            wrapperDireitaVenda.setBorder(new EmptyBorder(0, 16, 0, 0));
            JPanel alinhadorTopo = new JPanel(new BorderLayout());
            alinhadorTopo.setOpaque(false);
            alinhadorTopo.add(UiStyle.envolverCardLateralComScroll(cardDetalheVenda), BorderLayout.CENTER);
            wrapperDireitaVenda.add(alinhadorTopo, BorderLayout.CENTER);
            if (tabelaVendas.getSelectedRow() < 0 && tabelaVendas.getRowCount() > 1) {
                tabelaVendas.setRowSelectionInterval(1, 1);
            }
        } else {
            wrapperDireitaVenda.setBorder(new EmptyBorder(0, 0, 0, 0));
            tabelaVendas.clearSelection();
        }
        wrapperDireitaVenda.revalidate();
        wrapperDireitaVenda.repaint();
    }

    private JButton criarBotaoExportarPdf() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Exportar PDF", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignF.FILE_PDF_BOX, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            Component root = SwingUtilities.getWindowAncestor(this);
            if (root instanceof MainFrame) {
                ((MainFrame) root).mostrarPainel(MainFrame.PAINEL_RELATORIO_PDF);
            } else {
                RelatorioPdfPanel.abrirJanelaIsolada();
            }
        });
        return botao;
    }

    private JButton criarBotaoAcao(
            String texto,
            Color bg,
            Color fg,
            Color borda,
            boolean preencherFundo) {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                texto, bg, fg, borda, RAIO_COMPONENTE, preencherFundo
        );
        botao.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"" + texto + "\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));
        return botao;
    }

    private JPanel criarCorpoRelatorioCompleto() {
        JPanel corpo = new JPanel(new BorderLayout(0, 16));
        corpo.setOpaque(false);

        corpo.add(criarLinhaKpis(), BorderLayout.NORTH);
        corpo.add(criarGradeSecoes(), BorderLayout.CENTER);
        return corpo;
    }

    private JPanel criarCorpoVendas() {
        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);

        JPanel cardTabela = criarCardTabelaRanking(tabelaVendas);
        corpo.add(cardTabela, BorderLayout.CENTER);
        corpo.add(wrapperDireitaVenda, BorderLayout.EAST);
        return corpo;
    }

    private JTable criarTabelaVendas() {
        DefaultTableModel modelo = new DefaultTableModel(new Object[0][COLUNAS_VENDAS.length], COLUNAS_VENDAS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable t = new JTable(modelo);
        t.setRowHeight(42);
        t.setFont(UiStyle.fonte(13, Font.PLAIN));
        t.setBackground(UiStyle.SURFACE);
        t.setForeground(UiStyle.TEXT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(UiStyle.TABLE_ROW_BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(UiStyle.TABLE_SELECTION_BG);
        t.setSelectionForeground(UiStyle.TEXT);
        t.setFillsViewportHeight(true);
        t.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(UiStyle.SURFACE);
        header.setForeground(UiStyle.MUTED);
        header.setFont(UiStyle.fonteSemiBold(13));
        header.setPreferredSize(new Dimension(0, 42));
        header.setDefaultRenderer(new RenderizadorCabecalhoPadrao());

        RenderizadorCelulaPadrao renderizadorCelula = new RenderizadorCelulaPadrao();
        RenderizadorChipEstadoPadrao renderizadorEstado = new RenderizadorChipEstadoPadrao();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 5) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
                t.getColumnModel().getColumn(i).setPreferredWidth(140);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorCelula);
            }
        }

        t.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && t.getSelectedRow() >= 0) {
                if (!detalheVendaVisivel) {
                    mostrarDetalheVenda(true);
                }
                sincronizarDetalheVendaSelecionada();
            }
        });

        return t;
    }

    private JTable criarTabelaItensVendaDetalhe() {
        DefaultTableModel modelo = new DefaultTableModel(new Object[0][COLUNAS_ITENS_VENDA_DETALHE.length], COLUNAS_ITENS_VENDA_DETALHE) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable miniTabela = new JTable(modelo);
        miniTabela.setRowHeight(30);
        miniTabela.setFont(UiStyle.fonte(12, Font.PLAIN));
        miniTabela.setBackground(UiStyle.SURFACE);
        miniTabela.setForeground(UiStyle.TEXT);
        miniTabela.setShowVerticalLines(false);
        miniTabela.setShowHorizontalLines(true);
        miniTabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
        miniTabela.setIntercellSpacing(new Dimension(0, 1));
        miniTabela.setRowSelectionAllowed(false);

        JTableHeader header = miniTabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 29));
        header.setDefaultRenderer(new RenderizadorMiniCabecalho());

        RenderizadorMiniCelula renderizadorTexto = new RenderizadorMiniCelula();
        for (int i = 0; i < miniTabela.getColumnModel().getColumnCount(); i++) {
            miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
        }

        return miniTabela;
    }

    private JPanel criarCardDetalheVenda() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_DETALHE_VENDA, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_DETALHE_VENDA, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_DETALHE_VENDA, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        labelDetalheVendaTitulo = new JLabel("Venda -");
        labelDetalheVendaTitulo.setFont(UiStyle.fonte(13, Font.BOLD));
        labelDetalheVendaTitulo.setForeground(UiStyle.TEXT);
        labelDetalheVendaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelDetalheVendaTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaClienteEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaClienteEstado.setOpaque(false);
        linhaClienteEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelDetalheVendaCliente = new JLabel("-");
        labelDetalheVendaCliente.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelDetalheVendaCliente.setForeground(UiStyle.TEXT);
        linhaClienteEstado.add(labelDetalheVendaCliente);
        linhaClienteEstado.add(Box.createRigidArea(new Dimension(10, 0)));
        chipDetalheVendaEstado = new ChipEstado("CONCLUIDA", UiStyle.CHIP_GREEN_BG, UiStyle.CHIP_GREEN_FG);
        linhaClienteEstado.add(chipDetalheVendaEstado);
        card.add(linhaClienteEstado);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelDetalheVendaFuncionario = new JLabel("Funcionário: -");
        labelDetalheVendaFuncionario.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelDetalheVendaFuncionario.setForeground(UiStyle.MUTED);
        labelDetalheVendaFuncionario.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelDetalheVendaFuncionario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollItens = new JScrollPane(tabelaItensVendaDetalhe);
        scrollItens.setBorder(BorderFactory.createEmptyBorder());
        scrollItens.setOpaque(false);
        scrollItens.getViewport().setOpaque(false);
        scrollItens.getViewport().setBackground(UiStyle.SURFACE);
        scrollItens.setPreferredSize(new Dimension(LARGURA_CARD_DETALHE_VENDA - 32, 130));
        tabelaWrapper.add(scrollItens, BorderLayout.CENTER);
        card.add(tabelaWrapper);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel linhaTotal = new JPanel(new BorderLayout());
        linhaTotal.setOpaque(false);
        linhaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiStyle.BORDER),
                new EmptyBorder(12, 0, 6, 0)
        ));

        JLabel labelTotalTexto = new JLabel("Total");
        labelTotalTexto.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelTotalTexto.setForeground(UiStyle.TEXT);

        labelDetalheVendaTotal = new JLabel("0,00 MT");
        labelDetalheVendaTotal.setFont(UiStyle.fonte(20, Font.BOLD));
        labelDetalheVendaTotal.setForeground(UiStyle.TEXT);

        linhaTotal.add(labelTotalTexto, BorderLayout.WEST);
        linhaTotal.add(labelDetalheVendaTotal, BorderLayout.EAST);
        card.add(linhaTotal);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraFechar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraFechar.setOpaque(false);
        barraFechar.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraFechar.add(botaoFecharDetalheVenda);
        card.add(barraFechar);

        return card;
    }

    private JPanel criarCardTabelaRanking(JTable tabela) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(2, 2, 2, 2));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(UiStyle.SURFACE);

        JPanel topoWrapper = new JPanel(new BorderLayout());
        topoWrapper.setOpaque(false);
        topoWrapper.add(tabela.getTableHeader(), BorderLayout.NORTH);
        topoWrapper.add(scroll, BorderLayout.CENTER);

        card.add(topoWrapper, BorderLayout.CENTER);
        return card;
    }

    private JTable criarTabelaRanking(Object[][] dados, String[] colunas) {
        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable t = new JTable(modelo);
        t.setRowHeight(42);
        t.setFont(UiStyle.fonte(13, Font.PLAIN));
        t.setBackground(UiStyle.SURFACE);
        t.setForeground(UiStyle.TEXT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(UiStyle.TABLE_ROW_BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(UiStyle.TABLE_SELECTION_BG);
        t.setSelectionForeground(UiStyle.TEXT);
        t.setFillsViewportHeight(true);
        t.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(UiStyle.SURFACE);
        header.setForeground(UiStyle.MUTED);
        header.setFont(UiStyle.fonteSemiBold(13));
        header.setPreferredSize(new Dimension(0, 42));
        header.setDefaultRenderer(new RenderizadorCabecalhoPadrao());

        RenderizadorCelulaPadrao renderizadorCelula = new RenderizadorCelulaPadrao();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderizadorCelula);
        }
        return t;
    }

    private JPanel criarLinhaKpis() {
        JPanel linha = new JPanel(new GridLayout(1, 6, 12, 0));
        linha.setOpaque(false);

        labelKpiVendasPeriodo = new JLabel("0");
        labelKpiTotalConcluido = new JLabel("0,00 MT");
        labelKpiTotalReembolsado = new JLabel("0,00 MT");
        labelKpiEstoqueBaixo = new JLabel("0");
        labelKpiProdutosExpirar = new JLabel("0");
        labelKpiLotesExpirados = new JLabel("0");

        linha.add(criarCardKpi("Vendas no período", labelKpiVendasPeriodo));
        linha.add(criarCardKpi("Total concluído", labelKpiTotalConcluido));
        linha.add(criarCardKpi("Total reembolsado", labelKpiTotalReembolsado));
        linha.add(criarCardKpi("Produtos com estoque baixo", labelKpiEstoqueBaixo));
        linha.add(criarCardKpi("Produtos a expirar", labelKpiProdutosExpirar));
        linha.add(criarCardKpi("Lotes expirados", labelKpiLotesExpirados));

        return linha;
    }

    private JPanel criarCardKpi(String rotulo, JLabel labelValor) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel labelRotulo = new JLabel(rotulo);
        labelRotulo.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelRotulo.setForeground(UiStyle.MUTED);
        labelRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelValor.setFont(UiStyle.fonte(22, Font.BOLD));
        labelValor.setForeground(UiStyle.TEXT);
        labelValor.setBorder(new EmptyBorder(2, 0, 0, 0));
        labelValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(labelRotulo);
        card.add(labelValor);
        return card;
    }

    private JPanel criarGradeSecoes() {
        JPanel grade = new JPanel(new GridLayout(2, 3, 16, 16));
        grade.setOpaque(false);

        modeloMiniVendasEstado = criarModeloNaoEditavel(new String[] {"Estado", "Qtd", "Total"});
        modeloMiniMaisVendidos = criarModeloNaoEditavel(new String[] {"Produto", "Qtd"});
        modeloMiniMenosVendidos = criarModeloNaoEditavel(new String[] {"Produto", "Qtd"});
        modeloMiniEstoqueBaixo = criarModeloNaoEditavel(new String[] {"Produto", "Actual", "Mín."});
        modeloMiniReembolsos = criarModeloNaoEditavel(new String[] {"Item", "Qtd", "Valor"});
        modeloMiniAuditoria = criarModeloNaoEditavel(new String[] {"Tabela", "Operação", "Registos"});

        grade.add(criarCardMiniTabela("Vendas por estado", modeloMiniVendasEstado, true));

        grade.add(criarCardMiniTabela("Mais vendidos", modeloMiniMaisVendidos, false));

        grade.add(criarCardMiniTabela("Menos vendidos", modeloMiniMenosVendidos, false));

        grade.add(criarCardMiniTabela("Estoque baixo", modeloMiniEstoqueBaixo, false));

        grade.add(criarCardMiniTabela("Reembolsos", modeloMiniReembolsos, false));

        grade.add(criarCardMiniTabela("Auditoria", modeloMiniAuditoria, false));

        return grade;
    }

    private DefaultTableModel criarModeloNaoEditavel(String[] colunas) {
        return new DefaultTableModel(new Object[0][colunas.length], colunas) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel criarCardMiniTabela(
            String tituloSecao,
            DefaultTableModel modelo,
            boolean primeiraColunaChipEstado) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(tituloSecao);
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        card.add(titulo, BorderLayout.NORTH);

        JTable miniTabela = new JTable(modelo);
        miniTabela.setRowHeight(30);
        miniTabela.setFont(UiStyle.fonte(12, Font.PLAIN));
        miniTabela.setBackground(UiStyle.SURFACE);
        miniTabela.setForeground(UiStyle.TEXT);
        miniTabela.setShowVerticalLines(false);
        miniTabela.setShowHorizontalLines(true);
        miniTabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
        miniTabela.setIntercellSpacing(new Dimension(0, 1));
        miniTabela.setRowSelectionAllowed(false);

        JTableHeader header = miniTabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 29));
        header.setDefaultRenderer(new RenderizadorMiniCabecalho());

        RenderizadorMiniCelula renderizadorTexto = new RenderizadorMiniCelula();
        RenderizadorMiniChipEstado renderizadorChip = new RenderizadorMiniChipEstado();
        for (int i = 0; i < miniTabela.getColumnModel().getColumnCount(); i++) {
            if (primeiraColunaChipEstado && i == 0) {
                miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorChip);
                miniTabela.getColumnModel().getColumn(i).setPreferredWidth(125);
            } else {
                miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        JScrollPane scrollMini = new JScrollPane(miniTabela);
        scrollMini.setBorder(BorderFactory.createEmptyBorder());
        scrollMini.setOpaque(false);
        scrollMini.getViewport().setOpaque(false);
        scrollMini.getViewport().setBackground(UiStyle.SURFACE);
        tabelaWrapper.add(scrollMini, BorderLayout.CENTER);

        card.add(tabelaWrapper, BorderLayout.CENTER);
        return card;
    }

    private static final class RenderizadorCabecalhoPadrao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.MUTED);
            label.setFont(UiStyle.fonteSemiBold(13));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                    new EmptyBorder(11, 14, 11, 14)
            ));
            return label;
        }
    }

    private static final class RenderizadorCelulaPadrao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(isSelected);
            label.setBackground(isSelected ? UiStyle.TABLE_SELECTION_BG : UiStyle.SURFACE);
            label.setForeground(UiStyle.TEXT);
            label.setFont(UiStyle.fonte(13, Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(11, 14, 11, 14)
            ));
            return label;
        }
    }

    private static final class RenderizadorChipEstadoPadrao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";

            Color bgChip;
            Color fgChip;
            if ("PENDENTE".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_YELLOW_BG;
                fgChip = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(texto) || "CONCLUIDA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_GREEN_BG;
                fgChip = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.ERROR_BG;
                fgChip = UiStyle.ERROR_TEXT;
            } else {
                bgChip = UiStyle.CHIP_BLUE_BG;
                fgChip = UiStyle.CHIP_BLUE_FG;
            }

            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            container.setOpaque(isSelected);
            container.setBackground(isSelected ? UiStyle.TABLE_SELECTION_BG : UiStyle.SURFACE);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(8, 14, 8, 14)
            ));
            container.add(new ChipEstado(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class RenderizadorMiniCabecalho extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.MUTED);
            label.setFont(UiStyle.fonteSemiBold(12));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            return label;
        }
    }

    private static final class RenderizadorMiniCelula extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.TEXT);
            label.setFont(UiStyle.fonte(12, Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            return label;
        }
    }

    private static final class RenderizadorMiniChipEstado extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";

            Color bgChip;
            Color fgChip;
            if ("PENDENTE".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_YELLOW_BG;
                fgChip = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(texto) || "CONCLUIDA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_GREEN_BG;
                fgChip = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.ERROR_BG;
                fgChip = UiStyle.ERROR_TEXT;
            } else {
                bgChip = UiStyle.CHIP_BLUE_BG;
                fgChip = UiStyle.CHIP_BLUE_FG;
            }

            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            container.setOpaque(false);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            container.add(new ChipEstado(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class ChipEstado extends JLabel {
        private static final long serialVersionUID = 1L;

        private Color corFundo;

        ChipEstado(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonteSemiBold(12));
            setForeground(corTexto);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        void atualizarEstado(String texto, Color novoFundo, Color novoTexto) {
            setText(texto);
            this.corFundo = novoFundo;
            setForeground(novoTexto);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            String txt = getText() != null ? getText().trim() : "";
            Color bgDinamico = corFundo;
            Color fgDinamico = getForeground();
            if ("PENDENTE".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_YELLOW_BG;
                fgDinamico = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(txt) || "CONCLUIDA".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_GREEN_BG;
                fgDinamico = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.ERROR_BG;
                fgDinamico = UiStyle.ERROR_TEXT;
            } else if (!txt.isEmpty()) {
                bgDinamico = UiStyle.CHIP_BLUE_BG;
                fgDinamico = UiStyle.CHIP_BLUE_FG;
            }
            setForeground(fgDinamico);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(bgDinamico);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class BotaoAbaPilula extends JButton {
        private static final long serialVersionUID = 1L;

        private boolean ativa;

        BotaoAbaPilula(String texto, boolean ativa) {
            super(texto);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));
            setAtiva(ativa);
        }

        void setAtiva(boolean ativa) {
            this.ativa = ativa;
            if (ativa) {
                setFont(UiStyle.fonteSemiBold(13));
                setForeground(UiStyle.ACCENT_TEXT);
            } else {
                setFont(UiStyle.fonte(13, Font.PLAIN));
                setForeground(UiStyle.MUTED);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                setForeground(ativa ? UiStyle.ACCENT_TEXT : UiStyle.MUTED);
                g2.setColor(ativa ? UiStyle.ACCENT : UiStyle.TAB_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class CampoDataArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoDataArredondado(String textoInicial) {
            super(textoInicial);
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setForeground(UiStyle.TEXT);
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    setForeground(UiStyle.MUTED);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(isFocusOwner() ? UiStyle.ACCENT : UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class BotaoToolbarArredondado extends JButton {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final boolean preencherFundo;
        private final int raio;

        BotaoToolbarArredondado(
                String texto,
                Color corFundo,
                Color corTexto,
                Color corBorda,
                int raio,
                boolean preencherFundo) {
            super(texto);
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.raio = raio;
            this.preencherFundo = preencherFundo;
            setFont(UiStyle.fonteSemiBold(13));
            setForeground(corTexto);
            setBackground(corFundo);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, ALTURA_INPUT);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                boolean ehPrimario = UiStyle.ACCENT.equals(corFundo);

                if (preencherFundo && corFundo != null) {
                    Color baseBg = ehPrimario ? UiStyle.ACCENT : UiStyle.SECONDARY_BG;
                    setForeground(ehPrimario ? UiStyle.ACCENT_TEXT : UiStyle.TEXT);
                    Color atual = baseBg;
                    if (getModel().isPressed()) {
                        atual = baseBg.darker();
                    } else if (getModel().isRollover()) {
                        atual = new Color(
                                Math.min(255, baseBg.getRed() + 10),
                                Math.min(255, baseBg.getGreen() + 10),
                                Math.min(255, baseBg.getBlue() + 10)
                        );
                    }
                    g2.setColor(atual);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                }

                if (corBorda != null) {
                    g2.setColor(UiStyle.INPUT_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final int raio;

        PainelCard(Color corFundo, Color corBorda, int raio) {
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                super.paintComponent(g);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(UiStyle.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                super.paintChildren(g);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int arco = raio * 2;
                Shape clip = new RoundRectangle2D.Float(1, 1, Math.max(0, getWidth() - 2), Math.max(0, getHeight() - 2), arco, arco);
                g2.clip(clip);
                super.paintChildren(g2);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(UiStyle.BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    private LocalDateTime extrairDataInicio() {
        try {
            String texto = campoDataDe.getText().replaceAll("(?i)De:\\s*", "").trim();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(texto, dtf).atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now().minusMonths(1).with(LocalTime.MIN);
        }
    }

    private LocalDateTime extrairDataFim() {
        try {
            String texto = campoDataAte.getText().replaceAll("(?i)Até:\\s*", "").trim();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(texto, dtf).atTime(LocalTime.MAX);
        } catch (Exception e) {
            return LocalDateTime.now().with(LocalTime.MAX);
        }
    }

    private Utilizador obterUtilizadorAutenticadoOuPadrao() {
        Utilizador u = SessaoUtilizador.getUtilizador();
        if (u == null) {
            u = new Utilizador();
            u.setPerfil(Perfil.ADMIN);
        }
        return u;
    }

    private void sincronizarDetalheVendaSelecionada() {
        int row = tabelaVendas.getSelectedRow();
        DefaultTableModel mItens = (DefaultTableModel) tabelaItensVendaDetalhe.getModel();
        if (row < 0 || row >= tabelaVendas.getRowCount()) {
            mItens.setRowCount(0);
            if (labelDetalheVendaTitulo != null) labelDetalheVendaTitulo.setText("Venda -");
            if (labelDetalheVendaCliente != null) labelDetalheVendaCliente.setText("-");
            if (labelDetalheVendaFuncionario != null) labelDetalheVendaFuncionario.setText("Funcionário: -");
            if (labelDetalheVendaTotal != null) labelDetalheVendaTotal.setText("0,00 MT");
            return;
        }
        String idStr = String.valueOf(tabelaVendas.getValueAt(row, 0));
        String cliente = String.valueOf(tabelaVendas.getValueAt(row, 2));
        String func = String.valueOf(tabelaVendas.getValueAt(row, 3));
        String total = String.valueOf(tabelaVendas.getValueAt(row, 4));
        String estado = String.valueOf(tabelaVendas.getValueAt(row, 5));

        if (labelDetalheVendaTitulo != null) labelDetalheVendaTitulo.setText("Venda " + idStr);
        if (labelDetalheVendaCliente != null) labelDetalheVendaCliente.setText(cliente);
        if (labelDetalheVendaFuncionario != null) labelDetalheVendaFuncionario.setText("Funcionário: " + func);
        if (labelDetalheVendaTotal != null) labelDetalheVendaTotal.setText(total);

        if (chipDetalheVendaEstado != null) {
            Color bgChip;
            Color fgChip;
            if ("PENDENTE".equalsIgnoreCase(estado)) {
                bgChip = UiStyle.CHIP_YELLOW_BG;
                fgChip = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(estado) || "CONCLUIDA".equalsIgnoreCase(estado)) {
                bgChip = UiStyle.CHIP_GREEN_BG;
                fgChip = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(estado)) {
                bgChip = UiStyle.ERROR_BG;
                fgChip = UiStyle.ERROR_TEXT;
            } else {
                bgChip = UiStyle.CHIP_BLUE_BG;
                fgChip = UiStyle.CHIP_BLUE_FG;
            }
            chipDetalheVendaEstado.atualizarEstado(estado, bgChip, fgChip);
        }

        try {
            final Long idVenda = Long.parseLong(idStr);
            final Utilizador user = obterUtilizadorAutenticadoOuPadrao();
            AsyncTask.executar(
                () -> relatorioController.itensPorVenda(user, idVenda),
                itens -> {
                    mItens.setRowCount(0);
                    if (itens != null) {
                        for (Map<String, Object> it : itens) {
                            String nomeProd = String.valueOf(it.getOrDefault("nome_produto", ""));
                            String qtd = String.valueOf(it.getOrDefault("quantidade", 0));
                            double prc = 0.0;
                            double sub = 0.0;
                            try { prc = Double.parseDouble(String.valueOf(it.getOrDefault("preco", 0.0))); } catch (Exception ignored) {}
                            try { sub = Double.parseDouble(String.valueOf(it.getOrDefault("subtotal", 0.0))); } catch (Exception ignored) {}
                            mItens.addRow(new Object[] {
                                nomeProd,
                                qtd,
                                String.format(Locale.GERMANY, "%,.2f MT", prc),
                                String.format(Locale.GERMANY, "%,.2f MT", sub)
                            });
                        }
                    }
                },
                err -> mItens.setRowCount(0)
            );
        } catch (Exception ex) {
            mItens.setRowCount(0);
        }
    }

    public void carregarRelatorioAtual() {
        Utilizador user = obterUtilizadorAutenticadoOuPadrao();
        LocalDateTime inicio = extrairDataInicio();
        LocalDateTime fim = extrairDataFim();

        if ("Estoque baixo".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioController.estoqueBaixo(user),
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaEstoqueBaixo.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        for (Map<String, Object> map : lista) {
                            String prod = String.valueOf(map.getOrDefault("nome_produto", ""));
                            String atual = String.valueOf(map.getOrDefault("quantidade_atual", 0));
                            String min = String.valueOf(map.getOrDefault("quantidade_minima", 0));
                            int falta = 0;
                            try {
                                falta = Integer.parseInt(min) - Integer.parseInt(atual);
                            } catch (Exception ignored) {}
                            m.addRow(new Object[] {prod, atual, min, String.valueOf(Math.max(0, falta))});
                        }
                    }
                },
                erro -> ((DefaultTableModel) tabelaEstoqueBaixo.getModel()).setRowCount(0)
            );
        } else if ("Mais vendidos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioController.produtosMaisVendidos(user),
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaMaisVendidos.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        int pos = 1;
                        for (Map<String, Object> map : lista) {
                            String prod = String.valueOf(map.getOrDefault("nome_produto", ""));
                            String qtd = String.valueOf(map.getOrDefault("quantidade_vendida", 0));
                            double rec = 0.0;
                            try {
                                rec = Double.parseDouble(String.valueOf(map.getOrDefault("valor_vendido", 0.0)));
                            } catch (Exception ignored) {}
                            String recStr = String.format(Locale.GERMANY, "%,.2f MT", rec);
                            m.addRow(new Object[] {String.valueOf(pos++), prod, qtd, recStr});
                        }
                    }
                },
                erro -> ((DefaultTableModel) tabelaMaisVendidos.getModel()).setRowCount(0)
            );
        } else if ("Menos vendidos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> {
                    List<Map<String, Object>> lista = relatorioController.produtosMaisVendidos(user);
                    if (lista != null) {
                        lista = new ArrayList<>(lista);
                        Collections.sort(lista, Comparator.comparingDouble(o -> {
                            try {
                                return Double.parseDouble(String.valueOf(o.getOrDefault("quantidade_vendida", 0)));
                            } catch (Exception e) {
                                return 0.0;
                            }
                        }));
                    }
                    return lista;
                },
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaMenosVendidos.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        int pos = 1;
                        for (Map<String, Object> map : lista) {
                            String prod = String.valueOf(map.getOrDefault("nome_produto", ""));
                            String qtd = String.valueOf(map.getOrDefault("quantidade_vendida", 0));
                            double rec = 0.0;
                            try {
                                rec = Double.parseDouble(String.valueOf(map.getOrDefault("valor_vendido", 0.0)));
                            } catch (Exception ignored) {}
                            String recStr = String.format(Locale.GERMANY, "%,.2f MT", rec);
                            m.addRow(new Object[] {String.valueOf(pos++), prod, qtd, recStr});
                        }
                    }
                },
                erro -> ((DefaultTableModel) tabelaMenosVendidos.getModel()).setRowCount(0)
            );
        } else if ("Reembolsos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioController.reembolsosPeriodo(user, inicio, fim),
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaReembolsos.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        for (Map<String, Object> map : lista) {
                            String data = String.valueOf(map.getOrDefault("data_reembolso", ""));
                            if (data.length() >= 10) data = data.substring(0, 10);
                            String venda = String.valueOf(map.getOrDefault("id_venda", ""));
                            String item = String.valueOf(map.getOrDefault("nome_produto", ""));
                            String qtd = String.valueOf(map.getOrDefault("quantidade", ""));
                            double val = 0.0;
                            try {
                                val = Double.parseDouble(String.valueOf(map.getOrDefault("valor", 0.0)));
                            } catch (Exception ignored) {}
                            String valStr = String.format(Locale.GERMANY, "%,.2f MT", val);
                            String motivo = String.valueOf(map.getOrDefault("motivo", ""));
                            String func = String.valueOf(map.getOrDefault("nome_funcionario", ""));
                            m.addRow(new Object[] {data, venda, item, qtd, valStr, motivo, func});
                        }
                    }
                },
                erro -> ((DefaultTableModel) tabelaReembolsos.getModel()).setRowCount(0)
            );
        } else if ("Auditoria".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioController.resumoAuditoria(user, inicio, fim),
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaAuditoria.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        for (Map<String, Object> map : lista) {
                            String dia = String.valueOf(map.getOrDefault("dia", ""));
                            String tabela = String.valueOf(map.getOrDefault("tabela_afetada", ""));
                            String operacao = String.valueOf(map.getOrDefault("operacao", ""));
                            String qtd = String.valueOf(map.getOrDefault("quantidade", ""));
                            m.addRow(new Object[] {dia, tabela, operacao, qtd});
                        }
                    }
                },
                erro -> ((DefaultTableModel) tabelaAuditoria.getModel()).setRowCount(0)
            );
        } else if ("Vendas".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioController.vendasPeriodo(user, inicio, fim),
                lista -> {
                    DefaultTableModel m = (DefaultTableModel) tabelaVendas.getModel();
                    m.setRowCount(0);
                    if (lista != null) {
                        for (Map<String, Object> map : lista) {
                            String id = String.valueOf(map.getOrDefault("id_venda", ""));
                            String data = String.valueOf(map.getOrDefault("data_venda", ""));
                            if (data.length() >= 10) data = data.substring(0, 10);
                            String cli = String.valueOf(map.getOrDefault("nome_cliente", ""));
                            String func = String.valueOf(map.getOrDefault("nome_funcionario", ""));
                            double tot = 0.0;
                            try {
                                tot = Double.parseDouble(String.valueOf(map.getOrDefault("total", 0.0)));
                            } catch (Exception ignored) {}
                            String totStr = String.format(Locale.GERMANY, "%,.2f MT", tot);
                            String est = String.valueOf(map.getOrDefault("estado", ""));
                            m.addRow(new Object[] {id, data, cli, func, totStr, est});
                        }
                    }
                    if (tabelaVendas.getRowCount() > 0) {
                        tabelaVendas.setRowSelectionInterval(0, 0);
                    }
                    sincronizarDetalheVendaSelecionada();
                },
                erro -> {
                    ((DefaultTableModel) tabelaVendas.getModel()).setRowCount(0);
                    sincronizarDetalheVendaSelecionada();
                }
            );
        } else if ("Completo".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> {
                    Map<String, Integer> kpisDash = relatorioController.obterIndicadoresDashboard();
                    List<Map<String, Object>> vendas = relatorioController.vendasPeriodo(user, inicio, fim);
                    List<Map<String, Object>> resumoEstado = relatorioController.resumoVendasPorEstado(user, inicio, fim);
                    List<Map<String, Object>> maisVend = relatorioController.produtosMaisVendidos(user);
                    List<Map<String, Object>> estBaixo = relatorioController.estoqueBaixo(user);
                    List<Map<String, Object>> reemb = relatorioController.reembolsosPeriodo(user, inicio, fim);
                    List<Map<String, Object>> audit = relatorioController.resumoAuditoria(user, inicio, fim);
                    return new Object[] { kpisDash, vendas, resumoEstado, maisVend, estBaixo, reemb, audit };
                },
                res -> {
                    Object[] arr = (Object[]) res;
                    @SuppressWarnings("unchecked")
                    Map<String, Integer> kpisDash = (Map<String, Integer>) arr[0];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> vendas = (List<Map<String, Object>>) arr[1];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> resumoEstado = (List<Map<String, Object>>) arr[2];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> maisVend = (List<Map<String, Object>>) arr[3];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> estBaixo = (List<Map<String, Object>>) arr[4];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> reemb = (List<Map<String, Object>>) arr[5];
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> audit = (List<Map<String, Object>>) arr[6];

                    DefaultTableModel mVendas = (DefaultTableModel) tabelaVendas.getModel();
                    mVendas.setRowCount(0);
                    double totalConcluido = 0.0;
                    if (vendas != null) {
                        for (Map<String, Object> map : vendas) {
                            String id = String.valueOf(map.getOrDefault("id_venda", ""));
                            String data = String.valueOf(map.getOrDefault("data_venda", ""));
                            if (data.length() >= 10) data = data.substring(0, 10);
                            String cli = String.valueOf(map.getOrDefault("nome_cliente", ""));
                            String func = String.valueOf(map.getOrDefault("nome_funcionario", ""));
                            double tot = 0.0;
                            try { tot = Double.parseDouble(String.valueOf(map.getOrDefault("total", 0.0))); } catch (Exception ignored) {}
                            String est = String.valueOf(map.getOrDefault("estado", ""));
                            if ("CONCLUIDA".equalsIgnoreCase(est)) {
                                totalConcluido += tot;
                            }
                            mVendas.addRow(new Object[] {id, data, cli, func, String.format(Locale.GERMANY, "%,.2f MT", tot), est});
                        }
                    }

                    double totalReembolsado = 0.0;
                    if (reemb != null) {
                        for (Map<String, Object> r : reemb) {
                            try { totalReembolsado += Double.parseDouble(String.valueOf(r.getOrDefault("valor", 0.0))); } catch (Exception ignored) {}
                        }
                    }

                    if (labelKpiVendasPeriodo != null) labelKpiVendasPeriodo.setText(String.valueOf(vendas != null ? vendas.size() : 0));
                    if (labelKpiTotalConcluido != null) labelKpiTotalConcluido.setText(String.format(Locale.GERMANY, "%,.2f MT", totalConcluido));
                    if (labelKpiTotalReembolsado != null) labelKpiTotalReembolsado.setText(String.format(Locale.GERMANY, "%,.2f MT", totalReembolsado));
                    if (labelKpiEstoqueBaixo != null) labelKpiEstoqueBaixo.setText(String.valueOf(estBaixo != null ? estBaixo.size() : 0));
                    if (labelKpiProdutosExpirar != null) {
                        int exp = (kpisDash != null) ? kpisDash.getOrDefault("lotesAExpirar", 0) : 0;
                        labelKpiProdutosExpirar.setText(String.valueOf(exp));
                    }
                    if (labelKpiLotesExpirados != null) {
                        int expVenc = (kpisDash != null) ? kpisDash.getOrDefault("lotesExpirados", 0) : 0;
                        labelKpiLotesExpirados.setText(String.valueOf(expVenc));
                    }

                    if (modeloMiniVendasEstado != null) {
                        modeloMiniVendasEstado.setRowCount(0);
                        if (resumoEstado != null) {
                            for (Map<String, Object> m : resumoEstado) {
                                String est = String.valueOf(m.getOrDefault("estado", ""));
                                String qtd = String.valueOf(m.getOrDefault("quantidade", 0));
                                double tot = 0.0;
                                try { tot = Double.parseDouble(String.valueOf(m.getOrDefault("total", 0.0))); } catch (Exception ignored) {}
                                modeloMiniVendasEstado.addRow(new Object[] {est, qtd, String.format(Locale.GERMANY, "%,.2f MT", tot)});
                            }
                        }
                    }

                    if (modeloMiniMaisVendidos != null) {
                        modeloMiniMaisVendidos.setRowCount(0);
                        if (maisVend != null) {
                            for (int i = 0; i < maisVend.size(); i++) {
                                Map<String, Object> m = maisVend.get(i);
                                modeloMiniMaisVendidos.addRow(new Object[] {
                                    String.valueOf(m.getOrDefault("nome_produto", "")),
                                    String.valueOf(m.getOrDefault("quantidade_vendida", 0))
                                });
                            }
                        }
                    }

                    if (modeloMiniMenosVendidos != null) {
                        modeloMiniMenosVendidos.setRowCount(0);
                        if (maisVend != null) {
                            List<Map<String, Object>> menos = new ArrayList<>(maisVend);
                            Collections.sort(menos, Comparator.comparingDouble(o -> {
                                try { return Double.parseDouble(String.valueOf(o.getOrDefault("quantidade_vendida", 0))); }
                                catch (Exception e) { return 0.0; }
                            }));
                            for (int i = 0; i < menos.size(); i++) {
                                Map<String, Object> m = menos.get(i);
                                modeloMiniMenosVendidos.addRow(new Object[] {
                                    String.valueOf(m.getOrDefault("nome_produto", "")),
                                    String.valueOf(m.getOrDefault("quantidade_vendida", 0))
                                });
                            }
                        }
                    }

                    if (modeloMiniEstoqueBaixo != null) {
                        modeloMiniEstoqueBaixo.setRowCount(0);
                        if (estBaixo != null) {
                            for (int i = 0; i < estBaixo.size(); i++) {
                                Map<String, Object> m = estBaixo.get(i);
                                modeloMiniEstoqueBaixo.addRow(new Object[] {
                                    String.valueOf(m.getOrDefault("nome_produto", "")),
                                    String.valueOf(m.getOrDefault("quantidade_atual", 0)),
                                    String.valueOf(m.getOrDefault("quantidade_minima", 0))
                                });
                            }
                        }
                    }

                    if (modeloMiniReembolsos != null) {
                        modeloMiniReembolsos.setRowCount(0);
                        if (reemb != null) {
                            for (int i = 0; i < reemb.size(); i++) {
                                Map<String, Object> m = reemb.get(i);
                                double v = 0.0;
                                try { v = Double.parseDouble(String.valueOf(m.getOrDefault("valor", 0.0))); } catch (Exception ignored) {}
                                modeloMiniReembolsos.addRow(new Object[] {
                                    String.valueOf(m.getOrDefault("nome_produto", "")),
                                    String.valueOf(m.getOrDefault("quantidade", 0)),
                                    String.format(Locale.GERMANY, "%,.2f MT", v)
                                });
                            }
                        }
                    }

                    if (modeloMiniAuditoria != null) {
                        modeloMiniAuditoria.setRowCount(0);
                        if (audit != null) {
                            for (int i = 0; i < audit.size(); i++) {
                                Map<String, Object> m = audit.get(i);
                                modeloMiniAuditoria.addRow(new Object[] {
                                    String.valueOf(m.getOrDefault("tabela_afetada", "")),
                                    String.valueOf(m.getOrDefault("operacao", "")),
                                    String.valueOf(m.getOrDefault("quantidade", 0))
                                });
                            }
                        }
                    }
                },
                erro -> {}
            );
        }
    }

    @Override
    public void recarregarDados() {
        if (abaAtiva != null) {
            selecionarAba(abaAtiva);
        }
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardDetalheVenda);
    }
}
