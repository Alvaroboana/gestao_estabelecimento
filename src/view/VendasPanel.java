package view;

import com.formdev.flatlaf.FlatClientProperties;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.awt.event.ItemEvent;
import javax.swing.JComboBox;
import controller.VendaController;
import model.vo.Cliente;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Perfil;
import model.vo.Produto;
import model.vo.Reembolso;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import model.vo.Venda;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import org.kordamp.ikonli.materialdesign2.MaterialDesignU;
import util.AsyncTask;
import util.GeradorFaturaPDF;
import util.ServidorLeitorCodigoBarras;

public class VendasPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoPainelLateral {
        NENHUM,
        NOVA_VENDA,
        CONFIRMAR_ANULAR,
        REEMBOLSAR_ITEM
    }

    private static final String CARD_NOVA_VENDA = "NOVA_VENDA";
    private static final String CARD_CONFIRMAR_ANULAR = "CONFIRMAR_ANULAR";
    private static final String CARD_REEMBOLSAR_ITEM = "REEMBOLSAR_ITEM";

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_INPUT = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] COLUNAS_COMPLETAS = {
            "ID", "Data", "Total", "Estado", "Cliente", "Funcionário", "Itens"
    };

    private static final String[] COLUNAS_COMPACTAS = {
            "ID", "Data", "Total", "Estado", "Cliente"
    };

    private static final String[] COLUNAS_ITENS = {
            "Item", "Qtd", "Subtotal", ""
    };

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private final BotaoSeletorEstado seletorEstado;
    private final CampoBuscaArredondado campoBuscaId;
    private final JButton botaoNovaVenda;
    private final BotaoToolbarArredondado botaoConcluirPendente;
    private final BotaoToolbarArredondado botaoEditarPendente;
    private final BotaoToolbarArredondado botaoRecibo;
    private final BotaoToolbarArredondado botaoAnular;
    private final BotaoToolbarArredondado botaoReembolsar;
    private final JPanel containerToolbarBotoes;
    private final JPanel cardNovaVenda;
    private final JPanel cardConfirmarAnular;
    private final JPanel cardReembolsarItem;
    private final CardLayout layoutLateral;
    private final JPanel containerLateral;

    private JLabel labelVendaSelecionada;
    private ChipEstado chipEstadoAnular;
    private JLabel labelValorClienteAnular;
    private JLabel labelValorTotalAnular;

    private JLabel labelVendaReembolso;
    private ChipEstado chipEstadoReembolso;

    private JComboBox<String> comboClienteNV;
    private JComboBox<String> comboProdutoNV;
    private JTextField campoFiltroProdutoNV;
    private JTextField campoCodigoBarrasNV;
    private JLabel labelInfoProdutoNV;
    private JTextField campoQtdNV;
    private JTextField campoDescontoNV;
    private JTextField campoValorRecebidoNV;
    private JLabel labelTotalVendaNV;
    private JLabel labelTrocoVendaNV;
    private DefaultTableModel modeloItensVenda;
    private List<ItemVendido> itensCarrinho = new ArrayList<>();
    private List<Cliente> listaClientesNV = new ArrayList<>();
    private List<Produto> listaProdutosNV = new ArrayList<>();
    private List<Produto> listaProdutosFiltradosNV = new ArrayList<>();
    private Venda vendaEmEdicaoPendente = null;

    private JComboBox<String> comboItemReembolso;
    private JTextField campoQtdReembolso;
    private JLabel labelJaReembolsadoNV;
    private JTextField campoMotivoReembolso;
    private JLabel labelValorReembolsoNV;
    private List<ItemVendido> itensVendaSelecionada = new ArrayList<>();

    private final VendaController vendaController;
    private List<Object[]> dadosVendasCarregados = new ArrayList<>();
    private List<Venda> listaVendasEntidades = new ArrayList<>();

    private ModoPainelLateral modoLateral;
    private String estadoFiltro = "Todos";

    public VendasPanel() {
        this(ModoPainelLateral.NENHUM);
    }

    public VendasPanel(boolean exibirNovaVendaInicialmente) {
        this(exibirNovaVendaInicialmente ? ModoPainelLateral.NOVA_VENDA : ModoPainelLateral.NENHUM);
    }

    public VendasPanel(ModoPainelLateral modoInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoLateral = modoInicial != null ? modoInicial : ModoPainelLateral.NENHUM;
        this.vendaController = new VendaController();

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.seletorEstado = criarSeletorEstado();
        this.campoBuscaId = criarCampoBuscaId();
        this.botaoNovaVenda = criarBotaoNovaVenda();
        this.botaoConcluirPendente = criarBotaoConcluirPendente();
        this.botaoEditarPendente = criarBotaoEditarPendente();
        this.botaoRecibo = criarBotaoRecibo();
        this.botaoReembolsar = criarBotaoReembolsar();
        this.botaoAnular = criarBotaoAnular();
        this.containerToolbarBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerToolbarBotoes.setOpaque(false);

        this.modeloTabela = criarModeloTabela();
        this.tabela = criarTabela(this.modeloTabela);
        this.cardNovaVenda = criarCardNovaVenda();
        this.cardConfirmarAnular = criarCardConfirmarAnular();
        this.cardReembolsarItem = criarCardReembolsarItem();

        this.layoutLateral = new CardLayout();
        this.containerLateral = new JPanel(layoutLateral);
        this.containerLateral.setOpaque(false);
        this.containerLateral.add(UiStyle.envolverCardLateralComScroll(cardNovaVenda), CARD_NOVA_VENDA);
        this.containerLateral.add(UiStyle.envolverCardLateralComScroll(cardConfirmarAnular), CARD_CONFIRMAR_ANULAR);
        this.containerLateral.add(UiStyle.envolverCardLateralComScroll(cardReembolsarItem), CARD_REEMBOLSAR_ITEM);

        this.tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosVendaParaAnular();
                sincronizarDadosVendaParaReembolso();
            }
        });

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarCorpoPrincipal(), BorderLayout.CENTER);

        definirModoPainelLateral(this.modoLateral);
        recarregarDados();
    }

    public JTable getTabela() {
        return tabela;
    }

    public JTextField getCampoBuscaId() {
        return campoBuscaId;
    }

    public JButton getBotaoNovaVenda() {
        return botaoNovaVenda;
    }

    public JButton getBotaoAnular() {
        return botaoAnular;
    }

    public JButton getBotaoReembolsar() {
        return botaoReembolsar;
    }

    public JButton getBotaoConcluirPendente() {
        return botaoConcluirPendente;
    }

    public JPanel getCardNovaVenda() {
        return cardNovaVenda;
    }

    public JPanel getCardConfirmarAnular() {
        return cardConfirmarAnular;
    }

    public JPanel getCardReembolsarItem() {
        return cardReembolsarItem;
    }

    public boolean isNovaVendaVisivel() {
        return modoLateral == ModoPainelLateral.NOVA_VENDA;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public final void definirNovaVendaVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVA_VENDA : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelAnular() {
        definirModoPainelLateral(ModoPainelLateral.CONFIRMAR_ANULAR);
    }

    public final void exibirPainelReembolso() {
        definirModoPainelLateral(ModoPainelLateral.REEMBOLSAR_ITEM);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean lateralAberto = this.modoLateral != ModoPainelLateral.NENHUM;
        containerLateral.setVisible(lateralAberto);

        if (this.modoLateral == ModoPainelLateral.NOVA_VENDA) {
            layoutLateral.show(containerLateral, CARD_NOVA_VENDA);
        } else if (this.modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR) {
            layoutLateral.show(containerLateral, CARD_CONFIRMAR_ANULAR);
        } else if (this.modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM) {
            layoutLateral.show(containerLateral, CARD_REEMBOLSAR_ITEM);
        }

        if (lateralAberto) {
            botaoAnular.configurarEstilo(new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false);
        } else {
            botaoAnular.configurarEstilo(UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, true);
        }

        atualizarEstruturaTabela();
        atualizarTabelaFiltrada();

        if (this.modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR && tabela.getRowCount() > 0) {
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosVendaParaAnular();
        } else if (this.modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM && tabela.getRowCount() > 0) {
            int indiceVenda1041 = Math.min(1, tabela.getRowCount() - 1);
            if (tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(indiceVenda1041, indiceVenda1041);
            }
            sincronizarDadosVendaParaReembolso();
        } else {
            tabela.clearSelection();
        }

        revalidate();
        repaint();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Vendas");
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

        toolbar.add(seletorEstado);
        toolbar.add(campoBuscaId);
        toolbar.add(botaoNovaVenda);
        toolbar.add(botaoConcluirPendente);
        toolbar.add(botaoEditarPendente);
        toolbar.add(botaoRecibo);
        toolbar.add(botaoAnular);
        toolbar.add(botaoReembolsar);

        cabecalho.add(toolbar);
        return cabecalho;
    }

    private JPanel criarCorpoPrincipal() {
        JPanel linha = new JPanel(new BorderLayout(16, 0));
        linha.setOpaque(false);
        linha.add(criarCardTabela(), BorderLayout.CENTER);

        JPanel wrapperDireita = new JPanel(new BorderLayout());
        wrapperDireita.setOpaque(false);
        wrapperDireita.add(containerLateral, BorderLayout.CENTER);
        linha.add(wrapperDireita, BorderLayout.EAST);

        return linha;
    }

    private BotaoSeletorEstado criarSeletorEstado() {
        BotaoSeletorEstado botao = new BotaoSeletorEstado("Estado: Todos");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_INPUT);
        botao.setPreferredSize(dim);
        botao.setMinimumSize(dim);

        String[] opcoes = { "Todos", "PENDENTE", "CONCLUIDA", "ANULADA", "REEMBOLSADA" };
        botao.addActionListener(e -> {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(UiStyle.SURFACE);
            menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
            for (String opcao : opcoes) {
                JMenuItem item = new JMenuItem(opcao);
                item.setFont(UiStyle.fonte(13, Font.PLAIN));
                item.setBackground(UiStyle.SURFACE);
                item.setForeground(UiStyle.TEXT);
                item.addActionListener(ev -> {
                    estadoFiltro = opcao;
                    botao.setText("Estado: " + opcao);
                    atualizarTabelaFiltrada();
                });
                menu.add(item);
            }
            UiStyle.mostrarPopupSincronizado(menu, botao);
        });
        return botao;
    }

    private CampoBuscaArredondado criarCampoBuscaId() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar por ID");
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

    private JButton criarBotaoNovaVenda() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Nova venda", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true);
        botao.setIcon(UiStyle.icone(MaterialDesignC.CART_PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVA_VENDA) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVA_VENDA);
            }
        });
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoConcluirPendente() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Concluir pendente", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CHECK_CIRCLE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> concluirVendaPendenteSelecionada());
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoEditarPendente() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar pendente", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLIPBOARD_TEXT, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> carregarVendaPendenteParaEdicao());
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoRecibo() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Fatura / Recibo", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLIPBOARD_TEXT, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> exibirReciboVendaSelecionada());
        return botao;
    }

    private void carregarVendaPendenteParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda PENDENTE na tabela para editar os itens.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Long idVenda = Long.parseLong(String.valueOf(tabela.getValueAt(row, 0)));
            Venda vendaAlvo = (listaVendasEntidades != null) ? listaVendasEntidades.stream()
                    .filter(v -> idVenda.equals(v.getIdVenda()))
                    .findFirst().orElse(null) : null;
            if (vendaAlvo == null) {
                vendaAlvo = vendaController.buscarPorId(idVenda);
            }
            if (vendaAlvo == null || vendaAlvo.getEstado() != EstadoVenda.PENDENTE) {
                JOptionPane.showMessageDialog(this, "Apenas vendas com estado PENDENTE podem ser reabertas para edição.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            this.vendaEmEdicaoPendente = vendaAlvo;
            this.itensCarrinho.clear();
            if (modeloItensVenda != null) {
                modeloItensVenda.setRowCount(0);
            }
            if (vendaAlvo.getItemVendido() != null) {
                for (ItemVendido it : vendaAlvo.getItemVendido()) {
                    if (it == null) continue;
                    ItemVendido copia = new ItemVendido(it.getQuantidade(), it.getPreco(), it.getDesconto(), vendaAlvo, it.getProduto());
                    itensCarrinho.add(copia);
                    double sub = Math.max(0.0, (it.getPreco() != null ? it.getPreco() : 0.0) * (it.getQuantidade() != null ? it.getQuantidade() : 1)
                            - (it.getDesconto() != null ? it.getDesconto() : 0.0));
                    String nomeProd = (it.getProduto() != null && it.getProduto().getNome() != null) ? it.getProduto().getNome() : "Item";
                    modeloItensVenda.addRow(new Object[] {
                        nomeProd,
                        String.valueOf(it.getQuantidade() != null ? it.getQuantidade() : 1),
                        String.format(Locale.GERMANY, "%,.2f MT", sub),
                        "✕"
                    });
                }
            }
            if (comboClienteNV != null && vendaAlvo.getCliente() != null && vendaAlvo.getCliente().getNome() != null) {
                comboClienteNV.setSelectedItem(vendaAlvo.getCliente().getNome());
            }
            atualizarTotalNV();
            definirModoPainelLateral(ModoPainelLateral.NOVA_VENDA);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir venda pendente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void concluirVendaPendenteSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda PENDENTE na tabela para concluir.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idStr = String.valueOf(tabela.getValueAt(row, 0));
        try {
            Long idVenda = Long.parseLong(idStr);
            Venda vendaAlvo = (listaVendasEntidades != null) ? listaVendasEntidades.stream()
                    .filter(v -> v.getIdVenda() != null && v.getIdVenda().equals(idVenda))
                    .findFirst()
                    .orElse(null) : null;
            if (vendaAlvo == null) {
                vendaAlvo = vendaController.buscarPorId(idVenda);
            }
            if (vendaAlvo.getEstado() != EstadoVenda.PENDENTE) {
                JOptionPane.showMessageDialog(this, "Apenas vendas com estado PENDENTE podem ser concluídas.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja concluir a Venda #" + idVenda + " e efetuar a baixa automática no estoque (FEFO)?",
                    "Concluir Venda Pendente",
                    JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) {
                return;
            }
            final Venda fVenda = vendaAlvo;
            Utilizador operador = SessaoUtilizador.getUtilizador();
            botaoConcluirPendente.setEnabled(false);
            AsyncTask.executar(
                    () -> {
                        vendaController.concluirVenda(fVenda, operador);
                        return true;
                    },
                    sucesso -> {
                        botaoConcluirPendente.setEnabled(true);
                        exibirDialogoRecibo(fVenda, fVenda.getTotal() != null ? fVenda.getTotal() : 0.0, 0.0);
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                    },
                    erro -> {
                        botaoConcluirPendente.setEnabled(true);
                        JOptionPane.showMessageDialog(this, "Erro ao concluir venda: " + erro.getMessage(), "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar venda selecionada: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private BotaoToolbarArredondado criarBotaoReembolsar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Reembolsar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignU.UNDO_VARIANT, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.REEMBOLSAR_ITEM) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                tabela.clearSelection();
                definirModoPainelLateral(ModoPainelLateral.REEMBOLSAR_ITEM);
            }
        });
        return botao;
    }

    private BotaoToolbarArredondado criarBotaoAnular() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Anular", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false);
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CANCEL, 15,
                () -> botao.preencherFundo ? UiStyle.TEXT : UiStyle.DANGER_TEXT));
        botao.setIconTextGap(6);
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.CONFIRMAR_ANULAR) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                tabela.clearSelection();
                definirModoPainelLateral(ModoPainelLateral.CONFIRMAR_ANULAR);
            }
        });
        return botao;
    }

    private DefaultTableModel criarModeloTabela() {
        return new DefaultTableModel(new Object[0][COLUNAS_COMPACTAS.length], COLUNAS_COMPACTAS) {
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

        aplicarRenderizadoresColunas(t);
        return t;
    }

    private void aplicarRenderizadoresColunas(JTable t) {
        RenderizadorCelulaTexto renderizadorTexto = new RenderizadorCelulaTexto();
        RenderizadorCelulaEstado renderizadorEstado = new RenderizadorCelulaEstado();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 3) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorEstado);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
            }
        }
    }

    private void atualizarEstruturaTabela() {
        boolean lateralAberto = modoLateral != ModoPainelLateral.NENHUM;
        String[] colunas = lateralAberto ? COLUNAS_COMPACTAS : COLUNAS_COMPLETAS;
        modeloTabela.setColumnIdentifiers(colunas);
        aplicarRenderizadoresColunas(tabela);
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

    private JPanel criarCardNovaVenda() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Nova venda");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        comboClienteNV = new JComboBox<>();
        estilizarCombo(comboClienteNV);
        JPanel linhaClienteComMais = new JPanel(new BorderLayout(6, 0));
        linhaClienteComMais.setOpaque(false);
        BotaoToolbarArredondado btnMaisCliente = new BotaoToolbarArredondado(
                "", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        btnMaisCliente.setBorder(new EmptyBorder(0, 10, 0, 10));
        btnMaisCliente.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PLUS, 14, () -> UiStyle.TEXT));
        btnMaisCliente.setToolTipText("Novo cliente rápido");
        btnMaisCliente.addActionListener(e -> abrirDialogoNovoClienteRapido());
        linhaClienteComMais.add(comboClienteNV, BorderLayout.CENTER);
        linhaClienteComMais.add(btnMaisCliente, BorderLayout.EAST);
        JPanel blocoCliente = criarCampoComLabel("Cliente (padrão: Consumidor Final)", linhaClienteComMais);
        fixarLargura(blocoCliente, larguraInterna, 60);
        card.add(blocoCliente);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoCodigoBarrasNV = new JTextField();
        estilizarCampo(campoCodigoBarrasNV);
        campoCodigoBarrasNV.setToolTipText("Aponte o leitor USB ou digite o código de barras e prima Enter");
        campoCodigoBarrasNV.addActionListener(e -> {
            String cod = campoCodigoBarrasNV.getText() != null ? campoCodigoBarrasNV.getText().trim() : "";
            if (!cod.isEmpty()) {
                processarCodigoBarras(cod);
                campoCodigoBarrasNV.setText("");
            }
        });
        BotaoToolbarArredondado btnLeitorCelular = new BotaoToolbarArredondado(
                "Leitor Celular", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        btnLeitorCelular.setBorder(new EmptyBorder(0, 10, 0, 10));
        btnLeitorCelular.addActionListener(e -> abrirDialogoLeitorCelular());
        JPanel linhaCodigoBarras = new JPanel(new BorderLayout(6, 0));
        linhaCodigoBarras.setOpaque(false);
        linhaCodigoBarras.add(campoCodigoBarrasNV, BorderLayout.CENTER);
        linhaCodigoBarras.add(btnLeitorCelular, BorderLayout.EAST);
        JPanel blocoCodigoBarras = criarCampoComLabel("Código de barras (Leitor USB / Enter ou Celular)", linhaCodigoBarras);
        fixarLargura(blocoCodigoBarras, larguraInterna, 60);
        card.add(blocoCodigoBarras);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        campoFiltroProdutoNV = new JTextField();
        estilizarCampo(campoFiltroProdutoNV);
        campoFiltroProdutoNV.setToolTipText("Digite parte do nome, ID ou código de barras para filtrar a lista de produtos");
        campoFiltroProdutoNV.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrarComboProdutosNV(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrarComboProdutosNV(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarComboProdutosNV(); }
        });
        JPanel blocoFiltroProd = criarCampoComLabel("Pesquisar produto (nome, ID ou código)", campoFiltroProdutoNV);
        fixarLargura(blocoFiltroProd, larguraInterna, 60);
        card.add(blocoFiltroProd);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        JPanel linhaProduto = new JPanel(new GridBagLayout());
        linhaProduto.setOpaque(false);
        linhaProduto.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboProdutoNV = new JComboBox<>();
        estilizarCombo(comboProdutoNV);

        campoQtdNV = new JTextField("1");
        estilizarCampo(campoQtdNV);

        campoDescontoNV = new JTextField("0");
        estilizarCampo(campoDescontoNV);

        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.gridx = 0;
        gbcProd.gridy = 0;
        gbcProd.weightx = 2.0;
        gbcProd.fill = GridBagConstraints.HORIZONTAL;
        gbcProd.insets = new Insets(0, 0, 0, 8);
        linhaProduto.add(criarCampoComLabel("Produto", comboProdutoNV), gbcProd);

        GridBagConstraints gbcQtd = new GridBagConstraints();
        gbcQtd.gridx = 1;
        gbcQtd.gridy = 0;
        gbcQtd.weightx = 1.0;
        gbcQtd.fill = GridBagConstraints.HORIZONTAL;
        gbcQtd.insets = new Insets(0, 0, 0, 8);
        linhaProduto.add(criarCampoComLabel("Qtd", campoQtdNV), gbcQtd);

        GridBagConstraints gbcDesc = new GridBagConstraints();
        gbcDesc.gridx = 2;
        gbcDesc.gridy = 0;
        gbcDesc.weightx = 1.0;
        gbcDesc.fill = GridBagConstraints.HORIZONTAL;
        linhaProduto.add(criarCampoComLabel("Desc. (MT/%)", campoDescontoNV), gbcDesc);

        fixarLargura(linhaProduto, larguraInterna, 60);
        card.add(linhaProduto);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        labelInfoProdutoNV = new JLabel("Preço unit.: - | Stock disponível: -");
        labelInfoProdutoNV.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelInfoProdutoNV.setForeground(UiStyle.MUTED);
        labelInfoProdutoNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelInfoProdutoNV);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        comboProdutoNV.addActionListener(e -> atualizarInfoProdutoSelecionadoNV());

        BotaoToolbarArredondado botaoAdicionarItem = new BotaoToolbarArredondado(
                "Adicionar item", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botaoAdicionarItem.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PLUS, 15, () -> UiStyle.TEXT));
        botaoAdicionarItem.setIconTextGap(6);
        botaoAdicionarItem.setHorizontalAlignment(SwingConstants.CENTER);
        fixarLargura(botaoAdicionarItem, larguraInterna, 38);
        card.add(botaoAdicionarItem);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel miniTabelaPanel = criarMiniTabelaItens(larguraInterna);
        card.add(miniTabelaPanel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel linhaTotal = new JPanel(new BorderLayout());
        linhaTotal.setOpaque(false);
        linhaTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiStyle.BORDER),
                new EmptyBorder(8, 0, 0, 0)));
        JLabel labelTextoTotal = new JLabel("Total");
        labelTextoTotal.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelTextoTotal.setForeground(UiStyle.MUTED);
        labelTotalVendaNV = new JLabel("0,00 MT");
        labelTotalVendaNV.setFont(UiStyle.fonte(20, Font.BOLD));
        labelTotalVendaNV.setForeground(UiStyle.TEXT);
        linhaTotal.add(labelTextoTotal, BorderLayout.WEST);
        linhaTotal.add(labelTotalVendaNV, BorderLayout.EAST);
        fixarLargura(linhaTotal, larguraInterna, 34);
        card.add(linhaTotal);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel linhaPagamento = new JPanel(new GridLayout(1, 2, 8, 0));
        linhaPagamento.setOpaque(false);

        campoValorRecebidoNV = new JTextField("0");
        estilizarCampo(campoValorRecebidoNV);

        labelTrocoVendaNV = new JLabel("0,00 MT");
        labelTrocoVendaNV.setFont(UiStyle.fonte(13, Font.BOLD));
        labelTrocoVendaNV.setForeground(UiStyle.CHIP_YELLOW_FG);
        JPanel caixaTroco = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int arco = RAIO_COMPONENTE * 2;
                    g2.setColor(UiStyle.CHIP_YELLOW_BG);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arco, arco));
                    labelTrocoVendaNV.setForeground(UiStyle.CHIP_YELLOW_FG);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        caixaTroco.setOpaque(false);
        caixaTroco.setBorder(new EmptyBorder(0, 12, 0, 12));
        caixaTroco.add(labelTrocoVendaNV, BorderLayout.CENTER);

        linhaPagamento.add(criarCampoComLabel("Valor recebido (MT)", campoValorRecebidoNV));
        linhaPagamento.add(criarCampoComLabel("Troco", caixaTroco));
        fixarLargura(linhaPagamento, larguraInterna, 60);
        card.add(linhaPagamento);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel barraAcoesCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraAcoesCard.setOpaque(false);

        BotaoToolbarArredondado botaoFinalizar = new BotaoToolbarArredondado(
                "Finalizar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true);
        botaoFinalizar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoFinalizar.setIconTextGap(5);
        botaoFinalizar.setBorder(new EmptyBorder(9, 10, 9, 10));
        BotaoToolbarArredondado botaoGuardarPendente = new BotaoToolbarArredondado(
                "Guardar pendente", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botaoGuardarPendente.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLIPBOARD_TEXT, 14, () -> UiStyle.TEXT));
        botaoGuardarPendente.setIconTextGap(5);
        botaoGuardarPendente.setBorder(new EmptyBorder(9, 10, 9, 10));
        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 14, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(5);
        botaoCancelar.setBorder(new EmptyBorder(9, 10, 9, 10));

        barraAcoesCard.add(botaoFinalizar);
        barraAcoesCard.add(Box.createRigidArea(new Dimension(6, 0)));
        barraAcoesCard.add(botaoGuardarPendente);
        barraAcoesCard.add(Box.createRigidArea(new Dimension(6, 0)));
        barraAcoesCard.add(botaoCancelar);

        barraAcoesCard.setPreferredSize(new Dimension(larguraInterna, 38));
        barraAcoesCard.setMinimumSize(new Dimension(larguraInterna, 38));
        barraAcoesCard.setMaximumSize(new Dimension(larguraInterna, 38));
        barraAcoesCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(barraAcoesCard);

        campoValorRecebidoNV.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcularTroco();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcularTroco();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcularTroco();
            }
        });

        botaoAdicionarItem.addActionListener(e -> {
            int idx = comboProdutoNV.getSelectedIndex();
            List<Produto> fonte = !listaProdutosFiltradosNV.isEmpty() ? listaProdutosFiltradosNV : listaProdutosNV;
            if (idx < 0 || idx >= fonte.size()) {
                JOptionPane.showMessageDialog(this, "Selecione um produto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int qtd;
            try {
                qtd = Integer.parseInt(campoQtdNV.getText().trim());
                if (qtd <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Produto prod = fonte.get(idx);
            double preco = prod.getPreco() != null ? prod.getPreco() : 0.0;
            double bruto = preco * qtd;
            double desconto = 0;
            try {
                String descTxt = campoDescontoNV.getText() != null ? campoDescontoNV.getText().trim() : "0";
                if (descTxt.endsWith("%")) {
                    double perc = Double.parseDouble(descTxt.substring(0, descTxt.length() - 1).trim().replace(",", "."));
                    if (perc < 0 || perc > 100) {
                        JOptionPane.showMessageDialog(this, "O desconto percentual deve estar entre 0% e 100%.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    desconto = (bruto * perc) / 100.0;
                } else if (!descTxt.isEmpty()) {
                    desconto = Double.parseDouble(descTxt.replace(",", "."));
                }
            } catch (NumberFormatException ignored) {
                JOptionPane.showMessageDialog(this, "Desconto inválido. Informe valor em MT ou percentagem (ex.: 10%).", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (desconto < 0 || desconto > bruto) {
                JOptionPane.showMessageDialog(this, "O desconto não pode ser negativo nem superior ao subtotal do item.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double subtotal = bruto - desconto;
            if (subtotal < 0)
                subtotal = 0;

            final double desc = desconto;
            ItemVendido item = new ItemVendido();
            item.setQuantidade(qtd);
            item.setPreco(preco);
            item.setDesconto(desc);
            item.setProduto(prod);
            itensCarrinho.add(item);

            String subtotalStr = String.format(java.util.Locale.GERMANY, "%,.2f MT", subtotal);
            modeloItensVenda.addRow(new Object[] { prod.getNome(), String.valueOf(qtd), subtotalStr, "✕" });
            atualizarTotalNV();
        });


        botaoFinalizar.addActionListener(e -> submeterNovaVenda(true, botaoFinalizar, botaoGuardarPendente));

        botaoGuardarPendente.addActionListener(e -> submeterNovaVenda(false, botaoFinalizar, botaoGuardarPendente));

        botaoCancelar.addActionListener(e -> {
            vendaEmEdicaoPendente = null;
            itensCarrinho.clear();
            if (modeloItensVenda != null)
                modeloItensVenda.setRowCount(0);
            atualizarTotalNV();
            definirNovaVendaVisivel(false);
        });

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, Math.max(pref.height, 500));
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void submeterNovaVenda(boolean concluirImediatamente, JButton botaoFinalizar,
            JButton botaoGuardarPendente) {
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione ao menos um item à venda.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Utilizador user = SessaoUtilizador.getUtilizador();
        if (user == null || user.getFuncionario() == null) {
            JOptionPane.showMessageDialog(this, "Sessão inválida. Faça login novamente.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int cliIdx = comboClienteNV != null ? comboClienteNV.getSelectedIndex() : -1;
        Cliente clienteSelecionado = null;
        if (cliIdx > 0 && (cliIdx - 1) < listaClientesNV.size()) {
            clienteSelecionado = listaClientesNV.get(cliIdx - 1);
        }
        final Cliente clientePreSelecionado = clienteSelecionado;

        Venda venda = new Venda();
        if (vendaEmEdicaoPendente != null && vendaEmEdicaoPendente.getIdVenda() != null) {
            venda.setIdVenda(vendaEmEdicaoPendente.getIdVenda());
        }
        venda.setEstado(EstadoVenda.PENDENTE);
        venda.setData(java.time.LocalDateTime.now());
        venda.setFuncionario(user.getFuncionario());

        List<ItemVendido> itensParaPersistir = new ArrayList<>();
        for (ItemVendido item : itensCarrinho) {
            ItemVendido copia = new ItemVendido(item.getQuantidade(), item.getPreco(), item.getDesconto(), venda,
                    item.getProduto());
            itensParaPersistir.add(copia);
        }
        venda.setItemVendido(itensParaPersistir);
        venda.setTotal(venda.calcularTotal());

        double valorRecebido = venda.getTotal() != null ? venda.getTotal() : 0.0;
        try {
            double dig = Double.parseDouble(campoValorRecebidoNV.getText().trim().replace(",", "."));
            if (dig > 0) {
                valorRecebido = dig;
            }
        } catch (Exception ignored) {}
        final double finalRecebido = valorRecebido;
        final double finalTroco = Math.max(0.0, finalRecebido - (venda.getTotal() != null ? venda.getTotal() : 0.0));

        botaoFinalizar.setEnabled(false);
        botaoGuardarPendente.setEnabled(false);
        AsyncTask.executar(
                () -> {
                    Cliente cliEfetivo = clientePreSelecionado != null
                            ? clientePreSelecionado
                            : vendaController.obterOuCriarConsumidorFinal();
                    venda.setCliente(cliEfetivo);
                    if (venda.getIdVenda() != null && venda.getIdVenda() > 0) {
                        vendaController.atualizarRascunho(venda, user);
                    }
                    if (concluirImediatamente) {
                        vendaController.concluirVenda(venda, user);
                    } else if (venda.getIdVenda() == null || venda.getIdVenda() <= 0) {
                        vendaController.salvarRascunho(venda, user);
                    }
                    return true;
                },
                res -> {
                    vendaEmEdicaoPendente = null;
                    if (concluirImediatamente) {
                        exibirDialogoRecibo(venda, finalRecebido, finalTroco);
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Venda #" + venda.getIdVenda() + " guardada como PENDENTE!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    itensCarrinho.clear();
                    modeloItensVenda.setRowCount(0);
                    if (campoValorRecebidoNV != null) campoValorRecebidoNV.setText("0");
                    if (campoDescontoNV != null) campoDescontoNV.setText("0");
                    atualizarTotalNV();
                    definirNovaVendaVisivel(false);
                    recarregarDados();
                    botaoFinalizar.setEnabled(true);
                    botaoGuardarPendente.setEnabled(true);
                },
                erro -> {
                    JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + erro.getMessage(), "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    botaoFinalizar.setEnabled(true);
                    botaoGuardarPendente.setEnabled(true);
                });
    }

    private void atualizarTotalNV() {
        double total = itensCarrinho.stream()
                .mapToDouble(it -> {
                    double p = it.getPreco() != null ? it.getPreco() : 0;
                    int q = it.getQuantidade() != null ? it.getQuantidade() : 0;
                    double d = it.getDesconto() != null ? it.getDesconto() : 0;
                    return p * q - d;
                }).sum();
        if (labelTotalVendaNV != null) {
            labelTotalVendaNV.setText(String.format(java.util.Locale.GERMANY, "%,.2f MT", total));
        }
        recalcularTroco();
    }

    private void filtrarComboProdutosNV() {
        if (comboProdutoNV == null) return;
        String termo = (campoFiltroProdutoNV != null && campoFiltroProdutoNV.getText() != null)
                ? campoFiltroProdutoNV.getText().trim().toLowerCase()
                : "";
        listaProdutosFiltradosNV.clear();
        comboProdutoNV.removeAllItems();
        for (Produto p : listaProdutosNV) {
            if (p == null) continue;
            String nome = p.getNome() != null ? p.getNome().toLowerCase() : "";
            String idStr = p.getIdProduto() != null ? String.valueOf(p.getIdProduto()) : "";
            String cb = p.getCodigoBarras() != null ? p.getCodigoBarras().toLowerCase() : "";
            if (termo.isEmpty() || nome.contains(termo) || idStr.equals(termo) || cb.contains(termo)) {
                listaProdutosFiltradosNV.add(p);
                String preco = p.getPreco() != null
                        ? String.format(Locale.GERMANY, "%,.2f MT", p.getPreco())
                        : "";
                comboProdutoNV.addItem(p.getNome() + " (" + preco + ")");
            }
        }
        atualizarInfoProdutoSelecionadoNV();
    }

    private void atualizarInfoProdutoSelecionadoNV() {
        if (labelInfoProdutoNV == null || comboProdutoNV == null) return;
        int idx = comboProdutoNV.getSelectedIndex();
        List<Produto> fonte = !listaProdutosFiltradosNV.isEmpty() ? listaProdutosFiltradosNV : listaProdutosNV;
        if (idx < 0 || idx >= fonte.size()) {
            labelInfoProdutoNV.setText("Preço unit.: - | Stock disponível: -");
            return;
        }
        Produto p = fonte.get(idx);
        double preco = p.getPreco() != null ? p.getPreco() : 0.0;
        String un = p.getUnidade() != null ? p.getUnidade() : "un";
        int stock = p.calcularQuantidadeEmEstoque();
        labelInfoProdutoNV.setText(String.format(
                Locale.GERMANY,
                "Preço unit.: %,.2f MT/%s  |  Stock disponível: %d %s",
                preco, un, stock, un
        ));
    }

    public void processarCodigoBarras(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return;
        String cod = codigo.trim();
        Produto encontrado = null;
        for (Produto p : listaProdutosNV) {
            if (p == null) continue;
            if ((p.getCodigoBarras() != null && p.getCodigoBarras().equalsIgnoreCase(cod))
                    || (p.getIdProduto() != null && String.valueOf(p.getIdProduto()).equals(cod))) {
                encontrado = p;
                break;
            }
        }
        if (encontrado == null) {
            try {
                encontrado = vendaController.buscarProdutoPorCodigoBarras(cod);
            } catch (Exception ignored) {}
        }
        if (encontrado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum produto encontrado para o código: " + cod, "Leitor de Código de Barras", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double preco = encontrado.getPreco() != null ? encontrado.getPreco() : 0.0;
        for (int i = 0; i < itensCarrinho.size(); i++) {
            ItemVendido existente = itensCarrinho.get(i);
            if (existente.getProduto() != null && encontrado.getIdProduto() != null
                    && encontrado.getIdProduto().equals(existente.getProduto().getIdProduto())) {
                int novaQtd = (existente.getQuantidade() != null ? existente.getQuantidade() : 1) + 1;
                existente.setQuantidade(novaQtd);
                double desc = existente.getDesconto() != null ? existente.getDesconto() : 0.0;
                double sub = Math.max(0.0, preco * novaQtd - desc);
                modeloItensVenda.setValueAt(String.valueOf(novaQtd), i, 1);
                modeloItensVenda.setValueAt(String.format(Locale.GERMANY, "%,.2f MT", sub), i, 2);
                atualizarTotalNV();
                return;
            }
        }
        ItemVendido novoItem = new ItemVendido();
        novoItem.setProduto(encontrado);
        novoItem.setQuantidade(1);
        novoItem.setPreco(preco);
        novoItem.setDesconto(0.0);
        itensCarrinho.add(novoItem);
        modeloItensVenda.addRow(new Object[] {
            encontrado.getNome(),
            "1",
            String.format(Locale.GERMANY, "%,.2f MT", preco),
            "✕"
        });
        atualizarTotalNV();
    }

    private void abrirDialogoLeitorCelular() {
        try {
            String url = ServidorLeitorCodigoBarras.iniciarOuAtualizarOuvinte(this::processarCodigoBarras);
            JOptionPane.showMessageDialog(
                    this,
                    "Leitor Mobile ativo na rede local!\n\n"
                    + "1. Conecte o telemóvel na mesma rede Wi-Fi/Hotspot deste computador.\n"
                    + "2. Abra o navegador do telemóvel e aceda a:\n\n   " + url + "\n\n"
                    + "Cada código lido pela câmera ou enviado no telemóvel será adicionado automaticamente ao carrinho desta venda.",
                    "Leitor de Código de Barras no Telemóvel",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível iniciar o servidor do leitor mobile: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirDialogoNovoClienteRapido() {
        JTextField campoNome = new JTextField();
        JTextField campoContacto = new JTextField();
        Object[] msg = {
            "Nome do cliente *:", campoNome,
            "Contacto *:", campoContacto
        };
        int op = JOptionPane.showConfirmDialog(this, msg, "Novo Cliente Rápido", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op != JOptionPane.OK_OPTION) return;
        String nome = campoNome.getText() != null ? campoNome.getText().trim() : "";
        String contacto = campoContacto.getText() != null ? campoContacto.getText().trim() : "";
        if (nome.isEmpty() || contacto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome e o contacto do cliente.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AsyncTask.executar(
            () -> vendaController.salvarClienteRapido(nome, contacto),
            criado -> {
                if (criado != null) {
                    listaClientesNV.add(criado);
                    if (comboClienteNV != null) {
                        comboClienteNV.addItem(criado.getNome());
                        comboClienteNV.setSelectedItem(criado.getNome());
                    }
                }
            },
            err -> JOptionPane.showMessageDialog(this, "Erro ao criar cliente: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void exibirReciboVendaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para visualizar o recibo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Long idVenda = Long.parseLong(String.valueOf(tabela.getValueAt(row, 0)));
            Venda vendaAlvo = (listaVendasEntidades != null) ? listaVendasEntidades.stream()
                    .filter(v -> idVenda.equals(v.getIdVenda()))
                    .findFirst().orElse(null) : null;
            if (vendaAlvo == null) {
                vendaAlvo = vendaController.buscarPorId(idVenda);
            }
            if (vendaAlvo != null) {
                double tot = vendaAlvo.getTotal() != null ? vendaAlvo.getTotal() : 0.0;
                exibirDialogoRecibo(vendaAlvo, tot, 0.0);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar recibo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirDialogoRecibo(Venda venda, double valorRecebido, double troco) {
        if (venda == null) return;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("            MERCEARIA KUMBEZA             \n");
        sb.append("           RECIBO DE PAGAMENTO            \n");
        sb.append("==========================================\n");
        sb.append("Venda Nº   : #").append(venda.getIdVenda() != null ? venda.getIdVenda() : "-").append("\n");
        sb.append("Data/Hora  : ").append(venda.getData() != null ? venda.getData().format(dtf) : "-").append("\n");
        sb.append("Estado     : ").append(venda.getEstado() != null ? venda.getEstado().name() : "CONCLUIDA").append("\n");
        String cliNome = (venda.getCliente() != null && venda.getCliente().getNome() != null)
                ? venda.getCliente().getNome() : "Consumidor Final";
        String funcNome = (venda.getFuncionario() != null && venda.getFuncionario().getNome() != null)
                ? venda.getFuncionario().getNome() : "-";
        sb.append("Cliente    : ").append(cliNome).append("\n");
        sb.append("Operador   : ").append(funcNome).append("\n");
        sb.append("------------------------------------------\n");
        sb.append(String.format("%-18s %4s %18s\n", "Item", "Qtd", "Subtotal"));
        sb.append("------------------------------------------\n");
        double somaDescontos = 0.0;
        if (venda.getItemVendido() != null) {
            for (ItemVendido it : venda.getItemVendido()) {
                if (it == null) continue;
                String nomeProd = (it.getProduto() != null && it.getProduto().getNome() != null)
                        ? it.getProduto().getNome() : "Item";
                if (nomeProd.length() > 18) {
                    nomeProd = nomeProd.substring(0, 18);
                }
                int q = it.getQuantidade() != null ? it.getQuantidade() : 1;
                double p = it.getPreco() != null ? it.getPreco() : 0.0;
                double d = it.getDesconto() != null ? it.getDesconto() : 0.0;
                somaDescontos += d;
                double sub = Math.max(0.0, p * q - d);
                sb.append(String.format("%-18s %4d %15s MT\n",
                        nomeProd, q, String.format(Locale.GERMANY, "%,.2f", sub)));
                if (d > 0) {
                    sb.append(String.format("  (Desc. item: -%s MT)\n", String.format(Locale.GERMANY, "%,.2f", d)));
                }
            }
        }
        sb.append("------------------------------------------\n");
        if (somaDescontos > 0) {
            sb.append(String.format("Total Descontos : %21s MT\n", String.format(Locale.GERMANY, "%,.2f", somaDescontos)));
        }
        double total = venda.getTotal() != null ? venda.getTotal() : 0.0;
        sb.append(String.format("TOTAL A PAGAR   : %21s MT\n", String.format(Locale.GERMANY, "%,.2f", total)));
        sb.append(String.format("Valor Recebido  : %21s MT\n", String.format(Locale.GERMANY, "%,.2f", valorRecebido)));
        sb.append(String.format("Troco           : %21s MT\n", String.format(Locale.GERMANY, "%,.2f", troco)));
        sb.append("==========================================\n");
        sb.append("       Obrigado pela sua preferência!     \n");
        sb.append("==========================================\n");

        JTextArea area = new JTextArea(sb.toString(), 22, 44);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setBackground(UiStyle.SURFACE);
        area.setForeground(UiStyle.TEXT);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        Object[] opcoes = {"Exportar Fatura PDF", "Imprimir Recibo", "Fechar"};
        int esc = JOptionPane.showOptionDialog(
                this,
                scroll,
                "Fatura / Recibo da Venda #" + (venda.getIdVenda() != null ? venda.getIdVenda() : ""),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );
        if (esc == JOptionPane.YES_OPTION) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar Fatura / Recibo em PDF");
            String nomeArq = "fatura_venda_" + (venda.getIdVenda() != null ? venda.getIdVenda() : "nova") + ".pdf";
            chooser.setSelectedFile(new File(nomeArq));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File dest = chooser.getSelectedFile();
                if (!dest.getName().toLowerCase().endsWith(".pdf")) {
                    dest = new File(dest.getParentFile(), dest.getName() + ".pdf");
                }
                try {
                    GeradorFaturaPDF.gerar(venda, valorRecebido, troco, dest);
                    JOptionPane.showMessageDialog(this, "Fatura PDF gerada com sucesso em:\n" + dest.getAbsolutePath(), "Fatura PDF", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception exPdf) {
                    JOptionPane.showMessageDialog(this, "Erro ao gerar Fatura PDF: " + exPdf.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (esc == JOptionPane.NO_OPTION) {
            try {
                area.print();
            } catch (Exception exPrint) {
                JOptionPane.showMessageDialog(this, "Não foi possível imprimir: " + exPrint.getMessage(), "Impressão", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    private void recalcularTroco() {
        if (labelTrocoVendaNV == null || labelTotalVendaNV == null)
            return;
        double total = 0;
        try {
            String t = labelTotalVendaNV.getText().replace("MT", "").replace(".", "").replace(",", ".").trim();
            total = Double.parseDouble(t);
        } catch (NumberFormatException ignored) {
        }
        double recebido = 0;
        try {
            recebido = Double.parseDouble(campoValorRecebidoNV.getText().trim().replace(",", "."));
        } catch (NumberFormatException ignored) {
        }
        double troco = recebido - total;
        labelTrocoVendaNV.setText(String.format(java.util.Locale.GERMANY, "%,.2f MT", Math.max(troco, 0)));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        UiStyle.estilizarComboBox(combo);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));
        combo.setPreferredSize(new Dimension(100, ALTURA_INPUT));
    }

    private void estilizarCampo(JTextField campo) {
        UiStyle.estilizarCampo(campo);
        campo.setPreferredSize(new Dimension(60, ALTURA_INPUT));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));
    }

    private JPanel criarCardConfirmarAnular() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Anular venda");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaVenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaVenda.setOpaque(false);

        labelVendaSelecionada = new JLabel("Venda 1042");
        labelVendaSelecionada.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelVendaSelecionada.setForeground(UiStyle.TEXT);

        chipEstadoAnular = new ChipEstado("PENDENTE", UiStyle.CHIP_YELLOW_BG, UiStyle.CHIP_YELLOW_FG);

        linhaVenda.add(labelVendaSelecionada);
        linhaVenda.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaVenda.add(chipEstadoAnular);
        fixarLargura(linhaVenda, larguraInterna, 24);
        card.add(linhaVenda);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        CaixaValorArredondada caixaCliente = new CaixaValorArredondada("Ana Macie");
        labelValorClienteAnular = caixaCliente.getLabelValor();
        JPanel blocoCliente = criarCampoComLabel("Cliente", caixaCliente);
        fixarLargura(blocoCliente, larguraInterna, 60);
        card.add(blocoCliente);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        CaixaValorArredondada caixaTotal = new CaixaValorArredondada("1.250,00 MT");
        labelValorTotalAnular = caixaTotal.getLabelValor();
        JPanel blocoTotal = criarCampoComLabel("Total", caixaTotal);
        fixarLargura(blocoTotal, larguraInterna, 60);
        card.add(blocoTotal);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel aviso = new JLabel(
                "<html><div style='width:370px;line-height:1.5'>Só vendas PENDENTES podem ser anuladas. Esta acção não pode ser desfeita.</div></html>");
        aviso.setFont(UiStyle.fonte(12, Font.PLAIN));
        aviso.setForeground(UiStyle.MUTED);
        fixarLargura(aviso, larguraInterna, 42);
        card.add(aviso);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);

        BotaoToolbarArredondado botaoConfirmar = new BotaoToolbarArredondado(
                "Confirmar anulação", UiStyle.ERROR_TEXT, UiStyle.ERROR_BG, null, RAIO_COMPONENTE, true);
        botaoConfirmar.markDestrutivoPreenchido();
        botaoConfirmar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CANCEL, 15, () -> UiStyle.ERROR_TEXT));
        botaoConfirmar.setIconTextGap(6);
        botaoConfirmar.addActionListener(e -> {
            int row = tabela.getSelectedRow();
            if (row < 0 || row >= tabela.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Selecione uma venda na tabela para anular.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            String idStr = String.valueOf(tabela.getValueAt(row, 0));
            try {
                Long idVenda = Long.parseLong(idStr);
                Venda vendaAlvo = (listaVendasEntidades != null) ? listaVendasEntidades.stream()
                        .filter(v -> v.getIdVenda().equals(idVenda))
                        .findFirst()
                        .orElse(null) : null;

                if (vendaAlvo == null) {
                    vendaAlvo = new Venda();
                    vendaAlvo.setIdVenda(idVenda);
                    vendaAlvo.setEstado(EstadoVenda.PENDENTE);
                }

                final Venda fVenda = vendaAlvo;
                Utilizador operador = SessaoUtilizador.getUtilizador();
                AsyncTask.executar(
                        () -> {
                            vendaController.anular(fVenda, operador);
                            return null;
                        },
                        sucesso -> {
                            JOptionPane.showMessageDialog(this, "Venda #" + idVenda + " anulada com sucesso!",
                                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            definirModoPainelLateral(ModoPainelLateral.NENHUM);
                            recarregarDados();
                        },
                        erro -> {
                            JOptionPane.showMessageDialog(this, "Erro ao anular venda: " + erro.getMessage(), "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                        });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Identificador de venda inválido.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        barraBotoes.add(botaoConfirmar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, pref.height);
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void sincronizarDadosVendaParaAnular() {
        if (labelVendaSelecionada == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = 0;
        }
        String id = String.valueOf(tabela.getValueAt(row, 0));
        String total = String.valueOf(tabela.getValueAt(row, 2));
        String estado = String.valueOf(tabela.getValueAt(row, 3));
        String cliente = String.valueOf(tabela.getValueAt(row, 4));

        labelVendaSelecionada.setText("Venda " + id);
        labelValorClienteAnular.setText(cliente);
        labelValorTotalAnular.setText(total);

        Color bgChip;
        Color fgChip;
        if ("PENDENTE".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.CHIP_YELLOW_BG;
            fgChip = UiStyle.CHIP_YELLOW_FG;
        } else if ("CONCLUÍDA".equalsIgnoreCase(estado) || "CONCLUIDA".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.CHIP_GREEN_BG;
            fgChip = UiStyle.CHIP_GREEN_FG;
        } else if ("ANULADA".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.ERROR_BG;
            fgChip = UiStyle.ERROR_TEXT;
        } else {
            bgChip = UiStyle.CHIP_BLUE_BG;
            fgChip = UiStyle.CHIP_BLUE_FG;
        }
        chipEstadoAnular.atualizarEstado(estado, bgChip, fgChip);
    }

    private JPanel criarCardReembolsarItem() {
        PainelCardTabela card = new PainelCardTabela(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Reembolsar item");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaVenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linhaVenda.setOpaque(false);
        labelVendaReembolso = new JLabel("Venda -");
        labelVendaReembolso.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelVendaReembolso.setForeground(UiStyle.TEXT);
        chipEstadoReembolso = new ChipEstado("CONCLUIDA", UiStyle.CHIP_GREEN_BG, UiStyle.CHIP_GREEN_FG);
        linhaVenda.add(labelVendaReembolso);
        linhaVenda.add(Box.createRigidArea(new Dimension(10, 0)));
        linhaVenda.add(chipEstadoReembolso);
        fixarLargura(linhaVenda, larguraInterna, 24);
        card.add(linhaVenda);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        comboItemReembolso = new JComboBox<>();
        estilizarCombo(comboItemReembolso);
        JPanel blocoItem = criarCampoComLabel("Item", comboItemReembolso);
        fixarLargura(blocoItem, larguraInterna, 60);
        card.add(blocoItem);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel linhaQtd = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaQtd.setOpaque(false);
        campoQtdReembolso = new JTextField("1");
        estilizarCampo(campoQtdReembolso);
        CaixaValorArredondada caixaJaReemb = new CaixaValorArredondada("0");
        labelJaReembolsadoNV = caixaJaReemb.getLabelValor();
        linhaQtd.add(criarCampoComLabel("Quantidade", campoQtdReembolso));
        linhaQtd.add(criarCampoComLabel("Já reembolsado", caixaJaReemb));
        fixarLargura(linhaQtd, larguraInterna, 60);
        card.add(linhaQtd);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoMotivoReembolso = new JTextField();
        campoMotivoReembolso.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Motivo do reembolso");
        campoMotivoReembolso.setToolTipText("Descreva o motivo do reembolso");
        estilizarCampo(campoMotivoReembolso);
        JPanel blocoMotivo = criarCampoComLabel("Motivo *", campoMotivoReembolso);
        fixarLargura(blocoMotivo, larguraInterna, 60);
        card.add(blocoMotivo);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel linhaValorReembolso = new JPanel(new BorderLayout());
        linhaValorReembolso.setOpaque(false);
        linhaValorReembolso.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UiStyle.BORDER),
                new EmptyBorder(12, 0, 6, 0)));
        JLabel labelTextoReembolso = new JLabel("Valor a reembolsar");
        labelTextoReembolso.setFont(UiStyle.fonte(14, Font.PLAIN));
        labelTextoReembolso.setForeground(UiStyle.TEXT);
        labelValorReembolsoNV = new JLabel("0,00 MT");
        labelValorReembolsoNV.setFont(UiStyle.fonte(20, Font.BOLD));
        labelValorReembolsoNV.setForeground(UiStyle.TEXT);
        linhaValorReembolso.add(labelTextoReembolso, BorderLayout.WEST);
        linhaValorReembolso.add(labelValorReembolsoNV, BorderLayout.EAST);
        fixarLargura(linhaValorReembolso, larguraInterna, 44);
        card.add(linhaValorReembolso);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);
        BotaoToolbarArredondado botaoConfirmar = new BotaoToolbarArredondado(
                "Confirmar reembolso", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true);
        botaoConfirmar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoConfirmar.setIconTextGap(6);
        BotaoToolbarArredondado botaoCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true);
        botaoCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelar.setIconTextGap(6);
        barraBotoes.add(botaoConfirmar);
        barraBotoes.add(Box.createRigidArea(new Dimension(10, 0)));
        barraBotoes.add(botaoCancelar);
        fixarLargura(barraBotoes, larguraInterna, 38);
        card.add(barraBotoes);

        DocumentListener recalc = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalcularValorReembolso();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalcularValorReembolso();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalcularValorReembolso();
            }
        };
        campoQtdReembolso.getDocument().addDocumentListener(recalc);
        comboItemReembolso.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                recalcularValorReembolso();
        });

        botaoConfirmar.addActionListener(e -> {
            int itemIdx = comboItemReembolso.getSelectedIndex();
            if (itemIdx < 0 || itemIdx >= itensVendaSelecionada.size()) {
                JOptionPane.showMessageDialog(this, "Selecione um item para reembolsar.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int qtd;
            try {
                qtd = Integer.parseInt(campoQtdReembolso.getText().trim());
                if (qtd <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String motivo = campoMotivoReembolso.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o motivo do reembolso.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            Utilizador user = SessaoUtilizador.getUtilizador();
            if (user == null || user.getFuncionario() == null) {
                JOptionPane.showMessageDialog(this, "Sessão inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ItemVendido itemAlvo = itensVendaSelecionada.get(itemIdx);
            Funcionario func = user.getFuncionario();
            botaoConfirmar.setEnabled(false);
            final int qtdFinal = qtd;
            AsyncTask.executar(
                    () -> {
                        return vendaController.processarReembolso(itemAlvo, qtdFinal, motivo, func);
                    },
                    res -> {
                        JOptionPane.showMessageDialog(this, "Reembolso processado com sucesso!", "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        campoMotivoReembolso.setText("");
                        definirModoPainelLateral(ModoPainelLateral.NENHUM);
                        recarregarDados();
                        botaoConfirmar.setEnabled(true);
                    },
                    erro -> {
                        JOptionPane.showMessageDialog(this, "Erro ao processar reembolso: " + erro.getMessage(), "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        botaoConfirmar.setEnabled(true);
                    });
        });
        botaoCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        Dimension pref = card.getPreferredSize();
        Dimension dimCard = new Dimension(LARGURA_CARD_LATERAL, Math.max(pref.height, 450));
        card.setPreferredSize(dimCard);
        card.setMinimumSize(dimCard);
        card.setMaximumSize(dimCard);
        return card;
    }

    private void recalcularValorReembolso() {
        if (labelValorReembolsoNV == null)
            return;
        int itemIdx = comboItemReembolso != null ? comboItemReembolso.getSelectedIndex() : -1;
        if (itemIdx < 0 || itemIdx >= itensVendaSelecionada.size()) {
            labelValorReembolsoNV.setText("0,00 MT");
            if (labelJaReembolsadoNV != null)
                labelJaReembolsadoNV.setText("0");
            return;
        }
        ItemVendido item = itensVendaSelecionada.get(itemIdx);
        int jaReembolsado = 0;
        if (item.getReembolso() != null) {
            for (Reembolso r : item.getReembolso()) {
                if (r != null && r.getQuantidade() != null) {
                    jaReembolsado += r.getQuantidade();
                }
            }
        }
        if (labelJaReembolsadoNV != null) {
            labelJaReembolsadoNV.setText(String.valueOf(jaReembolsado));
        }
        int qtd = 0;
        try {
            qtd = Integer.parseInt(campoQtdReembolso.getText().trim());
        } catch (NumberFormatException ignored) {
        }
        double preco = item.getPreco() != null ? item.getPreco() : 0;
        double desconto = item.getDesconto() != null ? item.getDesconto() : 0;
        double valor = preco * qtd;
        if (item.getQuantidade() != null && item.getQuantidade() > 0) {
            valor -= (desconto / item.getQuantidade()) * qtd;
        }
        labelValorReembolsoNV.setText(String.format(java.util.Locale.GERMANY, "%,.2f MT", Math.max(valor, 0)));
    }

    private void sincronizarDadosVendaParaReembolso() {
        if (labelVendaReembolso == null || tabela.getRowCount() == 0) {
            return;
        }
        int row = tabela.getSelectedRow();
        if (row < 0 || row >= tabela.getRowCount()) {
            row = Math.min(1, tabela.getRowCount() - 1);
        }
        String id = String.valueOf(tabela.getValueAt(row, 0));
        String estado = String.valueOf(tabela.getValueAt(row, 3));

        labelVendaReembolso.setText("Venda " + id);

        Color bgChip;
        Color fgChip;
        if ("PENDENTE".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.CHIP_YELLOW_BG;
            fgChip = UiStyle.CHIP_YELLOW_FG;
        } else if ("CONCLUIDA".equalsIgnoreCase(estado) || "CONCLUIDA".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.CHIP_GREEN_BG;
            fgChip = UiStyle.CHIP_GREEN_FG;
        } else if ("ANULADA".equalsIgnoreCase(estado)) {
            bgChip = UiStyle.ERROR_BG;
            fgChip = UiStyle.ERROR_TEXT;
        } else {
            bgChip = UiStyle.CHIP_BLUE_BG;
            fgChip = UiStyle.CHIP_BLUE_FG;
        }
        chipEstadoReembolso.atualizarEstado(estado, bgChip, fgChip);

        if (listaVendasEntidades != null) {
            try {
                Long idVenda = Long.parseLong(id);
                Venda vendaSel = listaVendasEntidades.stream()
                        .filter(v -> v.getIdVenda() != null && v.getIdVenda().equals(idVenda))
                        .findFirst().orElse(null);
                if (vendaSel != null && vendaSel.getItemVendido() != null) {
                    itensVendaSelecionada = new ArrayList<>(vendaSel.getItemVendido());
                } else {
                    itensVendaSelecionada = new ArrayList<>();
                }
            } catch (NumberFormatException ex) {
                itensVendaSelecionada = new ArrayList<>();
            }
        }

        if (comboItemReembolso != null) {
            comboItemReembolso.removeAllItems();
            for (ItemVendido it : itensVendaSelecionada) {
                String nome = (it.getProduto() != null && it.getProduto().getNome() != null)
                        ? it.getProduto().getNome()
                        : "Item #" + it.getIdItem();
                comboItemReembolso.addItem(nome + " (x" + it.getQuantidade() + ")");
            }
        }
        if (labelJaReembolsadoNV != null)
            labelJaReembolsadoNV.setText("0");
        recalcularValorReembolso();
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        if (campo instanceof javax.swing.text.JTextComponent
                && campo.getClientProperty(FlatClientProperties.PLACEHOLDER_TEXT) == null) {
            String dica = textoLabel.replaceAll("\\(.*?\\)", "").replace("*", "").trim().toLowerCase();
            campo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Introduzir " + dica);
        }
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(textoLabel);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension pref = campo.getPreferredSize();
        campo.setPreferredSize(new Dimension(Math.max(60, pref.width), ALTURA_INPUT));
        campo.setMinimumSize(new Dimension(40, ALTURA_INPUT));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_INPUT));

        painel.add(label);
        painel.add(Box.createRigidArea(new Dimension(0, 6)));
        painel.add(campo);
        return painel;
    }

    private JPanel criarMiniTabelaItens(int larguraInterna) {
        modeloItensVenda = new DefaultTableModel(new Object[0][], COLUNAS_ITENS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable miniTabela = new JTable(modeloItensVenda);
        miniTabela.setRowHeight(29);
        miniTabela.setFont(UiStyle.fonte(12, Font.PLAIN));
        miniTabela.setBackground(UiStyle.SURFACE);
        miniTabela.setForeground(UiStyle.TEXT);
        miniTabela.setShowVerticalLines(false);
        miniTabela.setShowHorizontalLines(true);
        miniTabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
        miniTabela.setIntercellSpacing(new Dimension(0, 1));
        miniTabela.setSelectionBackground(UiStyle.SECONDARY_BG);
        miniTabela.setSelectionForeground(UiStyle.TEXT);

        JTableHeader header = miniTabela.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(0, 29));
        header.setDefaultRenderer(new RenderizadorMiniCabecalho());

        RenderizadorMiniCelula renderizadorMini = new RenderizadorMiniCelula();
        for (int i = 0; i < miniTabela.getColumnModel().getColumnCount(); i++) {
            miniTabela.getColumnModel().getColumn(i).setCellRenderer(renderizadorMini);
        }
        miniTabela.getColumnModel().getColumn(0).setPreferredWidth(165);
        miniTabela.getColumnModel().getColumn(1).setPreferredWidth(55);
        miniTabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        miniTabela.getColumnModel().getColumn(3).setPreferredWidth(34);

        miniTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int col = miniTabela.columnAtPoint(evt.getPoint());
                int row = miniTabela.rowAtPoint(evt.getPoint());
                if (col == 3 && row >= 0 && row < itensCarrinho.size()) {
                    itensCarrinho.remove(row);
                    modeloItensVenda.removeRow(row);
                    atualizarTotalNV();
                }
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        JScrollPane scrollMini = new JScrollPane(miniTabela);
        scrollMini.setBorder(BorderFactory.createEmptyBorder());
        scrollMini.setOpaque(false);
        scrollMini.getViewport().setOpaque(false);
        scrollMini.getViewport().setBackground(UiStyle.SURFACE);
        container.add(scrollMini, BorderLayout.CENTER);

        fixarLargura(container, larguraInterna, 29 * 4);
        return container;
    }

    private void fixarLargura(JComponent componente, int largura, int altura) {
        Dimension d = new Dimension(largura, altura);
        componente.setPreferredSize(d);
        componente.setMinimumSize(d);
        componente.setMaximumSize(d);
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void atualizarTabelaFiltrada() {
        String termoId = campoBuscaId.getTextoReal().trim().toLowerCase();
        boolean lateralAberto = modoLateral != ModoPainelLateral.NENHUM;
        List<Object[]> fonteDados = (dadosVendasCarregados != null) ? dadosVendasCarregados : new ArrayList<>();
        List<Object[]> filtrados = new ArrayList<>();
        for (Object[] linha : fonteDados) {
            String id = String.valueOf(linha[0]).toLowerCase();
            String estado = String.valueOf(linha[3]);
            String filtroNormalizado = estadoFiltro.replace("Í", "I").replace("í", "i");
            String estadoNormalizado = estado.replace("Í", "I").replace("í", "i");
            boolean combinaEstado = "Todos".equalsIgnoreCase(estadoFiltro)
                    || estadoNormalizado.equalsIgnoreCase(filtroNormalizado);
            boolean combinaId = termoId.isEmpty() || id.contains(termoId);
            if (combinaEstado && combinaId) {
                if (lateralAberto) {
                    filtrados.add(new Object[] { linha[0], linha[1], linha[2], linha[3], linha[4] });
                } else {
                    filtrados.add(linha);
                }
            }
        }

        modeloTabela.setRowCount(0);
        for (Object[] linha : filtrados) {
            modeloTabela.addRow(linha);
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
                    new EmptyBorder(6, 8, 6, 8)));
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
            label.setHorizontalAlignment(column == 3 ? SwingConstants.CENTER : SwingConstants.LEFT);
            label.setOpaque(false);
            label.setForeground(UiStyle.TEXT);
            label.setFont(UiStyle.fonte(12, Font.PLAIN));
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(6, 8, 6, 8)));
            return label;
        }
    }

    private static final class CaixaValorArredondada extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final JLabel labelValor;

        CaixaValorArredondada(String texto) {
            this(texto, UiStyle.INPUT_BG, UiStyle.INPUT_BORDER, UiStyle.MUTED, UiStyle.fonte(13, Font.PLAIN));
        }

        CaixaValorArredondada(String texto, Color corFundo, Color corBorda, Color corTexto, Font fonte) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            this.corFundo = corFundo;
            this.corBorda = corBorda;

            this.labelValor = new JLabel(texto);
            this.labelValor.setFont(fonte);
            this.labelValor.setForeground(corTexto);
            add(this.labelValor, BorderLayout.CENTER);
        }

        JLabel getLabelValor() {
            return labelValor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                if (corBorda != null) {
                    g2.setColor(UiStyle.INPUT_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
                labelValor.setForeground(UiStyle.MUTED);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
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
                    new EmptyBorder(11, 14, 11, 14)));
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
                    new EmptyBorder(11, 14, 11, 14)));
            return label;
        }
    }

    private static final class RenderizadorCelulaEstado extends DefaultTableCellRenderer {
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
            container.setOpaque(isSelected);
            container.setBackground(isSelected ? UiStyle.TABLE_SELECTION_BG : UiStyle.SURFACE);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(8, 14, 8, 14)));
            container.add(new ChipEstado(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class ChipEstado extends JLabel {
        private static final long serialVersionUID = 1L;

        private Color corFundo;

        ChipEstado(String texto, Color corFundo, Color corTexto) {
            super(texto);
            this.corFundo = corFundo;
            setFont(UiStyle.fonteSemiBold(12));
            setForeground(corTexto);
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        void atualizarEstado(String texto, Color novoFundo, Color novoTexto) {
            setText(texto);
            this.corFundo = novoFundo;
            setForeground(novoTexto);
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            String txt = getText() != null ? getText().trim() : "";
            Color bgDinamico = corFundo;
            Color fgDinamico = getForeground();
            if ("PENDENTE".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_YELLOW_BG;
                fgDinamico = UiStyle.CHIP_YELLOW_FG;
            } else if ("CONCLUÍDA".equalsIgnoreCase(txt) || "CONCLUIDA".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.CHIP_GREEN_BG;
                fgDinamico = UiStyle.CHIP_GREEN_FG;
            } else if ("ANULADA".equalsIgnoreCase(txt)) {
                bgDinamico = UiStyle.ERROR_BG;
                fgDinamico = UiStyle.ERROR_TEXT;
            } else if (!txt.isEmpty()) {
                bgDinamico = UiStyle.CHIP_BLUE_BG;
                fgDinamico = UiStyle.CHIP_BLUE_FG;
            }
            setForeground(fgDinamico);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(bgDinamico);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class BotaoSeletorEstado extends JButton {
        private static final long serialVersionUID = 1L;

        BotaoSeletorEstado(String texto) {
            super(texto);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setBackground(UiStyle.INPUT_BG);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 28));
        }

        @Override
        public String getText() {
            return UiStyle.removerMarcadorSeta(super.getText());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = RAIO_COMPONENTE * 2;
                g2.setColor(UiStyle.INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                g2.setColor(UiStyle.INPUT_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                super.paintComponent(g);
                UiStyle.desenharChevronExpansivel(g2, this, getForeground());
            } finally {
                g2.dispose();
            }
        }
    }

    private static final class CampoBuscaArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        private final String placeholder;
        private boolean exibindoPlaceholder;

        CampoBuscaArredondado(String placeholder) {
            super(placeholder);
            this.placeholder = placeholder;
            this.exibindoPlaceholder = true;
            setOpaque(false);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setCaretColor(UiStyle.TEXT);
            setBackground(UiStyle.INPUT_BG);
            setBorder(new EmptyBorder(0, 12, 0, 12));
            putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (exibindoPlaceholder) {
                        exibindoPlaceholder = false;
                        setText("");
                        setForeground(UiStyle.TEXT);
                    }
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().trim().isEmpty()) {
                        exibindoPlaceholder = true;
                        setText(placeholder);
                        setForeground(UiStyle.MUTED);
                    }
                    repaint();
                }
            });
        }

        String getTextoReal() {
            return exibindoPlaceholder ? "" : getText();
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

        private Color corFundo;
        private Color corBorda;
        private boolean preencherFundo;
        private boolean destrutivoPreenchido;
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

        void configurarEstilo(Color novoFundo, Color novoTexto, Color novaBorda, boolean preencher) {
            this.corFundo = novoFundo;
            this.corBorda = novaBorda;
            this.preencherFundo = preencher;
            setForeground(novoTexto);
            repaint();
        }

        void markDestrutivoPreenchido() {
            this.destrutivoPreenchido = true;
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

                if (destrutivoPreenchido) {
                    Color baseBg = UiStyle.ERROR_BG;
                    setForeground(UiStyle.ERROR_TEXT);
                    Color atual = getModel().isPressed() ? baseBg.darker() : baseBg;
                    g2.setColor(atual);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                    g2.setColor(UiStyle.DANGER_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                } else if (preencherFundo && corFundo != null) {
                    Color baseBg = ehPrimario ? UiStyle.ACCENT : UiStyle.SECONDARY_BG;
                    setForeground(ehPrimario ? UiStyle.ACCENT_TEXT : UiStyle.TEXT);
                    Color atual = baseBg;
                    if (getModel().isPressed()) {
                        atual = baseBg.darker();
                    } else if (getModel().isRollover()) {
                        atual = new Color(
                                Math.min(255, baseBg.getRed() + 10),
                                Math.min(255, baseBg.getGreen() + 10),
                                Math.min(255, baseBg.getBlue() + 10));
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

                if (!destrutivoPreenchido && corBorda != null) {
                    g2.setColor(preencherFundo ? UiStyle.INPUT_BORDER : UiStyle.DANGER_BORDER);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class PainelCardTabela extends JPanel {
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
                () -> vendaController.listarTodos(),
                vendas -> {
                    this.listaVendasEntidades = vendas != null ? vendas : new ArrayList<>();
                    List<Object[]> linhas = new ArrayList<>();
                    if (vendas != null) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        for (Venda v : vendas) {
                            String idStr = String.valueOf(v.getIdVenda());
                            String dataStr = v.getData() != null ? v.getData().format(dtf) : "";
                            String totalStr = String.format(Locale.GERMANY, "%,.2f MT",
                                    v.getTotal() != null ? v.getTotal() : 0.0);
                            String estadoStr = v.getEstado() != null ? v.getEstado().name() : "PENDENTE";
                            String cliStr = (v.getCliente() != null && v.getCliente().getNome() != null)
                                    ? v.getCliente().getNome()
                                    : "Consumidor";
                            String funcStr = (v.getFuncionario() != null && v.getFuncionario().getNome() != null)
                                    ? v.getFuncionario().getNome()
                                    : "";
                            String itensStr = String.valueOf(v.contarItens());
                            linhas.add(new Object[] { idStr, dataStr, totalStr, estadoStr, cliStr, funcStr, itensStr });
                        }
                    }
                    this.dadosVendasCarregados = linhas;
                    atualizarTabelaFiltrada();
                },
                erro -> {
                    this.listaVendasEntidades = new ArrayList<>();
                    this.dadosVendasCarregados = new ArrayList<>();
                    atualizarTabelaFiltrada();
                });
        AsyncTask.executar(
                () -> vendaController.listarClientes(),
                clientes -> {
                    this.listaClientesNV = clientes != null ? clientes : new ArrayList<>();
                    if (comboClienteNV != null) {
                        comboClienteNV.removeAllItems();
                        comboClienteNV.addItem("Consumidor Final");
                        for (model.vo.Cliente c : listaClientesNV) {
                            comboClienteNV.addItem(c.getNome());
                        }
                        comboClienteNV.setSelectedIndex(0);
                    }
                },
                erro -> {
                });
        AsyncTask.executar(
                () -> vendaController.listarProdutos(),
                produtos -> {
                    this.listaProdutosNV = produtos != null ? produtos : new ArrayList<>();
                    filtrarComboProdutosNV();
                },
                erro -> {
                });
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            if (botaoAnular != null) {
                botaoAnular.setVisible(false);
            }
        } else {
            if (botaoAnular != null) {
                botaoAnular.setVisible(true);
            }
        }
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardNovaVenda);
        UiStyle.atualizarComponentesTema(cardConfirmarAnular);
        UiStyle.atualizarComponentesTema(cardReembolsarItem);
    }
}
