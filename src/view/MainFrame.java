package view;

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
import model.bo.RelatorioBO;
import model.bo.UtilizadorBO;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Janela principal única (Single JFrame) da aplicação.
 * Hospeda tanto a tela de login (ECRA_LOGIN) quanto a área de trabalho do sistema (ECRA_APLICACAO),
 * eliminando a destruição de janelas, aplicando controle de perfis (ADMIN/GERENTE/VENDEDOR)
 * e suportando alternância em tempo real entre Tema Escuro e Tema Claro.
 */
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
    private static final int LARGURA_MENU_UTILIZADOR = 270;

    private final CardLayout rootCardLayout;
    private final JPanel rootPanel;

    private final LoginPanel loginPanel;
    private final JPanel appContainer;
    private final JPanel painelCentral;
    private final CardLayout layoutCentral;

    private final Map<String, JPanel> mapaPaineis = new HashMap<>();
    private final List<ItemMenuNavegacao> itensNavegacao = new ArrayList<>();

    private JPanel barraNavegacaoAtual;
    private JMenuBar menuBarItens;
    private BotaoAvatar botaoAvatar;
    private JLabel labelMenuUsername;
    private JLabel labelMenuPerfilChip;
    private JLabel labelMenuVendasMes;

    private Utilizador utilizadorLogado;
    private boolean temaEscuroAtivo = true;
    private boolean navegacaoSuperiorAtiva = true;

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

        // Instancia os painéis principais
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

    public void fazerLoginSucesso(Utilizador utilizador) {
        this.utilizadorLogado = utilizador;
        SessaoUtilizador.iniciarSessao(utilizador);

        atualizarDadosUsuarioInterface();
        aplicarPermissoesPerfil();
        iniciarMonitoramentoSegundoPlano();

        rootCardLayout.show(rootPanel, ECRA_APLICACAO);
        mostrarPainel(PAINEL_INICIO);

        // Recarrega o dashboard
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
                return new RelatorioBO().obterTotalVendasFuncionarioMes(idFunc);
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
                // Vendedor acessa apenas Início, Vendas, Produtos, Estoque e Clientes
                if (p.equals(PAINEL_RELATORIOS) || p.equals(PAINEL_FUNCIONARIOS)
                        || p.equals(PAINEL_FORNECEDORES) || p.equals(PAINEL_UTILIZADORES)) {
                    visivel = false;
                }
            } else if (perfil == Perfil.GERENTE) {
                // Gerente acessa tudo exceto Funcionários e Utilizadores
                if (p.equals(PAINEL_FUNCIONARIOS) || p.equals(PAINEL_UTILIZADORES)) {
                    visivel = false;
                }
            }

            item.setVisible(visivel);
        }

        // Notifica todos os painéis para ajustarem seus botões e ações
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
        JPanel barra = new JPanel(new BorderLayout(28, 0));
        barra.setBackground(UiStyle.HEADER_BG);
        barra.setPreferredSize(new Dimension(1280, 64));
        barra.setMinimumSize(new Dimension(600, 64));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                BorderFactory.createEmptyBorder(0, 28, 0, 28)
        ));

        JLabel marca = new JLabel("Estabelecimento");
        marca.setForeground(UiStyle.ACCENT);
        marca.setFont(UiStyle.fonte(15, Font.BOLD));
        barra.add(marca, BorderLayout.WEST);

        this.menuBarItens = new JMenuBar();
        menuBarItens.setOpaque(false);
        menuBarItens.setBorderPainted(false);
        menuBarItens.setBorder(new EmptyBorder(0, 0, 0, 0));
        menuBarItens.setLayout(new BoxLayout(menuBarItens, BoxLayout.X_AXIS));

        itensNavegacao.clear();
        adicionarItemMenu(menuBarItens, itemNavegacao("Início", PAINEL_INICIO));
        adicionarItemMenu(menuBarItens, itemNavegacao("Vendas", PAINEL_VENDAS));
        adicionarItemMenu(menuBarItens, itemNavegacao("Produtos", PAINEL_PRODUTOS));
        adicionarItemMenu(menuBarItens, itemNavegacao("Estoque", PAINEL_ESTOQUE));
        adicionarItemMenu(menuBarItens, itemNavegacao("Relatórios", PAINEL_RELATORIOS));
        adicionarItemMenu(menuBarItens, itemNavegacao("Funcionários", PAINEL_FUNCIONARIOS));
        adicionarItemMenu(menuBarItens, itemNavegacao("Clientes", PAINEL_CLIENTES));
        adicionarItemMenu(menuBarItens, itemNavegacao("Fornecedores", PAINEL_FORNECEDORES));
        adicionarItemMenu(menuBarItens, itemNavegacao("Utilizadores", PAINEL_UTILIZADORES));
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

    private JMenu itemNavegacao(String titulo, String painel) {
        ItemMenuNavegacao menu = new ItemMenuNavegacao(titulo, painel, PAINEL_INICIO.equals(painel));
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

        // Cabeçalho do utilizador
        JPanel blocoUtilizador = new JPanel();
        blocoUtilizador.setOpaque(false);
        blocoUtilizador.setLayout(new BoxLayout(blocoUtilizador, BoxLayout.Y_AXIS));
        blocoUtilizador.setBorder(new EmptyBorder(10, 12, 10, 12));
        blocoUtilizador.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.labelMenuUsername = new JLabel(username);
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

        // Seletor Tema: Escuro | Claro
        SeletorToggle toggleTema = new SeletorToggle(
                "Escuro",
                "Claro",
                temaEscuroAtivo,
                escuro -> alternarTema(escuro)
        );
        cardMenu.add(criarLinhaComControle("Tema", toggleTema));

        // Seletor Navegação: Superior | Lateral
        SeletorToggle toggleNavegacao = new SeletorToggle(
                "Superior",
                "Lateral",
                navegacaoSuperiorAtiva,
                superior -> alternarOrientacaoNavegacao(superior)
        );
        cardMenu.add(criarLinhaComControle("Navegação", toggleNavegacao));

        // Alterar Senha
        cardMenu.add(criarLinhaClicavel("Alterar senha", UiStyle.TEXT, false, () -> {
            popup.setVisible(false);
            abrirModalAlterarSenha();
        }));

        // Linha: As minhas vendas do mês
        this.labelMenuVendasMes = new JLabel("0,00 MT");
        labelMenuVendasMes.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelMenuVendasMes.setForeground(UiStyle.MUTED);
        cardMenu.add(criarLinhaComControle("As minhas vendas do mês", labelMenuVendasMes));
        buscarVendasDoMesAssincrono();

        cardMenu.add(criarDivisorMenu());

        // Linha Sair
        cardMenu.add(criarLinhaClicavel("Sair", UiStyle.ERROR_TEXT, true, () -> {
            popup.setVisible(false);
            fazerLogout();
        }));

        Dimension pref = cardMenu.getPreferredSize();
        Dimension dimFinal = new Dimension(LARGURA_MENU_UTILIZADOR, pref.height);
        cardMenu.setPreferredSize(dimFinal);
        cardMenu.setMinimumSize(dimFinal);
        cardMenu.setMaximumSize(dimFinal);

        popup.add(cardMenu);

        int x = origem.getWidth() - LARGURA_MENU_UTILIZADOR + 8;
        int y = origem.getHeight() + 9;
        popup.show(origem, x, y);
    }

    private void alternarTema(boolean escuro) {
        this.temaEscuroAtivo = escuro;
        UiStyle.alternarTema(escuro);

        getContentPane().setBackground(UiStyle.BACKGROUND);
        rootPanel.setBackground(UiStyle.BACKGROUND);
        appContainer.setBackground(UiStyle.BACKGROUND);
        painelCentral.setBackground(UiStyle.BACKGROUND);

        if (barraNavegacaoAtual != null) {
            barraNavegacaoAtual.setBackground(UiStyle.HEADER_BG);
        }

        loginPanel.aplicarTema();

        for (JPanel p : mapaPaineis.values()) {
            if (p instanceof PainelRecarregavel) {
                ((PainelRecarregavel) p).aplicarTema();
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
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
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(UiStyle.HEADER_BG);
        barra.setPreferredSize(new Dimension(200, 720));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UiStyle.BORDER),
                BorderFactory.createEmptyBorder(20, 16, 20, 16)
        ));

        JLabel marca = new JLabel("Estabelecimento");
        marca.setForeground(UiStyle.ACCENT);
        marca.setFont(UiStyle.fonte(15, Font.BOLD));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.add(marca);
        barra.add(Box.createRigidArea(new Dimension(0, 24)));

        this.menuBarItens = new JMenuBar();
        menuBarItens.setOpaque(false);
        menuBarItens.setBorderPainted(false);
        menuBarItens.setLayout(new BoxLayout(menuBarItens, BoxLayout.Y_AXIS));
        menuBarItens.setAlignmentX(Component.LEFT_ALIGNMENT);

        itensNavegacao.clear();
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Início", PAINEL_INICIO));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Vendas", PAINEL_VENDAS));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Produtos", PAINEL_PRODUTOS));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Estoque", PAINEL_ESTOQUE));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Relatórios", PAINEL_RELATORIOS));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Funcionários", PAINEL_FUNCIONARIOS));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Clientes", PAINEL_CLIENTES));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Fornecedores", PAINEL_FORNECEDORES));
        adicionarItemMenuVertical(menuBarItens, itemNavegacao("Utilizadores", PAINEL_UTILIZADORES));

        barra.add(menuBarItens);
        barra.add(Box.createVerticalGlue());

        String username = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "admin";
        String inicial = username.substring(0, 1).toUpperCase();

        this.botaoAvatar = new BotaoAvatar(inicial);
        botaoAvatar.setToolTipText(username);
        botaoAvatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        botaoAvatar.addActionListener(e -> mostrarMenuUtilizador(botaoAvatar));
        barra.add(botaoAvatar);

        return barra;
    }

    private void adicionarItemMenuVertical(JMenuBar menuBar, JMenu item) {
        if (menuBar.getComponentCount() > 0) {
            menuBar.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        item.setMaximumSize(new Dimension(170, 36));
        menuBar.add(item);
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
        painelCorpo.add(titulo);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 16)));

        JLabel lblAtual = new JLabel("Senha atual");
        lblAtual.setFont(UiStyle.fonte(12, Font.PLAIN));
        lblAtual.setForeground(UiStyle.MUTED);
        painelCorpo.add(lblAtual);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 4)));

        JPasswordField txtAtual = new JPasswordField();
        txtAtual.setBackground(UiStyle.INPUT_BG);
        txtAtual.setForeground(UiStyle.TEXT);
        txtAtual.setMaximumSize(new Dimension(300, 36));
        painelCorpo.add(txtAtual);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel lblNova = new JLabel("Nova senha");
        lblNova.setFont(UiStyle.fonte(12, Font.PLAIN));
        lblNova.setForeground(UiStyle.MUTED);
        painelCorpo.add(lblNova);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 4)));

        JPasswordField txtNova = new JPasswordField();
        txtNova.setBackground(UiStyle.INPUT_BG);
        txtNova.setForeground(UiStyle.TEXT);
        txtNova.setMaximumSize(new Dimension(300, 36));
        painelCorpo.add(txtNova);
        painelCorpo.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);

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
                JOptionPane.showMessageDialog(dialog, "Preencha a senha atual e a nova senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                new UtilizadorBO().atualizar(utilizadorLogado);
                return true;
            }, ok -> {
                JOptionPane.showMessageDialog(dialog, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }, err -> {
                btnSalvar.setEnabled(true);
                btnSalvar.setText("Salvar Senha");
                JOptionPane.showMessageDialog(dialog, "Erro ao alterar senha: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

        // Executa a cada 30 segundos
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
        Color bg = UiStyle.CHIP_YELLOW_BG;
        Color fg = UiStyle.CHIP_YELLOW_FG;

        if (perfil == Perfil.GERENTE) {
            bg = UiStyle.CHIP_BLUE_BG;
            fg = UiStyle.CHIP_BLUE_FG;
        } else if (perfil == Perfil.VENDEDOR) {
            bg = UiStyle.CHIP_GREEN_BG;
            fg = UiStyle.CHIP_GREEN_FG;
        }

        JLabel chip = new JLabel(texto) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(UiStyle.fonte(11, Font.BOLD));
        chip.setForeground(fg);
        chip.setBackground(bg);
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(2, 6, 2, 6));
        return chip;
    }

    private JPanel criarDivisorMenu() {
        JPanel divisor = new JPanel();
        divisor.setOpaque(false);
        divisor.setBorder(new EmptyBorder(4, 0, 4, 0));
        divisor.setLayout(new BorderLayout());
        divisor.setMaximumSize(new Dimension(LARGURA_MENU_UTILIZADOR, 9));
        divisor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linha = new JPanel();
        linha.setBackground(UiStyle.BORDER);
        linha.setPreferredSize(new Dimension(LARGURA_MENU_UTILIZADOR, 1));
        divisor.add(linha, BorderLayout.CENTER);
        return divisor;
    }

    private JPanel criarLinhaComControle(String rotulo, JComponent controle) {
        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(8, 12, 8, 12));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(LARGURA_MENU_UTILIZADOR, 38));

        JLabel label = new JLabel(rotulo);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);

        linha.add(label, BorderLayout.WEST);
        linha.add(controle, BorderLayout.EAST);
        return linha;
    }

    private JPanel criarLinhaClicavel(String texto, Color corTexto, boolean negrito, Runnable acao) {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(8, 12, 8, 12));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(LARGURA_MENU_UTILIZADOR, 36));
        linha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(texto);
        label.setFont(UiStyle.fonte(13, negrito ? Font.BOLD : Font.PLAIN));
        label.setForeground(corTexto);
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

    // Componentes internos reutilizáveis
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
            g2.setColor(UiStyle.AVATAR_BG);
            g2.fill(new Ellipse2D.Float(1, 1, getWidth() - 2, getHeight() - 2));
            g2.setColor(UiStyle.ACCENT);
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
        private boolean ativo;

        public ItemMenuNavegacao(String titulo, String painel, boolean ativoInicial) {
            super(titulo);
            this.painel = painel;
            this.ativo = ativoInicial;
            setOpaque(false);
            setBorderPainted(false);
            setFocusable(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 12, 8, 12));
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
            setForeground(ativo ? UiStyle.ACCENT_TEXT : UiStyle.MUTED);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (ativo) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UiStyle.ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
            }
            super.paintComponent(g);
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
            g2.setColor(UiStyle.SECONDARY_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
