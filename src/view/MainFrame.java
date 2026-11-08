package view;

import controller.RelatorioController;
import controller.UtilizadorController;
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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignH;
import org.kordamp.ikonli.materialdesign2.MaterialDesignL;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignS;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import org.kordamp.ikonli.materialdesign2.MaterialDesignW;
import util.AsyncTask;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final String PAINEL_INICIO = "inicio";
    public static final String PAINEL_VENDAS = "vendas";
    public static final String PAINEL_PRODUTOS = "produtos";
    public static final String PAINEL_ESTOQUE = "estoque";
    public static final String PAINEL_RELATORIOS = "relatorios";
    public static final String PAINEL_FUNCIONARIOS = "funcionarios";
    public static final String PAINEL_CLIENTES = "clientes";
    public static final String PAINEL_FORNECEDORES = "fornecedores";
    public static final String PAINEL_UTILIZADORES = "utilizadores";
    public static final String PAINEL_RELATORIO_PDF = "relatorio_pdf";

    private static final String ECRA_LOGIN = "ECRA_LOGIN";
    private static final String ECRA_APLICACAO = "ECRA_APLICACAO";
    private static final int LARGURA_MENU_UTILIZADOR = 286;

    private final CardLayout rootCardLayout;
    private final JPanel rootPanel;

    private final LoginPanel loginPanel;
    private final JPanel appContainer;
    private final JPanel painelCentral;
    private final CardLayout layoutCentral;

    private final Map<String, JPanel> mapaPaineis = new HashMap<>();
    private final List<ItemMenuNavegacao> itensNavegacao = new ArrayList<>();
    private final List<GrupoCategoriaSidebar> gruposCategoriaSidebar = new ArrayList<>();

    private JPanel barraNavegacaoAtual;
    private JMenuBar menuBarItens;
    private BotaoAvatar botaoAvatar;
    private JPopupMenu popupMenuUtilizadorAtivo;
    private JLabel labelMenuUsername;
    private JLabel labelMenuPerfilChip;
    private JLabel labelMenuVendasMes;

    private Utilizador utilizadorLogado;
    private final RelatorioController relatorioController = new RelatorioController();
    private final UtilizadorController utilizadorController = new UtilizadorController();
    private boolean temaEscuroAtivo = true;
    private boolean navegacaoSuperiorAtiva = true;
    private String painelAtivoId = PAINEL_INICIO;

    private ScheduledExecutorService executorMonitoramento;

    public MainFrame() {
        this(null);
    }

    public MainFrame(Utilizador utilizadorInicial) {
        super("Estabelecimento");
        UiStyle.instalarTemaEscuro();

        this.rootCardLayout = new CardLayout();
        this.rootPanel = new JPanel(rootCardLayout);

        this.layoutCentral = new CardLayout();
        this.painelCentral = new JPanel(layoutCentral);
        this.appContainer = new JPanel(new BorderLayout());

        this.loginPanel = new LoginPanel(null, this::fazerLoginSucesso);

        configurarJanela();
        construirInterface();

        if (utilizadorInicial != null) {
            fazerLoginSucesso(utilizadorInicial);
        } else {
            rootCardLayout.show(rootPanel, ECRA_LOGIN);
        }
    }

    public JPanel getPainelCentral() {
        return painelCentral;
    }

    public Utilizador getUtilizadorLogado() {
        return utilizadorLogado;
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);
    }

    private void construirInterface() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(rootPanel, BorderLayout.CENTER);

        rootPanel.add(loginPanel, ECRA_LOGIN);

        construirAreaAplicacao();
        rootPanel.add(appContainer, ECRA_APLICACAO);
    }

    private void construirAreaAplicacao() {
        appContainer.setLayout(new BorderLayout());
        appContainer.setBackground(UiStyle.BACKGROUND);

        this.barraNavegacaoAtual = criarBarraNavegacaoSuperior();
        appContainer.add(barraNavegacaoAtual, BorderLayout.NORTH);

        painelCentral.setBackground(UiStyle.BACKGROUND);

        DashboardPanel dashPanel = new DashboardPanel(utilizadorLogado);
        VendasPanel vendasPanel = new VendasPanel();
        ProdutosPanel produtosPanel = new ProdutosPanel();
        EstoquePanel estoquePanel = new EstoquePanel();
        RelatoriosPanel relatoriosPanel = new RelatoriosPanel();
        FuncionariosPanel funcionariosPanel = new FuncionariosPanel();
        ClientesPanel clientesPanel = new ClientesPanel();
        FornecedoresPanel fornecedoresPanel = new FornecedoresPanel();
        UtilizadoresPanel utilizadoresPanel = new UtilizadoresPanel();
        RelatorioPdfPanel pdfPanel = new RelatorioPdfPanel();

        registrarPainel(PAINEL_INICIO, dashPanel);
        registrarPainel(PAINEL_VENDAS, vendasPanel);
        registrarPainel(PAINEL_PRODUTOS, produtosPanel);
        registrarPainel(PAINEL_ESTOQUE, estoquePanel);
        registrarPainel(PAINEL_RELATORIOS, relatoriosPanel);
        registrarPainel(PAINEL_FUNCIONARIOS, funcionariosPanel);
        registrarPainel(PAINEL_CLIENTES, clientesPanel);
        registrarPainel(PAINEL_FORNECEDORES, fornecedoresPanel);
        registrarPainel(PAINEL_UTILIZADORES, utilizadoresPanel);
        registrarPainel(PAINEL_RELATORIO_PDF, pdfPanel);

        appContainer.add(painelCentral, BorderLayout.CENTER);
    }

    private void registrarPainel(String id, JPanel painel) {
        mapaPaineis.put(id, painel);
        painelCentral.add(painel, id);
    }

    public JPanel getPainelPorId(String id) {
        return mapaPaineis.get(id);
    }

    public void fazerLoginSucesso(Utilizador utilizador) {
        this.utilizadorLogado = utilizador;
        SessaoUtilizador.iniciarSessao(utilizador);

        atualizarDadosUsuarioInterface();
        aplicarPermissoesPerfil();
        iniciarMonitoramentoSegundoPlano();

        rootCardLayout.show(rootPanel, ECRA_APLICACAO);
        mostrarPainel(PAINEL_INICIO);

        JPanel inicio = mapaPaineis.get(PAINEL_INICIO);
        if (inicio instanceof PainelRecarregavel) {
            ((PainelRecarregavel) inicio).recarregarDados();
        }
    }

    public void fazerLogout() {
        pararMonitoramentoSegundoPlano();
        SessaoUtilizador.encerrarSessao();
        this.utilizadorLogado = null;

        loginPanel.limparCampos();
        rootCardLayout.show(rootPanel, ECRA_LOGIN);
        loginPanel.focarCampoInicial();
    }

    private void atualizarDadosUsuarioInterface() {
        String username = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "utilizador";
        String inicial = username.substring(0, 1).toUpperCase();

        if (botaoAvatar != null) {
            botaoAvatar.setText(inicial);
            botaoAvatar.setToolTipText(username);
            botaoAvatar.repaint();
        }

        buscarVendasDoMesAssincrono();
    }

    private void buscarVendasDoMesAssincrono() {
        if (utilizadorLogado == null || utilizadorLogado.getFuncionario() == null) {
            if (labelMenuVendasMes != null) {
                labelMenuVendasMes.setText("0,00 MT");
            }
            return;
        }

        final Long idFunc = utilizadorLogado.getFuncionario().getIdFuncionario();
        AsyncTask.executar(() -> {
            try {
                return relatorioController.obterTotalVendasFuncionarioMes(idFunc);
            } catch (Exception e) {
                return 0.0;
            }
        }, total -> {
            if (labelMenuVendasMes != null) {
                labelMenuVendasMes.setText(String.format("%,.2f MT", total));
            }
        }, err -> {
            if (labelMenuVendasMes != null) {
                labelMenuVendasMes.setText("0,00 MT");
            }
        });
    }

    private void aplicarPermissoesPerfil() {
        Perfil perfil = (utilizadorLogado != null) ? utilizadorLogado.getPerfil() : Perfil.ADMIN;

        for (ItemMenuNavegacao item : itensNavegacao) {
            String p = item.getPainel();
            boolean visivel = true;

            if (perfil == Perfil.VENDEDOR) {
                if (p.equals(PAINEL_RELATORIOS) || p.equals(PAINEL_FUNCIONARIOS)
                        || p.equals(PAINEL_FORNECEDORES) || p.equals(PAINEL_UTILIZADORES)) {
                    visivel = false;
                }
            } else if (perfil == Perfil.GERENTE) {
                if (p.equals(PAINEL_FUNCIONARIOS) || p.equals(PAINEL_UTILIZADORES)) {
                    visivel = false;
                }
            }

            item.setVisible(visivel);
        }

        for (GrupoCategoriaSidebar grupo : gruposCategoriaSidebar) {
            grupo.atualizarVisibilidade();
        }

        for (JPanel painel : mapaPaineis.values()) {
            if (painel instanceof PainelRecarregavel) {
                ((PainelRecarregavel) painel).aplicarPermissoes(utilizadorLogado);
            }
        }
    }

    public void mostrarPainel(String nomePainel) {
        if (nomePainel == null || nomePainel.isBlank()) {
            throw new IllegalArgumentException("O nome do painel é obrigatório.");
        }

        this.painelAtivoId = nomePainel;
        layoutCentral.show(painelCentral, nomePainel);

        for (ItemMenuNavegacao item : itensNavegacao) {
            item.setAtivo(nomePainel.equalsIgnoreCase(item.getPainel()));
        }

        JPanel painelAtivo = mapaPaineis.get(nomePainel);
        if (painelAtivo instanceof PainelRecarregavel) {
            ((PainelRecarregavel) painelAtivo).recarregarDados();
        }
    }

    public void navegarPara(String nomePainel) {
        mostrarPainel(nomePainel);
    }

    private JPanel criarBarraNavegacaoSuperior() {
        PainelBarraNavegacao barra = new PainelBarraNavegacao(true);
        barra.setLayout(new BorderLayout(28, 0));
        barra.setBackground(UiStyle.HEADER_BG);
        barra.setPreferredSize(new Dimension(1280, 64));
        barra.setMinimumSize(new Dimension(600, 64));
        barra.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JLabel marca = new JLabel("Mercearia Kumbeza");
        marca.setForeground(UiStyle.BRAND_TEXT);
        marca.setFont(UiStyle.fonte(15, Font.BOLD));
        marca.setIcon(UiStyle.iconeDinamico(MaterialDesignS.STORE, 18, () -> UiStyle.BRAND_TEXT));
        marca.setIconTextGap(8);
        barra.add(marca, BorderLayout.WEST);

        this.menuBarItens = new JMenuBar();
        menuBarItens.setBackground(UiStyle.HEADER_BG);
        menuBarItens.setOpaque(false);
        menuBarItens.setBorderPainted(false);
        menuBarItens.setBorder(new EmptyBorder(0, 0, 0, 0));
        menuBarItens.setLayout(new BoxLayout(menuBarItens, BoxLayout.X_AXIS));

        itensNavegacao.clear();
        gruposCategoriaSidebar.clear();
        adicionarItemMenu(menuBarItens, itemNavegacao("Início", PAINEL_INICIO, MaterialDesignH.HOME_OUTLINE));
        adicionarItemMenu(menuBarItens, itemNavegacao("Vendas", PAINEL_VENDAS, MaterialDesignC.CART_OUTLINE));
        adicionarItemMenu(menuBarItens,
                itemNavegacao("Produtos", PAINEL_PRODUTOS, MaterialDesignP.PACKAGE_VARIANT_CLOSED));
        adicionarItemMenu(menuBarItens, itemNavegacao("Estoque", PAINEL_ESTOQUE, MaterialDesignW.WAREHOUSE));
        adicionarItemMenu(menuBarItens, itemNavegacao("Relatórios", PAINEL_RELATORIOS, MaterialDesignC.CHART_BOX));
        adicionarItemMenu(menuBarItens,
                itemNavegacao("Funcionários", PAINEL_FUNCIONARIOS, MaterialDesignB.BRIEFCASE_OUTLINE));
        adicionarItemMenu(menuBarItens, itemNavegacao("Clientes", PAINEL_CLIENTES, MaterialDesignA.ACCOUNT_GROUP));
        adicionarItemMenu(menuBarItens,
                itemNavegacao("Fornecedores", PAINEL_FORNECEDORES, MaterialDesignT.TRUCK_DELIVERY));
        adicionarItemMenu(menuBarItens,
                itemNavegacao("Utilizadores", PAINEL_UTILIZADORES, MaterialDesignS.SHIELD_ACCOUNT));
        menuBarItens.add(Box.createHorizontalGlue());

        JPanel centroWrapper = new JPanel(new GridBagLayout());
        centroWrapper.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        centroWrapper.add(menuBarItens, gbc);
        barra.add(centroWrapper, BorderLayout.CENTER);

        String username = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "admin";
        String inicial = username.substring(0, 1).toUpperCase();

        this.botaoAvatar = new BotaoAvatar(inicial);
        botaoAvatar.setToolTipText(username);
        botaoAvatar.addActionListener(e -> mostrarMenuUtilizador(botaoAvatar));

        JPanel direita = new JPanel(new GridBagLayout());
        direita.setOpaque(false);
        direita.add(botaoAvatar);
        barra.add(direita, BorderLayout.EAST);

        return barra;
    }

    private void adicionarItemMenu(JMenuBar menuBar, JMenu item) {
        if (menuBar.getComponentCount() > 0) {
            menuBar.add(Box.createRigidArea(new Dimension(2, 0)));
        }
        menuBar.add(item);
    }

    private JMenu itemNavegacao(String titulo, String painel, Ikon ikon) {
        ItemMenuNavegacao menu = new ItemMenuNavegacao(titulo, painel, ikon, painelAtivoId.equalsIgnoreCase(painel));
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarPainel(painel);
            }
        });
        itensNavegacao.add(menu);
        return menu;
    }

    private void mostrarMenuUtilizador(JButton origem) {
        JPopupMenu popup = new JPopupMenu();
        this.popupMenuUtilizadorAtivo = popup;
        popup.setOpaque(false);
        popup.setBorder(new EmptyBorder(0, 0, 0, 0));
        popup.setBackground(new Color(0, 0, 0, 0));

        PainelCardMenu cardMenu = new PainelCardMenu();
        cardMenu.setLayout(new BoxLayout(cardMenu, BoxLayout.Y_AXIS));
        cardMenu.setBorder(new EmptyBorder(8, 8, 8, 8));

        String username = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "admin";
        Perfil perfil = (utilizadorLogado != null && utilizadorLogado.getPerfil() != null)
                ? utilizadorLogado.getPerfil()
                : Perfil.ADMIN;

        JPanel blocoUtilizador = new JPanel();
        blocoUtilizador.setOpaque(false);
        blocoUtilizador.setLayout(new BoxLayout(blocoUtilizador, BoxLayout.Y_AXIS));
        blocoUtilizador.setBorder(new EmptyBorder(10, 12, 10, 12));
        blocoUtilizador.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.labelMenuUsername = new JLabel(username) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(UiStyle.TEXT);
                super.paintComponent(g);
            }
        };
        labelMenuUsername.setFont(UiStyle.fonte(14, Font.BOLD));
        labelMenuUsername.setForeground(UiStyle.TEXT);
        labelMenuUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.labelMenuPerfilChip = criarChipPerfil(perfil);
        labelMenuPerfilChip.setAlignmentX(Component.LEFT_ALIGNMENT);

        blocoUtilizador.add(labelMenuUsername);
        blocoUtilizador.add(Box.createRigidArea(new Dimension(0, 6)));
        blocoUtilizador.add(labelMenuPerfilChip);
        cardMenu.add(blocoUtilizador);

        cardMenu.add(criarDivisorMenu());

        SeletorToggle toggleTema = new SeletorToggle(
                "Escuro",
                "Claro",
                temaEscuroAtivo,
                escuro -> alternarTema(escuro));
        cardMenu.add(criarLinhaComControle("Tema", MaterialDesignT.THEME_LIGHT_DARK, toggleTema));

        SeletorToggle toggleNavegacao = new SeletorToggle(
                "Superior",
                "Lateral",
                navegacaoSuperiorAtiva,
                superior -> alternarOrientacaoNavegacao(superior));
        cardMenu.add(criarLinhaComControle("Navegação", MaterialDesignL.LAYERS, toggleNavegacao));

        cardMenu.add(criarLinhaClicavel("Alterar senha", MaterialDesignL.LOCK_OUTLINE, UiStyle.TEXT, false, () -> {
            popup.setVisible(false);
            abrirModalAlterarSenha();
        }));

        this.labelMenuVendasMes = new JLabel("0,00 MT") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(UiStyle.MUTED);
                super.paintComponent(g);
            }
        };
        labelMenuVendasMes.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelMenuVendasMes.setForeground(UiStyle.MUTED);
        cardMenu.add(criarLinhaComControle("As minhas vendas do mês", MaterialDesignC.CASH, labelMenuVendasMes));
        buscarVendasDoMesAssincrono();

        cardMenu.add(criarDivisorMenu());

        cardMenu.add(criarLinhaClicavel("Sair", MaterialDesignL.LOGOUT, UiStyle.DANGER_TEXT, true, () -> {
            popup.setVisible(false);
            fazerLogout();
        }));

        Dimension pref = cardMenu.getPreferredSize();
        Dimension dimFinal = new Dimension(LARGURA_MENU_UTILIZADOR, pref.height);
        cardMenu.setPreferredSize(dimFinal);
        cardMenu.setMinimumSize(dimFinal);
        cardMenu.setMaximumSize(dimFinal);

        popup.add(cardMenu);

        int x;
        int y;
        if (navegacaoSuperiorAtiva) {
            x = origem.getWidth() - LARGURA_MENU_UTILIZADOR + 8;
            y = origem.getHeight() + 9;
        } else {
            x = 0;
            y = -pref.height - 8;
        }
        popup.show(origem, x, y);
    }

    private void alternarTema(boolean escuro) {
        this.temaEscuroAtivo = escuro;
        UiStyle.alternarTema(escuro);
        SwingUtilities.updateComponentTreeUI(this);

        getContentPane().setBackground(UiStyle.BACKGROUND);
        rootPanel.setBackground(UiStyle.BACKGROUND);
        appContainer.setBackground(UiStyle.BACKGROUND);
        painelCentral.setBackground(UiStyle.BACKGROUND);

        loginPanel.aplicarTema();

        for (JPanel p : mapaPaineis.values()) {
            if (p instanceof PainelRecarregavel) {
                ((PainelRecarregavel) p).aplicarTema();
            }
            UiStyle.atualizarComponentesTema(p);
        }

        alternarOrientacaoNavegacao(this.navegacaoSuperiorAtiva);
        if (popupMenuUtilizadorAtivo != null && popupMenuUtilizadorAtivo.isVisible()) {
            UiStyle.atualizarComponentesTema(popupMenuUtilizadorAtivo);
            popupMenuUtilizadorAtivo.repaint();
        }
        repaint();
    }

    private void alternarOrientacaoNavegacao(boolean superior) {
        this.navegacaoSuperiorAtiva = superior;
        appContainer.remove(barraNavegacaoAtual);

        if (superior) {
            barraNavegacaoAtual = criarBarraNavegacaoSuperior();
            appContainer.add(barraNavegacaoAtual, BorderLayout.NORTH);
        } else {
            barraNavegacaoAtual = criarBarraNavegacaoLateral();
            appContainer.add(barraNavegacaoAtual, BorderLayout.WEST);
        }

        aplicarPermissoesPerfil();
        appContainer.revalidate();
        appContainer.repaint();
    }

    private JPanel criarBarraNavegacaoLateral() {
        PainelBarraNavegacao barra = new PainelBarraNavegacao(false);
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(UiStyle.HEADER_BG);
        barra.setPreferredSize(new Dimension(224, 720));
        barra.setMinimumSize(new Dimension(224, 400));
        barra.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel marca = new JLabel("Mercearia Kumbeza");
        marca.setForeground(UiStyle.BRAND_TEXT);
        marca.setFont(UiStyle.fonte(15, Font.BOLD));
        marca.setIcon(UiStyle.iconeDinamico(MaterialDesignS.STORE, 18, () -> UiStyle.BRAND_TEXT));
        marca.setIconTextGap(8);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.add(marca);
        barra.add(Box.createRigidArea(new Dimension(0, 18)));

        this.menuBarItens = new JMenuBar();
        menuBarItens.setBackground(UiStyle.HEADER_BG);
        menuBarItens.setOpaque(false);
        menuBarItens.setBorderPainted(false);
        menuBarItens.setBorder(new EmptyBorder(0, 0, 0, 0));
        menuBarItens.setLayout(new BoxLayout(menuBarItens, BoxLayout.Y_AXIS));
        menuBarItens.setAlignmentX(Component.LEFT_ALIGNMENT);

        itensNavegacao.clear();
        gruposCategoriaSidebar.clear();

        adicionarCategoriaMenuVertical(menuBarItens, "VISÃO GERAL", new JMenu[] {
                itemNavegacao("Início", PAINEL_INICIO, MaterialDesignH.HOME_OUTLINE),
                itemNavegacao("Relatórios", PAINEL_RELATORIOS, MaterialDesignC.CHART_BOX)
        });

        adicionarCategoriaMenuVertical(menuBarItens, "OPERAÇÕES", new JMenu[] {
                itemNavegacao("Vendas", PAINEL_VENDAS, MaterialDesignC.CART_OUTLINE),
                itemNavegacao("Produtos", PAINEL_PRODUTOS, MaterialDesignP.PACKAGE_VARIANT_CLOSED),
                itemNavegacao("Estoque", PAINEL_ESTOQUE, MaterialDesignW.WAREHOUSE)
        });

        adicionarCategoriaMenuVertical(menuBarItens, "PARCEIROS", new JMenu[] {
                itemNavegacao("Clientes", PAINEL_CLIENTES, MaterialDesignA.ACCOUNT_GROUP),
                itemNavegacao("Fornecedores", PAINEL_FORNECEDORES, MaterialDesignT.TRUCK_DELIVERY)
        });

        adicionarCategoriaMenuVertical(menuBarItens, "ADMINISTRAÇÃO", new JMenu[] {
                itemNavegacao("Funcionários", PAINEL_FUNCIONARIOS, MaterialDesignB.BRIEFCASE_OUTLINE),
                itemNavegacao("Utilizadores", PAINEL_UTILIZADORES, MaterialDesignS.SHIELD_ACCOUNT)
        });

        javax.swing.JScrollPane scrollSidebar = new javax.swing.JScrollPane(
                menuBarItens,
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollSidebar.setBorder(BorderFactory.createEmptyBorder());
        scrollSidebar.setOpaque(false);
        scrollSidebar.getViewport().setOpaque(false);
        scrollSidebar.getVerticalScrollBar().setUnitIncrement(14);
        scrollSidebar.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        scrollSidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.add(scrollSidebar);
        barra.add(Box.createVerticalGlue());

        String username = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "admin";
        String inicial = username.substring(0, 1).toUpperCase();

        this.botaoAvatar = new BotaoAvatar(inicial);
        botaoAvatar.setToolTipText(username);
        botaoAvatar.addActionListener(e -> mostrarMenuUtilizador(botaoAvatar));

        JPanel rodapeUsuario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rodapeUsuario.setOpaque(false);
        rodapeUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        rodapeUsuario.setMaximumSize(new Dimension(192, 42));
        rodapeUsuario.add(botaoAvatar);
        JLabel lblUserLateral = new JLabel(username);
        lblUserLateral.setFont(UiStyle.fonteSemiBold(13));
        lblUserLateral.setForeground(UiStyle.TEXT);
        lblUserLateral.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblUserLateral.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarMenuUtilizador(botaoAvatar);
            }
        });
        rodapeUsuario.add(lblUserLateral);
        barra.add(rodapeUsuario);

        return barra;
    }

    private void adicionarCategoriaMenuVertical(JMenuBar menuBar, String tituloCategoria, JMenu[] itens) {
        Component espacadorTopo = null;
        if (menuBar.getComponentCount() > 0) {
            espacadorTopo = Box.createRigidArea(new Dimension(0, 14));
            menuBar.add(espacadorTopo);
        }

        JLabel labelCategoria = new JLabel(tituloCategoria);
        labelCategoria.setFont(UiStyle.fonte(10, Font.BOLD));
        labelCategoria.setForeground(UiStyle.MUTED);
        labelCategoria.setBorder(new EmptyBorder(0, 10, 4, 0));
        labelCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuBar.add(labelCategoria);

        List<JMenu> listaItens = new ArrayList<>();
        for (int i = 0; i < itens.length; i++) {
            JMenu item = itens[i];
            if (i > 0) {
                menuBar.add(Box.createRigidArea(new Dimension(0, 3)));
            }
            Dimension dimItem = new Dimension(192, 36);
            item.setPreferredSize(dimItem);
            item.setMaximumSize(dimItem);
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuBar.add(item);
            listaItens.add(item);
        }

        gruposCategoriaSidebar.add(new GrupoCategoriaSidebar(espacadorTopo, labelCategoria, listaItens));
    }

    private static final class GrupoCategoriaSidebar {
        private final Component espacadorTopo;
        private final JLabel labelCategoria;
        private final List<JMenu> itens;

        GrupoCategoriaSidebar(Component espacadorTopo, JLabel labelCategoria, List<JMenu> itens) {
            this.espacadorTopo = espacadorTopo;
            this.labelCategoria = labelCategoria;
            this.itens = itens;
        }

        void atualizarVisibilidade() {
            boolean algumVisivel = false;
            for (JMenu item : itens) {
                if (item.isVisible()) {
                    algumVisivel = true;
                    break;
                }
            }
            labelCategoria.setVisible(algumVisivel);
            if (espacadorTopo != null) {
                espacadorTopo.setVisible(algumVisivel);
            }
        }
    }

    private static final class PainelBarraNavegacao extends JPanel {
        private static final long serialVersionUID = 1L;
        private final boolean superior;

        PainelBarraNavegacao(boolean superior) {
            this.superior = superior;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setColor(UiStyle.HEADER_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UiStyle.BORDER);
                if (superior) {
                    g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                } else {
                    g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private void abrirModalAlterarSenha() {
        JDialog dialog = new JDialog(this, "Alterar Senha", true);
        dialog.getContentPane().setBackground(UiStyle.SURFACE);
        dialog.setLayout(new BorderLayout());

        JPanel painelCorpo = new JPanel();
        painelCorpo.setOpaque(false);
        painelCorpo.setLayout(new BoxLayout(painelCorpo, BoxLayout.Y_AXIS));
        painelCorpo.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("Alterar a sua senha");
        titulo.setFont(UiStyle.fonte(16, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCorpo.add(titulo);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel lblAtual = new JLabel("Senha atual");
        lblAtual.setFont(UiStyle.fonte(12, Font.PLAIN));
        lblAtual.setForeground(UiStyle.MUTED);
        lblAtual.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCorpo.add(lblAtual);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 4)));

        JPasswordField txtAtual = new JPasswordField();
        txtAtual.setBackground(UiStyle.INPUT_BG);
        txtAtual.setForeground(UiStyle.TEXT);
        txtAtual.setCaretColor(UiStyle.TEXT);
        txtAtual.setPreferredSize(new Dimension(300, 36));
        txtAtual.setMaximumSize(new Dimension(300, 36));
        txtAtual.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCorpo.add(txtAtual);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel lblNova = new JLabel("Nova senha");
        lblNova.setFont(UiStyle.fonte(12, Font.PLAIN));
        lblNova.setForeground(UiStyle.MUTED);
        lblNova.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCorpo.add(lblNova);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 4)));

        JPasswordField txtNova = new JPasswordField();
        txtNova.setBackground(UiStyle.INPUT_BG);
        txtNova.setForeground(UiStyle.TEXT);
        txtNova.setCaretColor(UiStyle.TEXT);
        txtNova.setPreferredSize(new Dimension(300, 36));
        txtNova.setMaximumSize(new Dimension(300, 36));
        txtNova.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelCorpo.add(txtNova);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBotoes.setMaximumSize(new Dimension(300, 40));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(UiStyle.SECONDARY_BG);
        btnCancelar.setForeground(UiStyle.TEXT);
        btnCancelar.addActionListener(e -> dialog.dispose());

        JButton btnSalvar = new JButton("Salvar Senha");
        btnSalvar.setBackground(UiStyle.ACCENT);
        btnSalvar.setForeground(UiStyle.ACCENT_TEXT);
        btnSalvar.setFont(UiStyle.fonte(13, Font.BOLD));
        btnSalvar.addActionListener(e -> {
            String senhaAtual = new String(txtAtual.getPassword());
            String senhaNova = new String(txtNova.getPassword());

            if (senhaAtual.isEmpty() || senhaNova.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha a senha atual e a nova senha.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (utilizadorLogado == null) {
                dialog.dispose();
                return;
            }

            btnSalvar.setEnabled(false);
            btnSalvar.setText("A salvar...");

            AsyncTask.executar(() -> {
                String novoHash = utilizadorLogado.alterarSenha(senhaAtual, senhaNova);
                if (novoHash == null) {
                    throw new RuntimeException("Senha atual incorreta.");
                }
                utilizadorController.atualizar(utilizadorLogado);
                return true;
            }, ok -> {
                JOptionPane.showMessageDialog(dialog, "Senha alterada com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }, err -> {
                btnSalvar.setEnabled(true);
                btnSalvar.setText("Salvar Senha");
                JOptionPane.showMessageDialog(dialog, "Erro ao alterar senha: " + err.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            });
        });

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);
        painelCorpo.add(painelBotoes);

        dialog.add(painelCorpo, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void iniciarMonitoramentoSegundoPlano() {
        pararMonitoramentoSegundoPlano();
        executorMonitoramento = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Monitoramento-Dashboard");
            t.setDaemon(true);
            return t;
        });

        executorMonitoramento.scheduleAtFixedRate(() -> {
            if (utilizadorLogado != null) {
                buscarVendasDoMesAssincrono();
                JPanel inicio = mapaPaineis.get(PAINEL_INICIO);
                if (inicio instanceof PainelRecarregavel && inicio.isShowing()) {
                    ((PainelRecarregavel) inicio).recarregarDados();
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private void pararMonitoramentoSegundoPlano() {
        if (executorMonitoramento != null && !executorMonitoramento.isShutdown()) {
            executorMonitoramento.shutdownNow();
            executorMonitoramento = null;
        }
    }

    private JLabel criarChipPerfil(Perfil perfil) {
        String texto = (perfil != null) ? perfil.name() : "ADMIN";

        JLabel chip = new JLabel(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Color bgAtual = UiStyle.CHIP_YELLOW_BG;
                Color fgAtual = UiStyle.CHIP_YELLOW_FG;
                if (perfil == Perfil.GERENTE) {
                    bgAtual = UiStyle.CHIP_BLUE_BG;
                    fgAtual = UiStyle.CHIP_BLUE_FG;
                } else if (perfil == Perfil.VENDEDOR) {
                    bgAtual = UiStyle.CHIP_GREEN_BG;
                    fgAtual = UiStyle.CHIP_GREEN_FG;
                }
                setForeground(fgAtual);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgAtual);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(UiStyle.fonte(11, Font.BOLD));
        chip.setForeground(UiStyle.CHIP_YELLOW_FG);
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(2, 6, 2, 6));
        return chip;
    }

    private JPanel criarDivisorMenu() {
        JPanel divisor = new JPanel();
        divisor.setOpaque(false);
        divisor.setBorder(new EmptyBorder(4, 0, 4, 0));
        divisor.setLayout(new BorderLayout());
        Dimension dimDiv = new Dimension(LARGURA_MENU_UTILIZADOR - 16, 9);
        divisor.setPreferredSize(dimDiv);
        divisor.setMaximumSize(dimDiv);
        divisor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linha = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                setBackground(UiStyle.BORDER);
                super.paintComponent(g);
            }
        };
        linha.setBackground(UiStyle.BORDER);
        linha.setPreferredSize(new Dimension(LARGURA_MENU_UTILIZADOR - 16, 1));
        divisor.add(linha, BorderLayout.CENTER);
        return divisor;
    }

    private JPanel criarLinhaComControle(String rotulo, Ikon ikon, JComponent controle) {
        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(8, 12, 8, 12));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension dimLinha = new Dimension(LARGURA_MENU_UTILIZADOR - 16, 38);
        linha.setPreferredSize(dimLinha);
        linha.setMinimumSize(dimLinha);
        linha.setMaximumSize(dimLinha);

        JLabel label = new JLabel(rotulo) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(UiStyle.MUTED);
                super.paintComponent(g);
            }
        };
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);
        if (ikon != null) {
            label.setIcon(UiStyle.iconeDinamico(ikon, 15, () -> UiStyle.MUTED));
            label.setIconTextGap(6);
        }

        linha.add(label, BorderLayout.WEST);
        linha.add(controle, BorderLayout.EAST);
        return linha;
    }

    private JPanel criarLinhaClicavel(String texto, Ikon ikon, Color corTexto, boolean negrito, Runnable acao) {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(8, 12, 8, 12));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension dimLinha = new Dimension(LARGURA_MENU_UTILIZADOR - 16, 36);
        linha.setPreferredSize(dimLinha);
        linha.setMinimumSize(dimLinha);
        linha.setMaximumSize(dimLinha);
        linha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final boolean ehPerigo = (corTexto == UiStyle.DANGER_TEXT);
        JLabel label = new JLabel(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(ehPerigo ? UiStyle.DANGER_TEXT : UiStyle.TEXT);
                super.paintComponent(g);
            }
        };
        label.setFont(UiStyle.fonte(13, negrito ? Font.BOLD : Font.PLAIN));
        label.setForeground(ehPerigo ? UiStyle.DANGER_TEXT : UiStyle.TEXT);
        if (ikon != null) {
            label.setIcon(UiStyle.iconeDinamico(ikon, 15, () -> ehPerigo ? UiStyle.DANGER_TEXT : UiStyle.TEXT));
            label.setIconTextGap(6);
        }
        linha.add(label, BorderLayout.WEST);

        linha.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (acao != null) {
                    acao.run();
                }
            }
        });

        return linha;
    }

    private static final class BotaoAvatar extends JButton {
        private static final long serialVersionUID = 1L;

        public BotaoAvatar(String texto) {
            super(texto);
            setPreferredSize(new Dimension(30, 30));
            setMinimumSize(new Dimension(30, 30));
            setMaximumSize(new Dimension(30, 30));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(UiStyle.fonte(12, Font.BOLD));
            setForeground(UiStyle.TEXT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            setForeground(UiStyle.TEXT);
            g2.setColor(UiStyle.AVATAR_BG);
            g2.fill(new Ellipse2D.Float(1, 1, getWidth() - 2, getHeight() - 2));
            g2.setColor(UiStyle.ACCENT);
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.draw(new Ellipse2D.Float(1, 1, getWidth() - 3, getHeight() - 3));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class PainelCardMenu extends JPanel {
        private static final long serialVersionUID = 1L;

        public PainelCardMenu() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiStyle.SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
            g2.setColor(UiStyle.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class ItemMenuNavegacao extends JMenu {
        private static final long serialVersionUID = 1L;
        private final String painel;
        private final Ikon ikon;
        private boolean ativo;

        public ItemMenuNavegacao(String titulo, String painel, Ikon ikon, boolean ativoInicial) {
            super(titulo);
            this.painel = painel;
            this.ikon = ikon;
            this.ativo = ativoInicial;
            setOpaque(false);
            setBorderPainted(false);
            setFocusable(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 12, 8, 12));
            setIconTextGap(6);
            atualizarVisual();
        }

        public String getPainel() {
            return painel;
        }

        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
            atualizarVisual();
            repaint();
        }


        private void atualizarVisual() {
            setFont(UiStyle.fonte(13, ativo ? Font.BOLD : Font.PLAIN));

            
            Color corTextoEIcone = ativo ? new Color(0x1B, 0x22, 0x32) : UiStyle.MUTED;

            setForeground(corTextoEIcone);
            if (ikon != null) {
                
                setIcon(UiStyle.icone(ikon, 15, corTextoEIcone));
            }
        }


           @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (ativo) {
                g2.setColor(UiStyle.ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            }

            Insets insets = getInsets();
            int x = insets.left;
            int yMid = getHeight() / 2;

            Color corComponentes = ativo ? new Color(0x1B, 0x22, 0x32) : UiStyle.MUTED;
            g2.setColor(corComponentes);

            javax.swing.Icon currentIcon = getIcon();
            if (currentIcon != null) {
                int iconY = yMid - (currentIcon.getIconHeight() / 2);
                currentIcon.paintIcon(this, g2, x, iconY);
                x += currentIcon.getIconWidth() + getIconTextGap();
            }

            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int textY = yMid + (fm.getAscent() - fm.getDescent() - fm.getLeading()) / 2;
            g2.drawString(getText(), x, textY);

            g2.dispose();
        }
    }

    private static final class SeletorToggle extends JPanel {
        private static final long serialVersionUID = 1L;
        private final String opcao1;
        private final String opcao2;
        private boolean primeiraAtiva;
        private final Consumer<Boolean> onAlternado;

        public SeletorToggle(String opcao1, String opcao2, boolean primeiraAtiva, Consumer<Boolean> onAlternado) {
            this.opcao1 = opcao1;
            this.opcao2 = opcao2;
            this.primeiraAtiva = primeiraAtiva;
            this.onAlternado = onAlternado;

            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(new EmptyBorder(2, 2, 2, 2));

            JButton btn1 = criarBotaoOpcao(opcao1, true);
            JButton btn2 = criarBotaoOpcao(opcao2, false);

            add(btn1);
            add(Box.createRigidArea(new Dimension(2, 0)));
            add(btn2);
        }

        private JButton criarBotaoOpcao(String texto, boolean ehPrimeira) {
            JButton btn = new JButton(texto) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    boolean selecionado = (ehPrimeira && primeiraAtiva) || (!ehPrimeira && !primeiraAtiva);
                    setFont(UiStyle.fonte(11, selecionado ? Font.BOLD : Font.PLAIN));
                    setForeground(selecionado ? UiStyle.ACCENT_TEXT : UiStyle.TEXT);
                    if (selecionado) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(UiStyle.ACCENT);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };

            btn.setFont(UiStyle.fonte(11, Font.PLAIN));
            btn.setForeground(UiStyle.TEXT);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(4, 8, 4, 8));

            btn.addActionListener(e -> {
                if (primeiraAtiva != ehPrimeira) {
                    primeiraAtiva = ehPrimeira;
                    if (onAlternado != null) {
                        onAlternado.accept(primeiraAtiva);
                    }
                    repaint();
                }
            });

            return btn;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiStyle.TAB_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.setColor(UiStyle.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
