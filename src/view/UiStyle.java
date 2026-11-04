package view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Centralizador de identidade visual do sistema.
 * Suporta alternância dinâmica entre Modo Escuro e Modo Claro em tempo de execução.
 */
public final class UiStyle {

    // Modo Escuro (Cores padrão)
    private static final Color ESCURO_BACKGROUND = new Color(0x12, 0x15, 0x1C);
    private static final Color ESCURO_HEADER_BG = new Color(0x18, 0x1C, 0x26);
    private static final Color ESCURO_SURFACE = new Color(0x1B, 0x20, 0x30);
    private static final Color ESCURO_BORDER = new Color(0x2A, 0x30, 0x40);
    private static final Color ESCURO_INPUT_BORDER = new Color(0x3A, 0x42, 0x56);
    private static final Color ESCURO_INPUT_BG = new Color(0x12, 0x15, 0x1C);
    private static final Color ESCURO_TEXT = new Color(0xE8, 0xEC, 0xF4);
    private static final Color ESCURO_MUTED = new Color(0xA3, 0xAD, 0xC2);
    private static final Color ESCURO_ERROR_BG = new Color(0x4F, 0x1B, 0x1B);
    private static final Color ESCURO_ERROR_TEXT = new Color(0xFF, 0x9C, 0x9C);
    private static final Color ESCURO_SECONDARY_BG = new Color(0x23, 0x2A, 0x3B);
    private static final Color ESCURO_TABLE_ROW_BORDER = new Color(0x24, 0x2B, 0x3B);
    private static final Color ESCURO_AVATAR_BG = new Color(0x2A, 0x30, 0x40);
    private static final Color ESCURO_CHIP_YELLOW_BG = new Color(0x4A, 0x3A, 0x0F);
    private static final Color ESCURO_CHIP_YELLOW_FG = new Color(0xFF, 0xD3, 0x6B);
    private static final Color ESCURO_CHIP_GREEN_BG = new Color(0x12, 0x40, 0x2A);
    private static final Color ESCURO_CHIP_GREEN_FG = new Color(0x7F, 0xE3, 0xA8);
    private static final Color ESCURO_CHIP_BLUE_BG = new Color(0x17, 0x34, 0x5E);
    private static final Color ESCURO_CHIP_BLUE_FG = new Color(0x9C, 0xC4, 0xFF);
    private static final Color ESCURO_DANGER_BORDER = new Color(0x6B, 0x2C, 0x2C);
    private static final Color ESCURO_TABLE_SELECTION_BG = new Color(0x2A, 0x33, 0x47);

    // Modo Claro (Padrão editorial/dourado harmonioso)
    private static final Color CLARO_BACKGROUND = new Color(0xF4, 0xF6, 0xFA);
    private static final Color CLARO_HEADER_BG = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_SURFACE = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_BORDER = new Color(0xDD, 0xE2, 0xEA);
    private static final Color CLARO_INPUT_BORDER = new Color(0xC5, 0xCE, 0xDB);
    private static final Color CLARO_INPUT_BG = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_TEXT = new Color(0x1B, 0x20, 0x30);
    private static final Color CLARO_MUTED = new Color(0x5A, 0x64, 0x78);
    private static final Color CLARO_ERROR_BG = new Color(0xFD, 0xE8, 0xE8);
    private static final Color CLARO_ERROR_TEXT = new Color(0x9B, 0x1C, 0x1C);
    private static final Color CLARO_SECONDARY_BG = new Color(0xEE, 0xF2, 0xF6);
    private static final Color CLARO_TABLE_ROW_BORDER = new Color(0xE6, 0xE9, 0xF0);
    private static final Color CLARO_AVATAR_BG = new Color(0xE2, 0xE8, 0xF0);
    private static final Color CLARO_CHIP_YELLOW_BG = new Color(0xFE, 0xF0, 0x8A);
    private static final Color CLARO_CHIP_YELLOW_FG = new Color(0x71, 0x3F, 0x12);
    private static final Color CLARO_CHIP_GREEN_BG = new Color(0xDC, 0xFC, 0xE7);
    private static final Color CLARO_CHIP_GREEN_FG = new Color(0x14, 0x53, 0x2D);
    private static final Color CLARO_CHIP_BLUE_BG = new Color(0xDB, 0xEA, 0xFE);
    private static final Color CLARO_CHIP_BLUE_FG = new Color(0x1E, 0x3A, 0x8A);
    private static final Color CLARO_DANGER_BORDER = new Color(0xF8, 0x71, 0x71);
    private static final Color CLARO_TABLE_SELECTION_BG = new Color(0xFE, 0xF3, 0xC7);

