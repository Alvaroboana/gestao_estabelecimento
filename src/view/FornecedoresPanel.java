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

import model.dao.FornecedorDAO;
import model.vo.Contacto;
import model.vo.Fornecedor;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Fornecedores, Novo Fornecedor e Editar Fornecedor em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Fornecedores" (Fornecedores.dc.html)
 * - "ADMIN · Barra superior · Escuro — Fornecedor — novo" (FornecedorForm.dc.html)
 * - "ADMIN · Barra superior · Escuro — Fornecedor — editar" (FornecedorEditar.dc.html)
 */
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
    private static final int LARGURA_CARD_FORMULARIO = 360;

    private static final String[] COLUNAS_COMPLETAS = {
        "ID", "Nome", "Contactos", "Endereço", "Nº produtos"
    };

    private static final String[] COLUNAS_RESUMIDAS = {
        "ID", "Nome", "Contactos", "Nº produtos"
    };

    private static final Object[][] DADOS_INICIAIS = {
        {"1", "Distribuidora Sol", "84 000 1111", "Matola, Bairro Fomento, Rua 5", "14"},
        {"2", "Lacticínios Norte", "82 222 3333", "Maputo, Bairro Central, Rua 12", "6"},
        {"3", "Mercearia Atlas", "86 444 5555", "Matola, Bairro Liberdade, Rua 2", "9"}
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

    // Controles do formulário "Novo fornecedor"
    private JTextField campoNomeNovo;
    private JTextField campoContactoNovo;
    private JLabel linkAdicionarContactoNovo;
    private BotaoSeletorFormulario seletorCidadeNovo;
    private BotaoSeletorFormulario seletorBairroNovo;
    private JTextField campoQuarteiraoNovo;
    private JTextField campoRuaNovo;
    private JTextField campoNumeroCasaNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    // Controles do formulário "Editar fornecedor"
    private JTextField campoNomeEditar;
    private JTextField campoContactoEditar;
    private JLabel linkAdicionarContactoEditar;
    private BotaoSeletorFormulario seletorCidadeEditar;
    private BotaoSeletorFormulario seletorBairroEditar;
    private JTextField campoQuarteiraoEditar;
    private JTextField campoRuaEditar;
    private JTextField campoNumeroCasaEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

    private final FornecedorDAO fornecedorDAO;
    private List<Object[]> dadosFornecedoresCarregados = null;
    private List<Fornecedor> listaFornecedoresEntidades = null;

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
        this.fornecedorDAO = new FornecedorDAO();

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarFornecedor = criarCampoBuscarFornecedor();
        this.botaoNovoFornecedor = criarBotaoNovoFornecedor();
        this.botaoEditar = criarBotaoEditarFornecedor();
        this.botaoProdutos = criarBotaoToolbar(
                "Produtos", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        this.botaoEliminar = criarBotaoToolbar(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );
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
            wrapperDireita.add(cardNovoFornecedor, BorderLayout.NORTH);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_FORNECEDOR) {
            wrapperDireita.add(cardEditarFornecedor, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFornecedorParaEdicao();
        } else {
            tabela.clearSelection();
        }

        revalidate();
        repaint();
    }

    private void atualizarBotoesToolbar() {
        containerBotoesToolbar.removeAll();
        containerBotoesToolbar.add(botaoNovoFornecedor);
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        containerBotoesToolbar.add(botaoEditar);
        if (modoLateral == ModoPainelLateral.NENHUM) {
            // Em Fornecedores.dc.html: Novo fornecedor | Editar | Produtos | Eliminar
            containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesToolbar.add(botaoProdutos);
        }
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        containerBotoesToolbar.add(botaoEliminar);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        // <h1>Fornecedores</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Fornecedores");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        // .tb { display:flex; gap:10px; align-items:center }
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(campoBuscarFornecedor);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(containerBotoesToolbar);

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

    /**
     * <span class="in">Buscar fornecedor</span>
     */
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
     * Card lateral "Novo fornecedor" fiel a FornecedorForm.dc.html:
     * <div class="card" style="width:360px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
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

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32; // 328px

        // <b>Novo fornecedor</b>
        JLabel titulo = new JLabel("Novo fornecedor");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0"></div></div>
        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 54);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto *</span><div class="in" style="min-width:0"></div></div>
        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 54);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <span class="k" style="color:#F5B638">+ Adicionar contacto</span>
        linkAdicionarContactoNovo = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoNovo.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoNovo.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoNovo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        FornecedoresPanel.this,
                        "A ação \"+ Adicionar contacto\" está reservada para a próxima etapa.",
                        "Módulo reservado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        card.add(linkAdicionarContactoNovo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Cidade *</span><div class="in" style="min-width:0">Matola ▾</div></div>
        seletorCidadeNovo = new BotaoSeletorFormulario("Matola ▾", new String[] {"Matola", "Maputo", "Boane"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", seletorCidadeNovo);
        fixarLargura(blocoCidade, larguraInterna, 54);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Bairro * (filtrado pela cidade)</span><div class="in" style="min-width:0">Fomento ▾</div></div>
        seletorBairroNovo = new BotaoSeletorFormulario("Fomento ▾", new String[] {"Fomento", "Liberdade", "Machava", "Central"});
        JPanel blocoBairro = criarCampoComLabel("Bairro * (filtrado pela cidade)", seletorBairroNovo);
        fixarLargura(blocoBairro, larguraInterna, 54);
        card.add(blocoBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Quarteirão (12) | Rua (Rua 5)</div>
        campoQuarteiraoNovo = new CampoFormularioArredondado("12");
        campoRuaNovo = new CampoFormularioArredondado("Rua 5");
        JPanel linhaQuarteiraoRua = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuarteiraoRua.setOpaque(false);
        linhaQuarteiraoRua.add(criarCampoComLabel("Quarteirão", campoQuarteiraoNovo));
        linhaQuarteiraoRua.add(criarCampoComLabel("Rua", campoRuaNovo));
        fixarLargura(linhaQuarteiraoRua, larguraInterna, 54);
        card.add(linhaQuarteiraoRua);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nº da casa</span><div class="in" style="min-width:0">148</div></div>
        campoNumeroCasaNovo = new CampoFormularioArredondado("148");
        JPanel blocoNumeroCasa = criarCampoComLabel("Nº da casa", campoNumeroCasaNovo);
        fixarLargura(blocoNumeroCasa, larguraInterna, 54);
        card.add(blocoNumeroCasa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb" style="margin-top:6px"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.addActionListener(e -> {
            String nome = (campoNomeNovo != null) ? campoNomeNovo.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do fornecedor.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contacto = (campoContactoNovo != null) ? campoContactoNovo.getText().trim() : "";
            Fornecedor f = new Fornecedor();
            f.setNome(nome);
            if (!contacto.isEmpty()) {
                Contacto c = new Contacto();
                c.setContacto(contacto);
                f.setContacto(Arrays.asList(c));
            }

            AsyncTask.executar(
                () -> {
                    fornecedorDAO.salvar(f);
                    return null;
                },
                sucesso -> {
                    JOptionPane.showMessageDialog(this, "Fornecedor cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    if (campoNomeNovo != null) campoNomeNovo.setText("");
                    if (campoContactoNovo != null) campoContactoNovo.setText("");
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
        botaoCancelarNovo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovo);
        card.add(acoes);

        return card;
    }

    /**
     * Card lateral "Editar fornecedor" fiel a FornecedorEditar.dc.html:
     * <div class="card" style="width:360px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
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

        int larguraInterna = LARGURA_CARD_FORMULARIO - 32; // 328px

        // <b>Editar fornecedor</b>
        JLabel titulo = new JLabel("Editar fornecedor");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0">Distribuidora Sol</div></div>
        campoNomeEditar = new CampoFormularioArredondado("Distribuidora Sol");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 54);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto *</span><div class="in" style="min-width:0">84 000 1111</div></div>
        campoContactoEditar = new CampoFormularioArredondado("84 000 1111");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoEditar);
        fixarLargura(blocoContacto, larguraInterna, 54);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <span class="k" style="color:#F5B638">+ Adicionar contacto</span>
        linkAdicionarContactoEditar = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoEditar.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoEditar.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoEditar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoEditar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        FornecedoresPanel.this,
                        "A ação \"+ Adicionar contacto\" está reservada para a próxima etapa.",
                        "Módulo reservado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        card.add(linkAdicionarContactoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Cidade *</span><div class="in" style="min-width:0">Matola ▾</div></div>
        seletorCidadeEditar = new BotaoSeletorFormulario("Matola ▾", new String[] {"Matola", "Maputo", "Boane"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", seletorCidadeEditar);
        fixarLargura(blocoCidade, larguraInterna, 54);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Bairro * (filtrado pela cidade)</span><div class="in" style="min-width:0">Fomento ▾</div></div>
        seletorBairroEditar = new BotaoSeletorFormulario("Fomento ▾", new String[] {"Fomento", "Liberdade", "Machava", "Central"});
        JPanel blocoBairro = criarCampoComLabel("Bairro * (filtrado pela cidade)", seletorBairroEditar);
        fixarLargura(blocoBairro, larguraInterna, 54);
        card.add(blocoBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Quarteirão (12) | Rua (Rua 5)</div>
        campoQuarteiraoEditar = new CampoFormularioArredondado("12");
        campoRuaEditar = new CampoFormularioArredondado("Rua 5");
        JPanel linhaQuarteiraoRua = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQuarteiraoRua.setOpaque(false);
        linhaQuarteiraoRua.add(criarCampoComLabel("Quarteirão", campoQuarteiraoEditar));
        linhaQuarteiraoRua.add(criarCampoComLabel("Rua", campoRuaEditar));
        fixarLargura(linhaQuarteiraoRua, larguraInterna, 54);
        card.add(linhaQuarteiraoRua);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nº da casa</span><div class="in" style="min-width:0">148</div></div>
        campoNumeroCasaEditar = new CampoFormularioArredondado("148");
        JPanel blocoNumeroCasa = criarCampoComLabel("Nº da casa", campoNumeroCasaEditar);
        fixarLargura(blocoNumeroCasa, larguraInterna, 54);
        card.add(blocoNumeroCasa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb" style="margin-top:6px"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true, 12
        );
        botaoGuardarAlteracoesEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Alterações do fornecedor guardadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });
        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true, 12
        );
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false, 12
        );
        botaoEliminarEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Fornecedor eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });

        acoes.add(botaoGuardarAlteracoesEditar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarEditar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoEliminarEditar);
        card.add(acoes);

        return card;
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
        List<Object[]> fonte = (dadosFornecedoresCarregados != null) ? dadosFornecedoresCarregados : Arrays.asList(DADOS_INICIAIS);
        for (Object[] f : fonte) {
            if (String.valueOf(f[0]).equals(id)) {
                campoNomeEditar.setText(String.valueOf(f[1]));
                campoContactoEditar.setText(String.valueOf(f[2]));
                if ("1".equals(id)) {
                    seletorCidadeEditar.setText("Matola ▾");
                    seletorBairroEditar.setText("Fomento ▾");
                    campoQuarteiraoEditar.setText("12");
                    campoRuaEditar.setText("Rua 5");
                    campoNumeroCasaEditar.setText("148");
                } else if ("2".equals(id)) {
                    seletorCidadeEditar.setText("Maputo ▾");
                    seletorBairroEditar.setText("Central ▾");
                    campoQuarteiraoEditar.setText("4");
                    campoRuaEditar.setText("Rua 12");
                    campoNumeroCasaEditar.setText("82");
                } else {
                    seletorCidadeEditar.setText("Matola ▾");
                    seletorBairroEditar.setText("Liberdade ▾");
                    campoQuarteiraoEditar.setText("9");
                    campoRuaEditar.setText("Rua 2");
                    campoNumeroCasaEditar.setText("35");
                }
                break;
            }
        }
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
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

        List<Object[]> fonteDados = (dadosFornecedoresCarregados != null) ? dadosFornecedoresCarregados : Arrays.asList(DADOS_INICIAIS);

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            String endereco = String.valueOf(linha[3]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo) || endereco.contains(termo)) {
                if (resumida) {
                    // Colunas em FornecedorForm.dc.html: ID, Nome, Contactos, Nº produtos
                    modeloTabela.addRow(new Object[] {
                        linha[0], linha[1], linha[2], linha[4]
                    });
                } else {
                    modeloTabela.addRow(linha);
                }
            }
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
     * Seletor de formulário estilo .in com min-width:0 (ex: "Matola ▾", "Fomento ▾").
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
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Nome *, Contacto *, Quarteirão, Rua, Nº da casa).
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
     * Campo de texto estilo .in ("Buscar fornecedor").
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
            () -> fornecedorDAO.listarTodos(),
            forns -> {
                this.listaFornecedoresEntidades = forns;
                if (forns != null && !forns.isEmpty()) {
                    List<Object[]> linhas = new ArrayList<>();
                    for (Fornecedor f : forns) {
                        String idStr = String.valueOf(f.getIdFornecedor());
                        String nomeStr = f.getNome() != null ? f.getNome() : "";
                        String contStr = (f.getContacto() != null && !f.getContacto().isEmpty() && f.getContacto().get(0) != null)
                                ? f.getContacto().get(0).getContacto() : "-";
                        String endStr = "Endereço cadastrado";
                        String numProdStr = "5";
                        linhas.add(new Object[] {idStr, nomeStr, contStr, endStr, numProdStr});
                    }
                    this.dadosFornecedoresCarregados = linhas;
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
        // Acesso restrito na barra de navegação para ADMIN e GERENTE
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
