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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.Arrays;
import java.util.Locale;
import controller.FornecedorController;
import controller.ProdutoController;
import model.vo.Categoria;
import model.vo.Contacto;
import model.vo.Estoque;
import model.vo.Fornecedor;
import model.vo.EstadoProduto;
import model.vo.Perfil;
import model.vo.Produto;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import java.awt.Shape;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;
import util.ServidorLeitorCodigoBarras;

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
    private static final int LARGURA_CARD_FORMULARIO = 420;
    private static final int LARGURA_CARD_FORNECEDORES = 420;

    private static final String[] COLUNAS = {
        "ID", "Nome", "Categoria", "Preço / Un.", "Estoque", "Mín.", "Próx. Validade", "Fornec.", "Estado"
    };

    private static final String[] COLUNAS_FORNECEDORES_PRODUTO = {
        "Fornecedor", "Contactos", ""
    };

    private static final int TAMANHO_MAXIMO_UNIDADE = 20;
    private static final String[] UNIDADES_PADRAO = {
        "Unidade", "Kg", "Grama", "Litro", "Caixa", "Pacote", "Saco", "Dúzia"
    };
    private final List<String> listaUnidades = new ArrayList<>(Arrays.asList(UNIDADES_PADRAO));

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

    private JTextField campoNomeNovoProduto;
    private BotaoSeletorFormulario seletorCategoriaNovo;
    private BotaoSeletorFormulario seletorUnidadeNovo;
    private JTextField campoQuantidadeMinimaNovoProduto;
    private JTextField campoDescricaoNovoProduto;
    private JTextField campoCodigoBarrasNovoProduto;
    private JTextField campoPrecoNovoProduto;
    private BotaoSeletorFormulario seletorFornecedorNovo;
    private JTextField campoQuantidadeNovoProduto;
    private JTextField campoValidadeNovoProduto;

    private JTextField campoNomeEditarProduto;
    private BotaoSeletorFormulario seletorCategoriaEditar;
    private BotaoSeletorFormulario seletorUnidadeEditar;
    private BotaoSeletorFormulario seletorEstadoEditar;
    private JTextField campoPrecoEditarProduto;
    private JTextField campoQuantidadeMinimaEditarProduto;
    private JTextField campoDescricaoEditarProduto;
    private JTextField campoCodigoBarrasEditarProduto;
    private BotaoSeletorFormulario seletorFornecedorEditar;

    private JLabel labelNomeProdutoFornecedores;
    private BotaoSeletorFormulario seletorAssociarFornecedor;
    private JButton botaoAssociarFornecedor;
    private DefaultTableModel modeloFornecedores;
    private JTable miniTabelaFornecedores;
    private List<Fornecedor> listaFornecedoresDoProduto = new ArrayList<>();
    private List<Fornecedor> listaTodosFornecedores = new ArrayList<>();
    private Fornecedor fornecedorSelecionadoParaAssociar = null;
    private Long idProdutoAtualFornecedores = null;

    private final ProdutoController produtoController;
    private final FornecedorController fornecedorController;
    private List<Object[]> dadosProdutosCarregados = new ArrayList<>();
    private List<Produto> listaProdutosEntidades = new ArrayList<>();
    private List<Categoria> listaCategoriasReais = new ArrayList<>();

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
        this.produtoController = new ProdutoController();
        this.fornecedorController = new FornecedorController();

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarProduto = criarCampoBuscarProduto();
        this.seletorCategoria = criarSeletorCategoria();
        this.botaoNovoProduto = criarBotaoNovoProduto();
        this.botaoEditar = criarBotaoEditarProduto();
        this.botaoFornecedores = criarBotaoFornecedoresProduto();
        this.botaoDescontinuar = criarBotaoToolbar("Descontinuar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);
        this.botaoDescontinuar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CANCEL, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoDescontinuar.setIconTextGap(6);
        this.botaoDescontinuar.addActionListener(e -> descontinuarProdutoSelecionado());

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
        recarregarDados();
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

    public JTable getMiniTabelaFornecedores() {
        return miniTabelaFornecedores;
    }

    public JButton getBotaoAssociarFornecedor() {
        return botaoAssociarFornecedor;
    }

    public JButton getSeletorAssociarFornecedor() {
        return seletorAssociarFornecedor;
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
            UiStyle.atualizarComponentesTema(cardNovoProduto);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardNovoProduto), BorderLayout.CENTER);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_PRODUTO) {
            UiStyle.atualizarComponentesTema(cardEditarProduto);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEditarProduto), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosProdutoParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.FORNECEDORES_PRODUTO) {
            UiStyle.atualizarComponentesTema(cardFornecedoresProduto);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardFornecedoresProduto), BorderLayout.CENTER);
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

        JLabel titulo = new JLabel("Produtos");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
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
                Insets in = getInsets();
                int maxW = Math.max(200, larguraDisp - in.left - in.right);
                int hgap = 8;
                int vgap = 6;
                int x = 0;
                int y = in.top + vgap;
                int rowH = 0;
                for (Component c : getComponents()) {
                    if (!c.isVisible()) {
                        continue;
                    }
                    Dimension d = c.getPreferredSize();
                    if (x > 0 && x + hgap + d.width > maxW) {
                        y += rowH + vgap;
                        x = 0;
                        rowH = 0;
                    }
                    if (x > 0) {
                        x += hgap;
                    }
                    x += d.width;
                    rowH = Math.max(rowH, d.height);
                }
                y += rowH + vgap + in.bottom;
                return new Dimension(larguraDisp, Math.max(ALTURA_INPUT, y));
            }
        };
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.setBorder(new EmptyBorder(-6, -8, -6, 0));

        toolbar.add(campoBuscarProduto);
        toolbar.add(seletorCategoria);
        toolbar.add(botaoNovoProduto);
        toolbar.add(botaoEditar);
        toolbar.add(botaoFornecedores);
        toolbar.add(botaoDescontinuar);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    private JPanel criarCorpoPrincipal() {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.add(criarCardTabela(), BorderLayout.CENTER);
        linha.add(wrapperDireita, BorderLayout.EAST);
        return linha;
    }

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

    private BotaoSeletorCategoria criarSeletorCategoria() {
        BotaoSeletorCategoria botao = new BotaoSeletorCategoria("Categoria: Todas");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        botao.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(UiStyle.SURFACE);
            menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
            List<String> opcoes = new ArrayList<>();
            opcoes.add("Todas");
            if (listaCategoriasReais != null) {
                for (Categoria cat : listaCategoriasReais) {
                    if (cat.getNome() != null) {
                        opcoes.add(cat.getNome());
                    }
                }
            }
            for (String opcao : opcoes) {
                JMenuItem item = new JMenuItem(opcao);
                item.setFont(UiStyle.fonte(13, Font.PLAIN));
                item.setBackground(UiStyle.SURFACE);
                item.setForeground(UiStyle.TEXT);
                item.addActionListener(ev -> {
                    categoriaFiltro = opcao;
                    botao.setText("Categoria: " + opcao);
                    atualizarTabelaFiltrada();
                });
                menu.add(item);
            }
            UiStyle.mostrarPopupSincronizado(menu, botao);
        });
        return botao;
    }

    private JButton criarBotaoNovoProduto() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo produto", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRUCK_DELIVERY, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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
        return new BotaoToolbarArredondado(
                texto, bg, fg, borda, RAIO_COMPONENTE, preencherFundo
        );
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
            if (i == 8) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }
        return t;
    }

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

    private JPanel criarCardNovoProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32;

        JLabel titulo = new JLabel("Novo produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovoProduto = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovoProduto);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorCategoriaNovo = new BotaoSeletorFormulario(
                "Cereais",
                new String[] {"Cereais", "Mercearia", "Lacticínios", "Snacks"}
        );
        seletorUnidadeNovo = new BotaoSeletorFormulario(
                "Unidade",
                listaUnidades.toArray(new String[0])
        );
        JPanel blocoCategoriaNovo = criarCampoComLabel("Categoria *", comBotaoNovaCategoria(seletorCategoriaNovo));
        fixarLargura(blocoCategoriaNovo, larguraInterna, 60);
        card.add(blocoCategoriaNovo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel blocoUnidadeNovo = criarCampoComLabel("Unidade *", comBotaoNovaUnidade(seletorUnidadeNovo));
        fixarLargura(blocoUnidadeNovo, larguraInterna, 60);
        card.add(blocoUnidadeNovo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaPrecoMinimo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaPrecoMinimo.setOpaque(false);
        campoPrecoNovoProduto = new CampoFormularioArredondado("");
        campoQuantidadeMinimaNovoProduto = new CampoFormularioArredondado("");
        campoQuantidadeMinimaNovoProduto.setText("5");
        linhaPrecoMinimo.add(criarCampoComLabel("Preço (MT) *", campoPrecoNovoProduto));
        linhaPrecoMinimo.add(criarCampoComLabel("Qtd. mínima *", campoQuantidadeMinimaNovoProduto));
        fixarLargura(linhaPrecoMinimo, larguraInterna, 60);
        card.add(linhaPrecoMinimo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoDescricaoNovoProduto = new CampoFormularioArredondado("");
        JPanel blocoDescricao = criarCampoComLabel("Descrição", campoDescricaoNovoProduto);
        fixarLargura(blocoDescricao, larguraInterna, 60);
        card.add(blocoDescricao);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoCodigoBarrasNovoProduto = new CampoFormularioArredondado("");
        campoCodigoBarrasNovoProduto.setToolTipText("Digite o código, use o leitor USB ou clique em Leitor Celular");
        JPanel blocoCodigoBarras = criarCampoComLabel(
                "Código de barras (opcional — Digitar / Leitor USB / Celular)",
                comBotaoLeitorCodigoBarras(campoCodigoBarrasNovoProduto)
        );
        fixarLargura(blocoCodigoBarras, larguraInterna, 60);
        card.add(blocoCodigoBarras);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorFornecedorNovo = new BotaoSeletorFormulario(
                "Nenhum",
                new String[] {"Nenhum"}
        );
        JPanel blocoFornecedor = criarCampoComLabel("Fornecedor", comBotaoNovoFornecedor(seletorFornecedorNovo));
        fixarLargura(blocoFornecedor, larguraInterna, 60);
        card.add(blocoFornecedor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);
        campoQuantidadeNovoProduto = new CampoFormularioArredondado("");
        campoValidadeNovoProduto = new CampoFormularioArredondado("dd/mm/aaaa");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade", campoQuantidadeNovoProduto));
        linhaQtdValidade.add(criarCampoComLabel("Validade", campoValidadeNovoProduto));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardar.setIconTextGap(6);
        botaoGuardar.addActionListener(e -> {
            String nome = campoNomeNovoProduto.getText().trim();
            if (nome.isEmpty()) {
                avisoNovoProduto("Informe o nome do produto.");
                return;
            }

            double preco;
            try {
                preco = Double.parseDouble(campoPrecoNovoProduto.getText().trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                avisoNovoProduto("Informe um preço válido.");
                return;
            }
            if (preco <= 0) {
                avisoNovoProduto("O preço deve ser maior que zero.");
                return;
            }

            String unidade = textoSeletor(seletorUnidadeNovo);
            if (unidade.isEmpty()) {
                avisoNovoProduto("Seleccione a unidade do produto.");
                return;
            }

            int qtdMinima;
            try {
                qtdMinima = Integer.parseInt(campoQuantidadeMinimaNovoProduto.getText().trim());
                if (qtdMinima < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                avisoNovoProduto("A quantidade mínima deve ser um número inteiro (0 ou mais).");
                return;
            }

            int qTmp = 0;
            String qStr = campoQuantidadeNovoProduto.getText().trim();
            if (!qStr.isEmpty()) {
                try {
                    qTmp = Integer.parseInt(qStr);
                    if (qTmp < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    avisoNovoProduto("A quantidade deve ser um número inteiro (0 ou mais).");
                    return;
                }
            }
            final int qtdInicial = qTmp;

            java.time.LocalDate vTmp = null;
            String vStr = campoValidadeNovoProduto.getText().trim();
            if (!vStr.isEmpty()) {
                try {
                    vTmp = java.time.LocalDate.parse(vStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (java.time.format.DateTimeParseException ex) {
                    avisoNovoProduto("Validade inválida. Use o formato dd/mm/aaaa.");
                    return;
                }
                if (vTmp.isBefore(java.time.LocalDate.now())) {
                    avisoNovoProduto("A data de validade já passou.");
                    return;
                }
            }
            final java.time.LocalDate validadeInicial = vTmp;

            Produto p = new Produto();
            p.setNome(nome);
            p.setPreco(preco);
            p.setUnidade(unidade);
            p.setQuantidadeMinima(qtdMinima);
            String descricao = campoDescricaoNovoProduto.getText().trim();
            p.setDescricao(descricao.isEmpty() ? null : descricao);
            if (campoCodigoBarrasNovoProduto != null) {
                p.setCodigoBarras(campoCodigoBarrasNovoProduto.getText());
            }
            p.setEstado(EstadoProduto.ATIVO);
            Categoria categoriaEscolhida = buscarCategoriaPorNome(textoSeletor(seletorCategoriaNovo));
            if (categoriaEscolhida == null) {
                avisoNovoProduto("Seleccione uma categoria válida.");
                return;
            }
            p.setCategoria(categoriaEscolhida);

            final String fornNome = textoSeletor(seletorFornecedorNovo);

            botaoGuardar.setEnabled(false);
            AsyncTask.executar(
                () -> {
                    Utilizador user = SessaoUtilizador.getUtilizador();
                    if (qtdInicial > 0 && (user == null || user.getFuncionario() == null)) {
                        throw new IllegalStateException(
                                "Sessão sem funcionário associado: não é possível registar o stock inicial.");
                    }

                    produtoController.salvar(p);

                    if (p.getIdProduto() != null && !fornNome.isEmpty() && !"Nenhum".equalsIgnoreCase(fornNome) && listaTodosFornecedores != null) {
                        for (Fornecedor f : listaTodosFornecedores) {
                            if (f.getNome() != null && f.getNome().equalsIgnoreCase(fornNome)) {
                                produtoController.associarFornecedor(p.getIdProduto(), f.getIdFornecedor());
                                break;
                            }
                        }
                    }
                    if (p.getIdProduto() != null && qtdInicial > 0) {
                        Estoque lote = new Estoque();
                        lote.setProduto(p);
                        lote.setFuncionario(user.getFuncionario());
                        lote.setQuantidade(qtdInicial);
                        lote.setDataEntrada(java.time.LocalDate.now());
                        lote.setDataValidade(validadeInicial);
                        produtoController.salvarLoteInicial(lote);
                    }
                    return null;
                },
                sucesso -> {
                    botaoGuardar.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    limparFormularioNovoProduto();
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> {
                    botaoGuardar.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        botaoCancelar.addActionListener(e -> {
            limparFormularioNovoProduto();
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        });

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

    private void avisoNovoProduto(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private static String textoSeletor(BotaoSeletorFormulario seletor) {
        if (seletor == null || seletor.getText() == null) {
            return "";
        }
        return seletor.getText().replace(" ▾", "").replace("▾", "").trim();
    }

    private void limparFormularioNovoProduto() {
        campoNomeNovoProduto.setText("");
        campoPrecoNovoProduto.setText("");
        campoDescricaoNovoProduto.setText("");
        if (campoCodigoBarrasNovoProduto != null) campoCodigoBarrasNovoProduto.setText("");
        campoQuantidadeNovoProduto.setText("");
        campoValidadeNovoProduto.setText("");
        campoQuantidadeMinimaNovoProduto.setText("5");
        seletorUnidadeNovo.setText("Unidade");
    }

    private JPanel criarCardEditarProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32;

        JLabel titulo = new JLabel("Editar produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditarProduto = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditarProduto);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorCategoriaEditar = new BotaoSeletorFormulario(
                "Cereais",
                new String[] {"Cereais", "Mercearia", "Lacticínios", "Snacks"}
        );
        seletorUnidadeEditar = new BotaoSeletorFormulario(
                "Unidade",
                listaUnidades.toArray(new String[0])
        );
        JPanel blocoCatEditar = criarCampoComLabel("Categoria *", comBotaoNovaCategoria(seletorCategoriaEditar));
        fixarLargura(blocoCatEditar, larguraInterna, 60);
        card.add(blocoCatEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel blocoUnidEditar = criarCampoComLabel("Unidade *", comBotaoNovaUnidade(seletorUnidadeEditar));
        fixarLargura(blocoUnidEditar, larguraInterna, 60);
        card.add(blocoUnidEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaPrecoMinimoEditar = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaPrecoMinimoEditar.setOpaque(false);
        campoPrecoEditarProduto = new CampoFormularioArredondado("");
        campoQuantidadeMinimaEditarProduto = new CampoFormularioArredondado("");
        linhaPrecoMinimoEditar.add(criarCampoComLabel("Preço (MT) *", campoPrecoEditarProduto));
        linhaPrecoMinimoEditar.add(criarCampoComLabel("Qtd. mínima *", campoQuantidadeMinimaEditarProduto));
        fixarLargura(linhaPrecoMinimoEditar, larguraInterna, 60);
        card.add(linhaPrecoMinimoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoDescricaoEditarProduto = new CampoFormularioArredondado("");
        JPanel blocoDescricaoEditar = criarCampoComLabel("Descrição", campoDescricaoEditarProduto);
        fixarLargura(blocoDescricaoEditar, larguraInterna, 60);
        card.add(blocoDescricaoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoCodigoBarrasEditarProduto = new CampoFormularioArredondado("");
        campoCodigoBarrasEditarProduto.setToolTipText("Digite o código, use o leitor USB ou clique em Leitor Celular");
        JPanel blocoCodigoBarrasEditar = criarCampoComLabel(
                "Código de barras (opcional — Digitar / Leitor USB / Celular)",
                comBotaoLeitorCodigoBarras(campoCodigoBarrasEditarProduto)
        );
        fixarLargura(blocoCodigoBarrasEditar, larguraInterna, 60);
        card.add(blocoCodigoBarrasEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorEstadoEditar = new BotaoSeletorFormulario(
                "ATIVO",
                new String[] {"ATIVO", "DESCONTINUADO"}
        );
        seletorFornecedorEditar = new BotaoSeletorFormulario(
                "Nenhum",
                new String[] {"Nenhum"}
        );
        JPanel blocoEstadoEditar = criarCampoComLabel("Estado", seletorEstadoEditar);
        fixarLargura(blocoEstadoEditar, larguraInterna, 60);
        card.add(blocoEstadoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel blocoFornEditar = criarCampoComLabel("Fornecedor", comBotaoNovoFornecedor(seletorFornecedorEditar));
        fixarLargura(blocoFornEditar, larguraInterna, 60);
        card.add(blocoFornEditar);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarAlteracoes = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true, 10
        );
        botaoGuardarAlteracoes.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 14, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoes.setIconTextGap(5);
        botaoGuardarAlteracoes.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um produto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            try {
                Long idProduto = Long.parseLong(idStr);
                Produto p = (listaProdutosEntidades != null) ? listaProdutosEntidades.stream()
                    .filter(item -> item.getIdProduto() != null && item.getIdProduto().equals(idProduto))
                    .findFirst().orElse(null) : null;
                if (p == null) {
                    p = new Produto();
                    p.setIdProduto(idProduto);
                }
                Categoria catEdit = buscarCategoriaPorNome(textoSeletor(seletorCategoriaEditar));
                if (catEdit == null) {
                    JOptionPane.showMessageDialog(this, "Seleccione uma categoria válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String nomeEdit = campoNomeEditarProduto.getText().trim();
                if (nomeEdit.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o nome do produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                p.setNome(nomeEdit);
                try {
                    String prcStr = campoPrecoEditarProduto.getText().trim().replace(" MT", "").replace(".", "").replace(",", ".");
                    double novoPreco = Double.parseDouble(prcStr);
                    if (novoPreco <= 0) {
                        JOptionPane.showMessageDialog(this, "O preço deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    p.setPreco(novoPreco);
                } catch (NumberFormatException exP) {
                    JOptionPane.showMessageDialog(this, "Informe um preço válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String unidadeEdit = textoSeletor(seletorUnidadeEditar);
                if (!unidadeEdit.isEmpty()) {
                    p.setUnidade(unidadeEdit);
                }
                if (campoQuantidadeMinimaEditarProduto != null) {
                    try {
                        int qMin = Integer.parseInt(campoQuantidadeMinimaEditarProduto.getText().trim());
                        if (qMin < 0) throw new NumberFormatException();
                        p.setQuantidadeMinima(qMin);
                    } catch (NumberFormatException exMin) {
                        JOptionPane.showMessageDialog(this, "A quantidade mínima deve ser um número inteiro >= 0.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                if (campoDescricaoEditarProduto != null) {
                    String descEdit = campoDescricaoEditarProduto.getText().trim();
                    p.setDescricao(descEdit.isEmpty() ? null : descEdit);
                }
                if (campoCodigoBarrasEditarProduto != null) {
                    p.setCodigoBarras(campoCodigoBarrasEditarProduto.getText());
                }
                String estadoStr = seletorEstadoEditar.getText().replace(" ▾", "").replace("▾", "").trim();
                try {
                    p.setEstado(EstadoProduto.valueOf(estadoStr));
                } catch (Exception ignored) {}
                p.setCategoria(catEdit);
                final String fornEditNome = textoSeletor(seletorFornecedorEditar);

                final Produto fProd = p;
                botaoGuardarAlteracoes.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        produtoController.atualizar(fProd);
                        if (fProd.getIdProduto() != null && !fornEditNome.isEmpty() && !"Nenhum".equalsIgnoreCase(fornEditNome) && listaTodosFornecedores != null) {
                            for (Fornecedor f : listaTodosFornecedores) {
                                if (f.getNome() != null && f.getNome().equalsIgnoreCase(fornEditNome)) {
                                    produtoController.associarFornecedor(fProd.getIdProduto(), f.getIdFornecedor());
                                    break;
                                }
                            }
                        }
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoGuardarAlteracoes.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar produto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoGuardarAlteracoes.setEnabled(true);
                    }
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de produto inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 10
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 14, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(5);
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado botaoDescontinuarCard = new BotaoToolbarArredondado(
                "Descontinuar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false, 10
        );
        botaoDescontinuarCard.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CANCEL, 14, () -> UiStyle.DANGER_TEXT));
        botaoDescontinuarCard.setIconTextGap(5);
        botaoDescontinuarCard.addActionListener(e -> descontinuarProdutoSelecionado());
        barraBotoes.add(botaoGuardarAlteracoes);
        barraBotoes.add(Box.createRigidArea(new Dimension(6, 0)));
        barraBotoes.add(botaoCancelar);
        barraBotoes.add(Box.createRigidArea(new Dimension(6, 0)));
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

    private JPanel criarCardFornecedoresProduto() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORNECEDORES - 32;

        JLabel titulo = new JLabel("Fornecedores do produto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelNomeProdutoFornecedores = new JLabel("Arroz 25kg");
        labelNomeProdutoFornecedores.setFont(UiStyle.fonteSemiBold(14));
        labelNomeProdutoFornecedores.setForeground(UiStyle.TEXT);
        labelNomeProdutoFornecedores.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelNomeProdutoFornecedores);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel labelAssociados = new JLabel("Fornecedores associados");
        labelAssociados.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelAssociados.setForeground(UiStyle.MUTED);
        labelAssociados.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelAssociados);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel miniTabelaFornecedores = criarMiniTabelaFornecedores(larguraInterna);
        card.add(miniTabelaFornecedores);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorAssociarFornecedor = new BotaoSeletorFormulario(
                "Lacticínios Norte",
                new String[] {"Lacticínios Norte", "Distribuidora Sol", "Mercearia Atlas"}
        );
        JPanel blocoAssociar = criarCampoComLabel("Associar fornecedor", seletorAssociarFornecedor);
        fixarLargura(blocoAssociar, larguraInterna, 60);
        card.add(blocoAssociar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        botaoAssociarFornecedor = new BotaoToolbarArredondado(
                "+ Associar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoAssociarFornecedor.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botaoAssociarFornecedor.setIconTextGap(6);
        botaoAssociarFornecedor.addActionListener(e -> {
            if (idProdutoAtualFornecedores == null) {
                JOptionPane.showMessageDialog(this, "Selecione um produto na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String textoSel = seletorAssociarFornecedor != null ? seletorAssociarFornecedor.getText() : "";
            if (textoSel == null || textoSel.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um fornecedor para associar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            final String nomeSel = textoSel.replace(" ▾", "").replace("▾", "").trim();
            Fornecedor fornecedorEncontrado = (listaTodosFornecedores != null) ? listaTodosFornecedores.stream()
                .filter(f -> f.getNome() != null && f.getNome().equalsIgnoreCase(nomeSel))
                .findFirst().orElse(null) : null;
            if (fornecedorEncontrado == null) {
                JOptionPane.showMessageDialog(this, "Fornecedor não encontrado na lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            final Long idForn = fornecedorEncontrado.getIdFornecedor();
            AsyncTask.executar(
                () -> {
                    produtoController.associarFornecedor(idProdutoAtualFornecedores, idForn);
                    return true;
                },
                sucesso -> {
                    JOptionPane.showMessageDialog(this, "Fornecedor associado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarFornecedoresDoProduto(idProdutoAtualFornecedores);
                },
                erro -> JOptionPane.showMessageDialog(this, "Erro ao associar fornecedor: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado botaoFechar = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoFechar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoFechar.setIconTextGap(6);
        botaoFechar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoAssociarFornecedor);
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
        this.modeloFornecedores = new DefaultTableModel(
                new Object[0][3], COLUNAS_FORNECEDORES_PRODUTO) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.miniTabelaFornecedores = new JTable(this.modeloFornecedores);
        this.miniTabelaFornecedores.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = miniTabelaFornecedores.columnAtPoint(e.getPoint());
                int row = miniTabelaFornecedores.rowAtPoint(e.getPoint());
                if (col == 2 && row >= 0 && row < miniTabelaFornecedores.getRowCount()) {
                    if (idProdutoAtualFornecedores == null || listaFornecedoresDoProduto == null || row >= listaFornecedoresDoProduto.size()) {
                        return;
                    }
                    Fornecedor f = listaFornecedoresDoProduto.get(row);
                    int opt = JOptionPane.showConfirmDialog(ProdutosPanel.this, 
                        "Deseja desassociar o fornecedor \"" + f.getNome() + "\" deste produto?", 
                        "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        AsyncTask.executar(
                            () -> {
                                produtoController.desassociarFornecedor(idProdutoAtualFornecedores, f.getIdFornecedor());
                                return true;
                            },
                            sucesso -> {
                                JOptionPane.showMessageDialog(ProdutosPanel.this, "Fornecedor desassociado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                carregarFornecedoresDoProduto(idProdutoAtualFornecedores);
                            },
                            erro -> JOptionPane.showMessageDialog(ProdutosPanel.this, "Erro ao desassociar: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
                        );
                    }
                }
            }
        });
        JTable miniTabela = this.miniTabelaFornecedores;
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
        JScrollPane scrollMini = new JScrollPane(miniTabela);
        scrollMini.setBorder(BorderFactory.createEmptyBorder());
        scrollMini.setOpaque(false);
        scrollMini.getViewport().setOpaque(false);
        scrollMini.getViewport().setBackground(UiStyle.SURFACE);
        container.add(scrollMini, BorderLayout.CENTER);

        int alturaTotal = 29 + (3 * 29);
        fixarLargura(container, larguraInterna, alturaTotal);
        return container;
    }

    private Categoria buscarCategoriaPorNome(String nome) {
        if (listaCategoriasReais == null || nome == null) {
            return null;
        }
        for (Categoria c : listaCategoriasReais) {
            if (c != null && c.getNome() != null && c.getNome().equalsIgnoreCase(nome.trim())) {
                return c;
            }
        }
        return null;
    }

    private JComponent comBotaoNovaCategoria(BotaoSeletorFormulario seletor) {
        return comBotoesMaisEMenos(
                seletor,
                "Nova categoria",
                mais -> abrirDialogoNovaCategoria(seletor, mais),
                "Eliminar categoria seleccionada",
                menos -> eliminarCategoriaSelecionada(seletor, menos)
        );
    }

    private JComponent comBotaoNovaUnidade(BotaoSeletorFormulario seletor) {
        return comBotoesMaisEMenos(
                seletor,
                "Nova unidade",
                mais -> abrirDialogoNovaUnidade(seletor),
                "Remover unidade seleccionada",
                menos -> removerUnidadeSelecionada(seletor)
        );
    }

    private JComponent comBotaoNovoFornecedor(BotaoSeletorFormulario seletor) {
        return comBotaoMais(seletor, "Novo fornecedor", mais -> abrirDialogoNovoFornecedorRapido(seletor));
    }

    private JComponent comBotaoLeitorCodigoBarras(JTextField campoAlvo) {
        JPanel linha = new JPanel(new BorderLayout(6, 0));
        linha.setOpaque(false);

        BotaoToolbarArredondado btnLeitor = new BotaoToolbarArredondado(
                "Leitor Celular", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 10
        );
        btnLeitor.setToolTipText("Escanear código de barras pelo telemóvel na rede Wi-Fi");
        btnLeitor.addActionListener(e -> abrirLeitorCodigoBarrasParaCampo(campoAlvo));

        linha.add(campoAlvo, BorderLayout.CENTER);
        linha.add(btnLeitor, BorderLayout.EAST);
        return linha;
    }

    private void abrirLeitorCodigoBarrasParaCampo(JTextField campoAlvo) {
        try {
            String url = ServidorLeitorCodigoBarras.iniciarOuAtualizarOuvinte(codigo -> {
                if (codigo != null && !codigo.trim().isEmpty() && campoAlvo != null) {
                    campoAlvo.setText(codigo.trim());
                    campoAlvo.setForeground(UiStyle.TEXT);
                    campoAlvo.repaint();
                }
            });
            JOptionPane.showMessageDialog(
                    this,
                    "Leitor Mobile ativo para preencher o Código de Barras do produto!\n\n"
                    + "1. Também pode usar diretamente qualquer leitor USB (basta clicar no campo e apontar o leitor).\n"
                    + "2. Para usar o telemóvel, conecte-o na mesma rede Wi-Fi/Hotspot e aceda a:\n\n   " + url + "\n\n"
                    + "O código lido será preenchido automaticamente no campo 'Código de barras'.",
                    "Scanner de Código de Barras (USB / Telemóvel)",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível iniciar o servidor do leitor móvel: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JComponent comBotaoMais(BotaoSeletorFormulario seletor, String dica,
            java.util.function.Consumer<JButton> acao) {
        JPanel linha = new JPanel(new BorderLayout(6, 0));
        linha.setOpaque(false);

        BotaoToolbarArredondado mais = new BotaoToolbarArredondado(
                "", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 11
        );
        mais.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PLUS, 15, () -> UiStyle.TEXT));
        if (mais.getIcon() == null) {
            mais.setText("+");
        }
        mais.setToolTipText(dica);
        mais.addActionListener(e -> acao.accept(mais));

        linha.add(seletor, BorderLayout.CENTER);
        linha.add(mais, BorderLayout.EAST);
        return linha;
    }

    private JComponent comBotoesMaisEMenos(
            BotaoSeletorFormulario seletor,
            String dicaMais,
            java.util.function.Consumer<JButton> acaoMais,
            String dicaMenos,
            java.util.function.Consumer<JButton> acaoMenos) {
        JPanel linha = new JPanel(new BorderLayout(4, 0));
        linha.setOpaque(false);

        BotaoToolbarArredondado mais = new BotaoToolbarArredondado(
                "", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 9
        );
        mais.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PLUS, 14, () -> UiStyle.TEXT));
        if (mais.getIcon() == null) {
            mais.setText("+");
        }
        mais.setToolTipText(dicaMais);
        mais.addActionListener(e -> acaoMais.accept(mais));

        BotaoToolbarArredondado menos = new BotaoToolbarArredondado(
                "", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false, 9
        );
        menos.setIcon(UiStyle.iconeDinamico(MaterialDesignD.DELETE_OUTLINE, 14, () -> UiStyle.DANGER_TEXT));
        if (menos.getIcon() == null) {
            menos.setText("-");
        }
        menos.setToolTipText(dicaMenos);
        menos.addActionListener(e -> acaoMenos.accept(menos));

        JPanel botoes = new JPanel(new GridLayout(1, 2, 4, 0));
        botoes.setOpaque(false);
        botoes.add(mais);
        botoes.add(menos);

        linha.add(seletor, BorderLayout.CENTER);
        linha.add(botoes, BorderLayout.EAST);
        return linha;
    }

    private void eliminarCategoriaSelecionada(BotaoSeletorFormulario seletorOrigem, JButton botaoOrigem) {
        String nomeCat = textoSeletor(seletorOrigem);
        Categoria cat = buscarCategoriaPorNome(nomeCat);
        if (cat == null || cat.getIdCategoria() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione uma categoria válida para eliminar.", "Eliminar categoria", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (listaProdutosEntidades != null) {
            long emUso = listaProdutosEntidades.stream()
                    .filter(p -> p.getCategoria() != null && cat.getIdCategoria().equals(p.getCategoria().getIdCategoria()))
                    .count();
            if (emUso > 0) {
                JOptionPane.showMessageDialog(this,
                        "Não é possível eliminar a categoria \"" + cat.getNome() + "\" porque está associada a " + emUso + " produto(s).",
                        "Categoria em uso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        int op = JOptionPane.showConfirmDialog(this,
                "Deseja eliminar a categoria \"" + cat.getNome() + "\"?",
                "Confirmar eliminação", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) {
            return;
        }
        botaoOrigem.setEnabled(false);
        AsyncTask.executar(
            () -> {
                produtoController.eliminarCategoria(cat.getIdCategoria());
                return true;
            },
            ok -> {
                botaoOrigem.setEnabled(true);
                recarregarDados();
                JOptionPane.showMessageDialog(this, "Categoria eliminada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            },
            erro -> {
                botaoOrigem.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                        "Não foi possível eliminar a categoria (pode estar em uso): " + erro.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void removerUnidadeSelecionada(BotaoSeletorFormulario seletorOrigem) {
        String unidadeAtual = textoSeletor(seletorOrigem);
        if (unidadeAtual.isEmpty()) {
            return;
        }
        if (listaUnidades.size() <= 1) {
            JOptionPane.showMessageDialog(this, "Deve existir pelo menos uma unidade na lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (listaProdutosEntidades != null) {
            boolean emUso = listaProdutosEntidades.stream()
                    .anyMatch(p -> p.getUnidade() != null && chaveUnidade(p.getUnidade()).equals(chaveUnidade(unidadeAtual)));
            if (emUso) {
                JOptionPane.showMessageDialog(this,
                        "A unidade \"" + unidadeAtual + "\" está em uso por produtos cadastrados e não pode ser removida.",
                        "Unidade em uso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        int op = JOptionPane.showConfirmDialog(this,
                "Deseja remover a unidade \"" + unidadeAtual + "\" da lista?",
                "Remover unidade", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) {
            return;
        }
        listaUnidades.removeIf(u -> chaveUnidade(u).equals(chaveUnidade(unidadeAtual)));
        actualizarOpcoesUnidade();
        String proxima = listaUnidades.isEmpty() ? "Unidade" : listaUnidades.get(0);
        seletorOrigem.setText(proxima);
    }

    private void abrirDialogoNovoFornecedorRapido(BotaoSeletorFormulario seletorDestino) {
        Component root = SwingUtilities.getWindowAncestor(this);
        if (root instanceof MainFrame) {
            int op = JOptionPane.showConfirmDialog(this,
                    "Deseja abrir o ecrã de Fornecedores para cadastrar um novo fornecedor completo?",
                    "Novo fornecedor", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                MainFrame mf = (MainFrame) root;
                mf.mostrarPainel(MainFrame.PAINEL_FORNECEDORES);
                JPanel pnl = mf.getPainelPorId(MainFrame.PAINEL_FORNECEDORES);
                if (pnl instanceof FornecedoresPanel) {
                    ((FornecedoresPanel) pnl).abrirModoLateral(FornecedoresPanel.ModoPainelLateral.NOVO_FORNECEDOR);
                }
            }
        }
    }

    private void abrirDialogoNovaUnidade(BotaoSeletorFormulario seletorDestino) {
        String entrada = JOptionPane.showInputDialog(
                this, "Nome da nova unidade (ex.: Fardo, Garrafa):", "Nova unidade", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) {
            return;
        }
        String nome = entrada.trim().replaceAll("\\s+", " ");
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da unidade.", "Nova unidade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nome.length() > TAMANHO_MAXIMO_UNIDADE) {
            JOptionPane.showMessageDialog(this,
                    "A unidade não pode ter mais de " + TAMANHO_MAXIMO_UNIDADE + " caracteres.",
                    "Nova unidade", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String existente = procurarUnidade(nome);
        if (existente != null) {
            seletorDestino.setText(existente);
            return;
        }
        nome = Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
        listaUnidades.add(nome);
        actualizarOpcoesUnidade();
        seletorDestino.setText(nome);
    }

    private void mesclarUnidadesDosProdutos(List<Produto> produtos) {
        if (produtos == null) {
            return;
        }
        for (Produto p : produtos) {
            if (p == null || p.getUnidade() == null || p.getUnidade().trim().isEmpty()) {
                continue;
            }
            String u = p.getUnidade().trim();
            if (procurarUnidade(u) == null) {
                listaUnidades.add(u);
            }
        }
        actualizarOpcoesUnidade();
    }

    private void actualizarOpcoesUnidade() {
        String[] arr = listaUnidades.toArray(new String[0]);
        if (seletorUnidadeNovo != null) {
            seletorUnidadeNovo.setOpcoes(arr);
        }
        if (seletorUnidadeEditar != null) {
            seletorUnidadeEditar.setOpcoes(arr);
        }
    }

    private String procurarUnidade(String nome) {
        String chave = chaveUnidade(nome);
        for (String u : listaUnidades) {
            if (chaveUnidade(u).equals(chave)) {
                return u;
            }
        }
        return null;
    }

    private static String chaveUnidade(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim().toLowerCase();
    }

    private void abrirDialogoNovaCategoria(BotaoSeletorFormulario seletorDestino, JButton botaoOrigem) {
        String entrada = JOptionPane.showInputDialog(
                this, "Nome da nova categoria:", "Nova categoria", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) {
            return;
        }
        botaoOrigem.setEnabled(false);
        AsyncTask.executar(
            () -> produtoController.criarCategoria(entrada),
            nova -> {
                botaoOrigem.setEnabled(true);
                registarCategoriaCriada(nova);
                seletorDestino.setText(nova.getNome());
            },
            erro -> {
                botaoOrigem.setEnabled(true);
                JOptionPane.showMessageDialog(this, erro.getMessage(), "Nova categoria", JOptionPane.WARNING_MESSAGE);
            }
        );
    }

    private void registarCategoriaCriada(Categoria nova) {
        List<Categoria> actualizada = new ArrayList<>();
        if (listaCategoriasReais != null) {
            actualizada.addAll(listaCategoriasReais);
        }
        actualizada.add(nova);
        this.listaCategoriasReais = actualizada;

        String[] nomes = actualizada.stream()
                .map(Categoria::getNome)
                .toArray(String[]::new);
        if (seletorCategoriaNovo != null) {
            seletorCategoriaNovo.setOpcoes(nomes);
        }
        if (seletorCategoriaEditar != null) {
            seletorCategoriaEditar.setOpcoes(nomes);
        }
    }

    private void descontinuarProdutoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para descontinuar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja descontinuar o produto #" + idStr + "?", "Confirmar Descontinuação", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idProduto = Long.parseLong(idStr);
            Produto p = (listaProdutosEntidades != null) ? listaProdutosEntidades.stream()
                .filter(item -> item.getIdProduto() != null && item.getIdProduto().equals(idProduto))
                .findFirst().orElse(null) : null;
            if (p == null) {
                p = new Produto();
                p.setIdProduto(idProduto);
            }
            p.setEstado(EstadoProduto.DESCONTINUADO);
            final Produto fProd = p;
            AsyncTask.executar(
                () -> {
                    produtoController.atualizar(fProd);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Produto #" + idStr + " descontinuado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao descontinuar produto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Identificador de produto inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sincronizarDadosProdutoParaEdicao() {
        if (campoNomeEditarProduto == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        String nome = String.valueOf(tabela.getValueAt(row, 1));
        String categoria = String.valueOf(tabela.getValueAt(row, 2));
        String estado = String.valueOf(tabela.getValueAt(row, 8));

        Produto entidade = null;
        try {
            Long idProd = Long.parseLong(idStr);
            if (listaProdutosEntidades != null) {
                for (Produto p : listaProdutosEntidades) {
                    if (p != null && idProd.equals(p.getIdProduto())) {
                        entidade = p;
                        break;
                    }
                }
            }
        } catch (NumberFormatException ignored) {}

        campoNomeEditarProduto.setText(nome);
        seletorCategoriaEditar.setText(categoria);
        seletorEstadoEditar.setText(estado);
        if (entidade != null) {
            campoPrecoEditarProduto.setText(String.format(Locale.GERMANY, "%.2f", entidade.getPreco() != null ? entidade.getPreco() : 0.0));
            if (seletorUnidadeEditar != null) {
                String un = entidade.getUnidade() != null && !entidade.getUnidade().isEmpty() ? entidade.getUnidade() : "Unidade";
                seletorUnidadeEditar.setText(un);
            }
            if (campoQuantidadeMinimaEditarProduto != null) {
                campoQuantidadeMinimaEditarProduto.setText(String.valueOf(entidade.getQuantidadeMinima() != null ? entidade.getQuantidadeMinima() : 0));
            }
            if (campoDescricaoEditarProduto != null) {
                campoDescricaoEditarProduto.setText(entidade.getDescricao() != null ? entidade.getDescricao() : "");
            }
            if (campoCodigoBarrasEditarProduto != null) {
                campoCodigoBarrasEditarProduto.setText(entidade.getCodigoBarras() != null ? entidade.getCodigoBarras() : "");
            }
            if (seletorFornecedorEditar != null) {
                if (entidade.getFornecedor() != null && !entidade.getFornecedor().isEmpty()) {
                    Fornecedor primeiro = entidade.getFornecedor().iterator().next();
                    seletorFornecedorEditar.setText(primeiro.getNome() != null ? primeiro.getNome() : "Nenhum");
                } else {
                    seletorFornecedorEditar.setText("Nenhum");
                }
            }
        }
    }

    private void sincronizarDadosProdutoParaFornecedores() {
        if (labelNomeProdutoFornecedores == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        String nome = String.valueOf(tabela.getValueAt(row, 1));
        labelNomeProdutoFornecedores.setText(nome);
        try {
            Long idProduto = Long.parseLong(idStr);
            this.idProdutoAtualFornecedores = idProduto;
            carregarFornecedoresDoProduto(idProduto);
        } catch (Exception ex) {
        }
    }

    private void carregarFornecedoresDoProduto(Long idProduto) {
        if (idProduto == null) return;
        AsyncTask.executar(
            () -> {
                List<Fornecedor> associados = produtoController.listarFornecedoresDoProduto(idProduto);
                List<Fornecedor> todos = fornecedorController.listarTodos();
                return new Object[] { associados, todos };
            },
            res -> {
                Object[] arr = (Object[]) res;
                @SuppressWarnings("unchecked")
                List<Fornecedor> associados = (List<Fornecedor>) arr[0];
                @SuppressWarnings("unchecked")
                List<Fornecedor> todos = (List<Fornecedor>) arr[1];
                this.listaFornecedoresDoProduto = associados != null ? associados : new ArrayList<>();
                this.listaTodosFornecedores = todos != null ? todos : new ArrayList<>();
                
                if (modeloFornecedores != null) {
                    modeloFornecedores.setRowCount(0);
                    for (Fornecedor f : this.listaFornecedoresDoProduto) {
                        String cto = (f.getContacto() != null && !f.getContacto().isEmpty()) 
                            ? f.getContacto().get(0).getContacto() : "-";
                        modeloFornecedores.addRow(new Object[] { f.getNome(), cto, "Remover" });
                    }
                }
                
                if (seletorAssociarFornecedor != null && !this.listaTodosFornecedores.isEmpty()) {
                    String[] nomes = this.listaTodosFornecedores.stream()
                        .map(Fornecedor::getNome)
                        .toArray(String[]::new);
                    seletorAssociarFornecedor.setOpcoes(nomes);
                    seletorAssociarFornecedor.setText(nomes[0]);
                }
            },
            erro -> {}
        );
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        if (campo instanceof javax.swing.text.JTextComponent
                && campo.getClientProperty(FlatClientProperties.PLACEHOLDER_TEXT) == null) {
            String dica = textoLabel.replaceAll("\\(.*?\\)", "").replace("*", "").trim().toLowerCase();
            campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Introduzir " + dica);
        }
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
        List<Object[]> fonteDados = (dadosProdutosCarregados != null) ? dadosProdutosCarregados : new ArrayList<>();
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] linha : fonteDados) {
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
            setBorder(new EmptyBorder(0, 12, 0, 28));
        }

        @Override
        public String getText() {
            return UiStyle.removerMarcadorSeta(super.getText());
        }

        @Override
        protected void paintComponent(Graphics g) {
            setForeground(UiStyle.TEXT);
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
                super.paintComponent(g);
                UiStyle.desenharChevronExpansivel(g2, w, h, UiStyle.MUTED);
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class BotaoSeletorFormulario extends JButton {
        private static final long serialVersionUID = 1L;
        private String[] opcoes;

        BotaoSeletorFormulario(String textoInicial, String[] opcoes) {
            super(textoInicial);
            this.opcoes = opcoes;
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setBackground(UiStyle.INPUT_BG);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 28));

            addActionListener(e -> {
                if (this.opcoes != null && this.opcoes.length > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.setBackground(UiStyle.SURFACE);
                    menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
                    for (String opcao : this.opcoes) {
                        JMenuItem item = new JMenuItem(opcao);
                        item.setFont(UiStyle.fonte(13, Font.PLAIN));
                        item.setBackground(UiStyle.SURFACE);
                        item.setForeground(UiStyle.TEXT);
                        item.addActionListener(ev -> setText(opcao));
                        menu.add(item);
                    }
                    UiStyle.mostrarPopupSincronizado(menu, this);
                }
            });
        }

        public void setOpcoes(String[] novasOpcoes) {
            this.opcoes = novasOpcoes;
        }

        @Override
        public String getText() {
            return UiStyle.removerMarcadorSeta(super.getText());
        }

        @Override
        protected void paintComponent(Graphics g) {
            setForeground(UiStyle.TEXT);
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
                super.paintComponent(g);
                UiStyle.desenharChevronExpansivel(g2, w, h, UiStyle.MUTED);
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class CampoFormularioArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoFormularioArredondado(String valorInicial) {
            super("");
            if (valorInicial != null && !valorInicial.isEmpty()) {
                putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, valorInicial);
            }
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
            setForeground(UiStyle.TEXT);
            setCaretColor(UiStyle.TEXT);
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
                } else {
                    setForeground(UiStyle.DANGER_TEXT);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(0x4F, 0x1B, 0x1B, 40));
                        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                    }
                }

                if (corBorda != null) {
                    g2.setColor(preencherFundo ? UiStyle.INPUT_BORDER : UiStyle.DANGER_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

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
                Shape clipAnterior = g2.getClip();
                g2.clip(new RoundRectangle2D.Float(1f, 1f, Math.max(0, getWidth() - 2f), Math.max(0, getHeight() - 2f), arco, arco));
                super.paintChildren(g2);
                g2.setClip(clipAnterior);
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

    public void abrirModoLateral(ModoPainelLateral modo) {
        definirModoPainelLateral(modo);
    }

    @Override
    public void recarregarDados() {
        final DateTimeFormatter dtfVal = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        AsyncTask.executar(
            () -> produtoController.listarTodos(),
            produtos -> {
                this.listaProdutosEntidades = produtos != null ? produtos : new ArrayList<>();
                mesclarUnidadesDosProdutos(this.listaProdutosEntidades);
                List<Object[]> linhas = new ArrayList<>();
                if (produtos != null) {
                    for (Produto p : produtos) {
                        String idStr = String.valueOf(p.getIdProduto());
                        String nomeStr = p.getNome() != null ? p.getNome() : "";
                        String catStr = (p.getCategoria() != null && p.getCategoria().getNome() != null)
                                ? p.getCategoria().getNome() : "Geral";
                        String unidStr = (p.getUnidade() != null && !p.getUnidade().isBlank()) ? p.getUnidade() : "un";
                        String precoStr = String.format(Locale.GERMANY, "%,.2f MT / %s", p.getPreco() != null ? p.getPreco() : 0.0, unidStr);
                        int qtdReal = p.calcularQuantidadeEmEstoque();
                        int qtdMin = p.getQuantidadeMinima() != null ? p.getQuantidadeMinima() : 0;
                        String estoqueStr = p.estaAbaixoDoMinimo() ? (qtdReal + " (Baixo)") : String.valueOf(qtdReal);
                        String minStr = String.valueOf(qtdMin);
                        LocalDate proxVal = p.obterProximaValidade();
                        String proxValStr = proxVal != null ? proxVal.format(dtfVal) : "-";
                        int numForn = p.getFornecedor() != null ? p.getFornecedor().size() : 0;
                        String fornStr = String.valueOf(numForn);
                        String estStr = p.getEstado() != null ? p.getEstado().name() : "ATIVO";
                        linhas.add(new Object[] {idStr, nomeStr, catStr, precoStr, estoqueStr, minStr, proxValStr, fornStr, estStr});
                    }
                }
                this.dadosProdutosCarregados = linhas;
                atualizarTabelaFiltrada();
            },
            erro -> {
                this.listaProdutosEntidades = new ArrayList<>();
                this.dadosProdutosCarregados = new ArrayList<>();
                atualizarTabelaFiltrada();
            }
        );
        AsyncTask.executar(
            () -> produtoController.listarCategorias(),
            categorias -> {
                this.listaCategoriasReais = categorias != null ? categorias : new ArrayList<>();
                String[] nomesCat = this.listaCategoriasReais.stream()
                        .map(Categoria::getNome)
                        .toArray(String[]::new);
                if (seletorCategoriaNovo != null && nomesCat.length > 0) {
                    seletorCategoriaNovo.setOpcoes(nomesCat);
                    seletorCategoriaNovo.setText(nomesCat[0]);
                }
                if (seletorCategoriaEditar != null && nomesCat.length > 0) {
                    seletorCategoriaEditar.setOpcoes(nomesCat);
                }
            },
            erro -> {}
        );
        AsyncTask.executar(
            () -> fornecedorController.listarTodos(),
            fornecedores -> {
                this.listaTodosFornecedores = fornecedores != null ? fornecedores : new ArrayList<>();
                List<String> opcoesForn = new ArrayList<>();
                opcoesForn.add("Nenhum");
                for (Fornecedor f : this.listaTodosFornecedores) {
                    if (f != null && f.getNome() != null) {
                        opcoesForn.add(f.getNome());
                    }
                }
                String[] nomesForn = opcoesForn.toArray(new String[0]);
                if (seletorFornecedorNovo != null && nomesForn.length > 0) {
                    seletorFornecedorNovo.setOpcoes(nomesForn);
                    seletorFornecedorNovo.setText(nomesForn[0]);
                }
                if (seletorFornecedorEditar != null && nomesForn.length > 0) {
                    seletorFornecedorEditar.setOpcoes(nomesForn);
                }
            },
            erro -> {}
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
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
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardNovoProduto);
        UiStyle.atualizarComponentesTema(cardEditarProduto);
        UiStyle.atualizarComponentesTema(cardFornecedoresProduto);
    }
}