    // Destaque Dourado da Marca (Comum a ambos os temas)
    public static final Color ACCENT = new Color(0xF5, 0xB6, 0x38);
    public static final Color ACCENT_TEXT = new Color(0x12, 0x15, 0x1C);

    // Variáveis ativas dinâmicas (utilizadas em toda a aplicação)
    public static Color BACKGROUND = ESCURO_BACKGROUND;
    public static Color HEADER_BG = ESCURO_HEADER_BG;
    public static Color SURFACE = ESCURO_SURFACE;
    public static Color BORDER = ESCURO_BORDER;
    public static Color INPUT_BORDER = ESCURO_INPUT_BORDER;
    public static Color INPUT_BG = ESCURO_INPUT_BG;
    public static Color TEXT = ESCURO_TEXT;
    public static Color MUTED = ESCURO_MUTED;
    public static Color ERROR_BG = ESCURO_ERROR_BG;
    public static Color ERROR_TEXT = ESCURO_ERROR_TEXT;
    public static Color SECONDARY_BG = ESCURO_SECONDARY_BG;
    public static Color TABLE_ROW_BORDER = ESCURO_TABLE_ROW_BORDER;
    public static Color AVATAR_BG = ESCURO_AVATAR_BG;
    public static Color CHIP_YELLOW_BG = ESCURO_CHIP_YELLOW_BG;
    public static Color CHIP_YELLOW_FG = ESCURO_CHIP_YELLOW_FG;
    public static Color CHIP_GREEN_BG = ESCURO_CHIP_GREEN_BG;
    public static Color CHIP_GREEN_FG = ESCURO_CHIP_GREEN_FG;
    public static Color CHIP_BLUE_BG = ESCURO_CHIP_BLUE_BG;
    public static Color CHIP_BLUE_FG = ESCURO_CHIP_BLUE_FG;
    public static Color DANGER_BORDER = ESCURO_DANGER_BORDER;
    public static Color TABLE_SELECTION_BG = ESCURO_TABLE_SELECTION_BG;

    // Aliases legados
    public static Color NAVY = HEADER_BG;
    public static Color NAVY_LIGHT = SECONDARY_BG;

