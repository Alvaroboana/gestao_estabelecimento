package view;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.JTextComponent;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

public final class UiStyle {

    private static final Color ESCURO_BACKGROUND = new Color(0x12, 0x15, 0x1C);
    static final Color ESCURO_HEADER_BG = new Color(0x1B, 0x22, 0x32);
    private static final Color ESCURO_SURFACE = new Color(0x1B, 0x20, 0x30);
    private static final Color ESCURO_BORDER = new Color(0x2A, 0x30, 0x40);
    private static final Color ESCURO_INPUT_BORDER = new Color(0x3A, 0x42, 0x56);
    private static final Color ESCURO_INPUT_BG = new Color(0x12, 0x15, 0x1C);
    private static final Color ESCURO_TEXT = new Color(0xE8, 0xEC, 0xF4);
    private static final Color ESCURO_MUTED = new Color(0xA3, 0xAD, 0xC2);
    private static final Color ESCURO_BRAND_TEXT = new Color(0xF5, 0xB6, 0x38);
    private static final Color ESCURO_ERROR_BG = new Color(0x4F, 0x1B, 0x1B);
    private static final Color ESCURO_ERROR_TEXT = new Color(0xFF, 0x9C, 0x9C);
    private static final Color ESCURO_SECONDARY_BG = new Color(0x23, 0x2A, 0x3B);
    private static final Color ESCURO_TAB_BG = new Color(0x23, 0x2A, 0x3B);
    private static final Color ESCURO_TABLE_ROW_BORDER = new Color(0x24, 0x2B, 0x3B);
    private static final Color ESCURO_AVATAR_BG = new Color(0x2A, 0x30, 0x40);
    private static final Color ESCURO_CHIP_YELLOW_BG = new Color(0x4A, 0x3A, 0x0F);
    private static final Color ESCURO_CHIP_YELLOW_FG = new Color(0xFF, 0xD3, 0x6B);
    private static final Color ESCURO_CHIP_GREEN_BG = new Color(0x12, 0x40, 0x2A);
    private static final Color ESCURO_CHIP_GREEN_FG = new Color(0x7F, 0xE3, 0xA8);
    private static final Color ESCURO_CHIP_BLUE_BG = new Color(0x17, 0x34, 0x5E);
    private static final Color ESCURO_CHIP_BLUE_FG = new Color(0x9C, 0xC4, 0xFF);
    private static final Color ESCURO_DANGER_TEXT = new Color(0xFF, 0x9C, 0x9C);
    private static final Color ESCURO_DANGER_BORDER = new Color(0x6B, 0x2C, 0x2C);
    private static final Color ESCURO_TABLE_SELECTION_BG = new Color(0x2A, 0x33, 0x47);

    private static final Color CLARO_BACKGROUND = new Color(0xF2, 0xF4, 0xF8);
    private static final Color CLARO_HEADER_BG = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_SURFACE = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_BORDER = new Color(0xDD, 0xE2, 0xEA);
    private static final Color CLARO_INPUT_BORDER = new Color(0xC5, 0xCC, 0xD8);
    private static final Color CLARO_INPUT_BG = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_TEXT = new Color(0x1B, 0x20, 0x30);
    private static final Color CLARO_MUTED = new Color(0x4A, 0x55, 0x68);
    private static final Color CLARO_BRAND_TEXT = new Color(0x8A, 0x5A, 0x00);
    private static final Color CLARO_ERROR_BG = new Color(0xFB, 0xDA, 0xDA);
    private static final Color CLARO_ERROR_TEXT = new Color(0x8F, 0x1A, 0x1A);
    private static final Color CLARO_SECONDARY_BG = new Color(0xFF, 0xFF, 0xFF);
    private static final Color CLARO_TAB_BG = new Color(0xE6, 0xEA, 0xF2);
    private static final Color CLARO_TABLE_ROW_BORDER = new Color(0xED, 0xF0, 0xF5);
    private static final Color CLARO_AVATAR_BG = new Color(0xE3, 0xE7, 0xEF);
    private static final Color CLARO_CHIP_YELLOW_BG = new Color(0xFF, 0xF3, 0xCD);
    private static final Color CLARO_CHIP_YELLOW_FG = new Color(0x6B, 0x4F, 0x00);
    private static final Color CLARO_CHIP_GREEN_BG = new Color(0xD9, 0xF2, 0xE3);
    private static final Color CLARO_CHIP_GREEN_FG = new Color(0x0F, 0x5B, 0x34);
    private static final Color CLARO_CHIP_BLUE_BG = new Color(0xDC, 0xE9, 0xFB);
    private static final Color CLARO_CHIP_BLUE_FG = new Color(0x1A, 0x4A, 0x93);
    private static final Color CLARO_DANGER_TEXT = new Color(0xA3, 0x20, 0x20);
    private static final Color CLARO_DANGER_BORDER = new Color(0xE3, 0xB5, 0xB5);
    private static final Color CLARO_TABLE_SELECTION_BG = new Color(0xFF, 0xF3, 0xD6);

