package testes;

import java.awt.GraphicsEnvironment;
import model.bo.LoginBO;
import model.bo.UtilizadorBO;
import model.dao.UtilizadorDAO;
import view.LoginFrame;

public class TesteLoginFrame {
    public static void main(String[] args) {
        System.out.println("========== Teste LoginFrame ==========");
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[AVISO] Teste visual ignorado: ambiente sem interface grafica.");
            return;
        }

        LoginFrame frame = new LoginFrame(new LoginBO(new UtilizadorBO(new UtilizadorDAO())));
        try {
            verificar(frame.getTitle() != null && !frame.getTitle().isEmpty(), "titulo");
            verificar(frame.getCampoUsername() != null, "campo username");
            verificar(frame.getCampoSenha() != null, "campo senha");
            verificar(frame.getBotaoEntrar() != null, "botao entrar");
            System.out.println("=== Teste LoginFrame concluido com sucesso ===");
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