    public static Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
    );

    private static boolean lookAndFeelInstalado = false;
    private static boolean temaEscuroAtivo = true;
    private static String familiaFontePadrao = FlatInterFont.FAMILY;

    private UiStyle() {
    }

    public static boolean isTemaEscuro() {
        return temaEscuroAtivo;
    }

    public static synchronized void instalarTemaEscuro() {
        aplicarTema(true);
    }

    public static synchronized void instalarTemaClaro() {
        aplicarTema(false);
    }

    public static synchronized void alternarTema(boolean escuro) {
        aplicarTema(escuro);
    }

    public static synchronized void aplicarTema(boolean escuro) {
        temaEscuroAtivo = escuro;

        if (escuro) {
            BACKGROUND = ESCURO_BACKGROUND;
            HEADER_BG = ESCURO_HEADER_BG;
            SURFACE = ESCURO_SURFACE;
            BORDER = ESCURO_BORDER;
            INPUT_BORDER = ESCURO_INPUT_BORDER;
            INPUT_BG = ESCURO_INPUT_BG;
            TEXT = ESCURO_TEXT;
            MUTED = ESCURO_MUTED;
            ERROR_BG = ESCURO_ERROR_BG;
            ERROR_TEXT = ESCURO_ERROR_TEXT;
            SECONDARY_BG = ESCURO_SECONDARY_BG;
            TABLE_ROW_BORDER = ESCURO_TABLE_ROW_BORDER;
            AVATAR_BG = ESCURO_AVATAR_BG;
            CHIP_YELLOW_BG = ESCURO_CHIP_YELLOW_BG;
            CHIP_YELLOW_FG = ESCURO_CHIP_YELLOW_FG;
            CHIP_GREEN_BG = ESCURO_CHIP_GREEN_BG;
            CHIP_GREEN_FG = ESCURO_CHIP_GREEN_FG;
            CHIP_BLUE_BG = ESCURO_CHIP_BLUE_BG;
            CHIP_BLUE_FG = ESCURO_CHIP_BLUE_FG;
            DANGER_BORDER = ESCURO_DANGER_BORDER;
            TABLE_SELECTION_BG = ESCURO_TABLE_SELECTION_BG;
        } else {
            BACKGROUND = CLARO_BACKGROUND;
            HEADER_BG = CLARO_HEADER_BG;
            SURFACE = CLARO_SURFACE;
            BORDER = CLARO_BORDER;
            INPUT_BORDER = CLARO_INPUT_BORDER;
            INPUT_BG = CLARO_INPUT_BG;
            TEXT = CLARO_TEXT;
            MUTED = CLARO_MUTED;
            ERROR_BG = CLARO_ERROR_BG;
            ERROR_TEXT = CLARO_ERROR_TEXT;
            SECONDARY_BG = CLARO_SECONDARY_BG;
            TABLE_ROW_BORDER = CLARO_TABLE_ROW_BORDER;
            AVATAR_BG = CLARO_AVATAR_BG;
            CHIP_YELLOW_BG = CLARO_CHIP_YELLOW_BG;
            CHIP_YELLOW_FG = CLARO_CHIP_YELLOW_FG;
            CHIP_GREEN_BG = CLARO_CHIP_GREEN_BG;
            CHIP_GREEN_FG = CLARO_CHIP_GREEN_FG;
            CHIP_BLUE_BG = CLARO_CHIP_BLUE_BG;
            CHIP_BLUE_FG = CLARO_CHIP_BLUE_FG;
            DANGER_BORDER = CLARO_DANGER_BORDER;
            TABLE_SELECTION_BG = CLARO_TABLE_SELECTION_BG;
        }

        NAVY = HEADER_BG;
        NAVY_LIGHT = SECONDARY_BG;
        CARD_BORDER = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );

        try {
            if (!GraphicsEnvironment.isHeadless()) {
                FlatInterFont.install();
            }

            if (escuro) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            UIManager.put("defaultFont", fonte(13, Font.PLAIN));
            UIManager.put("Panel.background", BACKGROUND);
            UIManager.put("Label.foreground", TEXT);
            UIManager.put("Component.arc", 16);
            UIManager.put("Button.arc", 16);
            UIManager.put("TextComponent.arc", 16);
            UIManager.put("Component.focusWidth", 0);
            UIManager.put("Button.innerFocusWidth", 0);

            // Só chama updateUI() em alternâncias de tema (não na instalação inicial)
            // para evitar avisos de estilo em componentes parcialmente construídos.
            if (lookAndFeelInstalado) {
                FlatLaf.updateUI();
            }
            lookAndFeelInstalado = true;
        } catch (Exception ignored) {
            familiaFontePadrao = "SansSerif";
        }
    }

    public static Font fonte(int tamanhoPx, int estilo) {
        if (estilo == Font.BOLD) {
            return new Font(familiaFontePadrao, Font.BOLD, tamanhoPx);
        }
        return new Font(familiaFontePadrao, estilo, tamanhoPx);
    }

    public static Font fonteSemiBold(int tamanhoPx) {
        Font semiBold = new Font(FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, tamanhoPx);
        if (semiBold.getFamily().equalsIgnoreCase("Dialog") && !FlatInterFont.FAMILY_SEMIBOLD.equalsIgnoreCase("Dialog")) {
            return new Font(familiaFontePadrao, Font.BOLD, tamanhoPx);
        }
        return semiBold;
    }

    public static void aplicarTexto(JComponent componente, int tamanho, boolean negrito) {
        if (componente == null) {
            return;
        }
        componente.setForeground(TEXT);
        componente.setFont(fonte(tamanho, negrito ? Font.BOLD : Font.PLAIN));
    }
}
