package view;

import controller.LoginController;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import model.vo.Utilizador;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int LARGURA_JANELA = 1280;
    private static final int ALTURA_JANELA = 720;

    private final LoginPanel loginPanel;

    public LoginFrame() {
        this(new LoginController());
    }

    public LoginFrame(LoginController loginController) {
        super("Gestão de Estabelecimento - Login");
        UiStyle.instalarTemaEscuro();

        this.loginPanel = new LoginPanel(loginController, this::onLoginSucesso);
        setContentPane(loginPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setMinimumSize(new Dimension(520, 560));
        setLocationRelativeTo(null);
    }

    private void onLoginSucesso(Utilizador utilizador) {
        MainFrame mainFrame = new MainFrame(utilizador);
        mainFrame.setVisible(true);
        dispose();
    }

    public javax.swing.JTextField getCampoUsername() {
        return loginPanel.getCampoUsername();
    }

    public javax.swing.JPasswordField getCampoSenha() {
        return loginPanel.getCampoSenha();
    }

    public javax.swing.JButton getBotaoEntrar() {
        return loginPanel.getBotaoEntrar();
    }

    public static void abrir() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
