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
import java.awt.geom.RoundRectangle2D;
import java.time.temporal.ChronoUnit;
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
import java.util.List;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Locale;
import controller.FuncionarioController;
import model.vo.Bairro;
import model.vo.Contacto;
import model.vo.Cargo;
import model.vo.Cidade;
import model.vo.Endereco;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;

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
        "ID", "Nome", "BI", "Idade", "Contacto", "Cidade/Bairro", "Início", "Cargo", "Estado", "Antiguidade"
    };

    private static final String[] COLUNAS_RESUMIDAS = {
        "ID", "Nome", "BI", "Contacto", "Cargo", "Estado", "Antiguidade"
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

    private JTextField campoNomeNovo;
    private JTextField campoBiNovo;
    private JTextField campoDataNascimentoNovo;
    private JTextField campoDataInicioNovo;
    private BotaoSeletorFormulario seletorCargoNovo;
    private JTextField campoContactoNovo;
    private BotaoSeletorFormulario seletorCidadeNovo;
    private BotaoSeletorFormulario seletorBairroNovo;
    private JTextField campoQuarteiraoNovo;
    private JTextField campoRuaNovo;
    private JTextField campoNumeroCasaNovo;
    private JTextField campoUsernameNovo;
    private BotaoSeletorFormulario seletorPerfilNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    private JTextField campoNomeEditar;
    private JTextField campoBiEditar;
    private JTextField campoDataNascimentoEditar;
    private JTextField campoDataInicioEditar;
    private BotaoSeletorFormulario seletorCargoEditar;
    private JTextField campoContactoEditar;
    private BotaoSeletorFormulario seletorCidadeEditar;
    private BotaoSeletorFormulario seletorBairroEditar;
    private JTextField campoQuarteiraoEditar;
    private JTextField campoRuaEditar;
    private JTextField campoNumeroCasaEditar;
    private JTextField campoUsernameEditar;
    private BotaoSeletorFormulario seletorPerfilEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

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

    private final FuncionarioController funcionarioController;
    private List<Object[]> dadosFuncionariosCarregados = new ArrayList<>();
    private List<Funcionario> listaFuncionariosEntidades = new ArrayList<>();
    private List<Utilizador> listaUtilizadoresEntidades = new ArrayList<>();
    private List<Cargo> listaCargosReais = new ArrayList<>();
    private List<Cidade> listaCidadesReais = new ArrayList<>();
    private List<Bairro> listaBairrosReais = new ArrayList<>();

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
        this.funcionarioController = new FuncionarioController();

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
        this.botaoSuspender.setIcon(UiStyle.iconeDinamico(MaterialDesignA.ACCOUNT_ALERT_OUTLINE, 15, () -> UiStyle.TEXT));
        this.botaoSuspender.setIconTextGap(6);
        this.botaoSuspender.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.SUSPENSO));
        this.botaoTransferirCargo = criarBotaoTransferirCargoToolbar();
        this.botaoEliminar = criarBotaoToolbar(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );
        this.botaoEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminar.setIconTextGap(6);
        this.botaoEliminar.addActionListener(e -> eliminarFuncionarioSelecionado());
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
        recarregarDados();
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
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardNovoFuncionario), BorderLayout.CENTER);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_FUNCIONARIO) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEditarFuncionario), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFuncionarioParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.ESTADO_CARGO_FUNCIONARIO) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEstadoCargoFuncionario), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosFuncionarioParaEstadoCargo();
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

        JLabel titulo = new JLabel("Funcionários");
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

        toolbar.add(seletorEstado);
        toolbar.add(campoBuscarFuncionario);
        toolbar.add(botaoNovoFuncionario);
        toolbar.add(botaoEditar);
        toolbar.add(botaoEstadoCargo);
        toolbar.add(botaoSuspender);
        toolbar.add(botaoTransferirCargo);
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

    private BotaoSeletorEstado criarSeletorEstado() {
        BotaoSeletorEstado botao = new BotaoSeletorEstado("Estado: Todos");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        String[] opcoes = {"Todos", "ATIVO", "INATIVO", "SUSPENSO"};
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
                    botao.setText("Estado: " + opcao);
                    atualizarTabelaFiltrada();
                });
                menu.add(item);
            }
            UiStyle.mostrarPopupSincronizado(menu, botao);
        });
        return botao;
    }

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
        return new BotaoToolbarArredondado(
                texto, bg, fg, borda, RAIO_COMPONENTE, preencherFundo
        );
    }

    private JButton criarBotaoNovoFuncionario() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo funcionário", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignA.ACCOUNT_PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignB.BRIEFCASE_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignS.SWAP_HORIZONTAL, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Novo funcionário");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 58);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoBiNovo = new CampoFormularioArredondado("");
        campoDataNascimentoNovo = new CampoFormularioArredondado("dd/mm/aaaa");
        JPanel linhaBiNascimento = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaBiNascimento.setOpaque(false);
        linhaBiNascimento.add(criarCampoComLabel("BI *", campoBiNovo));
        linhaBiNascimento.add(criarCampoComLabel("Data de nascimento *", campoDataNascimentoNovo));
        fixarLargura(linhaBiNascimento, larguraInterna, 58);
        card.add(linhaBiNascimento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoDataInicioNovo = new CampoFormularioArredondado("dd/mm/aaaa");
        seletorCargoNovo = new BotaoSeletorFormulario("Vendedor", new String[] {"Vendedor", "Gerente", "Caixa"});
        JPanel linhaInicioCargo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaInicioCargo.setOpaque(false);
        linhaInicioCargo.add(criarCampoComLabel("Data de início *", campoDataInicioNovo));
        linhaInicioCargo.add(criarCampoComLabel("Cargo *", comBotaoMais(seletorCargoNovo, "Novo cargo", () -> abrirDialogoNovoCargo(seletorCargoNovo))));
        fixarLargura(linhaInicioCargo, larguraInterna, 58);
        card.add(linhaInicioCargo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 58);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorCidadeNovo = new BotaoSeletorFormulario("Matola", new String[] {"Matola", "Maputo", "Boane"});
        seletorBairroNovo = new BotaoSeletorFormulario("Fomento", new String[] {"Fomento", "Machava", "Liberdade", "Matola A"});
        seletorCidadeNovo.setAoSelecionar(() -> atualizarBairrosPorCidade(seletorCidadeNovo, seletorBairroNovo, null));
        JPanel linhaCidadeBairro = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCidadeBairro.setOpaque(false);
        linhaCidadeBairro.add(criarCampoComLabel("Cidade *", comBotaoMais(seletorCidadeNovo, "Nova cidade", () -> abrirDialogoNovaCidade(seletorCidadeNovo, seletorBairroNovo))));
        linhaCidadeBairro.add(criarCampoComLabel("Bairro *", comBotaoMais(seletorBairroNovo, "Novo bairro", () -> abrirDialogoNovoBairro(seletorCidadeNovo, seletorBairroNovo))));
        fixarLargura(linhaCidadeBairro, larguraInterna, 58);
        card.add(linhaCidadeBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoQuarteiraoNovo = new CampoFormularioArredondado("");
        campoRuaNovo = new CampoFormularioArredondado("");
        campoNumeroCasaNovo = new CampoFormularioArredondado("");
        JPanel linhaEnderecoNovo = new JPanel(new GridLayout(1, 3, 8, 0));
        linhaEnderecoNovo.setOpaque(false);
        linhaEnderecoNovo.add(criarCampoComLabel("Quarteirão *", campoQuarteiraoNovo));
        linhaEnderecoNovo.add(criarCampoComLabel("Rua *", campoRuaNovo));
        linhaEnderecoNovo.add(criarCampoComLabel("Nº da casa *", campoNumeroCasaNovo));
        fixarLargura(linhaEnderecoNovo, larguraInterna, 58);
        card.add(linhaEnderecoNovo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoUsernameNovo = new CampoFormularioArredondado("");
        seletorPerfilNovo = new BotaoSeletorFormulario("VENDEDOR", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel linhaUsernamePerfil = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaUsernamePerfil.setOpaque(false);
        linhaUsernamePerfil.add(criarCampoComLabel("Username *", campoUsernameNovo));
        linhaUsernamePerfil.add(criarCampoComLabel("Perfil *", seletorPerfilNovo));
        fixarLargura(linhaUsernamePerfil, larguraInterna, 58);
        card.add(linhaUsernamePerfil);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarNovo.setIconTextGap(6);
        botaoGuardarNovo.addActionListener(e -> salvarNovoFuncionario());
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar funcionário");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoNomeEditar = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 58);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoBiEditar = new CampoFormularioArredondado("");
        campoDataNascimentoEditar = new CampoFormularioArredondado("");
        JPanel linhaBiNascimento = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaBiNascimento.setOpaque(false);
        linhaBiNascimento.add(criarCampoComLabel("BI *", campoBiEditar));
        linhaBiNascimento.add(criarCampoComLabel("Data de nascimento *", campoDataNascimentoEditar));
        fixarLargura(linhaBiNascimento, larguraInterna, 58);
        card.add(linhaBiNascimento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoDataInicioEditar = new CampoFormularioArredondado("");
        seletorCargoEditar = new BotaoSeletorFormulario("Vendedor", new String[] {"Vendedor", "Gerente", "Caixa"});
        JPanel linhaInicioCargo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaInicioCargo.setOpaque(false);
        linhaInicioCargo.add(criarCampoComLabel("Data de início *", campoDataInicioEditar));
        linhaInicioCargo.add(criarCampoComLabel("Cargo *", comBotaoMais(seletorCargoEditar, "Novo cargo", () -> abrirDialogoNovoCargo(seletorCargoEditar))));
        fixarLargura(linhaInicioCargo, larguraInterna, 58);
        card.add(linhaInicioCargo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoContactoEditar = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto *", campoContactoEditar);
        fixarLargura(blocoContacto, larguraInterna, 58);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        seletorCidadeEditar = new BotaoSeletorFormulario("Matola", new String[] {"Matola", "Maputo", "Boane"});
        seletorBairroEditar = new BotaoSeletorFormulario("Fomento", new String[] {"Fomento", "Machava", "Liberdade", "Matola A"});
        seletorCidadeEditar.setAoSelecionar(() -> atualizarBairrosPorCidade(seletorCidadeEditar, seletorBairroEditar, null));
        JPanel linhaCidadeBairro = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCidadeBairro.setOpaque(false);
        linhaCidadeBairro.add(criarCampoComLabel("Cidade *", comBotaoMais(seletorCidadeEditar, "Nova cidade", () -> abrirDialogoNovaCidade(seletorCidadeEditar, seletorBairroEditar))));
        linhaCidadeBairro.add(criarCampoComLabel("Bairro *", comBotaoMais(seletorBairroEditar, "Novo bairro", () -> abrirDialogoNovoBairro(seletorCidadeEditar, seletorBairroEditar))));
        fixarLargura(linhaCidadeBairro, larguraInterna, 58);
        card.add(linhaCidadeBairro);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoQuarteiraoEditar = new CampoFormularioArredondado("");
        campoRuaEditar = new CampoFormularioArredondado("");
        campoNumeroCasaEditar = new CampoFormularioArredondado("");
        JPanel linhaEnderecoEditar = new JPanel(new GridLayout(1, 3, 8, 0));
        linhaEnderecoEditar.setOpaque(false);
        linhaEnderecoEditar.add(criarCampoComLabel("Quarteirão *", campoQuarteiraoEditar));
        linhaEnderecoEditar.add(criarCampoComLabel("Rua *", campoRuaEditar));
        linhaEnderecoEditar.add(criarCampoComLabel("Nº da casa *", campoNumeroCasaEditar));
        fixarLargura(linhaEnderecoEditar, larguraInterna, 58);
        card.add(linhaEnderecoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoUsernameEditar = new CampoFormularioArredondado("");
        seletorPerfilEditar = new BotaoSeletorFormulario("VENDEDOR", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel linhaUsernamePerfil = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaUsernamePerfil.setOpaque(false);
        linhaUsernamePerfil.add(criarCampoComLabel("Username", campoUsernameEditar));
        linhaUsernamePerfil.add(criarCampoComLabel("Perfil", seletorPerfilEditar));
        fixarLargura(linhaUsernamePerfil, larguraInterna, 58);
        card.add(linhaUsernamePerfil);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoesEditar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoesEditar.setIconTextGap(5);
        botaoGuardarAlteracoesEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoGuardarAlteracoesEditar.addActionListener(e -> salvarAlteracoesEditarFuncionario());

        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarEditar.setIconTextGap(5);
        botaoCancelarEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        botaoEliminarEditar.setIconTextGap(5);
        botaoEliminarEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoEliminarEditar.addActionListener(e -> eliminarFuncionarioSelecionado());

        acoes.add(botaoGuardarAlteracoesEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoCancelarEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoEliminarEditar);
        card.add(acoes);

        return card;
    }

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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Estado e cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

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

        JPanel barraEstados = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraEstados.setOpaque(false);
        barraEstados.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoActivarEstadoCargo = criarBotaoToolbar(
                "Activar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoActivarEstadoCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignA.ACCOUNT_CHECK_OUTLINE, 15, () -> UiStyle.TEXT));
        botaoActivarEstadoCargo.setIconTextGap(5);
        botaoActivarEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.ATIVO));
        botaoInactivarEstadoCargo = criarBotaoToolbar(
                "Inactivar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoInactivarEstadoCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignA.ACCOUNT_CANCEL_OUTLINE, 15, () -> UiStyle.TEXT));
        botaoInactivarEstadoCargo.setIconTextGap(5);
        botaoInactivarEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.INATIVO));
        botaoSuspenderEstadoCargo = criarBotaoToolbar(
                "Suspender", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true
        );
        botaoSuspenderEstadoCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignA.ACCOUNT_ALERT_OUTLINE, 15, () -> UiStyle.TEXT));
        botaoSuspenderEstadoCargo.setIconTextGap(5);
        botaoSuspenderEstadoCargo.addActionListener(e -> alterarEstadoFuncionarioSelecionado(EstadoFuncionario.SUSPENSO));

        barraEstados.add(botaoActivarEstadoCargo);
        barraEstados.add(Box.createRigidArea(new Dimension(10, 0)));
        barraEstados.add(botaoInactivarEstadoCargo);
        barraEstados.add(Box.createRigidArea(new Dimension(10, 0)));
        barraEstados.add(botaoSuspenderEstadoCargo);
        card.add(barraEstados);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoCargoActualEstadoCargo = new CampoFormularioArredondado("");
        campoCargoActualEstadoCargo.setEditable(false);
        seletorNovoCargoEstadoCargo = new BotaoSeletorFormulario("Gerente", new String[] {"Gerente", "Vendedor", "Caixa"});
        JPanel linhaCargos = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaCargos.setOpaque(false);
        linhaCargos.add(criarCampoComLabel("Cargo actual", campoCargoActualEstadoCargo));
        linhaCargos.add(criarCampoComLabel("Novo cargo", comBotaoMais(seletorNovoCargoEstadoCargo, "Novo cargo", () -> abrirDialogoNovoCargo(seletorNovoCargoEstadoCargo))));
        fixarLargura(linhaCargos, larguraInterna, 58);
        card.add(linhaCargos);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoAntiguidadeEstadoCargo = new CampoFormularioArredondado("");
        campoAntiguidadeEstadoCargo.setEditable(false);
        campoVendasMesEstadoCargo = new CampoFormularioArredondado("");
        campoVendasMesEstadoCargo.setEditable(false);
        JPanel linhaAntiguidadeVendas = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaAntiguidadeVendas.setOpaque(false);
        linhaAntiguidadeVendas.add(criarCampoComLabel("Antiguidade", campoAntiguidadeEstadoCargo));
        linhaAntiguidadeVendas.add(criarCampoComLabel("Vendas no mês", campoVendasMesEstadoCargo));
        fixarLargura(linhaAntiguidadeVendas, larguraInterna, 58);
        card.add(linhaAntiguidadeVendas);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoConfirmarTransferirCargo = criarBotaoToolbar(
                "Transferir cargo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoConfirmarTransferirCargo.setIcon(UiStyle.icone(MaterialDesignS.SWAP_HORIZONTAL, 15, UiStyle.ACCENT_TEXT));
        botaoConfirmarTransferirCargo.setIconTextGap(6);
        botaoConfirmarTransferirCargo.addActionListener(e -> transferirCargoFuncionarioSelecionado());
        botaoCancelarEstadoCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEstadoCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarEstadoCargo.setIconTextGap(6);
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
                Funcionario f = funcionarioController.buscarPorId(id);
                f.setEstado(novoEstado);
                funcionarioController.atualizar(f);
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

    private void salvarNovoFuncionario() {
        String nome = (campoNomeNovo != null) ? campoNomeNovo.getText().trim() : "";
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do funcionário.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bi = (campoBiNovo != null) ? campoBiNovo.getText().trim() : "";
        if (bi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o BI do funcionário.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dataNascStr = (campoDataNascimentoNovo != null) ? campoDataNascimentoNovo.getText().trim() : "";
        if (dataNascStr.isEmpty() || "dd/mm/aaaa".equalsIgnoreCase(dataNascStr)) {
            JOptionPane.showMessageDialog(this, "Informe a data de nascimento (dd/mm/aaaa).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dataNascimento;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            dataNascimento = LocalDate.parse(dataNascStr, dtf);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato dd/mm/aaaa.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dataIniStr = (campoDataInicioNovo != null) ? campoDataInicioNovo.getText().trim() : "";
        if (dataIniStr.isEmpty() || "dd/mm/aaaa".equalsIgnoreCase(dataIniStr)) {
            JOptionPane.showMessageDialog(this, "Informe a data de início (dd/mm/aaaa).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dataInicio;
        try {
            dataInicio = LocalDate.parse(dataIniStr, dtf);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data de início inválida. Use o formato dd/mm/aaaa.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeCargo = (seletorCargoNovo != null) ? seletorCargoNovo.getText().replace("▾", "").trim() : "";
        String contacto = (campoContactoNovo != null) ? campoContactoNovo.getText().trim() : "";
        if (contacto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o contacto do funcionário.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String quarteirao = (campoQuarteiraoNovo != null) ? campoQuarteiraoNovo.getText().trim() : "";
        String rua = (campoRuaNovo != null) ? campoRuaNovo.getText().trim() : "";
        String numeroCasa = (campoNumeroCasaNovo != null) ? campoNumeroCasaNovo.getText().trim() : "";
        if (quarteirao.isEmpty() || rua.isEmpty() || numeroCasa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha Quarteirão, Rua e Nº da casa para o endereço.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Bairro bairroEscolhido = resolverBairroSelecionado(
                seletorBairroNovo != null ? seletorBairroNovo.getText() : null,
                seletorCidadeNovo != null ? seletorCidadeNovo.getText() : null);
        if (bairroEscolhido == null || bairroEscolhido.getIdBairro() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma Cidade e um Bairro válidos.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (campoUsernameNovo != null) ? campoUsernameNovo.getText().trim() : "";
        String perfilStr = (seletorPerfilNovo != null) ? seletorPerfilNovo.getText().replace("▾", "").trim() : "VENDEDOR";

        botaoGuardarNovo.setEnabled(false);
        final LocalDate finalDataNascimentoNovo = dataNascimento;
        final LocalDate finalDataInicioNovo = dataInicio;
        final Bairro finalBairroNovo = bairroEscolhido;

        AsyncTask.executar(
            () -> {
                List<Cargo> cargos = funcionarioController.listarCargos();
                Cargo cargoEscolhido = null;
                for (Cargo c : cargos) {
                    if (c.getNome() != null && c.getNome().equalsIgnoreCase(nomeCargo)) {
                        cargoEscolhido = c;
                        break;
                    }
                }
                if (cargoEscolhido == null) {
                    throw new IllegalArgumentException("Selecione um cargo válido cadastrado no sistema.");
                }

                Funcionario f = new Funcionario();
                f.setNome(nome);
                f.setBi(bi);
                f.setDataNascimento(finalDataNascimentoNovo);
                f.setDataInicio(finalDataInicioNovo);
                f.setCargo(cargoEscolhido);
                f.setEstado(EstadoFuncionario.ATIVO);

                Contacto c = new Contacto();
                c.setContacto(contacto);
                f.setContacto(Collections.singletonList(c));

                Endereco end = new Endereco();
                end.setBairro(finalBairroNovo);
                end.setQuarteirao(quarteirao);
                end.setRua(rua);
                end.setNumeroCasa(numeroCasa);
                f.setEndereco(end);

                funcionarioController.salvar(f);

                String avisoUtilizador = null;
                if (!username.isEmpty()) {
                    try {
                        Utilizador novoU = new Utilizador();
                        novoU.setUsername(username);
                        novoU.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw("Mudar@123", org.mindrot.jbcrypt.BCrypt.gensalt()));
                        novoU.setPerfil(Perfil.valueOf(perfilStr.toUpperCase()));
                        novoU.setFuncionario(f);
                        funcionarioController.salvarUtilizador(novoU);
                    } catch (Exception exU) {
                        avisoUtilizador = exU.getMessage();
                    }
                }

                return avisoUtilizador;
            },
            avisoUtilizador -> {
                botaoGuardarNovo.setEnabled(true);
                if (avisoUtilizador != null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Funcionário cadastrado, porém a conta de utilizador não pôde ser criada:\n" + avisoUtilizador,
                            "Aviso na Criação de Utilizador",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
                limparFormularioNovoFuncionario();
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> {
                botaoGuardarNovo.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar funcionário: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void salvarAlteracoesEditarFuncionario() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabela.getValueAt(row, 0)));

        String nome = (campoNomeEditar != null) ? campoNomeEditar.getText().trim() : "";
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome não pode estar vazio.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bi = (campoBiEditar != null) ? campoBiEditar.getText().trim() : "";
        if (bi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O BI não pode estar vazio.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dataNascStr = (campoDataNascimentoEditar != null) ? campoDataNascimentoEditar.getText().trim() : "";
        if (dataNascStr.isEmpty() || "dd/mm/aaaa".equalsIgnoreCase(dataNascStr)) {
            JOptionPane.showMessageDialog(this, "Informe a data de nascimento (dd/mm/aaaa).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento;
        try {
            dataNascimento = LocalDate.parse(dataNascStr, dtf);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data de nascimento inválida. Use o formato dd/mm/aaaa.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dataIniStr = (campoDataInicioEditar != null) ? campoDataInicioEditar.getText().trim() : "";
        if (dataIniStr.isEmpty() || "dd/mm/aaaa".equalsIgnoreCase(dataIniStr)) {
            JOptionPane.showMessageDialog(this, "Informe a data de início (dd/mm/aaaa).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dataInicio;
        try {
            dataInicio = LocalDate.parse(dataIniStr, dtf);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data de início inválida. Use o formato dd/mm/aaaa.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeCargo = (seletorCargoEditar != null) ? seletorCargoEditar.getText().replace("▾", "").trim() : "";
        String contacto = (campoContactoEditar != null) ? campoContactoEditar.getText().trim() : "";
        if (contacto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o contacto do funcionário.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String quarteirao = (campoQuarteiraoEditar != null) ? campoQuarteiraoEditar.getText().trim() : "";
        String rua = (campoRuaEditar != null) ? campoRuaEditar.getText().trim() : "";
        String numeroCasa = (campoNumeroCasaEditar != null) ? campoNumeroCasaEditar.getText().trim() : "";
        if (quarteirao.isEmpty() || rua.isEmpty() || numeroCasa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha Quarteirão, Rua e Nº da casa para o endereço.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Bairro bairroEscolhido = resolverBairroSelecionado(
                seletorBairroEditar != null ? seletorBairroEditar.getText() : null,
                seletorCidadeEditar != null ? seletorCidadeEditar.getText() : null);
        if (bairroEscolhido == null || bairroEscolhido.getIdBairro() == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma Cidade e um Bairro válidos.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (campoUsernameEditar != null) ? campoUsernameEditar.getText().trim() : "";
        String perfilStr = (seletorPerfilEditar != null) ? seletorPerfilEditar.getText().replace("▾", "").trim() : "VENDEDOR";

        botaoGuardarAlteracoesEditar.setEnabled(false);
        final LocalDate finalDataNascimentoEditar = dataNascimento;
        final LocalDate finalDataInicioEditar = dataInicio;
        final Bairro finalBairroEditar = bairroEscolhido;

        AsyncTask.executar(
            () -> {
                Funcionario f = funcionarioController.buscarPorId(id);
                f.setNome(nome);
                f.setBi(bi);
                f.setDataNascimento(finalDataNascimentoEditar);
                f.setDataInicio(finalDataInicioEditar);

                List<Cargo> cargos = funcionarioController.listarCargos();
                Cargo cargoEncontrado = null;
                for (Cargo c : cargos) {
                    if (c.getNome() != null && c.getNome().equalsIgnoreCase(nomeCargo)) {
                        cargoEncontrado = c;
                        break;
                    }
                }
                if (cargoEncontrado == null) {
                    throw new IllegalArgumentException("Cargo selecionado não encontrado: " + nomeCargo);
                }
                f.setCargo(cargoEncontrado);

                Contacto c = new Contacto();
                c.setContacto(contacto);
                f.setContacto(Collections.singletonList(c));

                Endereco end = f.getEndereco() != null ? f.getEndereco() : new Endereco();
                end.setBairro(finalBairroEditar);
                end.setQuarteirao(quarteirao);
                end.setRua(rua);
                end.setNumeroCasa(numeroCasa);
                f.setEndereco(end);

                funcionarioController.atualizar(f);

                String avisoUtilizador = null;
                try {
                    List<Utilizador> users = funcionarioController.listarUtilizadores();
                    Utilizador existente = null;
                    if (users != null) {
                        for (Utilizador u : users) {
                            if (u.getFuncionario() != null && id.equals(u.getFuncionario().getIdFuncionario())) {
                                existente = u;
                                break;
                            }
                        }
                    }
                    if (!username.isEmpty()) {
                        if (existente != null) {
                            existente.setUsername(username);
                            existente.setPerfil(Perfil.valueOf(perfilStr.toUpperCase()));
                            existente.setFuncionario(f);
                            funcionarioController.atualizarUtilizador(existente);
                        } else {
                            Utilizador novoU = new Utilizador();
                            novoU.setUsername(username);
                            novoU.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw("Mudar@123", org.mindrot.jbcrypt.BCrypt.gensalt()));
                            novoU.setPerfil(Perfil.valueOf(perfilStr.toUpperCase()));
                            novoU.setFuncionario(f);
                            funcionarioController.salvarUtilizador(novoU);
                        }
                    }
                } catch (Exception exU) {
                    avisoUtilizador = exU.getMessage();
                }

                return avisoUtilizador;
            },
            avisoUtilizador -> {
                botaoGuardarAlteracoesEditar.setEnabled(true);
                if (avisoUtilizador != null) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Dados do funcionário atualizados, mas ocorreu um aviso na conta de utilizador:\n" + avisoUtilizador,
                            "Aviso na Conta de Utilizador",
                            JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(this, "Alterações do funcionário guardadas com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> {
                botaoGuardarAlteracoesEditar.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Erro ao guardar alterações: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    private void eliminarFuncionarioSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabela.getValueAt(row, 0)));
        String nome = String.valueOf(modeloTabela.getValueAt(row, 1));
        int op = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente eliminar o funcionário \"" + nome + "\"?",
            "Confirmar Eliminação",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        AsyncTask.executar(
            () -> {
                try {
                    funcionarioController.eliminar(id);
                } catch (Exception ex) {
                    Funcionario f = funcionarioController.buscarPorId(id);
                    if (f != null) {
                        f.setEstado(EstadoFuncionario.INATIVO);
                        funcionarioController.atualizar(f);
                        return "inativado";
                    }
                    throw ex;
                }
                return "eliminado";
            },
            resultado -> {
                if ("inativado".equals(resultado)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "O funcionário possui registos associados (vendas/auditoria) e foi marcado como INATIVO.",
                        "Funcionário Inativado",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Funcionário eliminado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao processar eliminação: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
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
                Funcionario f = funcionarioController.buscarPorId(id);
                List<Cargo> cargos = funcionarioController.listarCargos();
                Cargo cargoEscolhido = null;
                for (Cargo c : cargos) {
                    if (c.getNome() != null && c.getNome().equalsIgnoreCase(novoCargoNome)) {
                        cargoEscolhido = c;
                        break;
                    }
                }
                if (cargoEscolhido == null) {
                    throw new IllegalArgumentException("Cargo selecionado não encontrado: " + novoCargoNome);
                }
                f.setCargo(cargoEscolhido);
                funcionarioController.atualizar(f);
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
        List<Object[]> fonte = (dadosFuncionariosCarregados != null) ? dadosFuncionariosCarregados : new ArrayList<>();
        for (Object[] f : fonte) {
            if (String.valueOf(f[0]).equals(id)) {
                String nome = String.valueOf(f[1]);
                String cargo = String.valueOf(f[7]);
                String estado = String.valueOf(f[8]);
                String antiguidade = String.valueOf(f[9]);

                labelNomeEstadoCargo.setText(nome);
                boolean ativo = "ATIVO".equalsIgnoreCase(estado);
                chipEstadoCargo.atualizarEstado(
                        estado,
                        ativo ? UiStyle.CHIP_GREEN_BG : UiStyle.CHIP_YELLOW_BG,
                        ativo ? UiStyle.CHIP_GREEN_FG : UiStyle.CHIP_YELLOW_FG
                );
                campoCargoActualEstadoCargo.setText(cargo);
                campoAntiguidadeEstadoCargo.setText(antiguidade);
                campoVendasMesEstadoCargo.setText("0,00 MT");
                try {
                    final Long idFunc = Long.parseLong(id);
                    AsyncTask.executar(
                        () -> funcionarioController.obterTotalVendasFuncionarioMes(idFunc),
                        tot -> campoVendasMesEstadoCargo.setText(String.format(Locale.GERMANY, "%,.2f MT", tot != null ? tot : 0.0)),
                        err -> campoVendasMesEstadoCargo.setText("0,00 MT")
                    );
                } catch (Exception ignored) {}
                if ("Vendedor".equalsIgnoreCase(cargo)) {
                    seletorNovoCargoEstadoCargo.setText("Gerente");
                } else {
                    seletorNovoCargoEstadoCargo.setText("Vendedor");
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            Long idFunc = Long.parseLong(id);
            if (listaFuncionariosEntidades != null) {
                for (Funcionario f : listaFuncionariosEntidades) {
                    if (idFunc.equals(f.getIdFuncionario())) {
                        campoNomeEditar.setText(f.getNome() != null ? f.getNome() : "");
                        campoBiEditar.setText(f.getBi() != null ? f.getBi() : "");
                        campoDataNascimentoEditar.setText(f.getDataNascimento() != null ? f.getDataNascimento().format(dtf) : "");
                        campoDataInicioEditar.setText(f.getDataInicio() != null ? f.getDataInicio().format(dtf) : "");
                        String cargoStr = (f.getCargo() != null && f.getCargo().getNome() != null) ? f.getCargo().getNome() : "Vendedor";
                        seletorCargoEditar.setText(cargoStr);
                        String contStr = (f.getContacto() != null && !f.getContacto().isEmpty() && f.getContacto().get(0).getContacto() != null)
                                ? f.getContacto().get(0).getContacto() : "";
                        campoContactoEditar.setText(contStr);
                        if (f.getEndereco() != null) {
                            String nomeBairro = null;
                            if (f.getEndereco().getBairro() != null) {
                                nomeBairro = f.getEndereco().getBairro().getNome();
                                if (f.getEndereco().getBairro().getCidade() != null
                                        && f.getEndereco().getBairro().getCidade().getNome() != null) {
                                    seletorCidadeEditar.setText(f.getEndereco().getBairro().getCidade().getNome());
                                }
                            }
                            atualizarBairrosPorCidade(seletorCidadeEditar, seletorBairroEditar, nomeBairro);
                            if (campoQuarteiraoEditar != null) {
                                campoQuarteiraoEditar.setText(f.getEndereco().getQuarteirao() != null ? f.getEndereco().getQuarteirao() : "");
                            }
                            if (campoRuaEditar != null) {
                                campoRuaEditar.setText(f.getEndereco().getRua() != null ? f.getEndereco().getRua() : "");
                            }
                            if (campoNumeroCasaEditar != null) {
                                campoNumeroCasaEditar.setText(f.getEndereco().getNumeroCasa() != null ? f.getEndereco().getNumeroCasa() : "");
                            }
                        } else {
                            if (campoQuarteiraoEditar != null) campoQuarteiraoEditar.setText("");
                            if (campoRuaEditar != null) campoRuaEditar.setText("");
                            if (campoNumeroCasaEditar != null) campoNumeroCasaEditar.setText("");
                        }
                        String uName = "";
                        String uPerf = "VENDEDOR";
                        if (listaUtilizadoresEntidades != null) {
                            for (Utilizador u : listaUtilizadoresEntidades) {
                                if (u.getFuncionario() != null && idFunc.equals(u.getFuncionario().getIdFuncionario())) {
                                    uName = u.getUsername() != null ? u.getUsername() : "";
                                    uPerf = u.getPerfil() != null ? u.getPerfil().name() : "VENDEDOR";
                                    break;
                                }
                            }
                        }
                        campoUsernameEditar.setText(uName);
                        seletorPerfilEditar.setText(uPerf);
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

        List<Object[]> fonteDados = (dadosFuncionariosCarregados != null) ? dadosFuncionariosCarregados : new ArrayList<>();

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            String bi = String.valueOf(linha[2]).toLowerCase();
            String estado = String.valueOf(linha[8]);

            boolean bateBusca = termo.isEmpty() || nome.contains(termo) || bi.contains(termo);
            boolean bateEstado = "Todos".equalsIgnoreCase(estadoFiltro)
                    || estado.equalsIgnoreCase(estadoFiltro);

            if (bateBusca && bateEstado) {
                if (resumida) {
                    modeloTabela.addRow(new Object[] {
                        linha[0], linha[1], linha[2], linha[4], linha[7], linha[8], linha[9]
                    });
                } else {
                    modeloTabela.addRow(linha);
                }
            }
        }
    }

    private void limparFormularioNovoFuncionario() {
        if (campoNomeNovo != null) campoNomeNovo.setText("");
        if (campoBiNovo != null) campoBiNovo.setText("");
        if (campoDataNascimentoNovo != null) campoDataNascimentoNovo.setText("");
        if (campoDataInicioNovo != null) campoDataInicioNovo.setText("");
        if (campoContactoNovo != null) campoContactoNovo.setText("");
        if (campoQuarteiraoNovo != null) campoQuarteiraoNovo.setText("");
        if (campoRuaNovo != null) campoRuaNovo.setText("");
        if (campoNumeroCasaNovo != null) campoNumeroCasaNovo.setText("");
        if (campoUsernameNovo != null) campoUsernameNovo.setText("");
    }

    private JComponent comBotaoMais(BotaoSeletorFormulario seletor, String dica, Runnable acao) {
        JPanel linha = new JPanel(new BorderLayout(6, 0));
        linha.setOpaque(false);

        BotaoToolbarArredondado mais = new BotaoToolbarArredondado(
                "", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        mais.setBorder(new EmptyBorder(0, 10, 0, 10));
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

    private void abrirDialogoNovoCargo(BotaoSeletorFormulario seletorAlvo) {
        JTextField campoNome = new JTextField();
        JTextField campoSalario = new JTextField("15000");
        Object[] msg = {
            "Nome do cargo *:", campoNome,
            "Salário base (MT) *:", campoSalario
        };
        int op = JOptionPane.showConfirmDialog(this, msg, "Novo Cargo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) {
            return;
        }
        String nome = campoNome.getText() != null ? campoNome.getText().trim() : "";
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do cargo.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salario;
        try {
            salario = Double.parseDouble(campoSalario.getText().trim().replace(",", "."));
            if (salario < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um salário base válido (>= 0).", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AsyncTask.executar(
            () -> funcionarioController.salvarCargo(nome, salario),
            criado -> {
                if (seletorAlvo != null && criado != null && criado.getNome() != null) {
                    seletorAlvo.setText(criado.getNome());
                }
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao criar cargo: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
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
            () -> funcionarioController.salvarCidade(nomeLimpo),
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
            () -> funcionarioController.salvarBairro(nomeLimpo, cid),
            criado -> {
                if (seletorBairro != null && criado != null && criado.getNome() != null) {
                    seletorBairro.setText(criado.getNome());
                }
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao criar bairro: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
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
            String txt = getText() != null ? getText().trim() : "";
            Color bgDinamico = corFundo;
            Color fgDinamico = getForeground();
            if ("ATIVO".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_GREEN_BG;
                fgDinamico = UiStyle.CHIP_GREEN_FG;
            } else if ("SUSPENSO".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_YELLOW_BG;
                fgDinamico = UiStyle.CHIP_YELLOW_FG;
            } else if ("INATIVO".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.ERROR_BG;
                fgDinamico = UiStyle.ERROR_TEXT;
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
            setBorder(new EmptyBorder(0, 12, 0, 28));
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
            () -> {
                List<Funcionario> funcs = funcionarioController.listarTodos();
                List<Utilizador> users = new ArrayList<>();
                try {
                    users = funcionarioController.listarUtilizadores();
                } catch (Exception ignored) {}
                return new Object[] { funcs, users };
            },
            resultado -> {
                Object[] arr = (Object[]) resultado;
                @SuppressWarnings("unchecked")
                List<Funcionario> funcs = (List<Funcionario>) arr[0];
                @SuppressWarnings("unchecked")
                List<Utilizador> users = (List<Utilizador>) arr[1];
                this.listaFuncionariosEntidades = funcs != null ? funcs : new ArrayList<>();
                this.listaUtilizadoresEntidades = users != null ? users : new ArrayList<>();
                List<Object[]> linhas = new ArrayList<>();
                if (funcs != null) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Funcionario f : funcs) {
                        String idStr = String.valueOf(f.getIdFuncionario());
                        String nomeStr = f.getNome() != null ? f.getNome() : "";
                        String biStr = f.getBi() != null ? f.getBi() : "";
                        String idadeStr = f.getDataNascimento() != null
                                ? ChronoUnit.YEARS.between(f.getDataNascimento(), LocalDate.now()) + " anos"
                                : "-";
                        String contStr = (f.getContacto() != null && !f.getContacto().isEmpty()
                                && f.getContacto().get(0) != null && f.getContacto().get(0).getContacto() != null
                                && !f.getContacto().get(0).getContacto().isBlank())
                                ? f.getContacto().get(0).getContacto() : "-";
                        String cidBairroStr = "-";
                        if (f.getEndereco() != null && f.getEndereco().getBairro() != null) {
                            String cidNome = (f.getEndereco().getBairro().getCidade() != null
                                    && f.getEndereco().getBairro().getCidade().getNome() != null)
                                    ? f.getEndereco().getBairro().getCidade().getNome() : "";
                            String baiNome = f.getEndereco().getBairro().getNome() != null
                                    ? f.getEndereco().getBairro().getNome() : "";
                            if (!cidNome.isEmpty() && !baiNome.isEmpty()) {
                                cidBairroStr = cidNome + " / " + baiNome;
                            } else if (!baiNome.isEmpty()) {
                                cidBairroStr = baiNome;
                            } else if (!cidNome.isEmpty()) {
                                cidBairroStr = cidNome;
                            }
                        }
                        String iniStr = f.getDataInicio() != null ? f.getDataInicio().format(dtf) : "-";
                        String cargoStr = (f.getCargo() != null && f.getCargo().getNome() != null)
                                ? f.getCargo().getNome() : "";
                        String estStr = f.getEstado() != null ? f.getEstado().name() : "ATIVO";
                        String antStr = f.calcularAntiguidadeAnos() + " anos";
                        linhas.add(new Object[] {
                            idStr, nomeStr, biStr, idadeStr, contStr, cidBairroStr, iniStr, cargoStr, estStr, antStr
                        });
                    }
                }
                this.dadosFuncionariosCarregados = linhas;
                atualizarTabelaFiltrada();
            },
            erro -> {
                this.listaFuncionariosEntidades = new ArrayList<>();
                this.listaUtilizadoresEntidades = new ArrayList<>();
                this.dadosFuncionariosCarregados = new ArrayList<>();
                atualizarTabelaFiltrada();
            }
        );
        AsyncTask.executar(
            () -> {
                List<Cargo> cargos = funcionarioController.listarCargos();
                List<Cidade> cidades = funcionarioController.listarCidades();
                List<Bairro> bairros = funcionarioController.listarBairros();
                return new Object[] { cargos, cidades, bairros };
            },
            res -> {
                Object[] arr = (Object[]) res;
                @SuppressWarnings("unchecked")
                List<Cargo> cargos = (List<Cargo>) arr[0];
                @SuppressWarnings("unchecked")
                List<Cidade> cidades = (List<Cidade>) arr[1];
                @SuppressWarnings("unchecked")
                List<Bairro> bairros = (List<Bairro>) arr[2];
                this.listaCargosReais = cargos != null ? cargos : new ArrayList<>();
                this.listaCidadesReais = cidades != null ? cidades : new ArrayList<>();
                this.listaBairrosReais = bairros != null ? bairros : new ArrayList<>();
                String[] nomesCargos = this.listaCargosReais.stream().map(Cargo::getNome).toArray(String[]::new);
                String[] nomesCids = this.listaCidadesReais.stream().map(Cidade::getNome).toArray(String[]::new);
                if (nomesCargos.length > 0) {
                    if (seletorCargoNovo != null) seletorCargoNovo.setOpcoes(nomesCargos);
                    if (seletorCargoEditar != null) seletorCargoEditar.setOpcoes(nomesCargos);
                    if (seletorNovoCargoEstadoCargo != null) seletorNovoCargoEstadoCargo.setOpcoes(nomesCargos);
                }
                if (nomesCids.length > 0) {
                    if (seletorCidadeNovo != null) {
                        seletorCidadeNovo.setOpcoes(nomesCids);
                        if (resolverCidadeSelecionada(seletorCidadeNovo.getText()) == null) {
                            seletorCidadeNovo.setText(nomesCids[0]);
                        }
                    }
                    if (seletorCidadeEditar != null) seletorCidadeEditar.setOpcoes(nomesCids);
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
        UiStyle.atualizarComponentesTema(cardNovoFuncionario);
        UiStyle.atualizarComponentesTema(cardEditarFuncionario);
        UiStyle.atualizarComponentesTema(cardEstadoCargoFuncionario);
    }
}
