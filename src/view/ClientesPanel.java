package view;

import com.formdev.flatlaf.FlatClientProperties;

import controller.ClienteController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.EstadoVenda;
import model.vo.Perfil;
import model.vo.Utilizador;
import model.vo.Venda;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignH;
import org.kordamp.ikonli.materialdesign2.MaterialDesignI;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;

public class ClientesPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVO_CLIENTE,
        EDITAR_CLIENTE,
        INFORMACOES_CLIENTE
    }

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] COLUNAS = {
        "ID", "Nome", "Contactos", "Total compras", "Última compra"
    };

    private static final String[] COLUNAS_HISTORICO_VENDAS = {
        "Venda", "Data", "Total", "Estado"
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final CampoBuscaArredondado campoBuscarCliente;
    private final JButton botaoNovoCliente;
    private final JButton botaoEditar;
    private final JButton botaoHistorico;
    private final JButton botaoInformacoes;
    private final JButton botaoEliminar;
    private final JPanel containerBotoesToolbar;
    private final JPanel cardNovoCliente;
    private final JPanel cardEditarCliente;
    private final JPanel cardInformacoesCliente;
    private final JPanel wrapperDireita;

    private JTextField campoNomeNovo;
    private JTextField campoContactoNovo;
    private JTextField campoContactoExtraNovo;
    private JPanel blocoContactoExtraNovo;
    private JLabel linkAdicionarContactoNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    private JTextField campoNomeEditar;
    private JTextField campoContacto1Editar;
    private JTextField campoContacto2Editar;
    private JLabel linkAdicionarContactoEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

    private JLabel labelNomeInformacoes;
    private JTextField campoTotalComprasInformacoes;
    private JTextField campoUltimaCompraInformacoes;
    private JTable tabelaHistoricoVendasInformacoes;
    private JButton botaoEditarInformacoes;
    private JButton botaoFecharInformacoes;

    private final ClienteController clienteController = new ClienteController();
    private List<Object[]> dadosClientesCarregados = new ArrayList<>();
    private List<Cliente> listaClientesEntidades = new ArrayList<>();
    private List<Venda> listaVendasReais = new ArrayList<>();

    private ModoPainelLateral modoLateral;

    public ClientesPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public ClientesPanel(boolean exibirNovoClienteInicialmente) {
        this(exibirNovoClienteInicialmente ? ModoPainelLateral.NOVO_CLIENTE : ModoPainelLateral.NENHUM);
    }

    public ClientesPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.campoBuscarCliente = criarCampoBuscarCliente();
        this.botaoNovoCliente = criarBotaoNovoCliente();
        this.botaoEditar = criarBotaoEditarCliente();
        this.botaoHistorico = criarBotaoInformacoesOuHistorico("Histórico");
        this.botaoInformacoes = criarBotaoInformacoesOuHistorico("Informações");
        this.botaoEliminar = criarBotaoToolbar(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );
        this.botaoEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminar.setIconTextGap(6);
        this.botaoEliminar.addActionListener(e -> eliminarClienteSelecionado());
        this.containerBotoesToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerBotoesToolbar.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovoCliente = criarCardNovoCliente();
        this.cardEditarCliente = criarCardEditarCliente();
        this.cardInformacoesCliente = criarCardInformacoesCliente();

        this.wrapperDireita = new JPanel(new BorderLayout());
        this.wrapperDireita.setOpaque(false);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosClienteParaEdicao();
                sincronizarDadosClienteParaInformacoes();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        atualizarTabelaFiltrada();
        definirModoPainelLateral(this.modoLateral);
        recarregarDados();
    }

    public JTable getTabela() {
        return tabela;
    }

    public JTextField getCampoBuscarCliente() {
        return campoBuscarCliente;
    }

    public JButton getBotaoNovoCliente() {
        return botaoNovoCliente;
    }

    public JButton getBotaoEditar() {
        return botaoEditar;
    }

    public JButton getBotaoHistorico() {
        return botaoHistorico;
    }

    public JButton getBotaoInformacoes() {
        return botaoInformacoes;
    }

    public JButton getBotaoEliminar() {
        return botaoEliminar;
    }

    public JPanel getCardNovoCliente() {
        return cardNovoCliente;
    }

    public JPanel getCardEditarCliente() {
        return cardEditarCliente;
    }

    public JPanel getCardInformacoesCliente() {
        return cardInformacoesCliente;
    }

    public JTextField getCampoNomeNovo() {
        return campoNomeNovo;
    }

    public JTextField getCampoContactoNovo() {
        return campoContactoNovo;
    }

    public JLabel getLinkAdicionarContactoNovo() {
        return linkAdicionarContactoNovo;
    }

    public JButton getBotaoGuardarNovo() {
        return botaoGuardarNovo;
    }

    public JButton getBotaoCancelarNovo() {
        return botaoCancelarNovo;
    }

    public JTextField getCampoNomeEditar() {
        return campoNomeEditar;
    }

    public JTextField getCampoContacto1Editar() {
        return campoContacto1Editar;
    }

    public JTextField getCampoContacto2Editar() {
        return campoContacto2Editar;
    }

    public JLabel getLinkAdicionarContactoEditar() {
        return linkAdicionarContactoEditar;
    }

    public JButton getBotaoGuardarAlteracoesEditar() {
        return botaoGuardarAlteracoesEditar;
    }

    public JButton getBotaoCancelarEditar() {
        return botaoCancelarEditar;
    }

    public JButton getBotaoEliminarEditar() {
        return botaoEliminarEditar;
    }

    public JLabel getLabelNomeInformacoes() {
        return labelNomeInformacoes;
    }

    public JTextField getCampoTotalComprasInformacoes() {
        return campoTotalComprasInformacoes;
    }

    public JTextField getCampoUltimaCompraInformacoes() {
        return campoUltimaCompraInformacoes;
    }

    public JTable getTabelaHistoricoVendasInformacoes() {
        return tabelaHistoricoVendasInformacoes;
    }

    public JButton getBotaoEditarInformacoes() {
        return botaoEditarInformacoes;
    }

    public JButton getBotaoFecharInformacoes() {
        return botaoFecharInformacoes;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isNovoClienteVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_CLIENTE;
    }

    public boolean isEditarClienteVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_CLIENTE;
    }

    public boolean isInformacoesClienteVisivel() {
        return modoLateral == ModoPainelLateral.INFORMACOES_CLIENTE;
    }

    public final void definirNovoClienteVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVO_CLIENTE : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelEditar() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_CLIENTE);
    }

    public final void exibirPainelInformacoes() {
        definirModoPainelLateral(ModoPainelLateral.INFORMACOES_CLIENTE);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean visivel = this.modoLateral != ModoPainelLateral.NENHUM;
        wrapperDireita.removeAll();
        wrapperDireita.setVisible(visivel);

        if (this.modoLateral == ModoPainelLateral.NOVO_CLIENTE) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardNovoCliente), BorderLayout.CENTER);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CLIENTE) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardEditarCliente), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosClienteParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.INFORMACOES_CLIENTE) {
            wrapperDireita.add(UiStyle.envolverCardLateralComScroll(cardInformacoesCliente), BorderLayout.CENTER);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosClienteParaInformacoes();
        } else {
            tabela.clearSelection();
        }

        atualizarBotoesToolbar();
        UiStyle.atualizarComponentesTema(wrapperDireita);
        revalidate();
        repaint();
    }

    private void atualizarBotoesToolbar() {
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Clientes");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                int larguraDisp = getWidth();
                if (larguraDisp <= 0 && getParent() != null) {
                    larguraDisp = getParent().getWidth();
                }
                if (larguraDisp <= 0) {
                    return super.getPreferredSize();
                }
                Insets in = getInsets();
                int maxW = Math.max(200, larguraDisp - in.left - in.right);
                int hgap = 8;
                int vgap = 6;
                int x = 0;
                int y = in.top + vgap;
                int rowH = 0;
                for (Component c : getComponents()) {
                    if (!c.isVisible()) {
                        continue;
                    }
                    Dimension d = c.getPreferredSize();
                    if (x > 0 && x + hgap + d.width > maxW) {
                        y += rowH + vgap;
                        x = 0;
                        rowH = 0;
                    }
                    if (x > 0) {
                        x += hgap;
                    }
                    x += d.width;
                    rowH = Math.max(rowH, d.height);
                }
                y += rowH + vgap + in.bottom;
                return new Dimension(larguraDisp, Math.max(ALTURA_INPUT, y));
            }
        };
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.setBorder(new EmptyBorder(-6, -8, -6, 0));

        toolbar.add(campoBuscarCliente);
        toolbar.add(botaoNovoCliente);
        toolbar.add(botaoEditar);
        toolbar.add(botaoHistorico);
        toolbar.add(botaoInformacoes);
        toolbar.add(botaoEliminar);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    private JPanel criarCorpoPrincipal() {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.add(criarCardTabela(), BorderLayout.CENTER);
        linha.add(wrapperDireita, BorderLayout.EAST);
        return linha;
    }

    private CampoBuscaArredondado criarCampoBuscarCliente() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar por nome");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        campo.setPreferredSize(dim);
        campo.setMinimumSize(dim);
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabelaFiltrada();
            }
        });
        return campo;
    }

    private JButton criarBotaoToolbar(
            String texto,
            Color bg,
            Color fg,
            Color borda,
            boolean preencherFundo) {
        return new BotaoToolbarArredondado(
                texto, bg, fg, borda, RAIO_COMPONENTE, preencherFundo
        );
    }

    private JButton criarBotaoNovoCliente() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo cliente", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignA.ACCOUNT_PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_CLIENTE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_CLIENTE);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarCliente() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_CLIENTE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_CLIENTE);
            }
        });
        return botao;
    }

    private JButton criarBotaoInformacoesOuHistorico(String texto) {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                texto, UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.iconeDinamico(
                "Histórico".equalsIgnoreCase(texto) ? MaterialDesignH.HISTORY : MaterialDesignI.INFORMATION_OUTLINE,
                15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.INFORMACOES_CLIENTE) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.INFORMACOES_CLIENTE);
            }
        });
        return botao;
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[0][COLUNAS.length], COLUNAS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable criarTabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo);
        t.setRowHeight(42);
        t.setFont(UiStyle.fonte(13, Font.PLAIN));
        t.setBackground(UiStyle.SURFACE);
        t.setForeground(UiStyle.TEXT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(UiStyle.TABLE_ROW_BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(UiStyle.TABLE_SELECTION_BG);
        t.setSelectionForeground(UiStyle.TEXT);
        t.setFillsViewportHeight(true);
        t.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(UiStyle.SURFACE);
        header.setForeground(UiStyle.MUTED);
        header.setFont(UiStyle.fonteSemiBold(13));
        header.setPreferredSize(new Dimension(0, 42));
        header.setDefaultRenderer(new RenderizadorCabecalho());

        RenderizadorCelulaTexto renderizadorTexto = new RenderizadorCelulaTexto();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
        }
        return t;
    }

    private JPanel criarCardTabela() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(2, 2, 2, 2));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(UiStyle.SURFACE);

        JPanel topoWrapper = new JPanel(new BorderLayout());
        topoWrapper.setOpaque(false);
        topoWrapper.add(tabela.getTableHeader(), BorderLayout.NORTH);
        topoWrapper.add(scroll, BorderLayout.CENTER);

        card.add(topoWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardNovoCliente() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Novo cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome * (máx. 100)", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto * (máx. 30)", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 60);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoContactoExtraNovo = new CampoFormularioArredondado("");
        blocoContactoExtraNovo = criarCampoComLabel("Contacto 2 (opcional)", campoContactoExtraNovo);
        fixarLargura(blocoContactoExtraNovo, larguraInterna, 60);
        blocoContactoExtraNovo.setVisible(false);
        card.add(blocoContactoExtraNovo);

        linkAdicionarContactoNovo = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoNovo.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoNovo.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoNovo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean visivel = !blocoContactoExtraNovo.isVisible();
                blocoContactoExtraNovo.setVisible(visivel);
                linkAdicionarContactoNovo.setText(visivel ? "- Remover 2º contacto" : "+ Adicionar contacto");
                if (!visivel && campoContactoExtraNovo != null) {
                    campoContactoExtraNovo.setText("");
                }
                card.revalidate();
                card.repaint();
            }
        });
        card.add(linkAdicionarContactoNovo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarNovo.setIconTextGap(6);
        botaoGuardarNovo.addActionListener(e -> {
            String nome = (campoNomeNovo != null) ? campoNomeNovo.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contacto = (campoContactoNovo != null) ? campoContactoNovo.getText().trim() : "";
            if (contacto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o contacto principal do cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contactoExtra = (campoContactoExtraNovo != null && blocoContactoExtraNovo != null && blocoContactoExtraNovo.isVisible())
                    ? campoContactoExtraNovo.getText().trim() : "";
            Cliente c = new Cliente();
            c.setNome(nome);
            List<Contacto> listaCont = new ArrayList<>();
            Contacto cont = new Contacto();
            cont.setContacto(contacto);
            listaCont.add(cont);
            if (!contactoExtra.isEmpty()) {
                Contacto cont2 = new Contacto();
                cont2.setContacto(contactoExtra);
                listaCont.add(cont2);
            }
            c.setContacto(listaCont);

            AsyncTask.executar(
                () -> {
                    clienteController.salvar(c);
                    return null;
                },
                sucesso -> {
                    JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    if (campoNomeNovo != null) campoNomeNovo.setText("");
                    if (campoContactoNovo != null) campoContactoNovo.setText("");
                    if (campoContactoExtraNovo != null) campoContactoExtraNovo.setText("");
                    if (blocoContactoExtraNovo != null) blocoContactoExtraNovo.setVisible(false);
                    if (linkAdicionarContactoNovo != null) linkAdicionarContactoNovo.setText("+ Adicionar contacto");
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        });
        botaoCancelarNovo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovo.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarNovo.setIconTextGap(6);
        botaoCancelarNovo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovo);
        card.add(acoes);

        return card;
    }

    private JPanel criarCardEditarCliente() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditar = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome * (máx. 100)", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoContacto1Editar = new CampoFormularioArredondado("");
        JPanel blocoContacto1 = criarCampoComLabel("Contacto 1 *", campoContacto1Editar);
        fixarLargura(blocoContacto1, larguraInterna, 60);
        card.add(blocoContacto1);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoContacto2Editar = new CampoFormularioArredondado("");
        JPanel blocoContacto2 = criarCampoComLabel("Contacto 2", campoContacto2Editar);
        fixarLargura(blocoContacto2, larguraInterna, 60);
        card.add(blocoContacto2);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        linkAdicionarContactoEditar = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoEditar.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoEditar.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoEditar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoEditar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tabela.getSelectedRow();
                if (row < 0 || row >= tabela.getRowCount()) {
                    JOptionPane.showMessageDialog(ClientesPanel.this, "Selecione um cliente para adicionar contacto extra.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String idStr = String.valueOf(tabela.getValueAt(row, 0));
                try {
                    Long idCliente = Long.parseLong(idStr);
                    Cliente c = (listaClientesEntidades != null) ? listaClientesEntidades.stream()
                        .filter(item -> item.getIdCliente() != null && item.getIdCliente().equals(idCliente))
                        .findFirst().orElse(null) : null;
                    Long idPessoa = (c != null && c.getIdPessoa() != null) ? c.getIdPessoa() : idCliente;
                    String novoContacto = JOptionPane.showInputDialog(ClientesPanel.this, 
                        "Informe o novo contacto (telefone ou email):", 
                        "Adicionar Contacto Extra", 
                        JOptionPane.PLAIN_MESSAGE);
                    if (novoContacto != null && !novoContacto.trim().isEmpty()) {
                        AsyncTask.executar(
                            () -> {
                                clienteController.adicionarContactoExtra(idPessoa, novoContacto.trim());
                                return true;
                            },
                            sucesso -> {
                                JOptionPane.showMessageDialog(ClientesPanel.this, "Contacto extra adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                                recarregarDados();
                            },
                            erro -> JOptionPane.showMessageDialog(ClientesPanel.this, "Erro ao adicionar contacto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClientesPanel.this, "Identificador de cliente inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        card.add(linkAdicionarContactoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoesEditar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoesEditar.setIconTextGap(5);
        botaoGuardarAlteracoesEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoGuardarAlteracoesEditar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            try {
                Long idCliente = Long.parseLong(idStr);
                String nome = (campoNomeEditar != null) ? campoNomeEditar.getText().trim() : "";
                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o nome do cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Cliente c = (listaClientesEntidades != null) ? listaClientesEntidades.stream()
                    .filter(item -> item.getIdCliente() != null && item.getIdCliente().equals(idCliente))
                    .findFirst().orElse(null) : null;
                if (c == null) {
                    c = new Cliente();
                    c.setIdCliente(idCliente);
                }
                c.setNome(nome);
                String c1 = (campoContacto1Editar != null) ? campoContacto1Editar.getText().trim() : "";
                if (c1.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe o contacto principal do cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String c2 = (campoContacto2Editar != null) ? campoContacto2Editar.getText().trim() : "";
                List<Contacto> listaCont = new ArrayList<>();
                if (!c1.isEmpty()) {
                    Contacto cont1 = new Contacto();
                    cont1.setContacto(c1);
                    listaCont.add(cont1);
                }
                if (!c2.isEmpty()) {
                    Contacto cont2 = new Contacto();
                    cont2.setContacto(c2);
                    listaCont.add(cont2);
                }
                c.setContacto(listaCont);

                final Cliente fCli = c;
                botaoGuardarAlteracoesEditar.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        clienteController.atualizar(fCli);
                        return true;
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoGuardarAlteracoesEditar.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        botaoGuardarAlteracoesEditar.setEnabled(true);
                    }
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de cliente inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarEditar.setIconTextGap(5);
        botaoCancelarEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        botaoEliminarEditar.setIconTextGap(5);
        botaoEliminarEditar.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoEliminarEditar.addActionListener(e -> eliminarClienteSelecionado());
        acoes.add(botaoGuardarAlteracoesEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoCancelarEditar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoEliminarEditar);
        card.add(acoes);

        return card;
    }

    private JPanel criarCardInformacoesCliente() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(LARGURA_CARD_LATERAL, d.height);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Informações do cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelNomeInformacoes = new JLabel("Ana Macie");
        labelNomeInformacoes.setFont(UiStyle.fonteSemiBold(14));
        labelNomeInformacoes.setForeground(UiStyle.TEXT);
        labelNomeInformacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelNomeInformacoes);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoTotalComprasInformacoes = new CampoFormularioArredondado("");
        campoTotalComprasInformacoes.setEditable(false);
        campoUltimaCompraInformacoes = new CampoFormularioArredondado("");
        campoUltimaCompraInformacoes.setEditable(false);
        JPanel linhaResumo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaResumo.setOpaque(false);
        linhaResumo.add(criarCampoComLabel("Total de compras", campoTotalComprasInformacoes));
        linhaResumo.add(criarCampoComLabel("Última compra", campoUltimaCompraInformacoes));
        fixarLargura(linhaResumo, larguraInterna, 60);
        card.add(linhaResumo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel labelHistorico = new JLabel("Histórico de vendas");
        labelHistorico.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelHistorico.setForeground(UiStyle.MUTED);
        labelHistorico.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelHistorico);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        tabelaHistoricoVendasInformacoes = criarMiniTabelaHistoricoVendas();
        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        JScrollPane scrollHistorico = new JScrollPane(tabelaHistoricoVendasInformacoes);
        scrollHistorico.setBorder(BorderFactory.createEmptyBorder());
        scrollHistorico.setOpaque(false);
        scrollHistorico.getViewport().setOpaque(false);
        scrollHistorico.getViewport().setBackground(UiStyle.SURFACE);
        tabelaWrapper.add(scrollHistorico, BorderLayout.CENTER);
        int alturaTabela = 29 + (3 * 30);
        fixarLargura(tabelaWrapper, larguraInterna, alturaTabela);
        card.add(tabelaWrapper);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoEditarInformacoes = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoEditarInformacoes.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botaoEditarInformacoes.setIconTextGap(6);
        botaoEditarInformacoes.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.EDITAR_CLIENTE));

        botaoFecharInformacoes = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoFecharInformacoes.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoFecharInformacoes.setIconTextGap(6);
        botaoFecharInformacoes.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoEditarInformacoes);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoFecharInformacoes);
        card.add(acoes);

        return card;
    }

    private JTable criarMiniTabelaHistoricoVendas() {
        DefaultTableModel modelo = new DefaultTableModel(new Object[0][COLUNAS_HISTORICO_VENDAS.length], COLUNAS_HISTORICO_VENDAS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable miniTabela = new JTable(modelo);
        miniTabela.setRowHeight(30);
        miniTabela.setFont(UiStyle.fonte(12, Font.PLAIN));
        miniTabela.setBackground(UiStyle.SURFACE);
        miniTabela.setForeground(UiStyle.TEXT);
        miniTabela.setShowVerticalLines(false);
        miniTabela.setShowHorizontalLines(true);
        miniTabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
        miniTabela.setIntercellSpacing(new Dimension(0, 1));
        miniTabela.setRowSelectionAllowed(false);

        JTableHeader header = miniTabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 29));
        header.setDefaultRenderer(new RenderizadorMiniCabecalho());

        RenderizadorMiniCelula renderizadorTexto = new RenderizadorMiniCelula();
        RenderizadorMiniChipEstado renderizadorChip = new RenderizadorMiniChipEstado();
        for (int i = 0; i < miniTabela.getColumnModel().getColumnCount(); i++) {
            if (i == 3) {
                miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorChip);
                miniTabela.getColumnModel().getColumn(i).setPreferredWidth(125);
            } else {
                miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }

        return miniTabela;
    }

    private void eliminarClienteSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o cliente #" + idStr + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idCliente = Long.parseLong(idStr);
            AsyncTask.executar(
                () -> {
                    clienteController.eliminar(idCliente);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Cliente #" + idStr + " eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao eliminar cliente: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Identificador de cliente inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sincronizarDadosClienteParaInformacoes() {
        if (labelNomeInformacoes == null) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            row = 0;
        }
        if (modeloTabela.getRowCount() == 0) {
            return;
        }

        String idStr = String.valueOf(modeloTabela.getValueAt(row, 0));
        String nome = String.valueOf(modeloTabela.getValueAt(row, 1));
        String totalCompras = String.valueOf(modeloTabela.getValueAt(row, 3));
        String ultimaCompra = String.valueOf(modeloTabela.getValueAt(row, 4));

        labelNomeInformacoes.setText(nome);
        campoTotalComprasInformacoes.setText(totalCompras);
        campoUltimaCompraInformacoes.setText(ultimaCompra);

        if (tabelaHistoricoVendasInformacoes != null) {
            DefaultTableModel modeloHist = (DefaultTableModel) tabelaHistoricoVendasInformacoes.getModel();
            modeloHist.setRowCount(0);
            try {
                Long idCliente = Long.parseLong(idStr);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                if (listaVendasReais != null) {
                    for (Venda v : listaVendasReais) {
                        if (v.getCliente() != null && idCliente.equals(v.getCliente().getIdCliente())) {
                            String vId = String.valueOf(v.getIdVenda());
                            String vData = v.getData() != null ? v.getData().format(dtf) : "-";
                            String vTot = String.format(Locale.GERMANY, "%,.2f MT", v.getTotal() != null ? v.getTotal() : 0.0);
                            String vEst = v.getEstado() != null ? v.getEstado().name() : "PENDENTE";
                            modeloHist.addRow(new Object[] {vId, vData, vTot, vEst});
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void sincronizarDadosClienteParaEdicao() {
        if (campoNomeEditar == null) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= modeloTabela.getRowCount()) {
            row = 0;
        }
        if (modeloTabela.getRowCount() == 0) {
            return;
        }

        String id = String.valueOf(modeloTabela.getValueAt(row, 0));
        String nome = String.valueOf(modeloTabela.getValueAt(row, 1));
        String contacto1 = String.valueOf(modeloTabela.getValueAt(row, 2));
        String contacto2 = "";

        try {
            Long idCli = Long.parseLong(id);
            if (listaClientesEntidades != null) {
                for (Cliente c : listaClientesEntidades) {
                    if (idCli.equals(c.getIdCliente())) {
                        if (c.getContacto() != null && !c.getContacto().isEmpty()) {
                            contacto1 = c.getContacto().get(0).getContacto() != null ? c.getContacto().get(0).getContacto() : "";
                            if (c.getContacto().size() > 1 && c.getContacto().get(1).getContacto() != null) {
                                contacto2 = c.getContacto().get(1).getContacto();
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        campoNomeEditar.setText(nome);
        campoContacto1Editar.setText("-".equals(contacto1) ? "" : contacto1);
        campoContacto2Editar.setText(contacto2);
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        if (campo instanceof javax.swing.text.JTextComponent
                && campo.getClientProperty(FlatClientProperties.PLACEHOLDER_TEXT) == null) {
            String dica = textoLabel.replaceAll("\\(.*?\\)", "").replace("*", "").trim().toLowerCase();
            campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Introduzir " + dica);
        }
        JPanel bloco = new JPanel(new BorderLayout(0, 6));
        bloco.setOpaque(false);

        JLabel label = new JLabel(textoLabel);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);

        Dimension dimCampo = new Dimension(0, ALTURA_INPUT);
        campo.setPreferredSize(dimCampo);
        campo.setMinimumSize(dimCampo);

        bloco.add(label, BorderLayout.NORTH);
        bloco.add(campo, BorderLayout.CENTER);
        return bloco;
    }

    private void fixarLargura(JComponent comp, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void atualizarTabelaFiltrada() {
        String termo = campoBuscarCliente.getTextoReal().trim().toLowerCase();
        modeloTabela.setRowCount(0);
        List<Object[]> fonteDados = (dadosClientesCarregados != null) ? dadosClientesCarregados : new ArrayList<>();

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo)) {
                modeloTabela.addRow(linha);
            }
        }
    }

    private static final class RenderizadorCabecalho extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.MUTED);
            label.setFont(UiStyle.fonteSemiBold(13));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                    new EmptyBorder(11, 14, 11, 14)
            ));
            return label;
        }
    }

    private static final class RenderizadorCelulaTexto extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(isSelected);
            label.setBackground(isSelected ? UiStyle.TABLE_SELECTION_BG : UiStyle.SURFACE);
            label.setForeground(UiStyle.TEXT);
            label.setFont(UiStyle.fonte(13, Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(11, 14, 11, 14)
            ));
            return label;
        }
    }

    private static final class RenderizadorMiniCabecalho extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.MUTED);
            label.setFont(UiStyle.fonteSemiBold(12));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.BORDER),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            return label;
        }
    }

    private static final class RenderizadorMiniCelula extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.TEXT);
            label.setFont(UiStyle.fonte(12, Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            return label;
        }
    }

    private static final class RenderizadorMiniChipEstado extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";

            Color bgChip;
            Color fgChip;
            if ("PENDENTE".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_YELLOW_BG;
                fgChip = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(texto) || "CONCLUIDA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.CHIP_GREEN_BG;
                fgChip = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(texto)) {
                bgChip = UiStyle.ERROR_BG;
                fgChip = UiStyle.ERROR_TEXT;
            } else {
                bgChip = UiStyle.CHIP_BLUE_BG;
                fgChip = UiStyle.CHIP_BLUE_FG;
            }

            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            container.setOpaque(false);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(4, 8, 4, 8)
            ));
            container.add(new ChipEstado(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class ChipEstado extends JLabel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;

        ChipEstado(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonteSemiBold(12));
            setForeground(corTexto);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class CampoFormularioArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoFormularioArredondado(String valorInicial) {
            super("");
            if (valorInicial != null && !valorInicial.isEmpty()) {
                putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, valorInicial);
            }
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    setForeground(UiStyle.TEXT);
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    setForeground(UiStyle.MUTED);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(isFocusOwner() ? UiStyle.ACCENT : UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class CampoBuscaArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoBuscaArredondado(String placeholder) {
            super("");
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.TEXT);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        }

        String getTextoReal() {
            return getText();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(isFocusOwner() ? UiStyle.ACCENT : UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1f, getHeight() - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class BotaoToolbarArredondado extends JButton {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final boolean preencherFundo;
        private final int raio;

        BotaoToolbarArredondado(
                String texto,
                Color corFundo,
                Color corTexto,
                Color corBorda,
                int raio,
                boolean preencherFundo) {
            super(texto);
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.raio = raio;
            this.preencherFundo = preencherFundo;
            setFont(UiStyle.fonteSemiBold(13));
            setForeground(corTexto);
            setBackground(corFundo);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, ALTURA_INPUT);
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                boolean ehPrimario = UiStyle.ACCENT.equals(corFundo);

                if (preencherFundo && corFundo != null) {
                    Color baseBg = ehPrimario ? UiStyle.ACCENT : UiStyle.SECONDARY_BG;
                    setForeground(ehPrimario ? UiStyle.ACCENT_TEXT : UiStyle.TEXT);
                    Color atual = baseBg;
                    if (getModel().isPressed()) {
                        atual = baseBg.darker();
                    } else if (getModel().isRollover()) {
                        atual = new Color(
                                Math.min(255, baseBg.getRed() + 10),
                                Math.min(255, baseBg.getGreen() + 10),
                                Math.min(255, baseBg.getBlue() + 10)
                        );
                    }
                    g2.setColor(atual);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                } else {
                    setForeground(UiStyle.DANGER_TEXT);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(0x4F, 0x1B, 0x1B, 40));
                        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                    }
                }

                if (corBorda != null) {
                    g2.setColor(preencherFundo ? UiStyle.INPUT_BORDER : UiStyle.DANGER_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static class PainelCardTabela extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final int raio;

        PainelCardTabela(Color corFundo, Color corBorda, int raio) {
            this.corFundo = corFundo;
            this.corBorda = corBorda;
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                super.paintComponent(g);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(UiStyle.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                super.paintChildren(g);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int arco = raio * 2;
                Shape clip = new RoundRectangle2D.Float(1, 1, Math.max(0, getWidth() - 2), Math.max(0, getHeight() - 2), arco, arco);
                g2.clip(clip);
                super.paintChildren(g2);
            } finally {
                g2.dispose();
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            if (Boolean.TRUE.equals(getClientProperty("UiStyle.internoScroll"))) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(UiStyle.BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
            } finally {
                g2.dispose();
            }
        }
    }

    public void abrirModoLateral(ModoPainelLateral modo) {
        definirModoPainelLateral(modo);
    }

    @Override
    public void recarregarDados() {
        AsyncTask.executar(
            () -> {
                List<Cliente> clientes = clienteController.listarTodos();
                List<Venda> vendas = new ArrayList<>();
                try {
                    vendas = clienteController.listarVendas();
                } catch (Exception ignored) {}
                return new Object[] { clientes, vendas };
            },
            resultado -> {
                Object[] arr = (Object[]) resultado;
                @SuppressWarnings("unchecked")
                List<Cliente> clientes = (List<Cliente>) arr[0];
                @SuppressWarnings("unchecked")
                List<Venda> vendas = (List<Venda>) arr[1];
                this.listaClientesEntidades = clientes != null ? clientes : new ArrayList<>();
                this.listaVendasReais = vendas != null ? vendas : new ArrayList<>();
                List<Object[]> linhas = new ArrayList<>();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                if (clientes != null) {
                    for (Cliente c : clientes) {
                        String idStr = String.valueOf(c.getIdCliente());
                        String nomeStr = c.getNome() != null ? c.getNome() : "";
                        String contStr = "-";
                        if (c.getContacto() != null && !c.getContacto().isEmpty()) {
                            List<String> nums = new ArrayList<>();
                            for (Contacto ct : c.getContacto()) {
                                if (ct != null && ct.getContacto() != null && !ct.getContacto().isBlank()) {
                                    nums.add(ct.getContacto().trim());
                                }
                            }
                            if (!nums.isEmpty()) {
                                contStr = String.join(" / ", nums);
                            }
                        }
                        double somaCompras = 0.0;
                        java.time.LocalDateTime ultimaData = null;
                        for (Venda v : this.listaVendasReais) {
                            if (v.getCliente() != null && c.getIdCliente() != null && c.getIdCliente().equals(v.getCliente().getIdCliente())) {
                                if (v.getEstado() == EstadoVenda.CONCLUIDA && v.getTotal() != null) {
                                    somaCompras += v.getTotal();
                                }
                                if (v.getData() != null && (ultimaData == null || v.getData().isAfter(ultimaData))) {
                                    ultimaData = v.getData();
                                }
                            }
                        }
                        String totStr = String.format(Locale.GERMANY, "%,.2f MT", somaCompras);
                        String ultStr = ultimaData != null ? ultimaData.format(dtf) : "-";
                        linhas.add(new Object[] {idStr, nomeStr, contStr, totStr, ultStr});
                    }
                }
                this.dadosClientesCarregados = linhas;
                atualizarTabelaFiltrada();
            },
            erro -> {
                this.listaClientesEntidades = new ArrayList<>();
                this.listaVendasReais = new ArrayList<>();
                this.dadosClientesCarregados = new ArrayList<>();
                atualizarTabelaFiltrada();
            }
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            if (botaoEliminar != null) botaoEliminar.setVisible(false);
        } else {
            if (botaoEliminar != null) botaoEliminar.setVisible(true);
        }
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardNovoCliente);
        UiStyle.atualizarComponentesTema(cardEditarCliente);
        UiStyle.atualizarComponentesTema(cardInformacoesCliente);
    }
}
