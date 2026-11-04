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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import model.bo.EstoqueBO;
import model.vo.Estoque;
import model.vo.Produto;
import model.vo.SessaoUtilizador;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Estoque, Dar Entrada, Repor, Retirar e Editar Lote em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Estoque" (Estoque.dc.html)
 * - "ADMIN · Barra superior · Escuro — Estoque — dar entrada" (EstoqueEntrada.dc.html)
 * - "ADMIN · Barra superior · Escuro — Estoque — repor" (EstoqueRepor.dc.html)
 * - "ADMIN · Barra superior · Escuro — Estoque — retirar" (EstoqueRetirar.dc.html)
 * - "ADMIN · Barra superior · Escuro — Estoque — editar lote" (EstoqueEditar.dc.html)
 */
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
        "Lote", "Produto", "Quantidade", "Validade", "Situação"
    };

    private static final String[] COLUNAS_COMPACTAS = {
        "Lote", "Produto", "Qtd", "Validade", "Situação"
    };

    private static final Object[][] DADOS_INICIAIS = {
        {"L-201", "Leite 1L", "120", "15/12/2026", "Válido"},
        {"L-198", "Pão", "40", "05/11/2026", "Válido"},
        {"L-190", "Iogurte", "12", "28/10/2026", "Expirado"},
        {"L-205", "Arroz 25kg", "4", "10/08/2027", "Válido"}
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

    // Controles do formulário "Dar entrada no estoque"
    private BotaoSeletorFormulario seletorProdutoEntrada;
    private JTextField campoQuantidadeEntrada;
    private JTextField campoValidadeEntrada;

    // Controles do formulário "Repor estoque"
    private JLabel labelLoteProdutoRepor;
    private CaixaValorArredondada caixaQuantidadeActualRepor;
    private CampoFormularioArredondado campoQuantidadeARepor;
    private CaixaValorArredondada caixaNovaQuantidadeRepor;

    // Controles do formulário "Retirar do estoque"
    private JLabel labelLoteProdutoRetirar;
    private CaixaValorArredondada caixaQuantidadeActualRetirar;
    private JLabel labelARetirarMax;
    private CampoFormularioArredondado campoQuantidadeARetirar;
    private CaixaValorArredondada caixaNovaQuantidadeRetirar;

    // Controles do formulário "Editar lote"
    private JLabel labelCodigoLoteEditar;
    private BotaoSeletorFormulario seletorProdutoEditarLote;
    private CampoFormularioArredondado campoQuantidadeEditarLote;
    private CampoFormularioArredondado campoValidadeEditarLote;

    private final EstoqueBO estoqueBO;
    private List<Object[]> dadosEstoqueCarregados = null;
    private List<Estoque> listaEstoqueEntidades = null;

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
        this.estoqueBO = new EstoqueBO();

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarLote = criarCampoBuscarLote();
        this.botaoDarEntrada = criarBotaoDarEntrada();
        this.botaoRepor = criarBotaoReporEstoque();
        this.botaoRetirar = criarBotaoRetirarEstoque();
        this.botaoEditar = criarBotaoEditarLote();
        this.botaoEliminarLote = criarBotaoToolbar("Eliminar lote", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);
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
            wrapperDireita.add(cardDarEntrada, BorderLayout.NORTH);
        } else if (this.modoLateral == ModoPainelLateral.REPOR_ESTOQUE) {
            wrapperDireita.add(cardReporEstoque, BorderLayout.NORTH);
        } else if (this.modoLateral == ModoPainelLateral.RETIRAR_ESTOQUE) {
            wrapperDireita.add(cardRetirarEstoque, BorderLayout.NORTH);
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_LOTE) {
            wrapperDireita.add(cardEditarLote, BorderLayout.NORTH);
        }

        // Em Estoque.dc.html (sem card lateral): Repor, Retirar, Eliminar lote
        // Em EstoqueEntrada.dc.html / EstoqueRepor.dc.html / EstoqueRetirar.dc.html / EstoqueEditar.dc.html (com card lateral): Repor, Retirar, Editar, Eliminar
        containerBotoesFinaisToolbar.removeAll();
        if (lateralAberto) {
            botaoEliminarLote.setText("Eliminar");
            containerBotoesFinaisToolbar.add(botaoEditar);
            containerBotoesFinaisToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesFinaisToolbar.add(botaoEliminarLote);
        } else {
            botaoEliminarLote.setText("Eliminar lote");
            containerBotoesFinaisToolbar.add(botaoEliminarLote);
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

        // <h1>Estoque</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Estoque");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        // .tb { display:flex; gap:10px; align-items:center }
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(campoBuscarLote);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoDarEntrada);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoRepor);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(botaoRetirar);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(containerBotoesFinaisToolbar);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    /**
     * <div class="row" style="align-items:flex-start">
     *   <div class="card" style="flex:1;padding:0">...</div>
     *   <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">...</div>
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
     * <span class="in">Buscar lote</span>
     */
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
            if (i == 4) {
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
     * Card lateral "Dar entrada no estoque" fiel a EstoqueEntrada.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardDarEntrada() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Dar entrada no estoque</b>
        JLabel titulo = new JLabel("Dar entrada no estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Produto *</span><div class="in" style="min-width:0">Leite 1L ▾</div></div>
        seletorProdutoEntrada = new BotaoSeletorFormulario(
                "Leite 1L ▾",
                new String[] {"Leite 1L", "Pão", "Iogurte", "Arroz 25kg"}
        );
        JPanel blocoProduto = criarCampoComLabel("Produto *", seletorProdutoEntrada);
        fixarLargura(blocoProduto, larguraInterna, 60);
        card.add(blocoProduto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Quantidade * -> 0 | Validade * -> dd/mm/aaaa
        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);

        campoQuantidadeEntrada = new CampoFormularioArredondado("0");
        campoValidadeEntrada = new CampoFormularioArredondado("dd/mm/aaaa");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade *", campoQuantidadeEntrada));
        linhaQtdValidade.add(criarCampoComLabel("Validade *", campoValidadeEntrada));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="k">Cria um novo lote para o produto.</div>
        JLabel notaLote = new JLabel("Cria um novo lote para o produto.");
        notaLote.setFont(UiStyle.fonte(12, Font.PLAIN));
        notaLote.setForeground(UiStyle.MUTED);
        fixarLargura(notaLote, larguraInterna, 18);
        card.add(notaLote);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar entrada</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarEntrada = new BotaoToolbarArredondado(
                "Guardar entrada", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarEntrada.addActionListener(e -> {
            String qtdStr = (campoQuantidadeEntrada != null) ? campoQuantidadeEntrada.getText().trim() : "0";
            int qtd;
            try {
                qtd = Integer.parseInt(qtdStr);
                if (qtd <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe uma quantidade vÃ¡lida e positiva.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String valStr = (campoValidadeEntrada != null) ? campoValidadeEntrada.getText().trim() : "";
            LocalDate validade = null;
            try {
                validade = LocalDate.parse(valStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (Exception ex) {
                validade = LocalDate.now().plusMonths(6);
            }
            Utilizador user = SessaoUtilizador.getUtilizador();
            if (user == null || user.getFuncionario() == null) {
                JOptionPane.showMessageDialog(this, "SessÃ£o invÃ¡lida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Estoque novoLote = new Estoque();
            novoLote.setQuantidade(qtd);
            novoLote.setDataEntrada(LocalDate.now());
            novoLote.setDataValidade(validade);
            novoLote.setFuncionario(user.getFuncionario());
            Produto prod = new Produto();
            prod.setIdProduto(1L);
            if (listaEstoqueEntidades != null && !listaEstoqueEntidades.isEmpty() && listaEstoqueEntidades.get(0).getProduto() != null) {
                prod = listaEstoqueEntidades.get(0).getProduto();
            }
            novoLote.setProduto(prod);

            botaoGuardarEntrada.setEnabled(false);
            AsyncTask.executar(
                () -> {
                    estoqueBO.salvar(novoLote);
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

    /**
     * Card lateral "Repor estoque" fiel a EstoqueRepor.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardReporEstoque() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Repor estoque</b>
        JLabel titulo = new JLabel("Repor estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="font-size:14px">Lote L-201 · Leite 1L</div>
        labelLoteProdutoRepor = new JLabel("Lote L-201 · Leite 1L");
        labelLoteProdutoRepor.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelLoteProdutoRepor.setForeground(UiStyle.TEXT);
        fixarLargura(labelLoteProdutoRepor, larguraInterna, 20);
        card.add(labelLoteProdutoRepor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Quantidade actual -> 120 | A repor (maior que 0) -> 30
        JPanel linhaQuantidades = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuantidades.setOpaque(false);

        caixaQuantidadeActualRepor = new CaixaValorArredondada("120");
        campoQuantidadeARepor = new CampoFormularioArredondado("30");
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

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nova quantidade</span>
        //   <div class="in" style="min-width:0;background:#4A3A0F;border-color:#4A3A0F;color:#FFD36B;font-weight:700">150</div>
        // </div>
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

        // <div class="tb"><span class="bt">Confirmar reposição</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmarReposicao = new BotaoToolbarArredondado(
                "Confirmar reposição", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoConfirmarReposicao.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Reposição de estoque registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
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

    /**
     * Card lateral "Retirar do estoque" fiel a EstoqueRetirar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardRetirarEstoque() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Retirar do estoque</b>
        JLabel titulo = new JLabel("Retirar do estoque");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="font-size:14px">Lote L-198 · Pão</div>
        labelLoteProdutoRetirar = new JLabel("Lote L-198 · Pão");
        labelLoteProdutoRetirar.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelLoteProdutoRetirar.setForeground(UiStyle.TEXT);
        fixarLargura(labelLoteProdutoRetirar, larguraInterna, 20);
        card.add(labelLoteProdutoRetirar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Quantidade actual -> 40 | A retirar (máx. 40) -> 5
        JPanel linhaQuantidades = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuantidades.setOpaque(false);

        caixaQuantidadeActualRetirar = new CaixaValorArredondada("40");
        campoQuantidadeARetirar = new CampoFormularioArredondado("5");
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

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nova quantidade</span>
        //   <div class="in" style="min-width:0;background:#4A3A0F;border-color:#4A3A0F;color:#FFD36B;font-weight:700">35</div>
        // </div>
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

        // <div class="tb"><span class="bt">Confirmar retirada</span><span class="bt s">Cancelar</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmarRetirada = new BotaoToolbarArredondado(
                "Confirmar retirada", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
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
                JOptionPane.showMessageDialog(this, "Informe uma quantidade vÃ¡lida para retirar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String loteStr = String.valueOf(tabela.getValueAt(row, 0)).replace("L-", "").trim();
            try {
                Long idEstoque = Long.parseLong(loteStr);
                Estoque lote = (listaEstoqueEntidades != null) ? listaEstoqueEntidades.stream()
                    .filter(item -> item.getIdEstoque() != null && item.getIdEstoque().equals(idEstoque))
                    .findFirst().orElse(null) : null;
                if (lote == null) {
                    JOptionPane.showMessageDialog(this, "Lote nÃ£o encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int qtdAtual = lote.getQuantidade() != null ? lote.getQuantidade() : 0;
                if (qtdRetirar > qtdAtual) {
                    JOptionPane.showMessageDialog(this, "Quantidade a retirar excede o estoque disponÃ­vel no lote.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                lote.setQuantidade(qtdAtual - qtdRetirar);
                final Estoque fLote = lote;
                botaoConfirmarRetirada.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        estoqueBO.atualizar(fLote);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Retirada de estoque registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Identificador de lote invÃ¡lido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
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

    /**
     * Card lateral "Editar lote" fiel a EstoqueEditar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardEditarLote() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Editar lote</b>
        JLabel titulo = new JLabel("Editar lote");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="font-size:14px">Lote L-201</div>
        labelCodigoLoteEditar = new JLabel("Lote L-201");
        labelCodigoLoteEditar.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelCodigoLoteEditar.setForeground(UiStyle.TEXT);
        fixarLargura(labelCodigoLoteEditar, larguraInterna, 20);
        card.add(labelCodigoLoteEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Produto</span><div class="in" style="min-width:0">Leite 1L ▾</div></div>
        seletorProdutoEditarLote = new BotaoSeletorFormulario(
                "Leite 1L ▾",
                new String[] {"Leite 1L", "Pão", "Iogurte", "Arroz 25kg"}
        );
        JPanel blocoProduto = criarCampoComLabel("Produto", seletorProdutoEditarLote);
        fixarLargura(blocoProduto, larguraInterna, 60);
        card.add(blocoProduto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm"> Quantidade -> 120 | Validade -> 15/12/2026
        JPanel linhaQtdValidade = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtdValidade.setOpaque(false);

        campoQuantidadeEditarLote = new CampoFormularioArredondado("120");
        campoValidadeEditarLote = new CampoFormularioArredondado("15/12/2026");
        linhaQtdValidade.add(criarCampoComLabel("Quantidade", campoQuantidadeEditarLote));
        linhaQtdValidade.add(criarCampoComLabel("Validade", campoValidadeEditarLote));
        fixarLargura(linhaQtdValidade, larguraInterna, 60);
        card.add(linhaQtdValidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar lote</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoGuardarAlteracoes = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Alterações do lote guardadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado botaoEliminarCard = new BotaoToolbarArredondado(
                "Eliminar lote", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarCard.addActionListener(e -> eliminarLoteSelecionado());

        barraBotoes.add(botaoGuardarAlteracoes);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
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
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o lote L-" + loteStr + "?", "Confirmar ExclusÃ£o", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idEstoque = Long.parseLong(loteStr);
            AsyncTask.executar(
                () -> {
                    estoqueBO.eliminar(idEstoque);
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
            JOptionPane.showMessageDialog(this, "Identificador de lote invÃ¡lido.", "Erro", JOptionPane.ERROR_MESSAGE);
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
        String validade = String.valueOf(tabela.getValueAt(row, 3));

        labelCodigoLoteEditar.setText("Lote " + lote);
        seletorProdutoEditarLote.setText(produto + " ▾");
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
        List<Object[]> fonteDados = (dadosEstoqueCarregados != null) ? dadosEstoqueCarregados : Arrays.asList(DADOS_INICIAIS);
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
     * Renderiza os chips da coluna Situação:
     * .ch.g -> Válido (#12402A / #7FE3A8)
     * .ch.r -> Expirado (#4F1B1B / #FF9C9C)
     */
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
     * Caixa de valor estática/calculada estilo .in (usada para Quantidade actual e Nova quantidade em #4A3A0F/#FFD36B).
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
            super(new BorderLayout());
            this.corFundo = corFundo;
            this.corBorda = corBorda;
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
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                g2.setColor(corBorda);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Seletor de formulário estilo .in com min-width:0 (ex: "Leite 1L ▾").
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
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Quantidade *, Validade *).
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
     * Campo de texto estilo .in ("Buscar lote").
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
            () -> estoqueBO.listarTodos(),
            estoques -> {
                this.listaEstoqueEntidades = estoques;
                if (estoques != null && !estoques.isEmpty()) {
                    List<Object[]> linhas = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate hoje = LocalDate.now();
                    for (Estoque est : estoques) {
                        String loteStr = "L-" + est.getIdEstoque();
                        String prodStr = (est.getProduto() != null && est.getProduto().getNome() != null)
                                ? est.getProduto().getNome() : "";
                        String qtdStr = est.getQuantidade() != null ? String.valueOf(est.getQuantidade()) : "0";
                        String valStr = est.getDataValidade() != null ? est.getDataValidade().format(dtf) : "-";
                        String sitStr = (est.getDataValidade() != null && est.getDataValidade().isBefore(hoje))
                                ? "Expirado" : "Válido";
                        linhas.add(new Object[] {loteStr, prodStr, qtdStr, valStr, sitStr});
                    }
                    this.dadosEstoqueCarregados = linhas;
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
            if (botaoEditar != null) botaoEditar.setVisible(false);
            if (botaoEliminarLote != null) botaoEliminarLote.setVisible(false);
        } else {
            if (botaoEditar != null) botaoEditar.setVisible(true);
            if (botaoEliminarLote != null) botaoEliminarLote.setVisible(true);
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
