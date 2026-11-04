package view;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
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
import model.vo.Perfil;
import model.vo.Utilizador;

/**
 * Painel Produtos, Novo Produto, Editar Produto e Fornecedores do Produto em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Produtos" (Produtos.dc.html)
 * - "ADMIN · Barra superior · Escuro — Produto — novo" (ProdutoNovo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Produto — editar" (ProdutoEditar.dc.html)
 * - "ADMIN · Barra superior · Escuro — Produto — fornecedores" (ProdutoFornecedores.dc.html)
 */
public class ProdutosPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVO_PRODUTO,
        EDITAR_PRODUTO,
        FORNECEDORES_PRODUTO
    }

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_FORMULARIO = 360;
    private static final int LARGURA_CARD_FORNECEDORES = 420;

    private static final String[] COLUNAS = {
        "ID", "Nome", "Categoria", "Preço", "Estoque", "Estado"
    };

    private static final Object[][] DADOS_INICIAIS = {
        {"1", "Arroz 25kg", "Cereais", "1.800,00 MT", "4", "ATIVO"},
        {"2", "Óleo 5L", "Mercearia", "650,00 MT", "2", "ATIVO"},
        {"3", "Leite 1L", "Lacticínios", "85,00 MT", "120", "ATIVO"},
        {"4", "Biscoito", "Snacks", "40,00 MT", "0", "DESCONTINUADO"}
    };

    private static final String[] COLUNAS_FORNECEDORES_PRODUTO = {
        "Fornecedor", "Contactos", ""
    };

    private static final Object[][] DADOS_FORNECEDORES_PRODUTO = {
        {"Distribuidora Sol", "84 000 1111", "Remover"},
        {"Mercearia Atlas", "86 444 5555", "Remover"}
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final CampoBuscaArredondado campoBuscarProduto;
    private final BotaoSeletorCategoria seletorCategoria;
    private final JButton botaoNovoProduto;
    private final JButton botaoEditar;
    private final JButton botaoFornecedores;
    private final JButton botaoDescontinuar;
    private final JPanel cardNovoProduto;
    private final JPanel cardEditarProduto;
    private final JPanel cardFornecedoresProduto;
    private final JPanel wrapperDireita;

    // Controles do formulário "Novo produto"
    private JTextField campoNomeNovoProduto;
    private BotaoSeletorFormulario seletorCategoriaNovo;
    private BotaoSeletorFormulario seletorEstadoNovo;
    private JTextField campoPrecoNovoProduto;
    private BotaoSeletorFormulario seletorFornecedorNovo;
    private JTextField campoQuantidadeNovoProduto;
    private JTextField campoValidadeNovoProduto;

    // Controles do formulário "Editar produto"
    private JTextField campoNomeEditarProduto;
    private BotaoSeletorFormulario seletorCategoriaEditar;
    private BotaoSeletorFormulario seletorEstadoEditar;
    private JTextField campoPrecoEditarProduto;
    private BotaoSeletorFormulario seletorFornecedorEditar;

    // Controles do card "Fornecedores do produto"
    private JLabel labelNomeProdutoFornecedores;
    private BotaoSeletorFormulario seletorAssociarFornecedor;

    private ModoPainelLateral modoLateral;
    private String categoriaFiltro = "Todas";

    public ProdutosPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public ProdutosPanel(boolean exibirNovoProdutoInicialmente) {
        this(exibirNovoProdutoInicialmente ? ModoPainelLateral.NOVO_PRODUTO : ModoPainelLateral.NENHUM);
    }

    public ProdutosPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarProduto = criarCampoBuscarProduto();
        this.seletorCategoria = criarSeletorCategoria();
        this.botaoNovoProduto = criarBotaoNovoProduto();
        this.botaoEditar = criarBotaoEditarProduto();
        this.botaoFornecedores = criarBotaoFornecedoresProduto();
        this.botaoDescontinuar = criarBotaoToolbar("Descontinuar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovoProduto = criarCardNovoProduto();
        this.cardEditarProduto = criarCardEditarProduto();
        this.cardFornecedoresProduto = criarCardFornecedoresProduto();

        this.wrapperDireita = new JPanel(new BorderLayout());
        this.wrapperDireita.setOpaque(false);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosProdutoParaEdicao();
                sincronizarDadosProdutoParaFornecedores();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        atualizarTabelaFiltrada();
        definirModoPainelLateral(this.modoLateral);
    }

    public JTable getTabela() {
        return tabela;
    }

    public JTextField getCampoBuscarProduto() {
        return campoBuscarProduto;
    }

    public JButton getBotaoNovoProduto() {
        return botaoNovoProduto;
    }

    public JButton getBotaoEditar() {
        return botaoEditar;
    }

    public JButton getBotaoFornecedores() {
        return botaoFornecedores;
    }

    public JButton getBotaoDescontinuar() {
        return botaoDescontinuar;
    }

    public JPanel getCardNovoProduto() {
        return cardNovoProduto;
    }

    public JPanel getCardEditarProduto() {
        return cardEditarProduto;
    }

    public JPanel getCardFornecedoresProduto() {
        return cardFornecedoresProduto;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isNovoProdutoVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_PRODUTO;
    }

    public boolean isEditarProdutoVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_PRODUTO;
    }

    public boolean isFornecedoresProdutoVisivel() {
        return modoLateral == ModoPainelLateral.FORNECEDORES_PRODUTO;
    }

    public final void definirNovoProdutoVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVO_PRODUTO : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelEditar() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_PRODUTO);
    }

    public final void exibirPainelFornecedores() {
        definirModoPainelLateral(ModoPainelLateral.FORNECEDORES_PRODUTO);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean visivel = this.modoLateral != ModoPainelLateral.NENHUM;
        wrapperDireita.removeAll();
        wrapperDireita.setVisible(visivel);

        if (this.modoLateral == ModoPainelLateral.NOVO_PRODUTO) {
            wrapperDireita.add(cardNovoProduto, BorderLayout.NORTH);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_PRODUTO) {
            wrapperDireita.add(cardEditarProduto, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosProdutoParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.FORNECEDORES_PRODUTO) {
            wrapperDireita.add(cardFornecedoresProduto, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosProdutoParaFornecedores();
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

        // <h1>Produtos</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Produtos");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        // .tb { display:flex; gap:10px; align-items:center }
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(campoBuscarProduto);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(seletorCategoria);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoNovoProduto);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoEditar);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoFornecedores);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoDescontinuar);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    /**
     * <div class="row" style="align-items:flex-start">
     *   <div class="card" style="flex:1;padding:0">...</div>
     *   <div class="card" style="width:360px;height:fit-content;display:flex;flex-direction:column;gap:12px">...</div>
     * </div>
     */
    private JPanel criarCorpoPrincipal() {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.add(criarCardTabela(), BorderLayout.CENTER);
        linha.add(wrapperDireita, BorderLayout.EAST);
        return linha;
    }

    /**
     * <span class="in">Buscar produto</span>
     */
    private CampoBuscaArredondado criarCampoBuscarProduto() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar produto");
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
     * <span class="in">Categoria: Todas ▾</span>
     */
    private BotaoSeletorCategoria criarSeletorCategoria() {
        BotaoSeletorCategoria botao = new BotaoSeletorCategoria("Categoria: Todas ▾");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        String[] opcoes = {"Todas", "Cereais", "Mercearia", "Lacticínios", "Snacks"};
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
                    categoriaFiltro = opcao;
                    botao.setText("Categoria: " + opcao + " ▾");
                    atualizarTabelaFiltrada();
                });
                menu.add(item);
            }
            menu.show(botao, 0, botao.getHeight() + 2);
        });
        return botao;
    }

    private JButton criarBotaoNovoProduto() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo produto", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_PRODUTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_PRODUTO);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarProduto() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_PRODUTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_PRODUTO);
            }
        });
        return botao;
    }

    private JButton criarBotaoFornecedoresProduto() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Fornecedores", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.FORNECEDORES_PRODUTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.FORNECEDORES_PRODUTO);
            }
        });
        return botao;
    }

    private JButton criarBotaoToolbar(
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

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[0][COLUNAS.length], COLUNAS) {
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

        RenderizadorCelulaTexto renderizadorTexto = new RenderizadorCelulaTexto();
        RenderizadorCelulaEstado renderizadorEstado = new RenderizadorCelulaEstado();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 5) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }
        return t;
    }

    /**
     * <div class="card" style="padding:0"><table>...</table></div>
     */
    private JPanel criarCardTabela() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
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

    /**
     * Card lateral "Novo produto" fiel a ProdutoNovo.dc.html:
     * <div class="card" style="width:360px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardNovoProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32; // 328px

        // <b>Novo produto</b>
        JLabel titulo = new JLabel("Novo produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0"></div></div>
        campoNomeNovoProduto = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovoProduto);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> (.fm { display:grid; grid-template-columns:1fr 1fr; gap:12px })
        // Categoria * -> Cereais ▾ | Estado -> ATIVO ▾
        JPanel linhaCatEstado = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCatEstado.setOpaque(false);

        seletorCategoriaNovo = new BotaoSeletorFormulario(
                "Cereais ▾",
                new String[] {"Cereais", "Mercearia", "Lacticínios", "Snacks"}
        );
        seletorEstadoNovo = new BotaoSeletorFormulario(
                "ATIVO ▾",
                new String[] {"ATIVO", "DESCONTINUADO"}
        );
        linhaCatEstado.add(criarCampoComLabel("Categoria *", seletorCategoriaNovo));
        linhaCatEstado.add(criarCampoComLabel("Estado", seletorEstadoNovo));
        fixarLargura(linhaCatEstado, larguraInterna, 60);
        card.add(linhaCatEstado);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Preço (MT) *</span><div class="in" style="min-width:0"></div></div>
        campoPrecoNovoProduto = new CampoFormularioArredondado("");
        JPanel blocoPreco = criarCampoComLabel("Preço (MT) *", campoPrecoNovoProduto);
        fixarLargura(blocoPreco, larguraInterna, 60);
        card.add(blocoPreco);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Fornecedor</span><div class="in" style="min-width:0">Distribuidora Sol ▾</div></div>
        seletorFornecedorNovo = new BotaoSeletorFormulario(
                "Distribuidora Sol ▾",
                new String[] {"Distribuidora Sol", "Moagens do Sul", "Lacticínios Frescos"}
        );
        JPanel blocoFornecedor = criarCampoComLabel("Fornecedor", seletorFornecedorNovo);
        fixarLargura(blocoFornecedor, larguraInterna, 60);
        card.add(blocoFornecedor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Quantidade -> 0 | Validade -> dd/mm/aaaa
        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);

        campoQuantidadeNovoProduto = new CampoFormularioArredondado("0");
        campoValidadeNovoProduto = new CampoFormularioArredondado("dd/mm/aaaa");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade", campoQuantidadeNovoProduto));
        linhaQtdValidade.add(criarCampoComLabel("Validade", campoValidadeNovoProduto));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 18))); // gap:12px + margin-top:6px

        // <div class="tb" style="margin-top:6px"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardar.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"Guardar\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoGuardar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_FORMULARIO, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    /**
     * Card lateral "Editar produto" fiel a ProdutoEditar.dc.html:
     * <div class="card" style="width:360px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardEditarProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32; // 328px

        // <b>Editar produto</b>
        JLabel titulo = new JLabel("Editar produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0">Arroz 25kg</div></div>
        campoNomeEditarProduto = new CampoFormularioArredondado("Arroz 25kg");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditarProduto);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Categoria * -> Cereais ▾ | Estado -> ATIVO ▾
        JPanel linhaCatEstado = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCatEstado.setOpaque(false);

        seletorCategoriaEditar = new BotaoSeletorFormulario(
                "Cereais ▾",
                new String[] {"Cereais", "Mercearia", "Lacticínios", "Snacks"}
        );
        seletorEstadoEditar = new BotaoSeletorFormulario(
                "ATIVO ▾",
                new String[] {"ATIVO", "DESCONTINUADO"}
        );
        linhaCatEstado.add(criarCampoComLabel("Categoria *", seletorCategoriaEditar));
        linhaCatEstado.add(criarCampoComLabel("Estado", seletorEstadoEditar));
        fixarLargura(linhaCatEstado, larguraInterna, 60);
        card.add(linhaCatEstado);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Preço (MT) *</span><div class="in" style="min-width:0">1.800,00</div></div>
        campoPrecoEditarProduto = new CampoFormularioArredondado("1.800,00");
        JPanel blocoPreco = criarCampoComLabel("Preço (MT) *", campoPrecoEditarProduto);
        fixarLargura(blocoPreco, larguraInterna, 60);
        card.add(blocoPreco);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Fornecedor</span><div class="in" style="min-width:0">Distribuidora Sol ▾</div></div>
        seletorFornecedorEditar = new BotaoSeletorFormulario(
                "Distribuidora Sol ▾",
                new String[] {"Distribuidora Sol", "Moagens do Sul", "Lacticínios Frescos"}
        );
        JPanel blocoFornecedor = criarCampoComLabel("Fornecedor", seletorFornecedorEditar);
        fixarLargura(blocoFornecedor, larguraInterna, 60);
        card.add(blocoFornecedor);
        card.add(Box.createRigidArea(new Dimension(0, 18))); // gap:12px + margin-top:6px

        // <div class="tb" style="margin-top:6px">
        //   <span class="bt">Guardar alterações</span>
        //   <span class="bt s">Cancelar</span>
        //   <span class="bt d">Descontinuar</span>
        // </div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarAlteracoes = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true, 12
        );
        botaoGuardarAlteracoes.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"Guardar alterações\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 12
        );
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado botaoDescontinuarCard = new BotaoToolbarArredondado(
                "Descontinuar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false, 12
        );
        botaoDescontinuarCard.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"Descontinuar\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));

        barraBotoes.add(botaoGuardarAlteracoes);
        barraBotoes.add(Box.createRigidArea(new Dimension(8, 0)));
        barraBotoes.add(botaoCancelar);
        barraBotoes.add(Box.createRigidArea(new Dimension(8, 0)));
        barraBotoes.add(botaoDescontinuarCard);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_FORMULARIO, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    /**
     * Card lateral "Fornecedores do produto" fiel a ProdutoFornecedores.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardFornecedoresProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORNECEDORES - 32; // 388px

        // <b>Fornecedores do produto</b>
        JLabel titulo = new JLabel("Fornecedores do produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="font-size:14px;font-weight:600">Arroz 25kg</div>
        labelNomeProdutoFornecedores = new JLabel("Arroz 25kg");
        labelNomeProdutoFornecedores.setFont(UiStyle.fonteSemiBold(14));
        labelNomeProdutoFornecedores.setForeground(UiStyle.TEXT);
        labelNomeProdutoFornecedores.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelNomeProdutoFornecedores);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <span class="k">Fornecedores associados</span>
        JLabel labelAssociados = new JLabel("Fornecedores associados");
        labelAssociados.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelAssociados.setForeground(UiStyle.MUTED);
        labelAssociados.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelAssociados);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="mt"><table>...</table></div>
        JPanel miniTabelaFornecedores = criarMiniTabelaFornecedores(larguraInterna);
        card.add(miniTabelaFornecedores);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Associar fornecedor</span><div class="in" style="min-width:0">Lacticínios Norte ▾</div></div>
        seletorAssociarFornecedor = new BotaoSeletorFormulario(
                "Lacticínios Norte ▾",
                new String[] {"Lacticínios Norte", "Distribuidora Sol", "Mercearia Atlas"}
        );
        JPanel blocoAssociar = criarCampoComLabel("Associar fornecedor", seletorAssociarFornecedor);
        fixarLargura(blocoAssociar, larguraInterna, 60);
        card.add(blocoAssociar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">+ Associar</span><span class="bt s">Fechar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoAssociar = new BotaoToolbarArredondado(
                "+ Associar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoAssociar.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"+ Associar\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));

        BotaoToolbarArredondado botaoFechar = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoFechar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoAssociar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoFechar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_FORNECEDORES, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private JPanel criarMiniTabelaFornecedores(int larguraInterna) {
        DefaultTableModel modeloFornecedores = new DefaultTableModel(
                DADOS_FORNECEDORES_PRODUTO, COLUNAS_FORNECEDORES_PRODUTO) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable miniTabela = new JTable(modeloFornecedores);
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
        miniTabela.getColumnModel().getColumn(1).setPreferredWidth(135);
        miniTabela.getColumnModel().getColumn(2).setPreferredWidth(88);

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.add(header, BorderLayout.NORTH);
        container.add(miniTabela, BorderLayout.CENTER);

        int alturaTotal = 29 + (DADOS_FORNECEDORES_PRODUTO.length * 29);
        fixarLargura(container, larguraInterna, alturaTotal);
        return container;
    }

    private void sincronizarDadosProdutoParaEdicao() {
        if (campoNomeEditarProduto == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String nome = String.valueOf(tabela.getValueAt(row, 1));
        String categoria = String.valueOf(tabela.getValueAt(row, 2));
        String preco = String.valueOf(tabela.getValueAt(row, 3)).replace(" MT", "").trim();
        String estado = String.valueOf(tabela.getValueAt(row, 5));

        campoNomeEditarProduto.setText(nome);
        seletorCategoriaEditar.setText(categoria + " ▾");
        seletorEstadoEditar.setText(estado + " ▾");
        campoPrecoEditarProduto.setText(preco);
    }

    private void sincronizarDadosProdutoParaFornecedores() {
        if (labelNomeProdutoFornecedores == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String nome = String.valueOf(tabela.getValueAt(row, 1));
        labelNomeProdutoFornecedores.setText(nome);
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
        campo.setPreferredSize(new Dimension(Math.max(40, pref.width), ALTURA_INPUT));
        campo.setMinimumSize(new Dimension(40, ALTURA_INPUT));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));

        painel.add(label);
        painel.add(Box.createRigidArea(new Dimension(0, 6)));
        painel.add(campo);
        return painel;
    }

    private void fixarLargura(JComponent componente, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        componente.setPreferredSize(d);
        componente.setMinimumSize(d);
        componente.setMaximumSize(d);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void atualizarTabelaFiltrada() {
        String termo = campoBuscarProduto.getTextoReal().trim().toLowerCase();
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] linha : DADOS_INICIAIS) {
            String id = String.valueOf(linha[0]).toLowerCase();
            String nome = String.valueOf(linha[1]).toLowerCase();
            String categoria = String.valueOf(linha[2]);

            boolean combinaCat = "Todas".equalsIgnoreCase(categoriaFiltro)
                    || categoria.equalsIgnoreCase(categoriaFiltro);
            boolean combinaBusca = termo.isEmpty() || nome.contains(termo) || id.contains(termo);
            if (combinaCat && combinaBusca) {
                filtrados.add(linha);
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
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(isSelected);
            label.setBackground(isSelected ? UiStyle.SECONDARY_BG : UiStyle.SURFACE);
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
     * .ch.g -> ATIVO (#12402A / #7FE3A8)
     * .ch.r -> DESCONTINUADO (#4F1B1B / #FF9C9C)
     */
    private static final class RenderizadorCelulaEstado extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";

            Color bgChip;
            Color fgChip;
            if ("ATIVO".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_GREEN_BG;
                fgChip = UiStyle.CHIP_GREEN_FG;
            } else {
                bgChip = UiStyle.ERROR_BG;
                fgChip = UiStyle.ERROR_TEXT;
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
     * Seletor estilo .in ("Categoria: Todas ▾").
     */
    private static final class BotaoSeletorCategoria extends JButton {
        private static final long serialVersionUID = 1L;

        BotaoSeletorCategoria(String texto) {
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
     * Seletor de formulário estilo .in com min-width:0 (ex: "Cereais ▾", "ATIVO ▾", "Distribuidora Sol ▾").
     */
    private static final class BotaoSeletorFormulario extends JButton {
        private static final long serialVersionUID = 1L;

        BotaoSeletorFormulario(String textoInicial, String[] opcoes) {
            super(textoInicial);
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

            if (opcoes != null && opcoes.length > 0) {
                addActionListener(e -> {
                    JPopupMenu menu = new JPopupMenu();
                    menu.setBackground(UiStyle.SURFACE);
                    menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
                    for (String opcao : opcoes) {
                        JMenuItem item = new JMenuItem(opcao);
                        item.setFont(UiStyle.fonte(13, Font.PLAIN));
                        item.setBackground(UiStyle.SURFACE);
                        item.setForeground(UiStyle.TEXT);
                        item.addActionListener(ev -> setText(opcao + " ▾"));
                        menu.add(item);
                    }
                    menu.show(this, 0, getHeight() + 2);
                });
            }
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
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Nome *, Preço (MT) *, Quantidade, Validade).
     */
    private static final class CampoFormularioArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoFormularioArredondado(String valorInicial) {
            super(valorInicial);
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
     * Campo de texto estilo .in ("Buscar produto").
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
            this(texto, corFundo, corTexto, corBorda, raio, preencherFundo, 16);
        }

        BotaoToolbarArredondado(
                String texto,
                Color corFundo,
                Color corTexto,
                Color corBorda,
                int raio,
                boolean preencherFundo,
                int paddingHorizontal) {
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
            setBorder(new EmptyBorder(9, paddingHorizontal, 9, paddingHorizontal));
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
        atualizarTabelaFiltrada();
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            // Vendedor apenas consulta o catálogo
            if (botaoNovoProduto != null) botaoNovoProduto.setVisible(false);
            if (botaoEditar != null) botaoEditar.setVisible(false);
            if (botaoFornecedores != null) botaoFornecedores.setVisible(false);
            if (botaoDescontinuar != null) botaoDescontinuar.setVisible(false);
            if (modoLateral != ModoPainelLateral.NENHUM) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else {
            if (botaoNovoProduto != null) botaoNovoProduto.setVisible(true);
            if (botaoEditar != null) botaoEditar.setVisible(true);
            if (botaoFornecedores != null) botaoFornecedores.setVisible(true);
            if (botaoDescontinuar != null) botaoDescontinuar.setVisible(true);
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
