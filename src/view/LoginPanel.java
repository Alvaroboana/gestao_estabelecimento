package view;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import model.bo.LoginBO;
import model.exception.NegocioException;
import model.vo.Utilizador;

/**
 * Painel de Login em Java Swing fiel ao design HTML "Login (escuro)".
 * Projetado para ser hospedado no Single JFrame principal.
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int LARGURA_CARD = 400;
    private static final int PADDING_CARD = 36;
    private static final int GAP_CARD = 14;
    private static final int LARGURA_INTERNA_CARD = LARGURA_CARD - (PADDING_CARD * 2); // 328px
    private static final int ALTURA_INPUT = 38;
    private static final int ALTURA_BOTAO = 42;
    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final String MENSAGEM_ERRO_PADRAO = "Credenciais inválidas ou funcionário inactivo.";

    private final LoginBO loginBO;
    private final JTextField campoUsername;
    private final JPasswordField campoSenha;
    private final JButton botaoEntrar;
    private final PainelArredondado painelErro;
    private final JLabel labelErro;
    private Consumer<Utilizador> onLoginSucesso;

    public LoginPanel() {
        this(new LoginBO(), null);
    }

    public LoginPanel(LoginBO loginBO, Consumer<Utilizador> onLoginSucesso) {
        this.loginBO = loginBO != null ? loginBO : new LoginBO();
        this.onLoginSucesso = onLoginSucesso;

        this.campoUsername = criarCampoTexto();
        this.campoSenha = criarCampoSenha();
        this.labelErro = criarLabelErro();
        this.painelErro = criarPainelErro(labelErro);
        this.botaoEntrar = criarBotaoEntrar();

        construirInterface();
        vincularEventos();
    }

    public void setOnLoginSucesso(Consumer<Utilizador> onLoginSucesso) {
        this.onLoginSucesso = onLoginSucesso;
    }

    public void limparCampos() {
        campoUsername.setText("");
        campoSenha.setText("");
        ocultarErro();
        botaoEntrar.setEnabled(true);
        botaoEntrar.setText("Entrar");
    }

    public void focarCampoInicial() {
        campoUsername.requestFocusInWindow();
    }

    public JTextField getCampoUsername() {
        return campoUsername;
    }

    public JPasswordField getCampoSenha() {
        return campoSenha;
    }

    public JButton getBotaoEntrar() {
        return botaoEntrar;
    }

    private void construirInterface() {
        setLayout(new GridBagLayout());
        setBackground(UiStyle.BACKGROUND);

        PainelArredondado card = new PainelArredondado(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(PADDING_CARD, PADDING_CARD, PADDING_CARD, PADDING_CARD));

        JLabel labelMarca = new JLabel("ESTABELECIMENTO");
        labelMarca.setFont(UiStyle.fonte(14, Font.BOLD));
        labelMarca.setForeground(UiStyle.ACCENT);
        alinharEsquerda(labelMarca);

        JLabel labelTitulo = new JLabel("Bem-vindo de volta");
        labelTitulo.setFont(UiStyle.fonte(26, Font.BOLD));
        labelTitulo.setForeground(UiStyle.TEXT);
        alinharEsquerda(labelTitulo);

        JLabel labelUsername = criarLabelCampo("Username");
        JLabel labelSenha = criarLabelCampo("Senha");

        card.add(labelMarca);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(labelTitulo);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(labelUsername);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(campoUsername);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(labelSenha);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(campoSenha);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(painelErro);
        card.add(Box.createRigidArea(new Dimension(0, GAP_CARD)));
        card.add(botaoEntrar);

        Dimension dimCard = new Dimension(LARGURA_CARD, card.getPreferredSize().height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);

        add(card);
    }

    private void vincularEventos() {
        botaoEntrar.addActionListener(e -> autenticar());
        campoUsername.addActionListener(e -> campoSenha.requestFocusInWindow());
        campoSenha.addActionListener(e -> autenticar());
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();
        configurarCampoComum(campo);
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Digite o seu utilizador");
        return campo;
    }

    private JPasswordField criarCampoSenha() {
        JPasswordField campo = new JPasswordField();
        configurarCampoComum(campo);
        campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
        return campo;
    }

    private void configurarCampoComum(JTextField campo) {
        fixarDimensao(campo, LARGURA_INTERNA_CARD, ALTURA_INPUT);
        campo.setFont(UiStyle.fonte(13, Font.PLAIN));
        campo.setForeground(UiStyle.TEXT);
        campo.setCaretColor(UiStyle.TEXT);
        campo.setBackground(UiStyle.INPUT_BG);
        campo.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 8;"
                + "borderColor: #3A4256;"
                + "focusedBorderColor: #F5B638;"
                + "background: #12151C;"
                + "placeholderForeground: #A3ADC2;");
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        alinharEsquerda(campo);
    }

    private JLabel criarLabelCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);
        alinharEsquerda(label);
        return label;
    }

    private JLabel criarLabelErro() {
        JLabel label = new JLabel(MENSAGEM_ERRO_PADRAO);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.ERROR_TEXT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private PainelArredondado criarPainelErro(JLabel label) {
        PainelArredondado painel = new PainelArredondado(UiStyle.ERROR_BG, null, RAIO_COMPONENTE);
        painel.setLayout(new BoxLayout(painel, BoxLayout.X_AXIS));
        painel.setBorder(new EmptyBorder(10, 12, 10, 12));
        fixarDimensao(painel, LARGURA_INTERNA_CARD, ALTURA_INPUT);
        painel.add(label);
        painel.setVisible(false);
        alinharEsquerda(painel);
        return painel;
    }

    private JButton criarBotaoEntrar() {
        JButton botao = new JButton("Entrar") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color corFundo = isEnabled()
                        ? (getModel().isPressed() ? UiStyle.ACCENT.darker() : UiStyle.ACCENT)
                        : UiStyle.SECONDARY_BG;
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RAIO_COMPONENTE, RAIO_COMPONENTE));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        fixarDimensao(botao, LARGURA_INTERNA_CARD, ALTURA_BOTAO);
        botao.setFont(UiStyle.fonte(14, Font.BOLD));
        botao.setForeground(UiStyle.ACCENT_TEXT);
        botao.setContentAreaFilled(false);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setOpaque(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        alinharEsquerda(botao);
        return botao;
    }

    private void alinharEsquerda(JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void fixarDimensao(JComponent componente, int largura, int altura) {
        Dimension dimensao = new Dimension(largura, altura);
        componente.setPreferredSize(dimensao);
        componente.setMinimumSize(dimensao);
        componente.setMaximumSize(dimensao);
    }

    private void autenticar() {
        final String username = campoUsername.getText() != null ? campoUsername.getText().trim() : "";
        final String senha = new String(campoSenha.getPassword());

        if (username.isEmpty() || senha.isEmpty()) {
            exibirErro("Por favor, preencha o utilizador e a senha.");
            return;
        }

        ocultarErro();
        botaoEntrar.setEnabled(false);
        botaoEntrar.setText("A autenticar...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Utilizador, Void> worker = new SwingWorker<>() {
            @Override
            protected Utilizador doInBackground() throws Exception {
                return loginBO.autenticar(username, senha);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                botaoEntrar.setEnabled(true);
                botaoEntrar.setText("Entrar");

                try {
                    Utilizador utilizador = get();
                    ocultarErro();
                    if (onLoginSucesso != null) {
                        onLoginSucesso.accept(utilizador);
                    }
                } catch (Exception e) {
                    Throwable causa = e.getCause();
                    if (causa instanceof NegocioException) {
                        exibirErro(causa.getMessage());
                    } else if (causa != null && causa.getMessage() != null && causa.getMessage().contains("banco")) {
                        exibirErro("Não foi possível conectar ao banco de dados.");
                    } else {
                        exibirErro(MENSAGEM_ERRO_PADRAO);
                    }
                }
            }
        };

        worker.execute();
    }

    private void exibirErro(String mensagem) {
        labelErro.setText(mensagem);
        painelErro.setVisible(true);
        revalidate();
        repaint();
    }

    private void ocultarErro() {
        if (painelErro.isVisible()) {
            painelErro.setVisible(false);
            revalidate();
            repaint();
        }
    }

    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        campoUsername.setBackground(UiStyle.INPUT_BG);
        campoUsername.setForeground(UiStyle.TEXT);
        campoSenha.setBackground(UiStyle.INPUT_BG);
        campoSenha.setForeground(UiStyle.TEXT);
        repaint();
    }

    private static final class PainelArredondado extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Color corFundo;
        private final Color corBorda;
        private final int raio;

        public PainelArredondado(Color corFundo, Color corBorda, int raio) {
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (corFundo != null) {
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), raio, raio));
            }

            if (corBorda != null) {
                g2.setColor(corBorda);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, raio, raio));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
