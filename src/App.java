import com.formdev.flatlaf.FlatLightLaf;
import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginFrame;
import view.MainFrame;
import view.UiStyle;

public class App {
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[AVISO] Ambiente sem interface grafica. Nao foi possivel abrir o LoginFrame.");
            return;
        }
        UiStyle.instalarTemaEscuro();
        abrir();
        
    }

     public static void abrir() {
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}