    public static final Color ACCENT = new Color(0xF5, 0xB6, 0x38);
    public static final Color ACCENT_TEXT = new Color(0x12, 0x15, 0x1C);

    public static Color BACKGROUND = ESCURO_BACKGROUND;
    public static Color HEADER_BG = ESCURO_HEADER_BG;
    public static Color SURFACE = ESCURO_SURFACE;
    public static Color BORDER = ESCURO_BORDER;
    public static Color INPUT_BORDER = ESCURO_INPUT_BORDER;
    public static Color INPUT_BG = ESCURO_INPUT_BG;
    public static Color TEXT = ESCURO_TEXT;
    public static Color MUTED = ESCURO_MUTED;
    public static Color BRAND_TEXT = ESCURO_BRAND_TEXT;
    public static Color ERROR_BG = ESCURO_ERROR_BG;
    public static Color ERROR_TEXT = ESCURO_ERROR_TEXT;
    public static Color SECONDARY_BG = ESCURO_SECONDARY_BG;
    public static Color TAB_BG = ESCURO_TAB_BG;
    public static Color TABLE_ROW_BORDER = ESCURO_TABLE_ROW_BORDER;
    public static Color AVATAR_BG = ESCURO_AVATAR_BG;
    public static Color CHIP_YELLOW_BG = ESCURO_CHIP_YELLOW_BG;
    public static Color CHIP_YELLOW_FG = ESCURO_CHIP_YELLOW_FG;
    public static Color CHIP_GREEN_BG = ESCURO_CHIP_GREEN_BG;
    public static Color CHIP_GREEN_FG = ESCURO_CHIP_GREEN_FG;
    public static Color CHIP_BLUE_BG = ESCURO_CHIP_BLUE_BG;
    public static Color CHIP_BLUE_FG = ESCURO_CHIP_BLUE_FG;
    public static Color DANGER_TEXT = ESCURO_DANGER_TEXT;
    public static Color DANGER_BORDER = ESCURO_DANGER_BORDER;
    public static Color TABLE_SELECTION_BG = ESCURO_TABLE_SELECTION_BG;

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
        if (!lookAndFeelInstalado) {
            aplicarTema(true);
        }
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
            BRAND_TEXT = ESCURO_BRAND_TEXT;
            ERROR_BG = ESCURO_ERROR_BG;
            ERROR_TEXT = ESCURO_ERROR_TEXT;
            SECONDARY_BG = ESCURO_SECONDARY_BG;
            TAB_BG = ESCURO_TAB_BG;
            TABLE_ROW_BORDER = ESCURO_TABLE_ROW_BORDER;
            AVATAR_BG = ESCURO_AVATAR_BG;
            CHIP_YELLOW_BG = ESCURO_CHIP_YELLOW_BG;
            CHIP_YELLOW_FG = ESCURO_CHIP_YELLOW_FG;
            CHIP_GREEN_BG = ESCURO_CHIP_GREEN_BG;
            CHIP_GREEN_FG = ESCURO_CHIP_GREEN_FG;
            CHIP_BLUE_BG = ESCURO_CHIP_BLUE_BG;
            CHIP_BLUE_FG = ESCURO_CHIP_BLUE_FG;
            DANGER_TEXT = ESCURO_DANGER_TEXT;
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
            BRAND_TEXT = CLARO_BRAND_TEXT;
            ERROR_BG = CLARO_ERROR_BG;
            ERROR_TEXT = CLARO_ERROR_TEXT;
            SECONDARY_BG = CLARO_SECONDARY_BG;
            TAB_BG = CLARO_TAB_BG;
            TABLE_ROW_BORDER = CLARO_TABLE_ROW_BORDER;
            AVATAR_BG = CLARO_AVATAR_BG;
            CHIP_YELLOW_BG = CLARO_CHIP_YELLOW_BG;
            CHIP_YELLOW_FG = CLARO_CHIP_YELLOW_FG;
            CHIP_GREEN_BG = CLARO_CHIP_GREEN_BG;
            CHIP_GREEN_FG = CLARO_CHIP_GREEN_FG;
            CHIP_BLUE_BG = CLARO_CHIP_BLUE_BG;
            CHIP_BLUE_FG = CLARO_CHIP_BLUE_FG;
            DANGER_TEXT = CLARO_DANGER_TEXT;
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

