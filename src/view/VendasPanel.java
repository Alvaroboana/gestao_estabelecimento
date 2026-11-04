package view;

import com.formdev.flatlaf.FlatClientProperties;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import model.bo.ReembolsoBO;
import model.bo.VendaBO;
import model.vo.EstadoVenda;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import model.vo.Venda;
import util.AsyncTask;

/**
 * Painel Vendas e Nova Venda em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Vendas" (Vendas.dc.html)
 * - "ADMIN · Barra superior · Escuro — Vendas — nova venda" (NovaVenda.dc.html)
 * - "ADMIN · Barra superior · Escuro — Vendas — anular" (ConfirmarAnular.dc.html)
 * - "ADMIN · Barra superior · Escuro — Vendas — reembolsar" (Reembolso.dc.html)
 */
public class VendasPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVA_VENDA,
        CONFIRMAR_ANULAR,
        REEMBOLSAR_ITEM
    }

    private static final String CARD_NOVA_VENDA = "NOVA_VENDA";
    private static final String CARD_CONFIRMAR_ANULAR = "CONFIRMAR_ANULAR";
    private static final String CARD_REEMBOLSAR_ITEM = "REEMBOLSAR_ITEM";

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] COLUNAS_COMPLETAS = {
        "ID", "Data", "Total", "Estado", "Cliente", "Funcionário"
    };

    private static final String[] COLUNAS_COMPACTAS = {
        "ID", "Data", "Total", "Estado", "Cliente"
    };

    private static final Object[][] DADOS_INICIAIS = {
        {"1042", "03/11/2026", "1.250,00 MT", "PENDENTE", "Ana Macie", "Carlos"},
        {"1041", "03/11/2026", "840,00 MT", "CONCLUÍDA", "João Sitoe", "Carlos"},
        {"1040", "02/11/2026", "300,00 MT", "ANULADA", "Maria Nhaca", "Sara"},
        {"1039", "02/11/2026", "2.100,00 MT", "REEMBOLSADA", "Ana Macie", "Sara"}
    };

    private static final String[] COLUNAS_ITENS = {
        "Item", "Qtd", "Subtotal", ""
    };

    private static final Object[][] ITENS_INICIAIS_NOVA_VENDA = {
        {"Leite 1L", "2", "170,00 MT", "✕"},
        {"Pão", "10", "50,00 MT", "✕"},
        {"Arroz 25kg", "1", "1.710,00 MT", "✕"}
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final BotaoSeletorEstado seletorEstado;
    private final CampoBuscaArredondado campoBuscaId;
    private final JButton botaoNovaVenda;
    private final BotaoToolbarArredondado botaoAnular;
    private final BotaoToolbarArredondado botaoReembolsar;
    private final JPanel containerToolbarBotoes;
    private final JPanel cardNovaVenda;
    private final JPanel cardConfirmarAnular;
    private final JPanel cardReembolsarItem;
    private final CardLayout layoutLateral;
    private final JPanel containerLateral;

    // Campos dinâmicos do card "Anular venda"
    private JLabel labelVendaSelecionada;
    private ChipEstado chipEstadoAnular;
    private JLabel labelValorClienteAnular;
    private JLabel labelValorTotalAnular;

    // Campos dinâmicos do card "Reembolsar item"
    private JLabel labelVendaReembolso;
    private ChipEstado chipEstadoReembolso;

    private final VendaBO vendaBO;
    private final ReembolsoBO reembolsoBO;
    private List<Object[]> dadosVendasCarregados = null;
    private List<Venda> listaVendasEntidades = null;

    private ModoPainelLateral modoLateral;
    private String estadoFiltro = "Todos";

    public VendasPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public VendasPanel(boolean exibirNovaVendaInicialmente) {
        this(exibirNovaVendaInicialmente ? ModoPainelLateral.NOVA_VENDA : ModoPainelLateral.NENHUM);
    }

    public VendasPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;
        this.vendaBO = new VendaBO();
        this.reembolsoBO = new ReembolsoBO();

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.seletorEstado = criarSeletorEstado();
        this.campoBuscaId = criarCampoBuscaId();
        this.botaoNovaVenda = criarBotaoNovaVenda();
        this.botaoReembolsar = criarBotaoReembolsar();
        this.botaoAnular = criarBotaoAnular();
        this.containerToolbarBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerToolbarBotoes.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovaVenda = criarCardNovaVenda();
        this.cardConfirmarAnular = criarCardConfirmarAnular();
        this.cardReembolsarItem = criarCardReembolsarItem();

        this.layoutLateral = new CardLayout();
        this.containerLateral = new JPanel(layoutLateral);
        this.containerLateral.setOpaque(false);
        this.containerLateral.add(cardNovaVenda, CARD_NOVA_VENDA);
        this.containerLateral.add(cardConfirmarAnular, CARD_CONFIRMAR_ANULAR);
        this.containerLateral.add(cardReembolsarItem, CARD_REEMBOLSAR_ITEM);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosVendaParaAnular();
                sincronizarDadosVendaParaReembolso();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        definirModoPainelLateral(this.modoLateral);
    }

    public JTable getTabela() {
        return tabela;
    }

    public JTextField getCampoBuscaId() {
        return campoBuscaId;
    }

    public JButton getBotaoNovaVenda() {
        return botaoNovaVenda;
    }

    public JButton getBotaoAnular() {
        return botaoAnular;
    }

    public JButton getBotaoReembolsar() {
        return botaoReembolsar;
    }

    public JPanel getCardNovaVenda() {
        return cardNovaVenda;
    }

    public JPanel getCardConfirmarAnular() {
        return cardConfirmarAnular;
    }

    public JPanel getCardReembolsarItem() {
        return cardReembolsarItem;
    }

    public boolean isNovaVendaVisivel() {
        return modoLateral == ModoPainelLateral.NOVA_VENDA;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public final void definirNovaVendaVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVA_VENDA : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelAnular() {
        definirModoPainelLateral(ModoPainelLateral.CONFIRMAR_ANULAR);
    }

    public final void exibirPainelReembolso() {
        definirModoPainelLateral(ModoPainelLateral.REEMBOLSAR_ITEM);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean lateralAberto = this.modoLateral != ModoPainelLateral.NENHUM;
        containerLateral.setVisible(lateralAberto);

        if (this.modoLateral == ModoPainelLateral.NOVA_VENDA) {
            layoutLateral.show(containerLateral, CARD_NOVA_VENDA);
        } else if (this.modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR) {
            layoutLateral.show(containerLateral, CARD_CONFIRMAR_ANULAR);
        } else if (this.modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM) {
            layoutLateral.show(containerLateral, CARD_REEMBOLSAR_ITEM);
        }

        // Em NovaVenda.dc.html a ordem após "Nova venda" é: Reembolsar (.bt.s), Anular (.bt.d)
        // Em Vendas.dc.html a ordem é: Anular (.bt.s), Reembolsar (.bt.s)
        containerToolbarBotoes.removeAll();
        if (lateralAberto) {
            botaoAnular.configurarEstilo(new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);
            containerToolbarBotoes.add(botaoReembolsar);
            containerToolbarBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarBotoes.add(botaoAnular);
        } else {
            botaoAnular.configurarEstilo(UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true);
            containerToolbarBotoes.add(botaoAnular);
            containerToolbarBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarBotoes.add(botaoReembolsar);
        }

        atualizarEstruturaTabela();
        atualizarTabelaFiltrada();

        if (this.modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR && tabela.getRowCount() > 0) {
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosVendaParaAnular();
        } else if (this.modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM && tabela.getRowCount() > 0) {
            int indiceVenda1041 = Math.min(1, tabela.getRowCount() - 1);
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(indiceVenda1041, indiceVenda1041);
            }
            sincronizarDadosVendaParaReembolso();
        } else {
            tabela.clearSelection();
        }

        revalidate();
        repaint();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        // <h1>Vendas</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Vendas");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        // .tb { display:flex; gap:10px; align-items:center }
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(seletorEstado);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(campoBuscaId);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoNovaVenda);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(containerToolbarBotoes);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    /**
     * .row { display:flex; gap:16px; align-items:flex-start }
     * Esquerda: card da tabela (flex:1)
     * Direita: card Nova venda (width:420px; height:fit-content)
     */
    private JPanel criarCorpoPrincipal() {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.add(criarCardTabela(), BorderLayout.CENTER);

        JPanel wrapperDireita = new JPanel(new BorderLayout());
        wrapperDireita.setOpaque(false);
        wrapperDireita.add(containerLateral, BorderLayout.NORTH);
        linha.add(wrapperDireita, BorderLayout.EAST);

        return linha;
    }

    /**
     * <span class="in">Estado: Todos ▾</span>
     * .in { height:38px; border:1px solid #3A4256; border-radius:8px; background:#12151C;
     *       padding:0 12px; font-size:13px; color:#A3ADC2; min-width:200px }
     */
    private BotaoSeletorEstado criarSeletorEstado() {
        BotaoSeletorEstado botao = new BotaoSeletorEstado("Estado: Todos ▾");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        String[] opcoes = {"Todos", "PENDENTE", "CONCLUÍDA", "ANULADA", "REEMBOLSADA"};
        botao.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(UiStyle.SURFACE);
            menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
            for (String opcao : opcoes) {
                JMenuItem item = new JMenuItem(opcao);
                item.setFont(UiStyle.fonte(13, Font.PLAIN));
                item.setBackground(UiStyle.SURFACE);
                item.setForeground(UiStyle.TEXT);
                item.addActionListener(ev -> {
                    estadoFiltro = opcao;
                    botao.setText("Estado: " + opcao + " ▾");
                    atualizarTabelaFiltrada();
                });
                menu.add(item);
            }
            menu.show(botao, 0, botao.getHeight() + 2);
        });
        return botao;
    }

    /**
     * <span class="in">Buscar por ID</span>
     */
    private CampoBuscaArredondado criarCampoBuscaId() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar por ID");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        campo.setPreferredSize(dim);
        campo.setMinimumSize(dim);
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }
        });
        return campo;
    }

    /**
     * .bt { border:0; border-radius:8px; padding:9px 16px; font-size:13px; font-weight:600; background:#F5B638; color:#12151C }
     * .bt.s { background:#232A3B; color:#E8ECF4; border:1px solid #3A4256 }
     */
    private JButton criarBotaoNovaVenda() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Nova venda", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVA_VENDA) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVA_VENDA);
            }
        });
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoReembolsar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Reembolsar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                tabela.clearSelection();
                definirModoPainelLateral(ModoPainelLateral.REEMBOLSAR_ITEM);
            }
        });
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoAnular() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Anular", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                tabela.clearSelection();
                definirModoPainelLateral(ModoPainelLateral.CONFIRMAR_ANULAR);
            }
        });
        return botao;
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[0][COLUNAS_COMPACTAS.length], COLUNAS_COMPACTAS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable criarTabela(DefaultTableModel modelo) {
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
        header.setDefaultRenderer(new RenderizadorCabecalho());

        aplicarRenderizadoresColunas(t);
        return t;
    }

    private void aplicarRenderizadoresColunas(JTable t) {
        RenderizadorCelulaTexto renderizadorTexto = new RenderizadorCelulaTexto();
        RenderizadorCelulaEstado renderizadorEstado = new RenderizadorCelulaEstado();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 3) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }
    }

    private void atualizarEstruturaTabela() {
        boolean lateralAberto = modoLateral != ModoPainelLateral.NENHUM;
        String[] colunas = lateralAberto ? COLUNAS_COMPACTAS : COLUNAS_COMPLETAS;
        modeloTabela.setColumnIdentifiers(colunas);
        aplicarRenderizadoresColunas(tabela);
    }

    /**
     * <div class="card" style="padding:0"><table>...</table></div>
     */
    private JPanel criarCardTabela() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout());
        // 1px de margem interna para que a tabela respeite o contorno arredondado de 14px
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

    /**
     * Card lateral "Nova venda" fiel a NovaVenda.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:8px">
     */
    private JPanel criarCardNovaVenda() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Nova venda</b>
        JLabel titulo = new JLabel("Nova venda");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Bloco Cliente: <span class="k">Cliente</span> + <div class="in">Ana Macie ▾</div>
        JPanel blocoCliente = criarCampoComLabel("Cliente", new CaixaValorArredondada("Ana Macie ▾"));
        fixarLargura(blocoCliente, larguraInterna, 60);
        card.add(blocoCliente);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Grid 2fr 1fr 1fr (gap: 8px): Produto (Leite 1L ▾), Qtd (2), Desc. (0%)
        JPanel linhaProduto = new JPanel(new GridBagLayout());
        linhaProduto.setOpaque(false);
        linhaProduto.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.gridx = 0;
        gbcProd.gridy = 0;
        gbcProd.weightx = 2.0;
        gbcProd.fill = GridBagConstraints.HORIZONTAL;
        gbcProd.insets = new Insets(0, 0, 0, 8);
        linhaProduto.add(criarCampoComLabel("Produto", new CaixaValorArredondada("Leite 1L ▾")), gbcProd);

        GridBagConstraints gbcQtd = new GridBagConstraints();
        gbcQtd.gridx = 1;
        gbcQtd.gridy = 0;
        gbcQtd.weightx = 1.0;
        gbcQtd.fill = GridBagConstraints.HORIZONTAL;
        gbcQtd.insets = new Insets(0, 0, 0, 8);
        linhaProduto.add(criarCampoComLabel("Qtd", new CaixaValorArredondada("2")), gbcQtd);

        GridBagConstraints gbcDesc = new GridBagConstraints();
        gbcDesc.gridx = 2;
        gbcDesc.gridy = 0;
        gbcDesc.weightx = 1.0;
        gbcDesc.fill = GridBagConstraints.HORIZONTAL;
        linhaProduto.add(criarCampoComLabel("Desc.", new CaixaValorArredondada("0%")), gbcDesc);

        fixarLargura(linhaProduto, larguraInterna, 60);
        card.add(linhaProduto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <span class="bt s" style="text-align:center">+ Adicionar item</span>
        BotaoToolbarArredondado botaoAdicionarItem = new BotaoToolbarArredondado(
                "+ Adicionar item", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoAdicionarItem.setHorizontalAlignment(SwingConstants.CENTER);
        fixarLargura(botaoAdicionarItem, larguraInterna, 38);
        card.add(botaoAdicionarItem);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Mini-tabela .mt (td/th padding: 6px 8px; font-size: 12px)
        JPanel miniTabelaPanel = criarMiniTabelaItens(larguraInterna);
        card.add(miniTabelaPanel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Linha Total: border-top:1px solid #2A3040; padding-top:8px
        JPanel linhaTotal = new JPanel(new BorderLayout());
        linhaTotal.setOpaque(false);
        linhaTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiStyle.BORDER),
                new EmptyBorder(8, 0, 0, 0)
        ));

        JLabel labelTextoTotal = new JLabel("Total (desconto −90,00 MT)");
        labelTextoTotal.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelTextoTotal.setForeground(UiStyle.MUTED);

        JLabel labelValorTotal = new JLabel("1.940,00 MT");
        labelValorTotal.setFont(UiStyle.fonte(20, Font.BOLD));
        labelValorTotal.setForeground(UiStyle.TEXT);

        linhaTotal.add(labelTextoTotal, BorderLayout.WEST);
        linhaTotal.add(labelValorTotal, BorderLayout.EAST);
        fixarLargura(linhaTotal, larguraInterna, 34);
        card.add(linhaTotal);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Grid 1fr 1fr (gap: 8px): Valor recebido (2.000,00 MT) | Troco (60,00 MT em #4A3A0F / #FFD36B bold)
        JPanel linhaPagamento = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaPagamento.setOpaque(false);

        CaixaValorArredondada caixaValorRecebido = new CaixaValorArredondada("2.000,00 MT");
        CaixaValorArredondada caixaTroco = new CaixaValorArredondada(
                "60,00 MT",
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_FG,
                UiStyle.fonte(13, Font.BOLD)
        );

        linhaPagamento.add(criarCampoComLabel("Valor recebido", caixaValorRecebido));
        linhaPagamento.add(criarCampoComLabel("Troco", caixaTroco));
        fixarLargura(linhaPagamento, larguraInterna, 60);
        card.add(linhaPagamento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // .tb: Finalizar venda (.bt) + Cancelar (.bt.s)
        JPanel barraAcoesCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraAcoesCard.setOpaque(false);

        BotaoToolbarArredondado botaoFinalizar = new BotaoToolbarArredondado(
                "Finalizar venda", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoFinalizar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Venda concluída e registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirNovaVendaVisivel(false);
            recarregarDados();
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.addActionListener(e -> definirNovaVendaVisivel(false));

        barraAcoesCard.add(botaoFinalizar);
        barraAcoesCard.add(Box.createRigidArea(new Dimension(10, 0)));
        barraAcoesCard.add(botaoCancelar);
        fixarLargura(barraAcoesCard, larguraInterna, 38);
        card.add(barraAcoesCard);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    /**
     * Card lateral "Anular venda" fiel a ConfirmarAnular.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardConfirmarAnular() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Anular venda</b>
        JLabel titulo = new JLabel("Anular venda");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;gap:10px;align-items:center"><span>Venda 1042</span><span class="ch y">PENDENTE</span></div>
        JPanel linhaVenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaVenda.setOpaque(false);

        labelVendaSelecionada = new JLabel("Venda 1042");
        labelVendaSelecionada.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelVendaSelecionada.setForeground(UiStyle.TEXT);

        chipEstadoAnular = new ChipEstado("PENDENTE", UiStyle.CHIP_YELLOW_BG, UiStyle.CHIP_YELLOW_FG);

        linhaVenda.add(labelVendaSelecionada);
        linhaVenda.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaVenda.add(chipEstadoAnular);
        fixarLargura(linhaVenda, larguraInterna, 24);
        card.add(linhaVenda);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Cliente</span><div class="in">Ana Macie</div></div>
        CaixaValorArredondada caixaCliente = new CaixaValorArredondada("Ana Macie");
        labelValorClienteAnular = caixaCliente.getLabelValor();
        JPanel blocoCliente = criarCampoComLabel("Cliente", caixaCliente);
        fixarLargura(blocoCliente, larguraInterna, 60);
        card.add(blocoCliente);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Total</span><div class="in">1.250,00 MT</div></div>
        CaixaValorArredondada caixaTotal = new CaixaValorArredondada("1.250,00 MT");
        labelValorTotalAnular = caixaTotal.getLabelValor();
        JPanel blocoTotal = criarCampoComLabel("Total", caixaTotal);
        fixarLargura(blocoTotal, larguraInterna, 60);
        card.add(blocoTotal);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="k" style="line-height:1.5">Só vendas PENDENTES podem ser anuladas. Esta acção não pode ser desfeita.</div>
        JLabel aviso = new JLabel("<html><div style='width:370px;line-height:1.5'>Só vendas PENDENTES podem ser anuladas. Esta acção não pode ser desfeita.</div></html>");
        aviso.setFont(UiStyle.fonte(12, Font.PLAIN));
        aviso.setForeground(UiStyle.MUTED);
        fixarLargura(aviso, larguraInterna, 22);
        card.add(aviso);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt" style="background:#FF9C9C;color:#4F1B1B">Confirmar anulação</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmar = new BotaoToolbarArredondado(
                "Confirmar anulação", UiStyle.ERROR_TEXT, UiStyle.ERROR_BG, null, RAIO_COMPONENTE, true
        );
        botaoConfirmar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para anular.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            try {
                Long idVenda = Long.parseLong(idStr);
                Venda vendaAlvo = (listaVendasEntidades != null) ? listaVendasEntidades.stream()
                        .filter(v -> v.getIdVenda().equals(idVenda))
                        .findFirst()
                        .orElse(null) : null;

                if (vendaAlvo == null) {
                    vendaAlvo = new Venda();
                    vendaAlvo.setIdVenda(idVenda);
                    vendaAlvo.setEstado(EstadoVenda.PENDENTE);
                }

                final Venda fVenda = vendaAlvo;
                Utilizador operador = SessaoUtilizador.getUtilizador();
                AsyncTask.executar(
                    () -> {
                        vendaBO.anular(fVenda, operador);
                        return null;
                    },
                    sucesso -> {
                        JOptionPane.showMessageDialog(this, "Venda #" + idVenda + " anulada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao anular venda: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de venda inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoConfirmar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void sincronizarDadosVendaParaAnular() {
        if (labelVendaSelecionada == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String id = String.valueOf(tabela.getValueAt(row, 0));
        String total = String.valueOf(tabela.getValueAt(row, 2));
        String estado = String.valueOf(tabela.getValueAt(row, 3));
        String cliente = String.valueOf(tabela.getValueAt(row, 4));

        labelVendaSelecionada.setText("Venda " + id);
        labelValorClienteAnular.setText(cliente);
        labelValorTotalAnular.setText(total);

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
        chipEstadoAnular.atualizarEstado(estado, bgChip, fgChip);
    }

    /**
     * Card lateral "Reembolsar item" fiel a Reembolso.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardReembolsarItem() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Reembolsar item</b>
        JLabel titulo = new JLabel("Reembolsar item");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;gap:10px;align-items:center"><span>Venda 1041</span><span class="ch g">CONCLUÍDA</span></div>
        JPanel linhaVenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaVenda.setOpaque(false);

        labelVendaReembolso = new JLabel("Venda 1041");
        labelVendaReembolso.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelVendaReembolso.setForeground(UiStyle.TEXT);

        chipEstadoReembolso = new ChipEstado("CONCLUÍDA", UiStyle.CHIP_GREEN_BG, UiStyle.CHIP_GREEN_FG);

        linhaVenda.add(labelVendaReembolso);
        linhaVenda.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaVenda.add(chipEstadoReembolso);
        fixarLargura(linhaVenda, larguraInterna, 24);
        card.add(linhaVenda);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Item</span><div class="in">Leite 1L ▾</div></div>
        JPanel blocoItem = criarCampoComLabel("Item", new CaixaValorArredondada("Leite 1L ▾"));
        fixarLargura(blocoItem, larguraInterna, 60);
        card.add(blocoItem);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> (.fm { display:grid; grid-template-columns:1fr 1fr; gap:12px })
        // Quantidade (máx. 3) -> 2 | Já reembolsado -> 1
        JPanel linhaQtd = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtd.setOpaque(false);
        linhaQtd.add(criarCampoComLabel("Quantidade (máx. 3)", new CaixaValorArredondada("2")));
        linhaQtd.add(criarCampoComLabel("Já reembolsado", new CaixaValorArredondada("1")));
        fixarLargura(linhaQtd, larguraInterna, 60);
        card.add(linhaQtd);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Motivo *</span><div class="in">Embalagem danificada</div></div>
        JPanel blocoMotivo = criarCampoComLabel("Motivo *", new CaixaValorArredondada("Embalagem danificada"));
        fixarLargura(blocoMotivo, larguraInterna, 60);
        card.add(blocoMotivo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Funcionário</span><div class="in">Sara ▾</div></div>
        JPanel blocoFuncionario = criarCampoComLabel("Funcionário", new CaixaValorArredondada("Sara ▾"));
        fixarLargura(blocoFuncionario, larguraInterna, 60);
        card.add(blocoFuncionario);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        // <div style="display:flex;justify-content:space-between;padding:6px 0;font-size:20px;font-weight:700;
        //             border-top:1px solid #2A3040;margin-top:6px;padding-top:12px">
        //   <span class="k" style="font-size:14px;color:#E8ECF4">Valor a reembolsar</span><span>170,00 MT</span>
        // </div>
        JPanel linhaValorReembolso = new JPanel(new BorderLayout());
        linhaValorReembolso.setOpaque(false);
        linhaValorReembolso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiStyle.BORDER),
                new EmptyBorder(12, 0, 6, 0)
        ));

        JLabel labelTextoReembolso = new JLabel("Valor a reembolsar");
        labelTextoReembolso.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelTextoReembolso.setForeground(UiStyle.TEXT);

        JLabel labelValorReembolso = new JLabel("170,00 MT");
        labelValorReembolso.setFont(UiStyle.fonte(20, Font.BOLD));
        labelValorReembolso.setForeground(UiStyle.TEXT);

        linhaValorReembolso.add(labelTextoReembolso, BorderLayout.WEST);
        linhaValorReembolso.add(labelValorReembolso, BorderLayout.EAST);
        fixarLargura(linhaValorReembolso, larguraInterna, 44);
        card.add(linhaValorReembolso);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Confirmar reembolso</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmar = new BotaoToolbarArredondado(
                "Confirmar reembolso", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoConfirmar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para reembolsar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Reembolso da venda #" + idStr + " processado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoConfirmar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void sincronizarDadosVendaParaReembolso() {
        if (labelVendaReembolso == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = Math.min(1, tabela.getRowCount() - 1);
        }
        String id = String.valueOf(tabela.getValueAt(row, 0));
        String estado = String.valueOf(tabela.getValueAt(row, 3));

        labelVendaReembolso.setText("Venda " + id);

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
        chipEstadoReembolso.atualizarEstado(estado, bgChip, fgChip);
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(textoLabel);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension pref = campo.getPreferredSize();
        campo.setPreferredSize(new Dimension(Math.max(60, pref.width), ALTURA_INPUT));
        campo.setMinimumSize(new Dimension(40, ALTURA_INPUT));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));

        painel.add(label);
        painel.add(Box.createRigidArea(new Dimension(0, 6)));
        painel.add(campo);
        return painel;
    }

    private JPanel criarMiniTabelaItens(int larguraInterna) {
        DefaultTableModel modeloItens = new DefaultTableModel(ITENS_INICIAIS_NOVA_VENDA, COLUNAS_ITENS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable miniTabela = new JTable(modeloItens);
        miniTabela.setRowHeight(29);
        miniTabela.setFont(UiStyle.fonte(12, Font.PLAIN));
        miniTabela.setBackground(UiStyle.SURFACE);
        miniTabela.setForeground(UiStyle.TEXT);
        miniTabela.setShowVerticalLines(false);
        miniTabela.setShowHorizontalLines(true);
        miniTabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
        miniTabela.setIntercellSpacing(new Dimension(0, 1));
        miniTabela.setSelectionBackground(UiStyle.SECONDARY_BG);
        miniTabela.setSelectionForeground(UiStyle.TEXT);

        JTableHeader header = miniTabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 29));
        header.setDefaultRenderer(new RenderizadorMiniCabecalho());

        RenderizadorMiniCelula renderizadorMini = new RenderizadorMiniCelula();
        for (int i = 0; i < miniTabela.getColumnModel().getColumnCount(); i++) {
            miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorMini);
        }
        miniTabela.getColumnModel().getColumn(0).setPreferredWidth(165);
        miniTabela.getColumnModel().getColumn(1).setPreferredWidth(55);
        miniTabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        miniTabela.getColumnModel().getColumn(3).setPreferredWidth(34);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(header, BorderLayout.NORTH);
        container.add(miniTabela, BorderLayout.CENTER);

        int alturaTotal = 29 + (ITENS_INICIAIS_NOVA_VENDA.length * 29);
        fixarLargura(container, larguraInterna, alturaTotal);
        return container;
    }

    private void fixarLargura(JComponent componente, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        componente.setPreferredSize(d);
        componente.setMinimumSize(d);
        componente.setMaximumSize(d);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void atualizarTabelaFiltrada() {
        String termoId = campoBuscaId.getTextoReal().trim().toLowerCase();
        boolean lateralAberto = modoLateral != ModoPainelLateral.NENHUM;
        List<Object[]> fonteDados = (dadosVendasCarregados != null) ? dadosVendasCarregados : Arrays.asList(DADOS_INICIAIS);
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] linha : fonteDados) {
            String id = String.valueOf(linha[0]).toLowerCase();
            String estado = String.valueOf(linha[3]);
            boolean combinaEstado = "Todos".equalsIgnoreCase(estadoFiltro) || estado.equalsIgnoreCase(estadoFiltro);
            boolean combinaId = termoId.isEmpty() || id.contains(termoId);
            if (combinaEstado && combinaId) {
                if (lateralAberto) {
                    filtrados.add(new Object[] {linha[0], linha[1], linha[2], linha[3], linha[4]});
                } else {
                    filtrados.add(linha);
                }
            }
        }

        modeloTabela.setRowCount(0);
        for (Object[] linha : filtrados) {
            modeloTabela.addRow(linha);
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
            label.setHorizontalAlignment(column == 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
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
     * Caixa de campo estilo .in usada no formulário de Nova Venda.
     */
    private static final class CaixaValorArredondada extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final JLabel labelValor;

        CaixaValorArredondada(String texto) {
            this(texto, UiStyle.INPUT_BG, UiStyle.INPUT_BORDER, UiStyle.MUTED, UiStyle.fonte(13, Font.PLAIN));
        }

        CaixaValorArredondada(String texto, Color corFundo, Color corBorda, Color corTexto, Font fonte) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            this.corFundo = corFundo;
            this.corBorda = corBorda;

            this.labelValor = new JLabel(texto);
            this.labelValor.setFont(fonte);
            this.labelValor.setForeground(corTexto);
            add(this.labelValor, BorderLayout.CENTER);
        }

        JLabel getLabelValor() {
            return labelValor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
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
     * th { text-align:left; color:#A3ADC2; font-weight:600; padding:11px 14px; border-bottom:1px solid #2A3040 }
     */
    private static final class RenderizadorCabecalho extends DefaultTableCellRenderer {
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
    private static final class RenderizadorCelulaTexto extends DefaultTableCellRenderer {
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
     * Renderiza os chips da coluna Estado:
     * .ch { padding:3px 10px; border-radius:99px; font-size:12px; font-weight:600 }
     * .y { background:#4A3A0F; color:#FFD36B } -> PENDENTE
     * .g { background:#12402A; color:#7FE3A8 } -> CONCLUÍDA
     * .r { background:#4F1B1B; color:#FF9C9C } -> ANULADA
     * .b { background:#17345E; color:#9CC4FF } -> REEMBOLSADA
     */
    private static final class RenderizadorCelulaEstado extends DefaultTableCellRenderer {
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
     * Seletor estilo .in ("Estado: Todos ▾").
     */
    private static final class BotaoSeletorEstado extends JButton {
        private static final long serialVersionUID = 1L;

        BotaoSeletorEstado(String texto) {
            super(texto);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setBackground(UiStyle.INPUT_BG);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                g2.setColor(UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Campo de texto estilo .in ("Buscar por ID").
     */
    private static final class CampoBuscaArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        private final String placeholder;
        private boolean exibindoPlaceholder;

        CampoBuscaArredondado(String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            this.exibindoPlaceholder = true;
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (exibindoPlaceholder) {
                        exibindoPlaceholder = false;
                        setText("");
                        setForeground(UiStyle.TEXT);
                    }
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        exibindoPlaceholder = true;
                        setText(placeholder);
                        setForeground(UiStyle.MUTED);
                    }
                    repaint();
                }
            });
        }

        String getTextoReal() {
            return exibindoPlaceholder ? "" : getText();
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
     * Botão da toolbar (.bt / .bt.s / .bt.d) com padding 9px 16px, altura 38px e border-radius 8px.
     */
    private static final class BotaoToolbarArredondado extends JButton {
        private static final long serialVersionUID = 1L;

        private Color corFundo;
        private Color corBorda;
        private boolean preencherFundo;
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

        void configurarEstilo(Color novoFundo, Color novoTexto, Color novaBorda, boolean preencher) {
            this.corFundo = novoFundo;
            this.corBorda = novaBorda;
            this.preencherFundo = preencher;
            setForeground(novoTexto);
            repaint();
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
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0x4F, 0x1B, 0x1B, 90));
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
     * Card da tabela com padding:0, background #1B2030, border 1px #2A3040 e border-radius 14px.
     */
    private static final class PainelCardTabela extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final int raio;

        PainelCardTabela(Color corFundo, Color corBorda, int raio) {
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

    public void abrirModoLateral(ModoPainelLateral modo) {
        definirModoPainelLateral(modo);
    }

    @Override
    public void recarregarDados() {
        AsyncTask.executar(
            () -> vendaBO.listarTodos(),
            vendas -> {
                this.listaVendasEntidades = vendas;
                if (vendas != null && !vendas.isEmpty()) {
                    List<Object[]> linhas = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Venda v : vendas) {
                        String idStr = String.valueOf(v.getIdVenda());
                        String dataStr = v.getData() != null ? v.getData().format(dtf) : "";
                        String totalStr = String.format(Locale.GERMANY, "%,.2f MT", v.getTotal() != null ? v.getTotal() : 0.0);
                        String estadoStr = v.getEstado() != null ? v.getEstado().name() : "PENDENTE";
                        String cliStr = (v.getCliente() != null && v.getCliente().getNome() != null)
                                ? v.getCliente().getNome() : "Consumidor";
                        String funcStr = (v.getFuncionario() != null && v.getFuncionario().getNome() != null)
                                ? v.getFuncionario().getNome() : "";
                        linhas.add(new Object[] {idStr, dataStr, totalStr, estadoStr, cliStr, funcStr});
                    }
                    this.dadosVendasCarregados = linhas;
                }
                atualizarTabelaFiltrada();
            },
            erro -> {
                atualizarTabelaFiltrada();
            }
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            if (botaoAnular != null) {
                botaoAnular.setVisible(false);
            }
        } else {
            if (botaoAnular != null) {
                botaoAnular.setVisible(true);
            }
        }
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        if (tabela != null) {
            tabela.setBackground(UiStyle.SURFACE);
            tabela.setForeground(UiStyle.TEXT);
            tabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
            tabela.getTableHeader().setBackground(UiStyle.SURFACE);
            tabela.getTableHeader().setForeground(UiStyle.MUTED);
        }
        repaint();
    }
}
