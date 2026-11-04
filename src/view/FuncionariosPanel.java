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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.bo.FuncionarioBO;
import model.vo.Cargo;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Funcionários, Novo Funcionário, Editar Funcionário e Estado/Cargo em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Funcionários" (Funcionarios.dc.html)
 * - "ADMIN · Barra superior · Escuro — Funcionário — novo" (FuncionarioNovo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Funcionário — editar" (FuncionarioEditar.dc.html)
 * - "ADMIN · Barra superior · Escuro — Funcionário — estado e cargo" (FuncionarioEstado.dc.html)
 */
public class FuncionariosPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVO_FUNCIONARIO,
        EDITAR_FUNCIONARIO,
        ESTADO_CARGO_FUNCIONARIO
    }

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int ALTURA_INPUT_FORM = 36;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] COLUNAS_COMPLETAS = {
        "ID", "Nome", "BI", "Início", "Cargo", "Estado", "Antiguidade"
    };

    private static final String[] COLUNAS_RESUMIDAS = {
        "ID", "Nome", "BI", "Cargo", "Estado", "Antiguidade"
    };

    private static final Object[][] DADOS_INICIAIS = {
        {"1", "Carlos Mabjaia", "110100123456A", "01/03/2021", "Vendedor", "ATIVO", "5 anos"},
        {"2", "Sara Cossa", "110100654321B", "10/06/2023", "Gerente", "ATIVO", "3 anos"},
        {"3", "Pedro Tembe", "110100987654C", "15/01/2024", "Vendedor", "SUSPENSO", "2 anos"}
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final BotaoSeletorEstado seletorEstado;
    private final CampoBuscaArredondado campoBuscarFuncionario;
    private final JButton botaoNovoFuncionario;
    private final JButton botaoEditar;
    private final JButton botaoEstadoCargo;
    private final JButton botaoSuspender;
    private final JButton botaoTransferirCargo;
    private final JButton botaoEliminar;
    private final JPanel containerBotoesToolbar;
    private final JPanel cardNovoFuncionario;
    private final JPanel cardEditarFuncionario;
    private final JPanel cardEstadoCargoFuncionario;
    private final JPanel wrapperDireita;

    // Controles do formulário "Novo funcionário"
    private JTextField campoNomeNovo;
    private JTextField campoBiNovo;
    private JTextField campoDataNascimentoNovo;
    private JTextField campoDataInicioNovo;
    private BotaoSeletorFormulario seletorCargoNovo;
    private JTextField campoContactoNovo;
    private BotaoSeletorFormulario seletorCidadeNovo;
    private BotaoSeletorFormulario seletorBairroNovo;
    private JTextField campoUsernameNovo;
    private BotaoSeletorFormulario seletorPerfilNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    // Controles do formulário "Editar funcionário"
    private JTextField campoNomeEditar;
    private JTextField campoBiEditar;
    private JTextField campoDataNascimentoEditar;
    private JTextField campoDataInicioEditar;
    private BotaoSeletorFormulario seletorCargoEditar;
    private JTextField campoContactoEditar;
    private BotaoSeletorFormulario seletorCidadeEditar;
    private BotaoSeletorFormulario seletorBairroEditar;
    private JTextField campoUsernameEditar;
    private BotaoSeletorFormulario seletorPerfilEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

    // Controles do card "Estado e cargo"
    private JLabel labelNomeEstadoCargo;
    private ChipEstado chipEstadoCargo;
    private JButton botaoActivarEstadoCargo;
    private JButton botaoInactivarEstadoCargo;
    private JButton botaoSuspenderEstadoCargo;
    private JTextField campoCargoActualEstadoCargo;
    private BotaoSeletorFormulario seletorNovoCargoEstadoCargo;
    private JTextField campoAntiguidadeEstadoCargo;
    private JTextField campoVendasMesEstadoCargo;
    private JButton botaoConfirmarTransferirCargo;
    private JButton botaoCancelarEstadoCargo;

    private final FuncionarioBO funcionarioBO;
    private List<Object[]> dadosFuncionariosCarregados = null;
    private List<Funcionario> listaFuncionariosEntidades = null;

    private ModoPainelLateral modoLateral;
    private String estadoFiltro = "Todos";

    public FuncionariosPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public FuncionariosPanel(boolean exibirNovoFuncionarioInicialmente) {
        this(exibirNovoFuncionarioInicialmente ? ModoPainelLateral.NOVO_FUNCIONARIO : ModoPainelLateral.NENHUM);
    }

    public FuncionariosPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;
        this.funcionarioBO = new FuncionarioBO();

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.seletorEstado = criarSeletorEstado();
        this.campoBuscarFuncionario = criarCampoBuscarFuncionario();
        this.botaoNovoFuncionario = criarBotaoNovoFuncionario();
        this.botaoEditar = criarBotaoEditarFuncionario();
        this.botaoEstadoCargo = criarBotaoEstadoCargoFuncionario();
        this.botaoSuspender = criarBotaoToolbar(
                "Suspender", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        this.botaoTransferirCargo = criarBotaoTransferirCargoToolbar();
        this.botaoEliminar = criarBotaoToolbar(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );
        this.containerBotoesToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesToolbar.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovoFuncionario = criarCardNovoFuncionario();
        this.cardEditarFuncionario = criarCardEditarFuncionario();
        this.cardEstadoCargoFuncionario = criarCardEstadoCargoFuncionario();

        this.wrapperDireita = new JPanel(new BorderLayout());
        this.wrapperDireita.setOpaque(false);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosFuncionarioParaEdicao();
                sincronizarDadosFuncionarioParaEstadoCargo();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        definirModoPainelLateral(this.modoLateral);
    }

    public JTable getTabela() {
        return tabela;
    }

    public JButton getSeletorEstado() {
        return seletorEstado;
    }

    public JTextField getCampoBuscarFuncionario() {
        return campoBuscarFuncionario;
    }

    public JButton getBotaoNovoFuncionario() {
        return botaoNovoFuncionario;
    }

    public JButton getBotaoEditar() {
        return botaoEditar;
    }

    public JButton getBotaoEstadoCargo() {
        return botaoEstadoCargo;
    }

    public JButton getBotaoSuspender() {
        return botaoSuspender;
    }

    public JButton getBotaoTransferirCargo() {
        return botaoTransferirCargo;
    }

    public JButton getBotaoEliminar() {
        return botaoEliminar;
    }

    public JPanel getCardNovoFuncionario() {
        return cardNovoFuncionario;
    }

    public JPanel getCardEditarFuncionario() {
        return cardEditarFuncionario;
    }

    public JPanel getCardEstadoCargoFuncionario() {
        return cardEstadoCargoFuncionario;
    }

    public JTextField getCampoNomeNovo() {
        return campoNomeNovo;
    }

    public JTextField getCampoBiNovo() {
        return campoBiNovo;
    }

    public JTextField getCampoDataNascimentoNovo() {
        return campoDataNascimentoNovo;
    }

    public JTextField getCampoDataInicioNovo() {
        return campoDataInicioNovo;
    }

    public JButton getSeletorCargoNovo() {
        return seletorCargoNovo;
    }

    public JTextField getCampoContactoNovo() {
        return campoContactoNovo;
    }

    public JButton getSeletorCidadeNovo() {
        return seletorCidadeNovo;
    }

    public JButton getSeletorBairroNovo() {
        return seletorBairroNovo;
    }

    public JTextField getCampoUsernameNovo() {
        return campoUsernameNovo;
    }

    public JButton getSeletorPerfilNovo() {
        return seletorPerfilNovo;
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

    public JTextField getCampoBiEditar() {
        return campoBiEditar;
    }

    public JTextField getCampoDataNascimentoEditar() {
        return campoDataNascimentoEditar;
    }

    public JTextField getCampoDataInicioEditar() {
        return campoDataInicioEditar;
    }

    public JButton getSeletorCargoEditar() {
        return seletorCargoEditar;
    }

    public JTextField getCampoContactoEditar() {
        return campoContactoEditar;
    }

    public JButton getSeletorCidadeEditar() {
        return seletorCidadeEditar;
    }

    public JButton getSeletorBairroEditar() {
        return seletorBairroEditar;
    }

    public JTextField getCampoUsernameEditar() {
        return campoUsernameEditar;
    }

    public JButton getSeletorPerfilEditar() {
        return seletorPerfilEditar;
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

    public JLabel getLabelNomeEstadoCargo() {
        return labelNomeEstadoCargo;
    }

    public JButton getBotaoActivarEstadoCargo() {
        return botaoActivarEstadoCargo;
    }

    public JButton getBotaoInactivarEstadoCargo() {
        return botaoInactivarEstadoCargo;
    }

    public JButton getBotaoSuspenderEstadoCargo() {
        return botaoSuspenderEstadoCargo;
    }

    public JTextField getCampoCargoActualEstadoCargo() {
        return campoCargoActualEstadoCargo;
    }

    public JButton getSeletorNovoCargoEstadoCargo() {
        return seletorNovoCargoEstadoCargo;
    }

    public JTextField getCampoAntiguidadeEstadoCargo() {
        return campoAntiguidadeEstadoCargo;
    }

    public JTextField getCampoVendasMesEstadoCargo() {
        return campoVendasMesEstadoCargo;
    }

    public JButton getBotaoConfirmarTransferirCargo() {
        return botaoConfirmarTransferirCargo;
    }

    public JButton getBotaoCancelarEstadoCargo() {
        return botaoCancelarEstadoCargo;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isNovoFuncionarioVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_FUNCIONARIO;
    }

    public boolean isEditarFuncionarioVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_FUNCIONARIO;
    }

    public boolean isEstadoCargoFuncionarioVisivel() {
        return modoLateral == ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO;
    }

    public final void definirNovoFuncionarioVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVO_FUNCIONARIO : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelEditar() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_FUNCIONARIO);
    }

    public final void exibirPainelEstadoCargo() {
        definirModoPainelLateral(ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean visivel = this.modoLateral != ModoPainelLateral.NENHUM;
        wrapperDireita.removeAll();
        wrapperDireita.setVisible(visivel);

        atualizarBotoesToolbar();
        atualizarTabelaFiltrada();

        if (this.modoLateral == ModoPainelLateral.NOVO_FUNCIONARIO) {
            wrapperDireita.add(cardNovoFuncionario, BorderLayout.NORTH);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_FUNCIONARIO) {
            wrapperDireita.add(cardEditarFuncionario, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFuncionarioParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO) {
            wrapperDireita.add(cardEstadoCargoFuncionario, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFuncionarioParaEstadoCargo();
        } else {
            tabela.clearSelection();
        }

        revalidate();
        repaint();
    }

    private void atualizarBotoesToolbar() {
        containerBotoesToolbar.removeAll();
        containerBotoesToolbar.add(botaoNovoFuncionario);
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        if (modoLateral != ModoPainelLateral.NENHUM) {
            // Em FuncionarioNovo.dc.html: Novo funcionário | Editar | Estado/Cargo | Eliminar
            containerBotoesToolbar.add(botaoEditar);
            containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesToolbar.add(botaoEstadoCargo);
        } else {
            // Em Funcionarios.dc.html: Novo funcionário | Suspender | Transferir cargo | Eliminar
            containerBotoesToolbar.add(botaoSuspender);
            containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerBotoesToolbar.add(botaoTransferirCargo);
        }
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        containerBotoesToolbar.add(botaoEliminar);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        // <h1>Funcionários</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Funcionários");
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
        toolbar.add(campoBuscarFuncionario);
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
     * <span class="in">Estado: Todos ▾</span>
     */
    private BotaoSeletorEstado criarSeletorEstado() {
        BotaoSeletorEstado botao = new BotaoSeletorEstado("Estado: Todos ▾");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        String[] opcoes = {"Todos", "ATIVO", "SUSPENSO"};
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
     * <span class="in">Buscar por nome/BI</span>
     */
    private CampoBuscaArredondado criarCampoBuscarFuncionario() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar por nome/BI");
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

    private JButton criarBotaoNovoFuncionario() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo funcionário", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_FUNCIONARIO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_FUNCIONARIO);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarFuncionario() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_FUNCIONARIO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_FUNCIONARIO);
            }
        });
        return botao;
    }

    private JButton criarBotaoEstadoCargoFuncionario() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Estado/Cargo", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO);
            }
        });
        return botao;
    }

    private JButton criarBotaoTransferirCargoToolbar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Transferir cargo", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO));
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
        RenderizadorCelulaEstado renderizadorEstado = new RenderizadorCelulaEstado();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            String nomeColuna = t.getColumnModel().getColumn(i).getHeaderValue().toString();
            if ("Estado".equalsIgnoreCase(nomeColuna)) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
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
     * Card lateral "Novo funcionário" fiel a FuncionarioNovo.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:8px">
     */
    private JPanel criarCardNovoFuncionario() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Novo funcionário</b>
        JLabel titulo = new JLabel("Novo funcionário");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0"></div></div>
        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 58);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">BI * | Data de nascimento * (dd/mm/aaaa)</div>
        campoBiNovo = new CampoFormularioArredondado("");
        campoDataNascimentoNovo = new CampoFormularioArredondado("dd/mm/aaaa");
        JPanel linhaBiNascimento = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaBiNascimento.setOpaque(false);
        linhaBiNascimento.add(criarCampoComLabel("BI *", campoBiNovo));
        linhaBiNascimento.add(criarCampoComLabel("Data de nascimento *", campoDataNascimentoNovo));
        fixarLargura(linhaBiNascimento, larguraInterna, 58);
        card.add(linhaBiNascimento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Data de início * (dd/mm/aaaa) | Cargo * (Vendedor ▾)</div>
        campoDataInicioNovo = new CampoFormularioArredondado("dd/mm/aaaa");
        seletorCargoNovo = new BotaoSeletorFormulario("Vendedor ▾", new String[] {"Vendedor", "Gerente", "Caixa"});
        JPanel linhaInicioCargo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaInicioCargo.setOpaque(false);
        linhaInicioCargo.add(criarCampoComLabel("Data de início *", campoDataInicioNovo));
        linhaInicioCargo.add(criarCampoComLabel("Cargo *", seletorCargoNovo));
        fixarLargura(linhaInicioCargo, larguraInterna, 58);
        card.add(linhaInicioCargo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto *</span><div class="in" style="min-width:0"></div></div>
        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 58);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Cidade * (Matola ▾) | Bairro * (Fomento ▾)</div>
        seletorCidadeNovo = new BotaoSeletorFormulario("Matola ▾", new String[] {"Matola", "Maputo", "Boane"});
        seletorBairroNovo = new BotaoSeletorFormulario("Fomento ▾", new String[] {"Fomento", "Machava", "Liberdade", "Matola A"});
        JPanel linhaCidadeBairro = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCidadeBairro.setOpaque(false);
        linhaCidadeBairro.add(criarCampoComLabel("Cidade *", seletorCidadeNovo));
        linhaCidadeBairro.add(criarCampoComLabel("Bairro *", seletorBairroNovo));
        fixarLargura(linhaCidadeBairro, larguraInterna, 58);
        card.add(linhaCidadeBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Username * | Perfil * (VENDEDOR ▾)</div>
        campoUsernameNovo = new CampoFormularioArredondado("");
        seletorPerfilNovo = new BotaoSeletorFormulario("VENDEDOR ▾", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel linhaUsernamePerfil = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaUsernamePerfil.setOpaque(false);
        linhaUsernamePerfil.add(criarCampoComLabel("Username *", campoUsernameNovo));
        linhaUsernamePerfil.add(criarCampoComLabel("Perfil *", seletorPerfilNovo));
        fixarLargura(linhaUsernamePerfil, larguraInterna, 58);
        card.add(linhaUsernamePerfil);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="tb"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
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
     * Card lateral "Editar funcionário" fiel a FuncionarioEditar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:8px">
     */
    private JPanel criarCardEditarFuncionario() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Editar funcionário</b>
        JLabel titulo = new JLabel("Editar funcionário");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0">Carlos Mabjaia</div></div>
        campoNomeEditar = new CampoFormularioArredondado("Carlos Mabjaia");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 58);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">BI * (110100123456A) | Data de nascimento * (12/05/1990)</div>
        campoBiEditar = new CampoFormularioArredondado("110100123456A");
        campoDataNascimentoEditar = new CampoFormularioArredondado("12/05/1990");
        JPanel linhaBiNascimento = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaBiNascimento.setOpaque(false);
        linhaBiNascimento.add(criarCampoComLabel("BI *", campoBiEditar));
        linhaBiNascimento.add(criarCampoComLabel("Data de nascimento *", campoDataNascimentoEditar));
        fixarLargura(linhaBiNascimento, larguraInterna, 58);
        card.add(linhaBiNascimento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Data de início * (01/03/2021) | Cargo * (Vendedor ▾)</div>
        campoDataInicioEditar = new CampoFormularioArredondado("01/03/2021");
        seletorCargoEditar = new BotaoSeletorFormulario("Vendedor ▾", new String[] {"Vendedor", "Gerente", "Caixa"});
        JPanel linhaInicioCargo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaInicioCargo.setOpaque(false);
        linhaInicioCargo.add(criarCampoComLabel("Data de início *", campoDataInicioEditar));
        linhaInicioCargo.add(criarCampoComLabel("Cargo *", seletorCargoEditar));
        fixarLargura(linhaInicioCargo, larguraInterna, 58);
        card.add(linhaInicioCargo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto *</span><div class="in" style="min-width:0">84 123 4567</div></div>
        campoContactoEditar = new CampoFormularioArredondado("84 123 4567");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoEditar);
        fixarLargura(blocoContacto, larguraInterna, 58);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Cidade * (Matola ▾) | Bairro * (Fomento ▾)</div>
        seletorCidadeEditar = new BotaoSeletorFormulario("Matola ▾", new String[] {"Matola", "Maputo", "Boane"});
        seletorBairroEditar = new BotaoSeletorFormulario("Fomento ▾", new String[] {"Fomento", "Machava", "Liberdade", "Matola A"});
        JPanel linhaCidadeBairro = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCidadeBairro.setOpaque(false);
        linhaCidadeBairro.add(criarCampoComLabel("Cidade *", seletorCidadeEditar));
        linhaCidadeBairro.add(criarCampoComLabel("Bairro *", seletorBairroEditar));
        fixarLargura(linhaCidadeBairro, larguraInterna, 58);
        card.add(linhaCidadeBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="fm">Username (carlos) | Perfil (VENDEDOR ▾)</div>
        campoUsernameEditar = new CampoFormularioArredondado("carlos");
        seletorPerfilEditar = new BotaoSeletorFormulario("VENDEDOR ▾", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel linhaUsernamePerfil = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaUsernamePerfil.setOpaque(false);
        linhaUsernamePerfil.add(criarCampoComLabel("Username", campoUsernameEditar));
        linhaUsernamePerfil.add(criarCampoComLabel("Perfil", seletorPerfilEditar));
        fixarLargura(linhaUsernamePerfil, larguraInterna, 58);
        card.add(linhaUsernamePerfil);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // <div class="tb"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoesEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Alterações do funcionário guardadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
            recarregarDados();
        });

        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funcionário eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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

    /**
     * Card lateral "Estado e cargo" fiel a FuncionarioEstado.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardEstadoCargoFuncionario() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Estado e cargo</b>
        JLabel titulo = new JLabel("Estado e cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;gap:10px;align-items:center"><span>Carlos Mabjaia</span><span class="ch g">ATIVO</span></div>
        JPanel linhaNomeEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaNomeEstado.setOpaque(false);
        linhaNomeEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelNomeEstadoCargo = new JLabel("Carlos Mabjaia");
        labelNomeEstadoCargo.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelNomeEstadoCargo.setForeground(UiStyle.TEXT);
        chipEstadoCargo = new ChipEstado("ATIVO", UiStyle.CHIP_GREEN_BG, UiStyle.CHIP_GREEN_FG);

        linhaNomeEstado.add(labelNomeEstadoCargo);
        linhaNomeEstado.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaNomeEstado.add(chipEstadoCargo);
        card.add(linhaNomeEstado);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt s">Activar</span><span class="bt s">Inactivar</span><span class="bt s">Suspender</span></div>
        JPanel barraEstados = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraEstados.setOpaque(false);
        barraEstados.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoActivarEstadoCargo = criarBotaoToolbar(
                "Activar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoActivarEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.ATIVO));
        botaoInactivarEstadoCargo = criarBotaoToolbar(
                "Inactivar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoInactivarEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.INATIVO));
        botaoSuspenderEstadoCargo = criarBotaoToolbar(
                "Suspender", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoSuspenderEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.SUSPENSO));

        barraEstados.add(botaoActivarEstadoCargo);
        barraEstados.add(Box.createRigidArea(new Dimension(10, 0)));
        barraEstados.add(botaoInactivarEstadoCargo);
        barraEstados.add(Box.createRigidArea(new Dimension(10, 0)));
        barraEstados.add(botaoSuspenderEstadoCargo);
        card.add(barraEstados);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm">Cargo actual (Vendedor) | Novo cargo (Gerente ▾)</div>
        campoCargoActualEstadoCargo = new CampoFormularioArredondado("Vendedor");
        campoCargoActualEstadoCargo.setEditable(false);
        seletorNovoCargoEstadoCargo = new BotaoSeletorFormulario("Gerente ▾", new String[] {"Gerente", "Vendedor", "Caixa"});
        JPanel linhaCargos = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCargos.setOpaque(false);
        linhaCargos.add(criarCampoComLabel("Cargo actual", campoCargoActualEstadoCargo));
        linhaCargos.add(criarCampoComLabel("Novo cargo", seletorNovoCargoEstadoCargo));
        fixarLargura(linhaCargos, larguraInterna, 58);
        card.add(linhaCargos);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm">Antiguidade (5 anos) | Vendas no mês ([valor])</div>
        campoAntiguidadeEstadoCargo = new CampoFormularioArredondado("5 anos");
        campoAntiguidadeEstadoCargo.setEditable(false);
        campoVendasMesEstadoCargo = new CampoFormularioArredondado("[valor]");
        campoVendasMesEstadoCargo.setEditable(false);
        JPanel linhaAntiguidadeVendas = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaAntiguidadeVendas.setOpaque(false);
        linhaAntiguidadeVendas.add(criarCampoComLabel("Antiguidade", campoAntiguidadeEstadoCargo));
        linhaAntiguidadeVendas.add(criarCampoComLabel("Vendas no mês", campoVendasMesEstadoCargo));
        fixarLargura(linhaAntiguidadeVendas, larguraInterna, 58);
        card.add(linhaAntiguidadeVendas);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Transferir cargo</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoConfirmarTransferirCargo = criarBotaoToolbar(
                "Transferir cargo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoConfirmarTransferirCargo.addActionListener(e -> transferirCargoFuncionarioSelecionado());
        botaoCancelarEstadoCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEstadoCargo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoConfirmarTransferirCargo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarEstadoCargo);
        card.add(acoes);

        return card;
    }

    private void alterarEstadoFuncionarioSelecionado(EstadoFuncionario novoEstado) {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabela.getValueAt(row, 0)));
        AsyncTask.executar(
            () -> {
                Funcionario f = funcionarioBO.buscarPorId(id);
                f.setEstado(novoEstado);
                funcionarioBO.atualizar(f);
                return f;
            },
            atualizado -> {
                JOptionPane.showMessageDialog(this, "Estado do funcionário alterado para " + novoEstado + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao alterar estado: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void transferirCargoFuncionarioSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabela.getValueAt(row, 0)));
        String novoCargoNome = seletorNovoCargoEstadoCargo.getText().replace("▾", "").trim();
        AsyncTask.executar(
            () -> {
                Funcionario f = funcionarioBO.buscarPorId(id);
                if (f.getCargo() == null) {
                    f.setCargo(new Cargo());
                }
                f.getCargo().setNome(novoCargoNome);
                funcionarioBO.atualizar(f);
                return f;
            },
            atualizado -> {
                JOptionPane.showMessageDialog(this, "Cargo transferido para " + novoCargoNome + " com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao transferir cargo: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void sincronizarDadosFuncionarioParaEstadoCargo() {
        if (labelNomeEstadoCargo == null) {
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
        List<Object[]> fonte = (dadosFuncionariosCarregados != null) ? dadosFuncionariosCarregados : Arrays.asList(DADOS_INICIAIS);
        for (Object[] f : fonte) {
            if (String.valueOf(f[0]).equals(id)) {
                String nome = String.valueOf(f[1]);
                String cargo = String.valueOf(f[4]);
                String estado = String.valueOf(f[5]);
                String antiguidade = String.valueOf(f[6]);

                labelNomeEstadoCargo.setText(nome);
                boolean ativo = "ATIVO".equalsIgnoreCase(estado);
                chipEstadoCargo.atualizarEstado(
                        estado,
                        ativo ? UiStyle.CHIP_GREEN_BG : UiStyle.CHIP_YELLOW_BG,
                        ativo ? UiStyle.CHIP_GREEN_FG : UiStyle.CHIP_YELLOW_FG
                );
                campoCargoActualEstadoCargo.setText(cargo);
                campoAntiguidadeEstadoCargo.setText(antiguidade);
                campoVendasMesEstadoCargo.setText("[valor]");
                if ("Vendedor".equalsIgnoreCase(cargo)) {
                    seletorNovoCargoEstadoCargo.setText("Gerente ▾");
                } else {
                    seletorNovoCargoEstadoCargo.setText("Vendedor ▾");
                }
                break;
            }
        }
    }

    private void sincronizarDadosFuncionarioParaEdicao() {
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
        List<Object[]> fonte = (dadosFuncionariosCarregados != null) ? dadosFuncionariosCarregados : Arrays.asList(DADOS_INICIAIS);
        for (Object[] f : fonte) {
            if (String.valueOf(f[0]).equals(id)) {
                String nome = String.valueOf(f[1]);
                String bi = String.valueOf(f[2]);
                String inicio = String.valueOf(f[3]);
                String cargo = String.valueOf(f[4]);

                campoNomeEditar.setText(nome);
                campoBiEditar.setText(bi);
                campoDataInicioEditar.setText(inicio);
                seletorCargoEditar.setText(cargo + " ▾");

                if ("1".equals(id)) {
                    campoDataNascimentoEditar.setText("12/05/1990");
                    campoContactoEditar.setText("84 123 4567");
                    seletorCidadeEditar.setText("Matola ▾");
                    seletorBairroEditar.setText("Fomento ▾");
                    campoUsernameEditar.setText("carlos");
                    seletorPerfilEditar.setText("VENDEDOR ▾");
                } else if ("2".equals(id)) {
                    campoDataNascimentoEditar.setText("24/08/1992");
                    campoContactoEditar.setText("84 654 3210");
                    seletorCidadeEditar.setText("Matola ▾");
                    seletorBairroEditar.setText("Fomento ▾");
                    campoUsernameEditar.setText("sara");
                    seletorPerfilEditar.setText("GERENTE ▾");
                } else {
                    campoDataNascimentoEditar.setText("05/11/1995");
                    campoContactoEditar.setText("84 987 6543");
                    seletorCidadeEditar.setText("Matola ▾");
                    seletorBairroEditar.setText("Fomento ▾");
                    campoUsernameEditar.setText("maria");
                    seletorPerfilEditar.setText("VENDEDOR ▾");
                }
                break;
            }
        }
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        JPanel bloco = new JPanel(new BorderLayout(0, 6));
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
        String termo = campoBuscarFuncionario.getTextoReal().trim().toLowerCase();
        boolean resumida = modoLateral != ModoPainelLateral.NENHUM;
        String[] colunasAlvo = resumida ? COLUNAS_RESUMIDAS : COLUNAS_COMPLETAS;

        if (modeloTabela.getColumnCount() != colunasAlvo.length) {
            modeloTabela.setDataVector(new Object[0][0], colunasAlvo);
            aplicarRenderizadoresColunas(tabela);
        } else {
            modeloTabela.setRowCount(0);
        }

        List<Object[]> fonteDados = (dadosFuncionariosCarregados != null) ? dadosFuncionariosCarregados : Arrays.asList(DADOS_INICIAIS);

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            String bi = String.valueOf(linha[2]).toLowerCase();
            String estado = String.valueOf(linha[5]);

            boolean bateBusca = termo.isEmpty() || nome.contains(termo) || bi.contains(termo);
            boolean bateEstado = "Todos".equalsIgnoreCase(estadoFiltro)
                    || estado.equalsIgnoreCase(estadoFiltro);

            if (bateBusca && bateEstado) {
                if (resumida) {
                    // Colunas em FuncionarioNovo.dc.html: ID, Nome, BI, Cargo, Estado, Antiguidade
                    modeloTabela.addRow(new Object[] {
                        linha[0], linha[1], linha[2], linha[4], linha[5], linha[6]
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
     * Renderiza os chips de Estado:
     * ATIVO (.ch.g: #12402A / #7FE3A8) e SUSPENSO (.ch.y: #4A3A0F / #FFD36B).
     */
    private static final class RenderizadorCelulaEstado extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";
            boolean ativo = "ATIVO".equalsIgnoreCase(texto);

            Color bgChip = ativo ? UiStyle.CHIP_GREEN_BG : UiStyle.CHIP_YELLOW_BG;
            Color fgChip = ativo ? UiStyle.CHIP_GREEN_FG : UiStyle.CHIP_YELLOW_FG;

            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            container.setOpaque(isSelected);
            container.setBackground(isSelected ? UiStyle.TABLE_SELECTION_BG : UiStyle.SURFACE);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(8, 14, 8, 14)
            ));

            ChipEstado chip = new ChipEstado(texto, bgChip, fgChip);
            container.add(chip);
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

        void atualizarEstado(String texto, Color novaCorFundo, Color novaCorTexto) {
            setText(texto);
            this.corFundo = novaCorFundo;
            setForeground(novaCorTexto);
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
     * Seletor de estado estilo .in ("Estado: Todos ▾").
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
     * Seletor de formulário estilo .in com min-width:0 (ex: "Vendedor ▾", "Matola ▾", "Fomento ▾", "VENDEDOR ▾").
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
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Nome *, BI *, dd/mm/aaaa, Contacto *, Username *).
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
     * Campo de texto estilo .in ("Buscar por nome/BI").
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
            () -> funcionarioBO.listarTodos(),
            funcs -> {
                this.listaFuncionariosEntidades = funcs;
                if (funcs != null && !funcs.isEmpty()) {
                    List<Object[]> linhas = new ArrayList<>();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Funcionario f : funcs) {
                        String idStr = String.valueOf(f.getIdFuncionario());
                        String nomeStr = f.getNome() != null ? f.getNome() : "";
                        String biStr = f.getBi() != null ? f.getBi() : "";
                        String iniStr = f.getDataInicio() != null ? f.getDataInicio().format(dtf) : "-";
                        String cargoStr = (f.getCargo() != null && f.getCargo().getNome() != null)
                                ? f.getCargo().getNome() : "";
                        String estStr = f.getEstado() != null ? f.getEstado().name() : "ATIVO";
                        String antStr = f.calcularAntiguidadeAnos() + " anos";
                        linhas.add(new Object[] {idStr, nomeStr, biStr, iniStr, cargoStr, estStr, antStr});
                    }
                    this.dadosFuncionariosCarregados = linhas;
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
        // Acesso restrito a ADMIN na barra de navegação
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
