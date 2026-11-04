package testes;

import java.awt.GraphicsEnvironment;
import view.MainFrame;

public class TesteMainFrame {
    public static void main(String[] args) {
        System.out.println("========== Teste MainFrame ==========");
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[AVISO] Teste visual ignorado: ambiente sem interface grafica.");
            return;
        }

        MainFrame frame = new MainFrame();
        try {
            verificar("Estabelecimento".equals(frame.getTitle()), "titulo da janela");
            verificar(frame.getPainelCentral() != null, "painel central criado");
            // Na arquitectura Single JFrame, o contentPane tem apenas 1 componente (rootPanel com CardLayout)
            verificar(frame.getContentPane().getComponentCount() >= 1, "navegacao superior e painel central");
            frame.mostrarPainel("inicio");
            System.out.println("=== Teste MainFrame concluido com sucesso ===");
        } finally {
            frame.dispose();
        }
    }

    private static void verificar(boolean condicao, String descricao) {
        if (!condicao) {
            throw new AssertionError("Falha: " + descricao);
        }
        System.out.println("[OK] " + descricao);
    }
}
