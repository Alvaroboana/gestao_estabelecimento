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
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
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
import model.bo.RelatorioBO;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Relatórios em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Relatório — completo" (RelCompleto.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — venda detalhada" (RelVendaDetalhe.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — estoque baixo" (RelEstoqueBaixo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — mais vendidos" (Relatorios.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — menos vendidos" (RelMenosVendidos.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — reembolsos" (RelReembolsos.dc.html)
 * - "ADMIN · Barra superior · Escuro — Relatório — auditoria" (RelAuditoria.dc.html)
 */
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

    private static final Object[][] DADOS_MAIS_VENDIDOS = {
        {"1", "Pão", "320", "1.600,00 MT"},
        {"2", "Leite 1L", "210", "17.850,00 MT"},
        {"3", "Água 1,5L", "180", "5.400,00 MT"},
        {"4", "Arroz 25kg", "35", "63.000,00 MT"}
    };

    private static final String[] COLUNAS_MENOS_VENDIDOS = {
        "#", "Produto", "Qtd vendida", "Receita"
    };

    private static final Object[][] DADOS_MENOS_VENDIDOS = {
        {"1", "Biscoito", "0", "0,00 MT"},
        {"2", "Óleo 5L", "4", "2.600,00 MT"},
        {"3", "Sal 1kg", "5", "100,00 MT"},
        {"4", "Açúcar 1kg", "7", "490,00 MT"}
    };

    private static final String[] COLUNAS_ESTOQUE_BAIXO = {
        "Produto", "Estoque actual", "Mínimo", "Em falta"
    };

    private static final Object[][] DADOS_ESTOQUE_BAIXO = {
        {"Arroz 25kg", "4", "10", "6"},
        {"Óleo 5L", "2", "8", "6"},
        {"Açúcar 1kg", "6", "15", "9"},
        {"Sal 1kg", "1", "5", "4"}
    };

    private static final String[] COLUNAS_REEMBOLSOS = {
        "Data", "Venda", "Item", "Qtd", "Valor", "Motivo", "Funcionário"
    };

    private static final Object[][] DADOS_REEMBOLSOS = {
        {"03/11/2026", "1039", "Leite 1L", "1", "85,00 MT", "Embalagem danificada", "Sara"},
        {"02/11/2026", "1039", "Pão", "4", "20,00 MT", "Fora do prazo", "Sara"}
    };

    private static final String[] COLUNAS_AUDITORIA = {
        "Dia", "Tabela afectada", "Operação", "Registos"
    };

    private static final Object[][] DADOS_AUDITORIA = {
        {"03/11/2026", "Vendas", "Registo criado", "12"},
        {"03/11/2026", "Estoque", "Registo alterado", "8"},
        {"02/11/2026", "Produtos", "Registo alterado", "3"},
        {"02/11/2026", "Reembolsos", "Registo criado", "2"}
    };

    private static final String[] COLUNAS_VENDAS = {
        "ID", "Data", "Cliente", "Funcionário", "Total", "Estado"
    };

    private static final Object[][] DADOS_VENDAS = {
        {"1042", "03/11/2026", "Ana Macie", "Carlos", "1.250,00 MT", "PENDENTE"},
        {"1041", "03/11/2026", "João Sitoe", "Carlos", "840,00 MT", "CONCLUÍDA"},
        {"1040", "02/11/2026", "Maria Nhaca", "Sara", "300,00 MT", "ANULADA"},
        {"1039", "02/11/2026", "Ana Macie", "Sara", "2.100,00 MT", "REEMBOLSADA"}
    };

    private static final String[] COLUNAS_ITENS_VENDA_DETALHE = {
        "Item", "Qtd", "Preço", "Subtotal"
    };

    private static final Object[][] DADOS_ITENS_VENDA_DETALHE = {
        {"Leite 1L", "4", "85,00 MT", "340,00 MT"},
        {"Pão", "20", "5,00 MT", "100,00 MT"},
        {"Água 1,5L", "10", "40,00 MT", "400,00 MT"}
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
    private final JTable tabelaEstoqueBaixo;
    private final JTable tabelaMaisVendidos;
    private final JTable tabelaMenosVendidos;
    private final JTable tabelaReembolsos;
    private final JTable tabelaAuditoria;
    private final RelatorioBO relatorioBO;
    private boolean detalheVendaVisivel = true;
    private String abaAtiva = "Completo";

    public RelatoriosPanel() {
        this(AbaRelatorio.COMPLETO);
    }

    public RelatoriosPanel(AbaRelatorio abaInicial) {
        UiStyle.instalarTemaEscuro();
        this.relatorioBO = new RelatorioBO();
        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoDataDe = new CampoDataArredondado("De: 01/11/2026");
        this.campoDataAte = new CampoDataArredondado("Até: 03/11/2026");
        this.botaoGerarRelatorio = new BotaoToolbarArredondado(
                "Gerar relatório completo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoGerarRelatorio.addActionListener(e -> carregarRelatorioAtual());
        this.botaoExportarPdf = criarBotaoExportarPdf();
        this.botaoImprimir = criarBotaoAcao(
                "Imprimir", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        this.botaoGerarSimples = new BotaoToolbarArredondado(
                "Gerar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoGerarSimples.addActionListener(e -> carregarRelatorioAtual());
        this.botaoExportarSimples = criarBotaoAcao(
                "Exportar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        this.botaoFecharDetalheVenda = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoFecharDetalheVenda.addActionListener(e -> mostrarDetalheVenda(false));
        this.containerBotoesFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesFiltro.setOpaque(false);

        this.tabelaVendas = criarTabelaVendas();
        this.tabelaItensVendaDetalhe = criarTabelaItensVendaDetalhe();
        this.wrapperDireitaVenda = new JPanel(new BorderLayout());
        this.wrapperDireitaVenda.setOpaque(false);
        this.cardDetalheVenda = criarCardDetalheVenda();
        this.tabelaEstoqueBaixo = criarTabelaRanking(DADOS_ESTOQUE_BAIXO, COLUNAS_ESTOQUE_BAIXO);
        this.tabelaMaisVendidos = criarTabelaRanking(DADOS_MAIS_VENDIDOS, COLUNAS_MAIS_VENDIDOS);
        this.tabelaMenosVendidos = criarTabelaRanking(DADOS_MENOS_VENDIDOS, COLUNAS_MENOS_VENDIDOS);
        this.tabelaReembolsos = criarTabelaRanking(DADOS_REEMBOLSOS, COLUNAS_REEMBOLSOS);
        this.tabelaAuditoria = criarTabelaRanking(DADOS_AUDITORIA, COLUNAS_AUDITORIA);
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

    private JPanel criarTopo() {
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setOpaque(false);

        // <h1>Relatórios</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Relatórios");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.add(titulo);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

        // Primeira .tb: Abas em formato pílula (.tab / .tab.on)
        JPanel barraAbas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraAbas.setOpaque(false);
        barraAbas.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < ABAS_RELATORIO.length; i++) {
            String nomeAba = ABAS_RELATORIO[i];
            BotaoAbaPilula botaoAba = new BotaoAbaPilula(nomeAba, "Completo".equals(nomeAba));
            botaoAba.addActionListener(e -> selecionarAba(nomeAba));
            botoesAbas.add(botaoAba);
            if (i > 0) {
                barraAbas.add(Box.createRigidArea(new Dimension(10, 0)));
            }
            barraAbas.add(botaoAba);
        }
        topo.add(barraAbas);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

        // Segunda .tb: De: 01/11/2026 | Até: 03/11/2026 | Gerar relatório completo | Exportar PDF | Imprimir
        JPanel barraFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraFiltros.setOpaque(false);
        barraFiltros.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension dimInput = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        campoDataDe.setPreferredSize(dimInput);
        campoDataDe.setMinimumSize(dimInput);
        campoDataAte.setPreferredSize(dimInput);
        campoDataAte.setMinimumSize(dimInput);

        barraFiltros.add(campoDataDe);
        barraFiltros.add(Box.createRigidArea(new Dimension(10, 0)));
        barraFiltros.add(campoDataAte);
        barraFiltros.add(Box.createRigidArea(new Dimension(10, 0)));
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
            // Em RelVendaDetalhe.dc.html (Vendas): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_VENDAS);
        } else if ("Estoque baixo".equalsIgnoreCase(this.abaAtiva)) {
            // Em RelEstoqueBaixo.dc.html (Estoque baixo): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_ESTOQUE_BAIXO);
        } else if ("Mais vendidos".equalsIgnoreCase(this.abaAtiva)) {
            // Em Relatorios.dc.html (Mais vendidos): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_MAIS_VENDIDOS);
        } else if ("Menos vendidos".equalsIgnoreCase(this.abaAtiva)) {
            // Em RelMenosVendidos.dc.html (Menos vendidos): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_MENOS_VENDIDOS);
        } else if ("Reembolsos".equalsIgnoreCase(this.abaAtiva)) {
            // Em RelReembolsos.dc.html (Reembolsos): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_REEMBOLSOS);
        } else if ("Auditoria".equalsIgnoreCase(this.abaAtiva)) {
            // Em RelAuditoria.dc.html (Auditoria): <span class="bt">Gerar</span><span class="bt s">Exportar</span>
            containerBotoesFiltro.add(botaoGerarSimples);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarSimples);
            layoutCorpo.show(containerCorpo, CARD_AUDITORIA);
        } else {
            // Em RelCompleto.dc.html (Completo): Gerar relatório completo | Exportar PDF | Imprimir
            containerBotoesFiltro.add(botaoGerarRelatorio);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoExportarPdf);
            containerBotoesFiltro.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFiltro.add(botaoImprimir);
            layoutCorpo.show(containerCorpo, CARD_COMPLETO);
        }

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
            alinhadorTopo.add(cardDetalheVenda, BorderLayout.NORTH);
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

    /**
     * Corpo principal contendo:
     * - .row de 5 KPIs
     * - Grid 3x2 (repeat(3, 1fr); gap: 16px) com 6 cards de mini-tabelas
     */
    private JPanel criarCorpoRelatorioCompleto() {
        JPanel corpo = new JPanel(new BorderLayout(0, 16));
        corpo.setOpaque(false);

        corpo.add(criarLinhaKpis(), BorderLayout.NORTH);
        corpo.add(criarGradeSecoes(), BorderLayout.CENTER);
        return corpo;
    }

    /**
     * Corpo da sub-aba "Vendas" fiel a RelVendaDetalhe.dc.html:
     * <div class="row" style="align-items:flex-start">
     *   <div class="card" style="flex:1;padding:0"><table>...</table></div>
     *   <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">...</div>
     * </div>
     */
    private JPanel criarCorpoVendas() {
        JPanel corpo = new JPanel(new BorderLayout());
        corpo.setOpaque(false);

        JPanel cardTabela = criarCardTabelaRanking(tabelaVendas);
        corpo.add(cardTabela, BorderLayout.CENTER);
        corpo.add(wrapperDireitaVenda, BorderLayout.EAST);
        return corpo;
    }

    private JTable criarTabelaVendas() {
        DefaultTableModel modelo = new DefaultTableModel(DADOS_VENDAS, COLUNAS_VENDAS) {
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

        // Em RelVendaDetalhe.dc.html, a linha 1041 (índice 1) aparece selecionada (#2A3347)
        t.setRowSelectionInterval(1, 1);
        t.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && t.getSelectedRow() >= 0 && !detalheVendaVisivel) {
                mostrarDetalheVenda(true);
            }
        });

        return t;
    }

    private JTable criarTabelaItensVendaDetalhe() {
        DefaultTableModel modelo = new DefaultTableModel(DADOS_ITENS_VENDA_DETALHE, COLUNAS_ITENS_VENDA_DETALHE) {
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

    /**
     * Card lateral "Venda 1041" (width:420px; height:fit-content; padding:16px; gap:12px)
     * fiel a RelVendaDetalhe.dc.html.
     */
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

        // <b>Venda 1041</b>
        JLabel titulo = new JLabel("Venda 1041");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;gap:10px;align-items:center"><span>João Sitoe</span><span class="ch g">CONCLUÍDA</span></div>
        JPanel linhaClienteEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaClienteEstado.setOpaque(false);
        linhaClienteEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelCliente = new JLabel("João Sitoe");
        labelCliente.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelCliente.setForeground(UiStyle.TEXT);
        linhaClienteEstado.add(labelCliente);
        linhaClienteEstado.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaClienteEstado.add(new ChipEstado("CONCLUÍDA", UiStyle.CHIP_GREEN_BG, UiStyle.CHIP_GREEN_FG));
        card.add(linhaClienteEstado);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="k">Funcionário: Carlos</div>
        JLabel labelFuncionario = new JLabel("Funcionário: Carlos");
        labelFuncionario.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelFuncionario.setForeground(UiStyle.MUTED);
        labelFuncionario.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelFuncionario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="mt"><table>...</table></div>
        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabelaWrapper.add(tabelaItensVendaDetalhe.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(tabelaItensVendaDetalhe, BorderLayout.CENTER);
        card.add(tabelaWrapper);
        card.add(Box.createRigidArea(new Dimension(0, 18))); // gap:12px + margin-top:6px

        // <div style="display:flex;justify-content:space-between;padding:6px 0;font-size:20px;font-weight:700;border-top:1px solid #2A3040;margin-top:6px;padding-top:12px">
        //   <span class="k" style="font-size:14px;color:#E8ECF4">Total</span><span>840,00 MT</span>
        // </div>
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

        JLabel labelTotalValor = new JLabel("840,00 MT");
        labelTotalValor.setFont(UiStyle.fonte(20, Font.BOLD));
        labelTotalValor.setForeground(UiStyle.TEXT);

        linhaTotal.add(labelTotalTexto, BorderLayout.WEST);
        linhaTotal.add(labelTotalValor, BorderLayout.EAST);
        card.add(linhaTotal);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt s">Fechar</span></div>
        JPanel barraFechar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraFechar.setOpaque(false);
        barraFechar.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraFechar.add(botaoFecharDetalheVenda);
        card.add(barraFechar);

        return card;
    }

    /**
     * Corpo das sub-abas "Mais vendidos" (Relatorios.dc.html) e "Menos vendidos" (RelMenosVendidos.dc.html):
     * <div class="card" style="padding:0"><table><tr><th>#</th><th>Produto</th><th>Qtd vendida</th><th>Receita</th></tr>...</table></div>
     */
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

    /**
     * <div class="row"> com 5 cards (flex:1; padding:12px 16px)
     */
    private JPanel criarLinhaKpis() {
        JPanel linha = new JPanel(new GridLayout(1, 5, 16, 0));
        linha.setOpaque(false);

        linha.add(criarCardKpi("Vendas no período", "4"));
        linha.add(criarCardKpi("Total concluído", "840,00 MT"));
        linha.add(criarCardKpi("Total reembolsado", "105,00 MT"));
        linha.add(criarCardKpi("Produtos com estoque baixo", "4"));
        linha.add(criarCardKpi("Produtos a expirar", "5"));

        return linha;
    }

    private JPanel criarCardKpi(String rotulo, String valor) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // <div class="k">...</div> (.k { font-size:12px; color:#A3ADC2 })
        JLabel labelRotulo = new JLabel(rotulo);
        labelRotulo.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelRotulo.setForeground(UiStyle.MUTED);
        labelRotulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // <div style="font-size:22px;font-weight:700;margin-top:2px">...</div>
        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(UiStyle.fonte(22, Font.BOLD));
        labelValor.setForeground(UiStyle.TEXT);
        labelValor.setBorder(new EmptyBorder(2, 0, 0, 0));
        labelValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(labelRotulo);
        card.add(labelValor);
        return card;
    }

    /**
     * <div style="display:grid;grid-template-columns:repeat(3,1fr);gap:16px">
     */
    private JPanel criarGradeSecoes() {
        JPanel grade = new JPanel(new GridLayout(2, 3, 16, 16));
        grade.setOpaque(false);

        // 1. Vendas por estado
        grade.add(criarCardMiniTabela(
                "Vendas por estado",
                new String[] {"Estado", "Qtd", "Total"},
                new Object[][] {
                    {"PENDENTE", "1", "1.250,00 MT"},
                    {"CONCLUÍDA", "1", "840,00 MT"},
                    {"ANULADA", "1", "300,00 MT"},
                    {"REEMBOLSADA", "1", "2.100,00 MT"}
                },
                true
        ));

        // 2. Mais vendidos
        grade.add(criarCardMiniTabela(
                "Mais vendidos",
                new String[] {"Produto", "Qtd"},
                new Object[][] {
                    {"Pão", "320"},
                    {"Leite 1L", "210"},
                    {"Água 1,5L", "180"}
                },
                false
        ));

        // 3. Menos vendidos
        grade.add(criarCardMiniTabela(
                "Menos vendidos",
                new String[] {"Produto", "Qtd"},
                new Object[][] {
                    {"Biscoito", "0"},
                    {"Óleo 5L", "4"},
                    {"Sal 1kg", "5"}
                },
                false
        ));

        // 4. Estoque baixo
        grade.add(criarCardMiniTabela(
                "Estoque baixo",
                new String[] {"Produto", "Actual", "Mín."},
                new Object[][] {
                    {"Arroz 25kg", "4", "10"},
                    {"Óleo 5L", "2", "8"},
                    {"Açúcar 1kg", "6", "15"}
                },
                false
        ));

        // 5. Reembolsos
        grade.add(criarCardMiniTabela(
                "Reembolsos",
                new String[] {"Item", "Qtd", "Valor"},
                new Object[][] {
                    {"Leite 1L", "1", "85,00 MT"},
                    {"Pão", "4", "20,00 MT"}
                },
                false
        ));

        // 6. Auditoria
        grade.add(criarCardMiniTabela(
                "Auditoria",
                new String[] {"Tabela", "Operação", "Registos"},
                new Object[][] {
                    {"Vendas", "Registo criado", "12"},
                    {"Estoque", "Registo alterado", "8"},
                    {"Produtos", "Registo alterado", "3"}
                },
                false
        ));

        return grade;
    }

    /**
     * <div class="card" style="padding:12px">
     *   <b style="font-size:13px">...</b>
     *   <div class="mt" style="margin-top:6px"><table>...</table></div>
     * </div>
     */
    private JPanel criarCardMiniTabela(
            String tituloSecao,
            String[] colunas,
            Object[][] dados,
            boolean primeiraColunaChipEstado) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout(0, 6));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(tituloSecao);
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        card.add(titulo, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
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
        tabelaWrapper.add(header, BorderLayout.NORTH);
        tabelaWrapper.add(miniTabela, BorderLayout.CENTER);

        card.add(tabelaWrapper, BorderLayout.CENTER);
        return card;
    }

    /**
     * th { text-align:left; color:#A3ADC2; font-weight:600; padding:11px 14px; border-bottom:1px solid #2A3040 }
     */
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

    /**
     * td { padding:11px 14px; border-bottom:1px solid #242B3B }
     */
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

    /**
     * Renderiza os chips de Estado em tabelas padrão (padding: 11px 14px, com suporte a seleção de linha #2A3347):
     * PENDENTE (.ch.y), CONCLUÍDA (.ch.g), ANULADA (.ch.r), REEMBOLSADA (.ch.b)
     */
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

    /**
     * .mt th { padding:6px 8px; font-size:12px; color:#A3ADC2; font-weight:600; border-bottom:1px solid #2A3040 }
     */
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

    /**
     * .mt td { padding:6px 8px; font-size:12px; border-bottom:1px solid #242B3B }
     */
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

    /**
     * Renderiza os chips da tabela "Vendas por estado":
     * PENDENTE (.ch.y), CONCLUÍDA (.ch.g), ANULADA (.ch.r), REEMBOLSADA (.ch.b)
     */
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

        private final Color corFundo;

        ChipEstado(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonteSemiBold(12));
            setForeground(corTexto);
            setBorder(new EmptyBorder(3, 10, 3, 10));
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

    /**
     * Aba em pílula (.tab / .tab.on):
     * .tab { padding:7px 14px; border-radius:99px; font-size:13px; background:#232A3B; color:#A3ADC2 }
     * .tab.on { background:#F5B638; color:#12151C; font-weight:600 }
     */
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
                g2.setColor(ativa ? UiStyle.ACCENT : UiStyle.SECONDARY_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Campo de data estilo .in ("De: 01/11/2026" / "Até: 03/11/2026").
     */
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

    /**
     * Botão da toolbar (.bt / .bt.s) com padding 9px 16px, altura 38px e border-radius 8px.
     */
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
            Dimension pref = getPreferredSize();
            setPreferredSize(new Dimension(pref.width, ALTURA_INPUT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;

                if (preencherFundo && corFundo != null) {
                    Color atual = corFundo;
                    if (getModel().isPressed()) {
                        atual = corFundo.darker();
                    } else if (getModel().isRollover()) {
                        atual = new Color(
                                Math.min(255, corFundo.getRed() + 10),
                                Math.min(255, corFundo.getGreen() + 10),
                                Math.min(255, corFundo.getBlue() + 10)
                        );
                    }
                    g2.setColor(atual);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                }

                if (corBorda != null) {
                    g2.setColor(corBorda);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Card (.card) com background #1B2030, border 1px #2A3040 e border-radius 14px.
     */
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
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
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
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(corBorda);
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

    public void carregarRelatorioAtual() {
        Utilizador user = obterUtilizadorAutenticadoOuPadrao();
        LocalDateTime inicio = extrairDataInicio();
        LocalDateTime fim = extrairDataFim();

        if ("Estoque baixo".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioBO.estoqueBaixo(user),
                lista -> {
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaEstoqueBaixo.getModel();
                        m.setRowCount(0);
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
                erro -> {}
            );
        } else if ("Mais vendidos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioBO.produtosMaisVendidos(user),
                lista -> {
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaMaisVendidos.getModel();
                        m.setRowCount(0);
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
                erro -> {}
            );
        } else if ("Menos vendidos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> {
                    List<Map<String, Object>> lista = relatorioBO.produtosMaisVendidos(user);
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
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaMenosVendidos.getModel();
                        m.setRowCount(0);
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
                erro -> {}
            );
        } else if ("Reembolsos".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioBO.reembolsosPeriodo(user, inicio, fim),
                lista -> {
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaReembolsos.getModel();
                        m.setRowCount(0);
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
                erro -> {}
            );
        } else if ("Auditoria".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioBO.resumoAuditoria(user, inicio, fim),
                lista -> {
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaAuditoria.getModel();
                        m.setRowCount(0);
                        for (Map<String, Object> map : lista) {
                            String dia = String.valueOf(map.getOrDefault("dia", ""));
                            String tabela = String.valueOf(map.getOrDefault("tabela_afetada", ""));
                            String operacao = String.valueOf(map.getOrDefault("operacao", ""));
                            String qtd = String.valueOf(map.getOrDefault("quantidade", ""));
                            m.addRow(new Object[] {dia, tabela, operacao, qtd});
                        }
                    }
                },
                erro -> {}
            );
        } else if ("Vendas".equalsIgnoreCase(abaAtiva)) {
            AsyncTask.executar(
                () -> relatorioBO.vendasPeriodo(user, inicio, fim),
                lista -> {
                    if (lista != null && !lista.isEmpty()) {
                        DefaultTableModel m = (DefaultTableModel) tabelaVendas.getModel();
                        m.setRowCount(0);
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
                },
                erro -> {}
            );
        }
    }

    @Override
    public void recarregarDados() {
        if (abaAtiva != null) {
            selecionarAba(abaAtiva);
            carregarRelatorioAtual();
        }
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        // Se for VENDEDOR, a navegação oculta essa aba
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        repaint();
    }
}
