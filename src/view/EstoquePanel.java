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
import java.awt.Shape;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import controller.EstoqueController;
import model.vo.Estoque;
import model.vo.Produto;
import model.vo.SessaoUtilizador;
import model.vo.Perfil;
import model.vo.Utilizador;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;

public class EstoquePanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        DAR_ENTRADA,
        REPOR_ESTOQUE,
        RETIRAR_ESTOQUE,
        EDITAR_LOTE
    }

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] COLUNAS_COMPLETAS = {
        "Lote", "Produto", "Quantidade", "Entrada", "Validade", "Dias p/ Exp.", "Registado por", "Situação"
    };

    private static final String[] COLUNAS_COMPACTAS = {
        "Lote", "Produto", "Qtd", "Entrada", "Validade", "Dias", "Funcionário", "Situação"
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final CampoBuscaArredondado campoBuscarLote;
    private final JButton botaoDarEntrada;
    private final JButton botaoRepor;
    private final JButton botaoRetirar;
    private final JButton botaoEditar;
    private final JButton botaoEliminarLote;
    private final JPanel containerBotoesFinaisToolbar;
    private final JPanel cardDarEntrada;
    private final JPanel cardReporEstoque;
    private final JPanel cardRetirarEstoque;
    private final JPanel cardEditarLote;
    private final JPanel wrapperDireita;

    private BotaoSeletorFormulario seletorProdutoEntrada;
    private JTextField campoQuantidadeEntrada;
    private JTextField campoValidadeEntrada;

    private JLabel labelLoteProdutoRepor;
    private CaixaValorArredondada caixaQuantidadeActualRepor;
    private CampoFormularioArredondado campoQuantidadeARepor;
    private CaixaValorArredondada caixaNovaQuantidadeRepor;

    private JLabel labelLoteProdutoRetirar;
    private CaixaValorArredondada caixaQuantidadeActualRetirar;
    private JLabel labelARetirarMax;
    private CampoFormularioArredondado campoQuantidadeARetirar;
    private CaixaValorArredondada caixaNovaQuantidadeRetirar;
    private BotaoSeletorFormulario seletorMotivoRetirada;

    private JLabel labelCodigoLoteEditar;
    private BotaoSeletorFormulario seletorProdutoEditarLote;
    private CampoFormularioArredondado campoQuantidadeEditarLote;
    private CampoFormularioArredondado campoValidadeEditarLote;

    private final EstoqueController estoqueController;
    private List<Object[]> dadosEstoqueCarregados = new ArrayList<>();
    private List<Estoque> listaEstoqueEntidades = new ArrayList<>();
    private List<Produto> listaProdutosReais = new ArrayList<>();

    private ModoPainelLateral modoLateral;

    public EstoquePanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public EstoquePanel(boolean exibirDarEntradaInicialmente) {
        this(exibirDarEntradaInicialmente ? ModoPainelLateral.DAR_ENTRADA : ModoPainelLateral.NENHUM);
    }

    public EstoquePanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;
        this.estoqueController = new EstoqueController();

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarLote = criarCampoBuscarLote();
        this.botaoDarEntrada = criarBotaoDarEntrada();
        this.botaoRepor = criarBotaoReporEstoque();
        this.botaoRetirar = criarBotaoRetirarEstoque();
        this.botaoEditar = criarBotaoEditarLote();
        this.botaoEliminarLote = criarBotaoToolbar("Eliminar lote", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);
        this.botaoEliminarLote.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarLote.setIconTextGap(6);
        this.botaoEliminarLote.addActionListener(e -> eliminarLoteSelecionado());
        this.containerBotoesFinaisToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesFinaisToolbar.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardDarEntrada = criarCardDarEntrada();
        this.cardReporEstoque = criarCardReporEstoque();
        this.cardRetirarEstoque = criarCardRetirarEstoque();
        this.cardEditarLote = criarCardEditarLote();
        this.wrapperDireita = new JPanel(new BorderLayout());
        this.wrapperDireita.setOpaque(false);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosLoteParaReposicao();
                sincronizarDadosLoteParaRetirada();
                sincronizarDadosLoteParaEdicao();
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

    public JTextField getCampoBuscarLote() {
        return campoBuscarLote;
    }

    public JButton getBotaoDarEntrada() {
        return botaoDarEntrada;
    }

    public JButton getBotaoRepor() {
        return botaoRepor;
    }

    public JButton getBotaoRetirar() {
        return botaoRetirar;
    }

    public JButton getBotaoEditar() {
        return botaoEditar;
    }

    public JButton getBotaoEliminarLote() {
        return botaoEliminarLote;
    }

    public JPanel getCardDarEntrada() {
        return cardDarEntrada;
    }

    public JPanel getCardReporEstoque() {
        return cardReporEstoque;
    }

    public JPanel getCardRetirarEstoque() {
        return cardRetirarEstoque;
    }

    public JPanel getCardEditarLote() {
        return cardEditarLote;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isDarEntradaVisivel() {
        return modoLateral == ModoPainelLateral.DAR_ENTRADA;
    }

    public boolean isReporEstoqueVisivel() {
        return modoLateral == ModoPainelLateral.REPOR_ESTOQUE;
    }

    public boolean isRetirarEstoqueVisivel() {
        return modoLateral == ModoPainelLateral.RETIRAR_ESTOQUE;
    }

    public boolean isEditarLoteVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_LOTE;
    }

    public final void definirDarEntradaVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.DAR_ENTRADA : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelDarEntrada() {
        definirModoPainelLateral(ModoPainelLateral.DAR_ENTRADA);
    }

    public final void exibirPainelRepor() {
        definirModoPainelLateral(ModoPainelLateral.REPOR_ESTOQUE);
    }

    public final void exibirPainelRetirar() {
        definirModoPainelLateral(ModoPainelLateral.RETIRAR_ESTOQUE);
    }

    public final void exibirPainelEditarLote() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_LOTE);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? modoInicialSeguro(novoModo) : ModoPainelLateral.NENHUM;
        boolean lateralAberto = this.modoLateral != ModoPainelLateral.NENHUM;

        wrapperDireita.removeAll();
        wrapperDireita.setVisible(lateralAberto);
        if (this.modoLateral == ModoPainelLateral.DAR_ENTRADA) {
            UiStyle.atualizarComponentesTema(cardDarEntrada);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardDarEntrada), BorderLayout.CENTER);
        } else if (this.modoLateral == ModoPainelLateral.REPOR_ESTOQUE) {
            UiStyle.atualizarComponentesTema(cardReporEstoque);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardReporEstoque), BorderLayout.CENTER);
        } else if (this.modoLateral == ModoPainelLateral.RETIRAR_ESTOQUE) {
            UiStyle.atualizarComponentesTema(cardRetirarEstoque);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardRetirarEstoque), BorderLayout.CENTER);
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_LOTE) {
            UiStyle.atualizarComponentesTema(cardEditarLote);
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEditarLote), BorderLayout.CENTER);
        }

        if (lateralAberto) {
            botaoEliminarLote.setText("Eliminar");
        } else {
            botaoEliminarLote.setText("Eliminar lote");
        }
        Dimension prefEliminar = botaoEliminarLote.getPreferredSize();
        botaoEliminarLote.setPreferredSize(new Dimension(prefEliminar.width, ALTURA_INPUT));

        atualizarEstruturaTabela();
        atualizarTabelaFiltrada();

        if (this.modoLateral == ModoPainelLateral.REPOR_ESTOQUE && tabela.getRowCount() > 0) {
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosLoteParaReposicao();
        } else if (this.modoLateral == ModoPainelLateral.RETIRAR_ESTOQUE && tabela.getRowCount() > 0) {
            int indiceLote198 = Math.min(1, tabela.getRowCount() - 1);
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(indiceLote198, indiceLote198);
            }
            sincronizarDadosLoteParaRetirada();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_LOTE && tabela.getRowCount() > 0) {
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosLoteParaEdicao();
        } else {
            tabela.clearSelection();
        }

        revalidate();
        repaint();
    }

    private ModoPainelLateral modoInicialSeguro(ModoPainelLateral modo) {
        return modo != null ? modo : ModoPainelLateral.NENHUM;
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Estoque");
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

        toolbar.add(campoBuscarLote);
        toolbar.add(botaoDarEntrada);
        toolbar.add(botaoRepor);
        toolbar.add(botaoRetirar);
        toolbar.add(botaoEditar);
        toolbar.add(botaoEliminarLote);

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

    private CampoBuscaArredondado criarCampoBuscarLote() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar lote");
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

    private JButton criarBotaoDarEntrada() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Dar entrada", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.DAR_ENTRADA) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.DAR_ENTRADA);
            }
        });
        return botao;
    }

    private JButton criarBotaoReporEstoque() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Repor", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PACKAGE_UP, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.REPOR_ESTOQUE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.REPOR_ESTOQUE);
            }
        });
        return botao;
    }

    private JButton criarBotaoRetirarEstoque() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Retirar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PACKAGE_DOWN, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.RETIRAR_ESTOQUE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                tabela.clearSelection();
                definirModoPainelLateral(ModoPainelLateral.RETIRAR_ESTOQUE);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarLote() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_LOTE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_LOTE);
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
        RenderizadorCelulaSituacao renderizadorSituacao = new RenderizadorCelulaSituacao();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 7) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorSituacao);
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

    private JPanel criarCardDarEntrada() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Dar entrada no estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorProdutoEntrada = new BotaoSeletorFormulario(
                "Selecione",
                new String[0]
        );
        JPanel blocoProduto = criarCampoComLabel("Produto *", seletorProdutoEntrada);
        fixarLargura(blocoProduto, larguraInterna, 60);
        card.add(blocoProduto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);

        campoQuantidadeEntrada = new CampoFormularioArredondado("");
        campoValidadeEntrada = new CampoFormularioArredondado("dd/mm/aaaa");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade *", campoQuantidadeEntrada));
        linhaQtdValidade.add(criarCampoComLabel("Validade", campoValidadeEntrada));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel notaLote = new JLabel("Cria um novo lote para o produto.");
        notaLote.setFont(UiStyle.fonte(12, Font.PLAIN));
        notaLote.setForeground(UiStyle.MUTED);
        fixarLargura(notaLote, larguraInterna, 18);
        card.add(notaLote);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarEntrada = new BotaoToolbarArredondado(
                "Guardar entrada", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarEntrada.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarEntrada.setIconTextGap(6);
        botaoGuardarEntrada.addActionListener(e -> {
            String qtdStr = (campoQuantidadeEntrada != null) ? campoQuantidadeEntrada.getText().trim() : "0";
            int qtd;
            try {
                qtd = Integer.parseInt(qtdStr);
                if (qtd <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe uma quantidade válida e positiva.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String valStr = (campoValidadeEntrada != null) ? campoValidadeEntrada.getText().trim() : "";
            LocalDate validade = null;
            if (!valStr.isEmpty() && !"-".equals(valStr)) {
                try {
                    validade = LocalDate.parse(valStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Validade inválida. Use o formato dd/mm/aaaa ou deixe em branco.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (validade.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, "A data de validade já passou.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            Utilizador user = SessaoUtilizador.getUtilizador();
            if (user == null || user.getFuncionario() == null) {
                JOptionPane.showMessageDialog(this, "Sessão inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String nomeProdSel = seletorProdutoEntrada != null
                    ? seletorProdutoEntrada.getText().replace(" ▾", "").replace("▾", "").trim()
                    : "";
            Produto prod = null;
            if (listaProdutosReais != null) {
                for (Produto p : listaProdutosReais) {
                    if (p.getNome() != null && p.getNome().equalsIgnoreCase(nomeProdSel)) {
                        prod = p;
                        break;
                    }
                }
                if (prod == null && !listaProdutosReais.isEmpty()) {
                    prod = listaProdutosReais.get(0);
                }
            }
            if (prod == null) {
                JOptionPane.showMessageDialog(this, "Selecione um produto válido cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Estoque novoLote = new Estoque();
            novoLote.setQuantidade(qtd);
            novoLote.setDataEntrada(LocalDate.now());
            novoLote.setDataValidade(validade);
            novoLote.setFuncionario(user.getFuncionario());
            novoLote.setProduto(prod);

            botaoGuardarEntrada.setEnabled(false);
            AsyncTask.executar(
                () -> {
                    estoqueController.salvar(novoLote);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Entrada de lote registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                    botaoGuardarEntrada.setEnabled(true);
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao registrar entrada: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    botaoGuardarEntrada.setEnabled(true);
                }
            );
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoGuardarEntrada);
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

    private JPanel criarCardReporEstoque() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Repor estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelLoteProdutoRepor = new JLabel("Lote L-201 · Leite 1L");
        labelLoteProdutoRepor.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelLoteProdutoRepor.setForeground(UiStyle.TEXT);
        fixarLargura(labelLoteProdutoRepor, larguraInterna, 20);
        card.add(labelLoteProdutoRepor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQuantidades = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuantidades.setOpaque(false);

        caixaQuantidadeActualRepor = new CaixaValorArredondada("120");
        campoQuantidadeARepor = new CampoFormularioArredondado("");
        campoQuantidadeARepor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRepor();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRepor();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRepor();
            }
        });

        linhaQuantidades.add(criarCampoComLabel("Quantidade actual", caixaQuantidadeActualRepor));
        linhaQuantidades.add(criarCampoComLabel("A repor (maior que 0)", campoQuantidadeARepor));
        fixarLargura(linhaQuantidades, larguraInterna, 60);
        card.add(linhaQuantidades);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        caixaNovaQuantidadeRepor = new CaixaValorArredondada(
                "150",
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_FG,
                UiStyle.fonte(13, Font.BOLD)
        );
        JPanel blocoNovaQuantidade = criarCampoComLabel("Nova quantidade", caixaNovaQuantidadeRepor);
        fixarLargura(blocoNovaQuantidade, larguraInterna, 60);
        card.add(blocoNovaQuantidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmarReposicao = new BotaoToolbarArredondado(
                "Confirmar reposição", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoConfirmarReposicao.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoConfirmarReposicao.setIconTextGap(6);
        botaoConfirmarReposicao.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um lote na tabela para repor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String qtdStr = (campoQuantidadeARepor != null) ? campoQuantidadeARepor.getText().trim() : "0";
            int qtdRepor;
            try {
                qtdRepor = Integer.parseInt(qtdStr);
                if (qtdRepor <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe uma quantidade válida e positiva para repor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String loteStr = String.valueOf(tabela.getValueAt(row, 0)).replace("L-", "").trim();
            try {
                Long idEstoque = Long.parseLong(loteStr);
                Estoque lote = (listaEstoqueEntidades != null) ? listaEstoqueEntidades.stream()
                        .filter(item -> item.getIdEstoque() != null && item.getIdEstoque().equals(idEstoque))
                        .findFirst().orElse(null) : null;
                if (lote == null) {
                    lote = estoqueController.buscarPorId(idEstoque);
                }
                if (lote.getFuncionario() != null && lote.getFuncionario().getEstado() == null) {
                    lote.getFuncionario().setEstado(model.vo.EstadoFuncionario.ATIVO);
                }
                int qtdAtual = lote.getQuantidade() != null ? lote.getQuantidade() : 0;
                lote.setQuantidade(qtdAtual + qtdRepor);
                final Estoque fLote = lote;
                botaoConfirmarReposicao.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        estoqueController.atualizar(fLote);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Reposição de estoque registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoConfirmarReposicao.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao repor estoque: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoConfirmarReposicao.setEnabled(true);
                    }
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Identificador de lote inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoConfirmarReposicao);
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

    private JPanel criarCardRetirarEstoque() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Retirar do estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelLoteProdutoRetirar = new JLabel("Lote L-198 · Pão");
        labelLoteProdutoRetirar.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelLoteProdutoRetirar.setForeground(UiStyle.TEXT);
        fixarLargura(labelLoteProdutoRetirar, larguraInterna, 20);
        card.add(labelLoteProdutoRetirar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQuantidades = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuantidades.setOpaque(false);

        caixaQuantidadeActualRetirar = new CaixaValorArredondada("40");
        campoQuantidadeARetirar = new CampoFormularioArredondado("");
        campoQuantidadeARetirar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRetirar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRetirar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcularNovaQuantidadeRetirar();
            }
        });

        JPanel blocoARetirar = new JPanel();
        blocoARetirar.setOpaque(false);
        blocoARetirar.setLayout(new BoxLayout(blocoARetirar, BoxLayout.Y_AXIS));
        labelARetirarMax = new JLabel("A retirar (máx. 40)");
        labelARetirarMax.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelARetirarMax.setForeground(UiStyle.MUTED);
        labelARetirarMax.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoQuantidadeARetirar.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoQuantidadeARetirar.setPreferredSize(new Dimension(60, ALTURA_INPUT));
        campoQuantidadeARetirar.setMinimumSize(new Dimension(40, ALTURA_INPUT));
        campoQuantidadeARetirar.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));
        blocoARetirar.add(labelARetirarMax);
        blocoARetirar.add(Box.createRigidArea(new Dimension(0, 6)));
        blocoARetirar.add(campoQuantidadeARetirar);

        linhaQuantidades.add(criarCampoComLabel("Quantidade actual", caixaQuantidadeActualRetirar));
        linhaQuantidades.add(blocoARetirar);
        fixarLargura(linhaQuantidades, larguraInterna, 60);
        card.add(linhaQuantidades);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        caixaNovaQuantidadeRetirar = new CaixaValorArredondada(
                "35",
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_BG,
                UiStyle.CHIP_YELLOW_FG,
                UiStyle.fonte(13, Font.BOLD)
        );
        JPanel blocoNovaQuantidade = criarCampoComLabel("Nova quantidade", caixaNovaQuantidadeRetirar);
        fixarLargura(blocoNovaQuantidade, larguraInterna, 60);
        card.add(blocoNovaQuantidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorMotivoRetirada = new BotaoSeletorFormulario(
                "Perda / Quebra",
                new String[] {"Perda / Quebra", "Dano / Avaria", "Produto expirado", "Ajuste de inventário"}
        );
        JPanel blocoMotivoRetirada = criarCampoComLabel("Motivo da retirada *", seletorMotivoRetirada);
        fixarLargura(blocoMotivoRetirada, larguraInterna, 60);
        card.add(blocoMotivoRetirada);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmarRetirada = new BotaoToolbarArredondado(
                "Confirmar retirada", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoConfirmarRetirada.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoConfirmarRetirada.setIconTextGap(6);
        botaoConfirmarRetirada.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um lote para retirada.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String qtdStr = (campoQuantidadeARetirar != null) ? campoQuantidadeARetirar.getText().trim() : "0";
            int qtdRetirar;
            try {
                qtdRetirar = Integer.parseInt(qtdStr);
                if (qtdRetirar <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe uma quantidade válida para retirar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String loteStr = String.valueOf(tabela.getValueAt(row, 0)).replace("L-", "").trim();
            try {
                Long idEstoque = Long.parseLong(loteStr);
                Estoque lote = (listaEstoqueEntidades != null) ? listaEstoqueEntidades.stream()
                    .filter(item -> item.getIdEstoque() != null && item.getIdEstoque().equals(idEstoque))
                    .findFirst().orElse(null) : null;
                if (lote == null) {
                    lote = estoqueController.buscarPorId(idEstoque);
                }
                if (lote.getFuncionario() != null && lote.getFuncionario().getEstado() == null) {
                    lote.getFuncionario().setEstado(model.vo.EstadoFuncionario.ATIVO);
                }
                int qtdAtual = lote.getQuantidade() != null ? lote.getQuantidade() : 0;
                if (qtdRetirar > qtdAtual) {
                    JOptionPane.showMessageDialog(this, "Quantidade a retirar excede o estoque disponível no lote.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                lote.setQuantidade(qtdAtual - qtdRetirar);
                final Estoque fLote = lote;
                final String motivoEscolhido = seletorMotivoRetirada != null ? seletorMotivoRetirada.getText() : "Ajuste";
                botaoConfirmarRetirada.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        estoqueController.atualizar(fLote);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this,
                                "Retirada de estoque registrada com sucesso! (Motivo: " + motivoEscolhido + ")",
                                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoConfirmarRetirada.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao retirar estoque: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoConfirmarRetirada.setEnabled(true);
                    }
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de lote inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoConfirmarRetirada);
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

    private JPanel criarCardEditarLote() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar lote");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelCodigoLoteEditar = new JLabel("Lote L-201");
        labelCodigoLoteEditar.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelCodigoLoteEditar.setForeground(UiStyle.TEXT);
        fixarLargura(labelCodigoLoteEditar, larguraInterna, 20);
        card.add(labelCodigoLoteEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorProdutoEditarLote = new BotaoSeletorFormulario(
                "Selecione",
                new String[0]
        );
        JPanel blocoProduto = criarCampoComLabel("Produto", seletorProdutoEditarLote);
        fixarLargura(blocoProduto, larguraInterna, 60);
        card.add(blocoProduto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);

        campoQuantidadeEditarLote = new CampoFormularioArredondado("");
        campoValidadeEditarLote = new CampoFormularioArredondado("");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade", campoQuantidadeEditarLote));
        linhaQtdValidade.add(criarCampoComLabel("Validade", campoValidadeEditarLote));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarAlteracoes = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoes.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoes.setIconTextGap(5);
        botaoGuardarAlteracoes.setBorder(new EmptyBorder(9, 9, 9, 9));
        botaoGuardarAlteracoes.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um lote na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int novaQtd;
            try {
                novaQtd = Integer.parseInt(campoQuantidadeEditarLote.getText().trim());
                if (novaQtd < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String valStr = campoValidadeEditarLote.getText().trim();
            LocalDate novaValidade = null;
            if (!valStr.isEmpty() && !"-".equals(valStr)) {
                try {
                    novaValidade = LocalDate.parse(valStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Data de validade inválida (use dd/mm/aaaa).", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String loteStr = String.valueOf(tabela.getValueAt(row, 0)).replace("L-", "").trim();
            try {
                Long idEstoque = Long.parseLong(loteStr);
                Estoque lote = (listaEstoqueEntidades != null) ? listaEstoqueEntidades.stream()
                        .filter(item -> item.getIdEstoque() != null && item.getIdEstoque().equals(idEstoque))
                        .findFirst().orElse(null) : null;
                if (lote == null) {
                    lote = estoqueController.buscarPorId(idEstoque);
                }
                if (lote.getFuncionario() != null && lote.getFuncionario().getEstado() == null) {
                    lote.getFuncionario().setEstado(model.vo.EstadoFuncionario.ATIVO);
                }
                String nomeProdSel = seletorProdutoEditarLote != null
                        ? seletorProdutoEditarLote.getText().replace(" ▾", "").replace("▾", "").trim()
                        : "";
                if (listaProdutosReais != null) {
                    for (Produto p : listaProdutosReais) {
                        if (p.getNome() != null && p.getNome().equalsIgnoreCase(nomeProdSel)) {
                            lote.setProduto(p);
                            break;
                        }
                    }
                }
                lote.setQuantidade(novaQtd);
                lote.setDataValidade(novaValidade);
                final Estoque fLote = lote;
                botaoGuardarAlteracoes.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        estoqueController.atualizar(fLote);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Alterações do lote guardadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoGuardarAlteracoes.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao guardar alterações: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoGuardarAlteracoes.setEnabled(true);
                    }
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Identificador de lote inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(5);
        botaoCancelar.setBorder(new EmptyBorder(9, 9, 9, 9));
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado botaoEliminarCard = new BotaoToolbarArredondado(
                "Eliminar lote", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarCard.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        botaoEliminarCard.setIconTextGap(5);
        botaoEliminarCard.setBorder(new EmptyBorder(9, 9, 9, 9));
        botaoEliminarCard.addActionListener(e -> eliminarLoteSelecionado());

        barraBotoes.add(botaoGuardarAlteracoes);
        barraBotoes.add(Box.createRigidArea(new Dimension(6, 0)));
        barraBotoes.add(botaoCancelar);
        barraBotoes.add(Box.createRigidArea(new Dimension(6, 0)));
        barraBotoes.add(botaoEliminarCard);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void sincronizarDadosLoteParaReposicao() {
        if (labelLoteProdutoRepor == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String lote = String.valueOf(tabela.getValueAt(row, 0));
        String produto = String.valueOf(tabela.getValueAt(row, 1));
        String qtdActual = String.valueOf(tabela.getValueAt(row, 2));

        labelLoteProdutoRepor.setText("Lote " + lote + " · " + produto);
        caixaQuantidadeActualRepor.setTexto(qtdActual);
        recalcularNovaQuantidadeRepor();
    }

    private void recalcularNovaQuantidadeRepor() {
        if (caixaQuantidadeActualRepor == null || campoQuantidadeARepor == null || caixaNovaQuantidadeRepor == null) {
            return;
        }
        int actual = parseInteiroSeguro(caixaQuantidadeActualRepor.getTexto(), 0);
        int aRepor = Math.max(0, parseInteiroSeguro(campoQuantidadeARepor.getText(), 0));
        caixaNovaQuantidadeRepor.setTexto(String.valueOf(actual + aRepor));
    }

    private void sincronizarDadosLoteParaRetirada() {
        if (labelLoteProdutoRetirar == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = Math.min(1, tabela.getRowCount() - 1);
        }
        String lote = String.valueOf(tabela.getValueAt(row, 0));
        String produto = String.valueOf(tabela.getValueAt(row, 1));
        String qtdActual = String.valueOf(tabela.getValueAt(row, 2));

        labelLoteProdutoRetirar.setText("Lote " + lote + " · " + produto);
        caixaQuantidadeActualRetirar.setTexto(qtdActual);
        labelARetirarMax.setText("A retirar (máx. " + qtdActual + ")");
        recalcularNovaQuantidadeRetirar();
    }

    private void recalcularNovaQuantidadeRetirar() {
        if (caixaQuantidadeActualRetirar == null || campoQuantidadeARetirar == null || caixaNovaQuantidadeRetirar == null) {
            return;
        }
        int actual = parseInteiroSeguro(caixaQuantidadeActualRetirar.getTexto(), 0);
        int aRetirar = Math.max(0, parseInteiroSeguro(campoQuantidadeARetirar.getText(), 0));
        caixaNovaQuantidadeRetirar.setTexto(String.valueOf(Math.max(0, actual - aRetirar)));
    }

    private void eliminarLoteSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um lote na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String loteStr = String.valueOf(tabela.getValueAt(row, 0)).replace("L-", "").trim();
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o lote L-" + loteStr + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idEstoque = Long.parseLong(loteStr);
            AsyncTask.executar(
                () -> {
                    estoqueController.eliminar(idEstoque);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Lote L-" + loteStr + " eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao eliminar lote: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Identificador de lote inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sincronizarDadosLoteParaEdicao() {
        if (labelCodigoLoteEditar == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String lote = String.valueOf(tabela.getValueAt(row, 0));
        String produto = String.valueOf(tabela.getValueAt(row, 1));
        String qtd = String.valueOf(tabela.getValueAt(row, 2));
        String validade = String.valueOf(tabela.getValueAt(row, 4));

        labelCodigoLoteEditar.setText("Lote " + lote);
        seletorProdutoEditarLote.setText(produto);
        campoQuantidadeEditarLote.setText(qtd);
        campoValidadeEditarLote.setText(validade);
    }

    private int parseInteiroSeguro(String valor, int padrao) {
        if (valor == null) {
            return padrao;
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
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
        String termo = campoBuscarLote.getTextoReal().trim().toLowerCase();
        List<Object[]> fonteDados = (dadosEstoqueCarregados != null) ? dadosEstoqueCarregados : new ArrayList<>();
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] linha : fonteDados) {
            String lote = String.valueOf(linha[0]).toLowerCase();
            String produto = String.valueOf(linha[1]).toLowerCase();
            if (termo.isEmpty() || lote.contains(termo) || produto.contains(termo)) {
                filtrados.add(linha);
            }
        }

        modeloTabela.setRowCount(0);
        for (Object[] linha : filtrados) {
            modeloTabela.addRow(linha);
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

    private static final class RenderizadorCelulaSituacao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";

            Color bgChip;
            Color fgChip;
            if ("Válido".equalsIgnoreCase(texto) || "Valido".equalsIgnoreCase(texto)) {
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
            container.add(new ChipSituacao(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class ChipSituacao extends JLabel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;

        ChipSituacao(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonteSemiBold(12));
            setForeground(corTexto);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            String txt = getText() != null ? getText().trim() : "";
            boolean valido = "Válido".equalsIgnoreCase(txt) || "Valido".equalsIgnoreCase(txt);
            Color bgDinamico = valido ? UiStyle.CHIP_GREEN_BG : UiStyle.ERROR_BG;
            Color fgDinamico = valido ? UiStyle.CHIP_GREEN_FG : UiStyle.ERROR_TEXT;
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

    private static final class CaixaValorArredondada extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final boolean destaque;
        private final JLabel labelValor;

        CaixaValorArredondada(String texto) {
            this(texto, UiStyle.INPUT_BG, UiStyle.INPUT_BORDER, UiStyle.MUTED, UiStyle.fonte(13, Font.PLAIN), false);
        }

        CaixaValorArredondada(String texto, Color corFundo, Color corBorda, Color corTexto, Font fonte) {
            this(texto, corFundo, corBorda, corTexto, fonte, true);
        }

        CaixaValorArredondada(String texto, Color corFundo, Color corBorda, Color corTexto, Font fonte, boolean destaque) {
            super(new BorderLayout());
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.destaque = destaque;
            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            setPreferredSize(new Dimension(60, ALTURA_INPUT));

            this.labelValor = new JLabel(texto);
            this.labelValor.setFont(fonte);
            this.labelValor.setForeground(corTexto);
            add(this.labelValor, BorderLayout.CENTER);
        }

        String getTexto() {
            return labelValor.getText();
        }

        void setTexto(String novoTexto) {
            labelValor.setText(novoTexto);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(destaque ? UiStyle.CHIP_YELLOW_BG : UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                g2.setColor(destaque ? UiStyle.CHIP_YELLOW_BG : UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                labelValor.setForeground(destaque ? UiStyle.CHIP_YELLOW_FG : UiStyle.MUTED);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
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

        void setOpcoes(String[] novasOpcoes) {
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

        CampoBuscaArredondado(String placeholder) {
            super("");
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.TEXT);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        }

        String getTextoReal() {
            return getText();
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
        AsyncTask.executar(
            () -> estoqueController.listarTodos(),
            estoques -> {
                this.listaEstoqueEntidades = estoques != null ? estoques : new ArrayList<>();
                List<Object[]> linhas = new ArrayList<>();
                if (estoques != null) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate hoje = LocalDate.now();
                    for (Estoque est : estoques) {
                        String loteStr = "L-" + est.getIdEstoque();
                        String prodStr = (est.getProduto() != null && est.getProduto().getNome() != null)
                                ? est.getProduto().getNome() : "";
                        String qtdStr = est.getQuantidade() != null ? String.valueOf(est.getQuantidade()) : "0";
                        String entStr = est.getDataEntrada() != null ? est.getDataEntrada().format(dtf) : "-";
                        String valStr = est.getDataValidade() != null ? est.getDataValidade().format(dtf) : "-";
                        String diasStr = "-";
                        if (est.getDataValidade() != null) {
                            int dias = est.calcularDiasParaExpirar();
                            diasStr = dias < 0 ? ("Exp. há " + Math.abs(dias) + "d") : (dias + " d");
                        }
                        String funcStr = (est.getFuncionario() != null && est.getFuncionario().getNome() != null)
                                ? est.getFuncionario().getNome() : "-";
                        String sitStr = (est.getDataValidade() != null && est.getDataValidade().isBefore(hoje))
                                ? "Expirado" : "Válido";
                        linhas.add(new Object[] {loteStr, prodStr, qtdStr, entStr, valStr, diasStr, funcStr, sitStr});
                    }
                }
                this.dadosEstoqueCarregados = linhas;
                atualizarTabelaFiltrada();
            },
            erro -> {
                this.listaEstoqueEntidades = new ArrayList<>();
                this.dadosEstoqueCarregados = new ArrayList<>();
                atualizarTabelaFiltrada();
            }
        );
        AsyncTask.executar(
            () -> estoqueController.listarProdutos(),
            produtos -> {
                this.listaProdutosReais = produtos != null ? produtos : new ArrayList<>();
                String[] nomes = this.listaProdutosReais.stream()
                        .map(Produto::getNome)
                        .toArray(String[]::new);
                if (seletorProdutoEntrada != null) {
                    seletorProdutoEntrada.setOpcoes(nomes);
                    if (nomes.length > 0) {
                        seletorProdutoEntrada.setText(nomes[0]);
                    }
                }
                if (seletorProdutoEditarLote != null) {
                    seletorProdutoEditarLote.setOpcoes(nomes);
                }
            },
            erro -> {}
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            if (botaoDarEntrada != null) botaoDarEntrada.setVisible(false);
            if (botaoRepor != null) botaoRepor.setVisible(false);
            if (botaoRetirar != null) botaoRetirar.setVisible(false);
            if (botaoEditar != null) botaoEditar.setVisible(false);
            if (botaoEliminarLote != null) botaoEliminarLote.setVisible(false);
            if (modoLateral != ModoPainelLateral.NENHUM) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else {
            if (botaoDarEntrada != null) botaoDarEntrada.setVisible(true);
            if (botaoRepor != null) botaoRepor.setVisible(true);
            if (botaoRetirar != null) botaoRetirar.setVisible(true);
            if (botaoEditar != null) botaoEditar.setVisible(true);
            if (botaoEliminarLote != null) botaoEliminarLote.setVisible(true);
        }
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardDarEntrada);
        UiStyle.atualizarComponentesTema(cardReporEstoque);
        UiStyle.atualizarComponentesTema(cardRetirarEstoque);
        UiStyle.atualizarComponentesTema(cardEditarLote);
    }
}