    private static String hex(Color c) {
        if (c == null) {
            return "#000000";
        }
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static void estilizarCampo(JTextComponent campo) {
        if (campo == null) {
            return;
        }
        campo.putClientProperty("UiStyle.arredondado", Boolean.TRUE);
        campo.setFont(fonte(13, Font.PLAIN));
        campo.setBackground(INPUT_BG);
        campo.setForeground(TEXT);
        campo.setCaretColor(TEXT);
        campo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc: 16; borderWidth: 1; focusWidth: 0; innerFocusWidth: 0; margin: 0,10,0,10; background: "
                        + hex(INPUT_BG) + "; foreground: " + hex(TEXT) + "; borderColor: " + hex(INPUT_BORDER)
                        + "; focusedBorderColor: " + hex(ACCENT)
        );
    }

    public static void estilizarComboBox(JComboBox<?> combo) {
        if (combo == null) {
            return;
        }
        combo.putClientProperty("UiStyle.arredondado", Boolean.TRUE);
        combo.setFont(fonte(13, Font.PLAIN));
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT);
        combo.putClientProperty(
                FlatClientProperties.STYLE,
                "arc: 16; borderWidth: 1; focusWidth: 0; innerFocusWidth: 0; background: "
                        + hex(INPUT_BG) + "; foreground: " + hex(TEXT) + "; borderColor: " + hex(INPUT_BORDER)
                        + "; focusedBorderColor: " + hex(ACCENT) + "; buttonBackground: " + hex(INPUT_BG)
                        + "; buttonArrowColor: " + hex(MUTED)
        );
    }

    public static Icon icone(Ikon ikon, int tamanho, Color cor) {
        if (ikon == null) {
            return null;
        }
        try {
            return FontIcon.of(ikon, tamanho, cor != null ? cor : TEXT);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Icon iconeDinamico(Ikon ikon, int tamanho, Supplier<Color> provedorCor) {
        if (ikon == null) {
            return null;
        }
        try {
            FontIcon base = FontIcon.of(ikon, tamanho, provedorCor != null ? provedorCor.get() : TEXT);
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Color corAtual = provedorCor != null ? provedorCor.get() : (c != null ? c.getForeground() : TEXT);
                    if (corAtual != null) {
                        base.setIconColor(corAtual);
                    }
                    base.paintIcon(c, g, x, y);
                }

                @Override
                public int getIconWidth() {
                    return base.getIconWidth();
                }

                @Override
                public int getIconHeight() {
                    return base.getIconHeight();
                }
            };
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static String removerMarcadorSeta(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(" ▾", "").replace("▾", "").trim();
    }

    public static void desenharChevronExpansivel(Graphics2D g2, Component botao, Color cor) {
        if (botao != null) {
            desenharChevronExpansivel(g2, botao.getWidth(), botao.getHeight(), cor);
        }
    }

    public static void desenharChevronExpansivel(Graphics2D g2, int larguraBotao, int alturaBotao, Color cor) {
        if (g2 == null || larguraBotao < 28 || alturaBotao < 16) {
            return;
        }
        Graphics2D g = (Graphics2D) g2.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(cor != null ? cor : MUTED);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double cx = larguraBotao - 15.0;
            double cy = alturaBotao / 2.0;
            Path2D.Double seta = new Path2D.Double();
            seta.moveTo(cx - 4.5, cy - 2.0);
            seta.lineTo(cx, cy + 2.5);
            seta.lineTo(cx + 4.5, cy - 2.0);
            g.draw(seta);
        } finally {
            g.dispose();
        }
    }

    public static void mostrarPopupSincronizado(JPopupMenu menu, Component botao) {
        if (menu == null || botao == null) {
            return;
        }
        menu.pack();
        Dimension pref = menu.getPreferredSize();
        int largura = Math.max(botao.getWidth(), pref != null ? pref.width : 160);
        int altura = pref != null ? pref.height : 120;
        menu.setPopupSize(largura, altura);
        menu.show(botao, 0, botao.getHeight() + 2);
    }

    public static JScrollPane envolverCardLateralComScroll(JComponent card) {
        if (card == null) {
            return new JScrollPane();
        }
        Dimension prefOriginal = card.getPreferredSize();
        int larguraCard = (prefOriginal != null && prefOriginal.width > 0) ? prefOriginal.width : 420;
        Object propLargura = card.getClientProperty("UiStyle.larguraCard");
        if (propLargura instanceof Integer) {
            larguraCard = (Integer) propLargura;
        } else {
            card.putClientProperty("UiStyle.larguraCard", larguraCard);
        }

        card.putClientProperty("UiStyle.internoScroll", Boolean.TRUE);
        card.setPreferredSize(null);
        card.setMinimumSize(null);
        card.setMaximumSize(null);

        PainelConteudoCardScrollable wrapperConteudo = new PainelConteudoCardScrollable(card);
        return new ScrollCardArredondado(wrapperConteudo, larguraCard, 14);
    }

    public static void estilizarScrollBarCard(JScrollBar bar) {
        if (bar == null) {
            return;
        }
        bar.setUnitIncrement(16);
        bar.setOpaque(false);
        if (bar.getOrientation() == JScrollBar.VERTICAL) {
            bar.setPreferredSize(new Dimension(6, 0));
        } else {
            bar.setPreferredSize(new Dimension(0, 6));
        }
        bar.setBorder(BorderFactory.createEmptyBorder());
        bar.setUI(new ScrollBarArredondadaUI());
    }

    private static final class ScrollBarArredondadaUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = INPUT_BORDER;
            this.trackColor = SURFACE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return criarBotaoZero();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return criarBotaoZero();
        }

        private JButton criarBotaoZero() {
            JButton b = new JButton();
            Dimension d = new Dimension(0, 0);
            b.setPreferredSize(d);
            b.setMinimumSize(d);
            b.setMaximumSize(d);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BORDER);
                if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                    int w = Math.min(4, thumbBounds.width);
                    int x = thumbBounds.x + Math.max(0, (thumbBounds.width - w) / 2);
                    int y = thumbBounds.y + 4;
                    int h = Math.max(8, thumbBounds.height - 8);
                    g2.fill(new RoundRectangle2D.Float(x, y, w, h, 4, 4));
                } else {
                    int h = Math.min(4, thumbBounds.height);
                    int y = thumbBounds.y + Math.max(0, (thumbBounds.height - h) / 2);
                    int x = thumbBounds.x + 4;
                    int w = Math.max(8, thumbBounds.width - 8);
                    g2.fill(new RoundRectangle2D.Float(x, y, w, h, 4, 4));
                }
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class PainelConteudoCardScrollable extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;

        PainelConteudoCardScrollable(JComponent card) {
            super(new BorderLayout());
            setOpaque(false);
            add(card, BorderLayout.NORTH);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static final class ScrollCardArredondado extends JScrollPane {
        private static final long serialVersionUID = 1L;
        private final JComponent conteudo;
        private final int larguraFixa;
        private final int raio;

        ScrollCardArredondado(JComponent conteudo, int larguraFixa, int raio) {
            super(
                    conteudo,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            );
            this.conteudo = conteudo;
            this.larguraFixa = larguraFixa;
            this.raio = raio;
            configurarAparencia();
        }

        @Override
        protected JViewport createViewport() {
            return new JViewport() {
                private static final long serialVersionUID = 1L;

                @Override
                public void updateUI() {
                    super.updateUI();
                    setOpaque(false);
                }
            };
        }

        @Override
        public JScrollBar createVerticalScrollBar() {
            return new JScrollPane.ScrollBar(JScrollBar.VERTICAL) {
                private static final long serialVersionUID = 1L;

                @Override
                public void updateUI() {
                    super.updateUI();
                    estilizarScrollBarCard(this);
                }
            };
        }

        @Override
        public void updateUI() {
            super.updateUI();
            configurarAparencia();
        }

        void configurarAparencia() {
            setOpaque(false);
            if (getViewport() != null) {
                getViewport().setOpaque(false);
            }
            setBorder(new EmptyBorder(6, 2, 6, 6));
            JScrollBar vBar = getVerticalScrollBar();
            if (vBar != null) {
                estilizarScrollBarCard(vBar);
            }
        }

        @Override
        public void doLayout() {
            super.doLayout();
            int hCartao = calcularAlturaCartao();
            Insets in = getInsets();
            int hInterna = Math.max(0, hCartao - in.top - in.bottom);
            if (getViewport() != null) {
                Rectangle rv = getViewport().getBounds();
                if (rv.height > hInterna) {
                    getViewport().setBounds(rv.x, in.top, rv.width, hInterna);
                }
            }
            JScrollBar vBar = getVerticalScrollBar();
            if (vBar != null && vBar.isVisible()) {
                Rectangle rb = vBar.getBounds();
                int barY = Math.max(in.top + 4, rb.y);
                int barH = Math.max(0, Math.min(rb.height, hInterna) - 8);
                int barX = Math.min(rb.x, getWidth() - in.right - rb.width);
                vBar.setBounds(barX, barY, rb.width, barH);
            }
        }

        private int calcularAlturaCartao() {
            int hDisp = getHeight();
            Dimension pref = conteudo != null ? conteudo.getPreferredSize() : null;
            int hPref = (pref != null ? pref.height : 0) + 8;
            if (hDisp <= 0) {
                return Math.max(80, hPref);
            }
            return Math.max(60, Math.min(hDisp, hPref));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension pref = conteudo != null ? conteudo.getPreferredSize() : super.getPreferredSize();
            int h = (pref != null ? pref.height : 200) + 8;
            return new Dimension(larguraFixa, h);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(larguraFixa, 100);
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension pref = getPreferredSize();
            return new Dimension(larguraFixa, pref.height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                setOpaque(false);
            }
            if (getViewport() != null && getViewport().isOpaque()) {
                getViewport().setOpaque(false);
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = calcularAlturaCartao();
                int arco = raio * 2;
                g2.setColor(SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = calcularAlturaCartao();
                int arco = raio * 2;
                Shape clip = new RoundRectangle2D.Float(1, 1, Math.max(0, w - 2), Math.max(0, h - 2), arco, arco);
                g2.clip(clip);
                super.paintChildren(g2);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = calcularAlturaCartao();
                int arco = raio * 2;
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    public static void atualizarComponentesTema(Component comp) {
        if (comp == null) {
            return;
        }
        if (comp instanceof JTable) {
            JTable t = (JTable) comp;
            t.setBackground(SURFACE);
            t.setForeground(TEXT);
            t.setGridColor(TABLE_ROW_BORDER);
            t.setSelectionBackground(TABLE_SELECTION_BG);
            t.setSelectionForeground(TEXT);
            if (t.getTableHeader() != null) {
                t.getTableHeader().setBackground(SURFACE);
                t.getTableHeader().setForeground(MUTED);
            }
        } else if (comp instanceof JLabel) {
            JLabel lbl = (JLabel) comp;
            Color fg = lbl.getForeground();
            if (fg != null) {
                if (fg.equals(ESCURO_TEXT) || fg.equals(CLARO_TEXT)) {
                    lbl.setForeground(TEXT);
                } else if (fg.equals(ESCURO_MUTED) || fg.equals(CLARO_MUTED)) {
                    lbl.setForeground(MUTED);
                } else if (fg.equals(ESCURO_ERROR_TEXT) || fg.equals(CLARO_ERROR_TEXT)) {
                    lbl.setForeground(ERROR_TEXT);
                } else if (fg.equals(ESCURO_BRAND_TEXT) || fg.equals(CLARO_BRAND_TEXT)) {
                    lbl.setForeground(BRAND_TEXT);
                } else if (fg.equals(ESCURO_DANGER_TEXT) || fg.equals(CLARO_DANGER_TEXT)) {
                    lbl.setForeground(DANGER_TEXT);
                } else if (fg.equals(ESCURO_CHIP_YELLOW_FG) || fg.equals(CLARO_CHIP_YELLOW_FG)) {
                    lbl.setForeground(CHIP_YELLOW_FG);
                } else if (fg.equals(ESCURO_CHIP_GREEN_FG) || fg.equals(CLARO_CHIP_GREEN_FG)) {
                    lbl.setForeground(CHIP_GREEN_FG);
                } else if (fg.equals(ESCURO_CHIP_BLUE_FG) || fg.equals(CLARO_CHIP_BLUE_FG)) {
                    lbl.setForeground(CHIP_BLUE_FG);
                }
            }
        } else if (comp instanceof JTextComponent) {
            JTextComponent txt = (JTextComponent) comp;
            if (Boolean.TRUE.equals(txt.getClientProperty("UiStyle.arredondado"))) {
                estilizarCampo(txt);
            }
            txt.setBackground(INPUT_BG);
            txt.setCaretColor(TEXT);
            Color fg = txt.getForeground();
            if (fg != null && (fg.equals(ESCURO_MUTED) || fg.equals(CLARO_MUTED))) {
                txt.setForeground(MUTED);
            } else {
                txt.setForeground(TEXT);
            }
            Border b = txt.getBorder();
            if (b instanceof CompoundBorder) {
                CompoundBorder cb = (CompoundBorder) b;
                if (cb.getOutsideBorder() instanceof LineBorder) {
                    txt.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(INPUT_BORDER, 1, true),
                            cb.getInsideBorder()
                    ));
                }
            }
        } else if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            if (Boolean.TRUE.equals(combo.getClientProperty("UiStyle.arredondado"))) {
                estilizarComboBox(combo);
            }
            combo.setBackground(INPUT_BG);
            combo.setForeground(TEXT);
        } else if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            Color fg = btn.getForeground();
            if (fg != null) {
                if (fg.equals(ESCURO_TEXT) || fg.equals(CLARO_TEXT)) {
                    btn.setForeground(TEXT);
                } else if (fg.equals(ESCURO_MUTED) || fg.equals(CLARO_MUTED)) {
                    btn.setForeground(MUTED);
                } else if (fg.equals(ESCURO_DANGER_TEXT) || fg.equals(CLARO_DANGER_TEXT)) {
                    btn.setForeground(DANGER_TEXT);
                }
            }
            Color bg = btn.getBackground();
            if (bg != null) {
                if (bg.equals(ESCURO_INPUT_BG) || bg.equals(CLARO_INPUT_BG)) {
                    btn.setBackground(INPUT_BG);
                } else if (bg.equals(ESCURO_SECONDARY_BG) || bg.equals(CLARO_SECONDARY_BG)) {
                    btn.setBackground(SECONDARY_BG);
                } else if (bg.equals(ESCURO_SURFACE) || bg.equals(CLARO_SURFACE)) {
                    btn.setBackground(SURFACE);
                }
            }
        } else if (comp instanceof JScrollPane) {
            if (comp instanceof ScrollCardArredondado) {
                ((ScrollCardArredondado) comp).configurarAparencia();
            } else {
                JScrollPane sp = (JScrollPane) comp;
                sp.setBorder(BorderFactory.createEmptyBorder());
                Color bg = sp.getBackground();
                if (bg != null && (bg.equals(ESCURO_BACKGROUND) || bg.equals(CLARO_BACKGROUND))) {
                    sp.setBackground(BACKGROUND);
                    if (sp.getViewport() != null) {
                        sp.getViewport().setBackground(BACKGROUND);
                    }
                } else {
                    sp.setOpaque(false);
                    sp.setBackground(SURFACE);
                    if (sp.getViewport() != null) {
                        sp.getViewport().setOpaque(false);
                        sp.getViewport().setBackground(SURFACE);
                    }
                }
                if (sp.getVerticalScrollBar() != null) {
                    estilizarScrollBarCard(sp.getVerticalScrollBar());
                }
                if (sp.getHorizontalScrollBar() != null) {
                    estilizarScrollBarCard(sp.getHorizontalScrollBar());
                }
            }
        } else if (comp instanceof JPanel) {
            JPanel pnl = (JPanel) comp;
            Color bg = pnl.getBackground();
            if (bg != null) {
                if (bg.equals(ESCURO_BACKGROUND) || bg.equals(CLARO_BACKGROUND)) {
                    pnl.setBackground(BACKGROUND);
                } else if (bg.equals(ESCURO_SURFACE) || bg.equals(CLARO_SURFACE)) {
                    pnl.setBackground(SURFACE);
                } else if (bg.equals(ESCURO_HEADER_BG) || bg.equals(CLARO_HEADER_BG)) {
                    pnl.setBackground(HEADER_BG);
                } else if (bg.equals(ESCURO_INPUT_BG) || bg.equals(CLARO_INPUT_BG)) {
                    pnl.setBackground(INPUT_BG);
                } else if (bg.equals(ESCURO_CHIP_YELLOW_BG) || bg.equals(CLARO_CHIP_YELLOW_BG)) {
                    pnl.setBackground(CHIP_YELLOW_BG);
                } else if (bg.equals(ESCURO_CHIP_GREEN_BG) || bg.equals(CLARO_CHIP_GREEN_BG)) {
                    pnl.setBackground(CHIP_GREEN_BG);
                } else if (bg.equals(ESCURO_CHIP_BLUE_BG) || bg.equals(CLARO_CHIP_BLUE_BG)) {
                    pnl.setBackground(CHIP_BLUE_BG);
                }
            }
            Border b = pnl.getBorder();
            if (b instanceof CompoundBorder) {
                CompoundBorder cb = (CompoundBorder) b;
                if (cb.getOutsideBorder() instanceof LineBorder) {
                    pnl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(INPUT_BORDER, 1, true),
                            cb.getInsideBorder()
                    ));
                } else if (cb.getOutsideBorder() instanceof MatteBorder) {
                    pnl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                            cb.getInsideBorder()
                    ));
                }
            }
        }
        if (comp instanceof Container) {
            for (Component filho : ((Container) comp).getComponents()) {
                atualizarComponentesTema(filho);
            }
        }
        comp.repaint();
    }
}
