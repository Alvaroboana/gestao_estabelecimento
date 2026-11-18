package util;
import javax.swing.*;
public final class AvisoCarregamento {
    private AvisoCarregamento() {}
    public static void mostrar(JComponent painel, Throwable erro) {
        painel.setToolTipText("Falha na actualização. Os dados apresentados podem estar desactualizados.");
        Long anterior = (Long) painel.getClientProperty("ultimoAvisoCarregamento");
        long agora = System.currentTimeMillis();
        if (anterior != null && agora - anterior < 3000) return;
        painel.putClientProperty("ultimoAvisoCarregamento", agora);
        JOptionPane.showMessageDialog(painel,
            "Não foi possível carregar os dados. Os dados anteriores foram mantidos.\nTente actualizar novamente.\n" + erro.getMessage(),
            "Falha na actualização", JOptionPane.WARNING_MESSAGE);
    }
}
