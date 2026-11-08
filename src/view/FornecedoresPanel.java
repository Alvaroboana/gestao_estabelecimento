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
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.FornecedorController;
import model.vo.Bairro;
import model.vo.Cidade;
import model.vo.Contacto;
import model.vo.Endereco;
import model.vo.Fornecedor;
import model.vo.Perfil;
import model.vo.Produto;
import model.vo.Utilizador;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;

public class FornecedoresPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVO_FORNECEDOR,
        EDITAR_FORNECEDOR
    }

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int ALTURA_INPUT_FORM = 34;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_FORMULARIO = 396;

    private static final String[] COLUNAS_COMPLETAS = {
        "ID", "Nome", "Contactos", "Endereço", "Nº produtos"
    };

    private static final String[] COLUNAS_RESUMIDAS = {
        "ID", "Nome", "Contactos", "Nº produtos"
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final CampoBuscaArredondado campoBuscarFornecedor;
    private final JButton botaoNovoFornecedor;
    private final JButton botaoEditar;
    private final JButton botaoProdutos;
    private final JButton botaoEliminar;
    private final JPanel containerBotoesToolbar;
    private final JPanel cardNovoFornecedor;
    private final JPanel cardEditarFornecedor;
    private final JPanel wrapperDireita;

    private JTextField campoNomeNovo;
    private JTextField campoContactoNovo;
    private JTextField campoContactoExtraNovo;
    private JPanel blocoContactoExtraNovo;
    private JLabel linkAdicionarContactoNovo;
    private BotaoSeletorFormulario seletorCidadeNovo;
    private BotaoSeletorFormulario seletorBairroNovo;
    private JTextField campoQuarteiraoNovo;
    private JTextField campoRuaNovo;
    private JTextField campoNumeroCasaNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    private JTextField campoNomeEditar;
    private JTextField campoContactoEditar;
    private JTextField campoContactoExtraEditar;
    private JPanel blocoContactoExtraEditar;
    private JLabel linkAdicionarContactoEditar;
    private BotaoSeletorFormulario seletorCidadeEditar;
    private BotaoSeletorFormulario seletorBairroEditar;
    private JTextField campoQuarteiraoEditar;
    private JTextField campoRuaEditar;
    private JTextField campoNumeroCasaEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

    private final FornecedorController fornecedorController = new FornecedorController();
    private List<Object[]> dadosFornecedoresCarregados = new ArrayList<>();
    private List<Fornecedor> listaFornecedoresEntidades = new ArrayList<>();
    private List<Cidade> listaCidadesReais = new ArrayList<>();
    private List<Bairro> listaBairrosReais = new ArrayList<>();

    private ModoPainelLateral modoLateral;

    public FornecedoresPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public FornecedoresPanel(boolean exibirNovoFornecedorInicialmente) {
        this(exibirNovoFornecedorInicialmente ? ModoPainelLateral.NOVO_FORNECEDOR : ModoPainelLateral.NENHUM);
    }

    public FornecedoresPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarFornecedor = criarCampoBuscarFornecedor();
        this.botaoNovoFornecedor = criarBotaoNovoFornecedor();
        this.botaoEditar = criarBotaoEditarFornecedor();
        this.botaoProdutos = new BotaoToolbarArredondado(
                "Produtos", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoProdutos.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PACKAGE_VARIANT_CLOSED, 15, () -> UiStyle.TEXT));
        this.botaoProdutos.setIconTextGap(6);
        this.botaoProdutos.addActionListener(e -> exibirProdutosDoFornecedorSelecionado());
        this.botaoEliminar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminar.setIconTextGap(6);
        this.botaoEliminar.addActionListener(e -> eliminarFornecedorSelecionado());
        this.containerBotoesToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesToolbar.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovoFornecedor = criarCardNovoFornecedor();
        this.cardEditarFornecedor = criarCardEditarFornecedor();

        this.wrapperDireita = new JPanel(new BorderLayout());
        this.wrapperDireita.setOpaque(false);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosFornecedorParaEdicao();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        definirModoPainelLateral(this.modoLateral);
        recarregarDados();
    }

    public JTable getTabela() {
        return tabela;
    }

    public JTextField getCampoBuscarFornecedor() {
        return campoBuscarFornecedor;
    }

    public JButton getBotaoNovoFornecedor() {
        return botaoNovoFornecedor;
    }

    public JButton getBotaoEditar() {
        return botaoEditar;
    }

    public JButton getBotaoProdutos() {
        return botaoProdutos;
    }

    public JButton getBotaoEliminar() {
        return botaoEliminar;
    }

    public JPanel getCardNovoFornecedor() {
        return cardNovoFornecedor;
    }

    public JPanel getCardEditarFornecedor() {
        return cardEditarFornecedor;
    }

    public JTextField getCampoNomeNovo() {
        return campoNomeNovo;
    }

    public JTextField getCampoContactoNovo() {
        return campoContactoNovo;
    }

    public JLabel getLinkAdicionarContactoNovo() {
        return linkAdicionarContactoNovo;
    }

    public JButton getSeletorCidadeNovo() {
        return seletorCidadeNovo;
    }

    public JButton getSeletorBairroNovo() {
        return seletorBairroNovo;
    }

    public JTextField getCampoQuarteiraoNovo() {
        return campoQuarteiraoNovo;
    }

    public JTextField getCampoRuaNovo() {
        return campoRuaNovo;
    }

    public JTextField getCampoNumeroCasaNovo() {
        return campoNumeroCasaNovo;
    }

    public JButton getBotaoGuardarNovo() {
        return botaoGuardarNovo;
    }

    public JButton getBotaoCancelarNovo() {
        return botaoCancelarNovo;
    }

    public JTextField getCampoNomeEditar() {
        return campoNomeEditar;
    }

    public JTextField getCampoContactoEditar() {
        return campoContactoEditar;
    }

    public JLabel getLinkAdicionarContactoEditar() {
        return linkAdicionarContactoEditar;
    }

    public JButton getSeletorCidadeEditar() {
        return seletorCidadeEditar;
    }

    public JButton getSeletorBairroEditar() {
        return seletorBairroEditar;
    }

    public JTextField getCampoQuarteiraoEditar() {
        return campoQuarteiraoEditar;
    }

    public JTextField getCampoRuaEditar() {
        return campoRuaEditar;
    }

    public JTextField getCampoNumeroCasaEditar() {
        return campoNumeroCasaEditar;
    }

    public JButton getBotaoGuardarAlteracoesEditar() {
        return botaoGuardarAlteracoesEditar;
    }

    public JButton getBotaoCancelarEditar() {
        return botaoCancelarEditar;
    }

    public JButton getBotaoEliminarEditar() {
        return botaoEliminarEditar;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isNovoFornecedorVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_FORNECEDOR;
    }

    public boolean isEditarFornecedorVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_FORNECEDOR;
    }

    public final void definirNovoFornecedorVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVO_FORNECEDOR : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelEditar() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_FORNECEDOR);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean visivel = this.modoLateral != ModoPainelLateral.NENHUM;
        wrapperDireita.removeAll();
        wrapperDireita.setVisible(visivel);

        atualizarBotoesToolbar();
        atualizarTabelaFiltrada();

        if (this.modoLateral == ModoPainelLateral.NOVO_FORNECEDOR) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardNovoFornecedor), BorderLayout.CENTER);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_FORNECEDOR) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEditarFornecedor), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFornecedorParaEdicao();
        } else {
            tabela.clearSelection();
        }

        UiStyle.atualizarComponentesTema(wrapperDireita);
        revalidate();
        repaint();
    }

    private void atualizarBotoesToolbar() {
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Fornecedores");
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

        toolbar.add(campoBuscarFornecedor);
        toolbar.add(botaoNovoFornecedor);
        toolbar.add(botaoEditar);
        toolbar.add(botaoProdutos);
        toolbar.add(botaoEliminar);

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

    private CampoBuscaArredondado criarCampoBuscarFornecedor() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar fornecedor");
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

    private JButton criarBotaoNovoFornecedor() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo fornecedor", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_FORNECEDOR) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_FORNECEDOR);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarFornecedor() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_FORNECEDOR) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_FORNECEDOR);
            }
        });
        return botao;
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[0][COLUNAS_COMPLETAS.length], COLUNAS_COMPLETAS) {
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
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
        }
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

    private JPanel criarCardNovoFornecedor() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32;

        JLabel titulo = new JLabel("Novo fornecedor");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 54);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 54);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoExtraNovo = new CampoFormularioArredondado("");
        blocoContactoExtraNovo = criarCampoComLabel("2º Contacto (opcional)", campoContactoExtraNovo);
        fixarLargura(blocoContactoExtraNovo, larguraInterna, 54);
        blocoContactoExtraNovo.setVisible(false);
        card.add(blocoContactoExtraNovo);

        linkAdicionarContactoNovo = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoNovo.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoNovo.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoNovo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean visivel = !blocoContactoExtraNovo.isVisible();
                blocoContactoExtraNovo.setVisible(visivel);
                linkAdicionarContactoNovo.setText(visivel ? "- Remover 2º contacto" : "+ Adicionar contacto");
                if (!visivel && campoContactoExtraNovo != null) {
                    campoContactoExtraNovo.setText("");
                }
                card.revalidate();
                card.repaint();
            }
        });
        card.add(linkAdicionarContactoNovo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorCidadeNovo = new BotaoSeletorFormulario("Matola", new String[] {"Matola", "Maputo", "Boane"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", comBotaoMais(seletorCidadeNovo, "Nova cidade", () -> abrirDialogoNovaCidade(seletorCidadeNovo, seletorBairroNovo)));
        fixarLargura(blocoCidade, larguraInterna, 54);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorBairroNovo = new BotaoSeletorFormulario("Fomento", new String[] {"Fomento", "Liberdade", "Machava", "Central"});
        seletorCidadeNovo.setAoSelecionar(() -> atualizarBairrosPorCidade(seletorCidadeNovo, seletorBairroNovo, null));
        JPanel blocoBairro = criarCampoComLabel("Bairro * (filtrado pela cidade)", comBotaoMais(seletorBairroNovo, "Novo bairro", () -> abrirDialogoNovoBairro(seletorCidadeNovo, seletorBairroNovo)));
        fixarLargura(blocoBairro, larguraInterna, 54);
        card.add(blocoBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoQuarteiraoNovo = new CampoFormularioArredondado("");
        campoRuaNovo = new CampoFormularioArredondado("");
        JPanel linhaQuarteiraoRua = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuarteiraoRua.setOpaque(false);
        linhaQuarteiraoRua.add(criarCampoComLabel("Quarteirão *", campoQuarteiraoNovo));
        linhaQuarteiraoRua.add(criarCampoComLabel("Rua *", campoRuaNovo));
        fixarLargura(linhaQuarteiraoRua, larguraInterna, 54);
        card.add(linhaQuarteiraoRua);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNumeroCasaNovo = new CampoFormularioArredondado("");
        JPanel blocoNumeroCasa = criarCampoComLabel("Nº da casa *", campoNumeroCasaNovo);
        fixarLargura(blocoNumeroCasa, larguraInterna, 54);
        card.add(blocoNumeroCasa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarNovo.setIconTextGap(6);
        botaoGuardarNovo.addActionListener(e -> {
            String nome = (campoNomeNovo != null) ? campoNomeNovo.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contacto = (campoContactoNovo != null) ? campoContactoNovo.getText().trim() : "";
            if (contacto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o contacto principal do fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contactoExtra = (campoContactoExtraNovo != null && blocoContactoExtraNovo != null && blocoContactoExtraNovo.isVisible())
                    ? campoContactoExtraNovo.getText().trim() : "";
            String quarteirao = campoQuarteiraoNovo != null ? campoQuarteiraoNovo.getText().trim() : "";
            String rua = campoRuaNovo != null ? campoRuaNovo.getText().trim() : "";
            String numeroCasa = campoNumeroCasaNovo != null ? campoNumeroCasaNovo.getText().trim() : "";
            if (quarteirao.isEmpty() || rua.isEmpty() || numeroCasa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha Quarteirão, Rua e Nº da casa para o endereço.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Fornecedor f = new Fornecedor();
            f.setNome(nome);
            List<Contacto> listaContactosNovo = new ArrayList<>();
            Contacto c1 = new Contacto();
            c1.setContacto(contacto);
            listaContactosNovo.add(c1);
            if (!contactoExtra.isEmpty()) {
                Contacto c2 = new Contacto();
                c2.setContacto(contactoExtra);
                listaContactosNovo.add(c2);
            }
            f.setContacto(listaContactosNovo);
            Bairro bSel = resolverBairroSelecionado(
                    seletorBairroNovo != null ? seletorBairroNovo.getText() : null,
                    seletorCidadeNovo != null ? seletorCidadeNovo.getText() : null);
            if (bSel == null || bSel.getIdBairro() == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma Cidade e um Bairro válidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Endereco end = new Endereco();
            end.setBairro(bSel);
            end.setQuarteirao(quarteirao);
            end.setRua(rua);
            end.setNumeroCasa(numeroCasa);
            f.setEndereco(end);

            AsyncTask.executar(
                () -> {
                    fornecedorController.salvar(f);
                    return null;
                },
                sucesso -> {
                    JOptionPane.showMessageDialog(this, "Fornecedor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    if (campoNomeNovo != null) campoNomeNovo.setText("");
                    if (campoContactoNovo != null) campoContactoNovo.setText("");
                    if (campoContactoExtraNovo != null) campoContactoExtraNovo.setText("");
                    if (blocoContactoExtraNovo != null) blocoContactoExtraNovo.setVisible(false);
                    if (linkAdicionarContactoNovo != null) linkAdicionarContactoNovo.setText("+ Adicionar contacto");
                    if (campoQuarteiraoNovo != null) campoQuarteiraoNovo.setText("");
                    if (campoRuaNovo != null) campoRuaNovo.setText("");
                    if (campoNumeroCasaNovo != null) campoNumeroCasaNovo.setText("");
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar fornecedor: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        });
        botaoCancelarNovo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovo.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarNovo.setIconTextGap(6);
        botaoCancelarNovo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovo);
        card.add(acoes);

        return card;
    }

    private JPanel criarCardEditarFornecedor() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_FORMULARIO, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32;

        JLabel titulo = new JLabel("Editar fornecedor");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNomeEditar = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 54);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoEditar = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoEditar);
        fixarLargura(blocoContacto, larguraInterna, 54);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoExtraEditar = new CampoFormularioArredondado("");
        blocoContactoExtraEditar = criarCampoComLabel("2º Contacto (opcional)", campoContactoExtraEditar);
        fixarLargura(blocoContactoExtraEditar, larguraInterna, 54);
        blocoContactoExtraEditar.setVisible(false);
        card.add(blocoContactoExtraEditar);

        linkAdicionarContactoEditar = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoEditar.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoEditar.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoEditar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoEditar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean visivel = !blocoContactoExtraEditar.isVisible();
                blocoContactoExtraEditar.setVisible(visivel);
                linkAdicionarContactoEditar.setText(visivel ? "- Remover 2º contacto" : "+ Adicionar contacto");
                if (!visivel && campoContactoExtraEditar != null) {
                    campoContactoExtraEditar.setText("");
                }
                card.revalidate();
                card.repaint();
            }
        });
        card.add(linkAdicionarContactoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorCidadeEditar = new BotaoSeletorFormulario("Matola", new String[] {"Matola", "Maputo", "Boane"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", comBotaoMais(seletorCidadeEditar, "Nova cidade", () -> abrirDialogoNovaCidade(seletorCidadeEditar, seletorBairroEditar)));
        fixarLargura(blocoCidade, larguraInterna, 54);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorBairroEditar = new BotaoSeletorFormulario("Fomento", new String[] {"Fomento", "Liberdade", "Machava", "Central"});
        seletorCidadeEditar.setAoSelecionar(() -> atualizarBairrosPorCidade(seletorCidadeEditar, seletorBairroEditar, null));
        JPanel blocoBairro = criarCampoComLabel("Bairro * (filtrado pela cidade)", comBotaoMais(seletorBairroEditar, "Novo bairro", () -> abrirDialogoNovoBairro(seletorCidadeEditar, seletorBairroEditar)));
        fixarLargura(blocoBairro, larguraInterna, 54);
        card.add(blocoBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoQuarteiraoEditar = new CampoFormularioArredondado("");
        campoRuaEditar = new CampoFormularioArredondado("");
        JPanel linhaQuarteiraoRua = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuarteiraoRua.setOpaque(false);
        linhaQuarteiraoRua.add(criarCampoComLabel("Quarteirão *", campoQuarteiraoEditar));
        linhaQuarteiraoRua.add(criarCampoComLabel("Rua *", campoRuaEditar));
        fixarLargura(linhaQuarteiraoRua, larguraInterna, 54);
        card.add(linhaQuarteiraoRua);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNumeroCasaEditar = new CampoFormularioArredondado("");
        JPanel blocoNumeroCasa = criarCampoComLabel("Nº da casa *", campoNumeroCasaEditar);
        fixarLargura(blocoNumeroCasa, larguraInterna, 54);
        card.add(blocoNumeroCasa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true, 10
        );
        botaoGuardarAlteracoesEditar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 14, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoesEditar.setIconTextGap(5);
        botaoGuardarAlteracoesEditar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um fornecedor para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            try {
                Long idFornecedor = Long.parseLong(idStr);
                String nome = (campoNomeEditar != null) ? campoNomeEditar.getText().trim() : "";
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o nome do fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Fornecedor f = (listaFornecedoresEntidades != null) ? listaFornecedoresEntidades.stream()
                    .filter(item -> item.getIdFornecedor() != null && item.getIdFornecedor().equals(idFornecedor))
                    .findFirst().orElse(null) : null;
                if (f == null) {
                    f = new Fornecedor();
                    f.setIdFornecedor(idFornecedor);
                }
                f.setNome(nome);
                String cont = (campoContactoEditar != null) ? campoContactoEditar.getText().trim() : "";
                if (cont.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o contacto principal do fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String contExtra = (campoContactoExtraEditar != null && blocoContactoExtraEditar != null && blocoContactoExtraEditar.isVisible())
                        ? campoContactoExtraEditar.getText().trim() : "";
                String quarteirao = campoQuarteiraoEditar != null ? campoQuarteiraoEditar.getText().trim() : "";
                String rua = campoRuaEditar != null ? campoRuaEditar.getText().trim() : "";
                String numeroCasa = campoNumeroCasaEditar != null ? campoNumeroCasaEditar.getText().trim() : "";
                if (quarteirao.isEmpty() || rua.isEmpty() || numeroCasa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Preencha Quarteirão, Rua e Nº da casa para o endereço.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                List<Contacto> contList = new ArrayList<>();
                Contacto c = new Contacto();
                c.setContacto(cont);
                contList.add(c);
                if (!contExtra.isEmpty()) {
                    Contacto c2 = new Contacto();
                    c2.setContacto(contExtra);
                    contList.add(c2);
                }
                f.setContacto(contList);
                Bairro bSel = resolverBairroSelecionado(
                        seletorBairroEditar != null ? seletorBairroEditar.getText() : null,
                        seletorCidadeEditar != null ? seletorCidadeEditar.getText() : null);
                if (bSel == null || bSel.getIdBairro() == null) {
                    JOptionPane.showMessageDialog(this, "Selecione uma Cidade e um Bairro válidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Endereco end = new Endereco();
                end.setBairro(bSel);
                end.setQuarteirao(quarteirao);
                end.setRua(rua);
                end.setNumeroCasa(numeroCasa);
                f.setEndereco(end);

                final Fornecedor fFornec = f;
                botaoGuardarAlteracoesEditar.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        fornecedorController.atualizar(fFornec);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Fornecedor atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoGuardarAlteracoesEditar.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar fornecedor: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoGuardarAlteracoesEditar.setEnabled(true);
                    }
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de fornecedor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 10
        );
        botaoCancelarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 14, () -> UiStyle.TEXT));
        botaoCancelarEditar.setIconTextGap(5);
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false, 10
        );
        botaoEliminarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 14, () -> UiStyle.DANGER_TEXT));
        botaoEliminarEditar.setIconTextGap(5);
        botaoEliminarEditar.addActionListener(e -> eliminarFornecedorSelecionado());
        acoes.add(botaoGuardarAlteracoesEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoCancelarEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoEliminarEditar);
        card.add(acoes);

        return card;
    }

    private void eliminarFornecedorSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o fornecedor #" + idStr + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idFornecedor = Long.parseLong(idStr);
            AsyncTask.executar(
                () -> {
                    fornecedorController.eliminar(idFornecedor);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Fornecedor #" + idStr + " eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao eliminar fornecedor: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Identificador de fornecedor inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirProdutosDoFornecedorSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para ver os seus produtos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        try {
            Long idFornecedor = Long.parseLong(idStr);
            Fornecedor forn = (listaFornecedoresEntidades != null) ? listaFornecedoresEntidades.stream()
                    .filter(f -> idFornecedor.equals(f.getIdFornecedor()))
                    .findFirst().orElse(null) : null;
            if (forn == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            if (forn.getProduto() == null || forn.getProduto().isEmpty()) {
                sb.append("Nenhum produto associado a este fornecedor.");
            } else {
                for (Produto p : forn.getProduto()) {
                    sb.append("• ").append(p.getNome()).append("\n");
                }
            }
            JOptionPane.showMessageDialog(
                    this,
                    sb.toString(),
                    "Produtos de " + forn.getNome(),
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ignored) {}
    }

    private JComponent comBotaoMais(BotaoSeletorFormulario seletor, String dica, Runnable acao) {
        JPanel linha = new JPanel(new BorderLayout(6, 0));
        linha.setOpaque(false);

        BotaoToolbarArredondado mais = new BotaoToolbarArredondado(
                "", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 10
        );
        mais.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PLUS, 14, () -> UiStyle.TEXT));
        if (mais.getIcon() == null) {
            mais.setText("+");
        }
        mais.setToolTipText(dica);
        mais.addActionListener(e -> acao.run());

        linha.add(seletor, BorderLayout.CENTER);
        linha.add(mais, BorderLayout.EAST);
        return linha;
    }

    private Cidade resolverCidadeSelecionada(String textoCidade) {
        String nomeCidade = textoCidade != null ? UiStyle.removerMarcadorSeta(textoCidade).trim() : "";
        if (listaCidadesReais != null) {
            for (Cidade c : listaCidadesReais) {
                if (c != null && c.getNome() != null && c.getNome().equalsIgnoreCase(nomeCidade)) {
                    return c;
                }
            }
        }
        return null;
    }

    private Bairro resolverBairroSelecionado(String textoBairro, String textoCidade) {
        String nomeBairro = textoBairro != null ? UiStyle.removerMarcadorSeta(textoBairro).trim() : "";
        Cidade cidade = resolverCidadeSelecionada(textoCidade);
        if (listaBairrosReais != null) {
            for (Bairro b : listaBairrosReais) {
                if (b != null && b.getNome() != null && b.getNome().equalsIgnoreCase(nomeBairro)) {
                    if (cidade == null || b.getCidade() == null
                            || cidade.getIdCidade().equals(b.getCidade().getIdCidade())) {
                        return b;
                    }
                }
            }
            for (Bairro b : listaBairrosReais) {
                if (b != null && b.getNome() != null && b.getNome().equalsIgnoreCase(nomeBairro)) {
                    return b;
                }
            }
        }
        return null;
    }

    private void atualizarBairrosPorCidade(
            BotaoSeletorFormulario seletorCidade,
            BotaoSeletorFormulario seletorBairro,
            String bairroPreferido) {
        if (seletorCidade == null || seletorBairro == null) {
            return;
        }
        Cidade cid = resolverCidadeSelecionada(seletorCidade.getText());
        List<String> nomes = new ArrayList<>();
        if (listaBairrosReais != null) {
            for (Bairro b : listaBairrosReais) {
                if (b != null && b.getNome() != null) {
                    if (cid == null || (b.getCidade() != null && cid.getIdCidade().equals(b.getCidade().getIdCidade()))) {
                        nomes.add(b.getNome());
                    }
                }
            }
        }
        String[] opcoes = nomes.toArray(new String[0]);
        seletorBairro.setOpcoes(opcoes);
        if (bairroPreferido != null && nomes.stream().anyMatch(n -> n.equalsIgnoreCase(bairroPreferido))) {
            seletorBairro.setText(bairroPreferido);
        } else if (opcoes.length > 0) {
            seletorBairro.setText(opcoes[0]);
        } else {
            seletorBairro.setText("Sem bairros");
        }
    }

    private void abrirDialogoNovaCidade(BotaoSeletorFormulario seletorCidade, BotaoSeletorFormulario seletorBairro) {
        String nome = JOptionPane.showInputDialog(this, "Nome da nova cidade *:", "Nova Cidade", JOptionPane.PLAIN_MESSAGE);
        if (nome == null) {
            return;
        }
        String nomeLimpo = nome.trim();
        if (nomeLimpo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da cidade.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AsyncTask.executar(
            () -> fornecedorController.salvarCidade(nomeLimpo),
            criada -> {
                if (seletorCidade != null && criada != null && criada.getNome() != null) {
                    seletorCidade.setText(criada.getNome());
                }
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao criar cidade: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void abrirDialogoNovoBairro(BotaoSeletorFormulario seletorCidade, BotaoSeletorFormulario seletorBairro) {
        Cidade cid = resolverCidadeSelecionada(seletorCidade != null ? seletorCidade.getText() : null);
        if (cid == null) {
            JOptionPane.showMessageDialog(this, "Selecione ou cadastre uma cidade primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nome = JOptionPane.showInputDialog(
                this,
                "Nome do novo bairro para a cidade \"" + cid.getNome() + "\" *:",
                "Novo Bairro",
                JOptionPane.PLAIN_MESSAGE
        );
        if (nome == null) {
            return;
        }
        String nomeLimpo = nome.trim();
        if (nomeLimpo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do bairro.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AsyncTask.executar(
            () -> fornecedorController.salvarBairro(nomeLimpo, cid),
            criado -> {
                if (seletorBairro != null && criado != null && criado.getNome() != null) {
                    seletorBairro.setText(criado.getNome());
                }
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao criar bairro: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void sincronizarDadosFornecedorParaEdicao() {
        if (campoNomeEditar == null) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            row = 0;
        }
        if (modeloTabela.getRowCount() == 0) {
            return;
        }

        String id = String.valueOf(modeloTabela.getValueAt(row, 0));
        try {
            Long idForn = Long.parseLong(id);
            if (listaFornecedoresEntidades != null) {
                for (Fornecedor f : listaFornecedoresEntidades) {
                    if (idForn.equals(f.getIdFornecedor())) {
                        campoNomeEditar.setText(f.getNome() != null ? f.getNome() : "");
                        String cont = (f.getContacto() != null && !f.getContacto().isEmpty() && f.getContacto().get(0).getContacto() != null)
                                ? f.getContacto().get(0).getContacto() : "";
                        campoContactoEditar.setText(cont);
                        String cont2 = (f.getContacto() != null && f.getContacto().size() > 1 && f.getContacto().get(1) != null
                                && f.getContacto().get(1).getContacto() != null)
                                ? f.getContacto().get(1).getContacto() : "";
                        if (campoContactoExtraEditar != null) {
                            campoContactoExtraEditar.setText(cont2);
                        }
                        if (blocoContactoExtraEditar != null) {
                            boolean temSegundo = !cont2.isEmpty();
                            blocoContactoExtraEditar.setVisible(temSegundo);
                            if (linkAdicionarContactoEditar != null) {
                                linkAdicionarContactoEditar.setText(temSegundo ? "- Remover 2º contacto" : "+ Adicionar contacto");
                            }
                        }
                        Endereco end = f.getEndereco();
                        if (end != null) {
                            String nomeBairro = null;
                            if (end.getBairro() != null) {
                                nomeBairro = end.getBairro().getNome();
                                if (end.getBairro().getCidade() != null && end.getBairro().getCidade().getNome() != null) {
                                    seletorCidadeEditar.setText(end.getBairro().getCidade().getNome());
                                }
                            }
                            atualizarBairrosPorCidade(seletorCidadeEditar, seletorBairroEditar, nomeBairro);
                            campoQuarteiraoEditar.setText(end.getQuarteirao() != null ? end.getQuarteirao() : "");
                            campoRuaEditar.setText(end.getRua() != null ? end.getRua() : "");
                            campoNumeroCasaEditar.setText(end.getNumeroCasa() != null ? end.getNumeroCasa() : "");
                        } else {
                            campoQuarteiraoEditar.setText("");
                            campoRuaEditar.setText("");
                            campoNumeroCasaEditar.setText("");
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        if (campo instanceof javax.swing.text.JTextComponent
                && campo.getClientProperty(FlatClientProperties.PLACEHOLDER_TEXT) == null) {
            String dica = textoLabel.replaceAll("\\(.*?\\)", "").replace("*", "").trim().toLowerCase();
            campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Introduzir " + dica);
        }
        JPanel bloco = new JPanel(new BorderLayout(0, 4));
        bloco.setOpaque(false);

        JLabel label = new JLabel(textoLabel);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);

        Dimension dimCampo = new Dimension(0, ALTURA_INPUT_FORM);
        campo.setPreferredSize(dimCampo);
        campo.setMinimumSize(dimCampo);

        bloco.add(label, BorderLayout.NORTH);
        bloco.add(campo, BorderLayout.CENTER);
        return bloco;
    }

    private void fixarLargura(JComponent comp, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void atualizarTabelaFiltrada() {
        String termo = campoBuscarFornecedor.getTextoReal().trim().toLowerCase();
        boolean resumida = modoLateral != ModoPainelLateral.NENHUM;
        String[] colunasAlvo = resumida ? COLUNAS_RESUMIDAS : COLUNAS_COMPLETAS;

        if (modeloTabela.getColumnCount() != colunasAlvo.length) {
            modeloTabela.setDataVector(new Object[0][0], colunasAlvo);
            aplicarRenderizadoresColunas(tabela);
        } else {
            modeloTabela.setRowCount(0);
        }

        List<Object[]> fonteDados = (dadosFornecedoresCarregados != null) ? dadosFornecedoresCarregados : new ArrayList<>();

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            String endereco = String.valueOf(linha[3]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo) || endereco.contains(termo)) {
                if (resumida) {
                    modeloTabela.addRow(new Object[] {
                        linha[0], linha[1], linha[2], linha[4]
                    });
                } else {
                    modeloTabela.addRow(linha);
                }
            }
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

    private static final class BotaoSeletorFormulario extends JButton {
        private static final long serialVersionUID = 1L;
        private String[] opcoes;
        private Runnable aoSelecionar;

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
                        item.addActionListener(ev -> {
                            setText(opcao);
                            if (this.aoSelecionar != null) {
                                this.aoSelecionar.run();
                            }
                        });
                        menu.add(item);
                    }
                    UiStyle.mostrarPopupSincronizado(menu, this);
                }
            });
        }

        void setOpcoes(String[] novasOpcoes) {
            this.opcoes = novasOpcoes;
        }

        void setAoSelecionar(Runnable callback) {
            this.aoSelecionar = callback;
        }

        @Override
        public String getText() {
            return UiStyle.removerMarcadorSeta(super.getText());
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
                super.paintComponent(g);
                UiStyle.desenharChevronExpansivel(g2, this, getForeground());
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

    private static class PainelCardTabela extends JPanel {
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

    public void abrirModoLateral(ModoPainelLateral modo) {
        definirModoPainelLateral(modo);
    }

    @Override
    public void recarregarDados() {
        AsyncTask.executar(
            () -> fornecedorController.listarTodos(),
            forns -> {
                this.listaFornecedoresEntidades = forns != null ? forns : new ArrayList<>();
                List<Object[]> linhas = new ArrayList<>();
                if (forns != null) {
                    for (Fornecedor f : forns) {
                        String idStr = String.valueOf(f.getIdFornecedor());
                        String nomeStr = f.getNome() != null ? f.getNome() : "";
                        String contStr = "-";
                        if (f.getContacto() != null && !f.getContacto().isEmpty()) {
                            List<String> nums = new ArrayList<>();
                            for (Contacto c : f.getContacto()) {
                                if (c != null && c.getContacto() != null && !c.getContacto().isBlank()) {
                                    nums.add(c.getContacto().trim());
                                }
                            }
                            if (!nums.isEmpty()) {
                                contStr = String.join(" / ", nums);
                            }
                        }
                        String endStr = "-";
                        if (f.getEndereco() != null) {
                            List<String> partes = new ArrayList<>();
                            if (f.getEndereco().getBairro() != null) {
                                if (f.getEndereco().getBairro().getCidade() != null
                                        && f.getEndereco().getBairro().getCidade().getNome() != null) {
                                    partes.add(f.getEndereco().getBairro().getCidade().getNome());
                                }
                                if (f.getEndereco().getBairro().getNome() != null) {
                                    partes.add("Bairro " + f.getEndereco().getBairro().getNome());
                                }
                            }
                            if (f.getEndereco().getRua() != null && !f.getEndereco().getRua().isBlank()) {
                                partes.add(f.getEndereco().getRua());
                            }
                            if (!partes.isEmpty()) {
                                endStr = String.join(", ", partes);
                            }
                        }
                        int qtdProd = (f.getProduto() != null) ? f.getProduto().size() : 0;
                        String numProdStr = String.valueOf(qtdProd);
                        linhas.add(new Object[] {idStr, nomeStr, contStr, endStr, numProdStr});
                    }
                }
                this.dadosFornecedoresCarregados = linhas;
                atualizarTabelaFiltrada();
            },
            erro -> {
                this.listaFornecedoresEntidades = new ArrayList<>();
                this.dadosFornecedoresCarregados = new ArrayList<>();
                atualizarTabelaFiltrada();
            }
        );
        AsyncTask.executar(
            () -> {
                List<Cidade> cids = fornecedorController.listarCidades();
                List<Bairro> bairs = fornecedorController.listarBairros();
                return new Object[] { cids, bairs };
            },
            res -> {
                Object[] arr = (Object[]) res;
                @SuppressWarnings("unchecked")
                List<Cidade> cids = (List<Cidade>) arr[0];
                @SuppressWarnings("unchecked")
                List<Bairro> bairs = (List<Bairro>) arr[1];
                this.listaCidadesReais = cids != null ? cids : new ArrayList<>();
                this.listaBairrosReais = bairs != null ? bairs : new ArrayList<>();
                String[] nomesCid = this.listaCidadesReais.stream().map(Cidade::getNome).toArray(String[]::new);
                if (seletorCidadeNovo != null && nomesCid.length > 0) {
                    seletorCidadeNovo.setOpcoes(nomesCid);
                    if (resolverCidadeSelecionada(seletorCidadeNovo.getText()) == null) {
                        seletorCidadeNovo.setText(nomesCid[0]);
                    }
                }
                if (seletorCidadeEditar != null && nomesCid.length > 0) {
                    seletorCidadeEditar.setOpcoes(nomesCid);
                }
                atualizarBairrosPorCidade(seletorCidadeNovo, seletorBairroNovo,
                        seletorBairroNovo != null ? seletorBairroNovo.getText() : null);
                atualizarBairrosPorCidade(seletorCidadeEditar, seletorBairroEditar,
                        seletorBairroEditar != null ? seletorBairroEditar.getText() : null);
            },
            erro -> {}
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardNovoFornecedor);
        UiStyle.atualizarComponentesTema(cardEditarFornecedor);
    }
}
