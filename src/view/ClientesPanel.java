package view;

import com.formdev.flatlaf.FlatClientProperties;
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
import java.awt.RenderingHints;
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

import model.bo.ClienteBO;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Clientes, Novo Cliente, Editar Cliente e Informações do Cliente em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Clientes" (Clientes.dc.html)
 * - "ADMIN · Barra superior · Escuro — Cliente — novo" (ClienteNovo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Cliente — editar" (ClienteEditar.dc.html)
 * - "ADMIN · Barra superior · Escuro — Cliente — informações" (ClienteInfo.dc.html)
 */
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

    private static final Object[][] DADOS_INICIAIS = {
        {"1", "Ana Macie", "84 123 4567", "3.350,00 MT", "03/11/2026"},
        {"2", "João Sitoe", "82 765 4321", "840,00 MT", "03/11/2026"},
        {"3", "Maria Nhaca", "86 555 0102", "0,00 MT", "02/11/2026"}
    };

    private static final String[] COLUNAS_HISTORICO_VENDAS = {
        "Venda", "Data", "Total", "Estado"
    };

    private static final Object[][] DADOS_HISTORICO_VENDAS = {
        {"1042", "03/11/2026", "1.250,00 MT", "PENDENTE"},
        {"1039", "02/11/2026", "2.100,00 MT", "REEMBOLSADA"},
        {"1031", "28/10/2026", "1.350,00 MT", "CONCLUÍDA"}
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

    // Controles do formulário "Novo cliente"
    private JTextField campoNomeNovo;
    private JTextField campoContactoNovo;
    private JLabel linkAdicionarContactoNovo;
    private JButton botaoGuardarNovo;
    private JButton botaoCancelarNovo;

    // Controles do formulário "Editar cliente"
    private JTextField campoNomeEditar;
    private JTextField campoContacto1Editar;
    private JTextField campoContacto2Editar;
    private JLabel linkAdicionarContactoEditar;
    private JButton botaoGuardarAlteracoesEditar;
    private JButton botaoCancelarEditar;
    private JButton botaoEliminarEditar;

    // Controles do card "Informações do cliente"
    private JLabel labelNomeInformacoes;
    private JTextField campoTotalComprasInformacoes;
    private JTextField campoUltimaCompraInformacoes;
    private JTable tabelaHistoricoVendasInformacoes;
    private JButton botaoEditarInformacoes;
    private JButton botaoFecharInformacoes;

    private final ClienteBO clienteBO;
    private final controller.ClienteController clienteController = new controller.ClienteController();
    private List<Object[]> dadosClientesCarregados = null;
    private List<Cliente> listaClientesEntidades = null;

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
        this.clienteBO = new ClienteBO();

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
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
            wrapperDireita.add(cardNovoCliente, BorderLayout.NORTH);
            tabela.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CLIENTE) {
            wrapperDireita.add(cardEditarCliente, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosClienteParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.INFORMACOES_CLIENTE) {
            wrapperDireita.add(cardInformacoesCliente, BorderLayout.NORTH);
            if (tabela.getRowCount() > 0 && tabela.getSelectedRow() < 0) {
                tabela.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosClienteParaInformacoes();
        } else {
            tabela.clearSelection();
        }

        atualizarBotoesToolbar();
        revalidate();
        repaint();
    }

    private void atualizarBotoesToolbar() {
        containerBotoesToolbar.removeAll();
        containerBotoesToolbar.add(botaoNovoCliente);
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        containerBotoesToolbar.add(botaoEditar);
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        if (modoLateral != ModoPainelLateral.NENHUM) {
            // Em ClienteNovo.dc.html: Novo cliente | Editar | Informações | Eliminar
            containerBotoesToolbar.add(botaoInformacoes);
        } else {
            // Em Clientes.dc.html: Novo cliente | Editar | Histórico | Eliminar
            containerBotoesToolbar.add(botaoHistorico);
        }
        containerBotoesToolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        containerBotoesToolbar.add(botaoEliminar);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        // <h1>Clientes</h1> (h1 { font-size:24px; margin:0 })
        JLabel titulo = new JLabel("Clientes");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(titulo);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        // .tb { display:flex; gap:10px; align-items:center }
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbar.setOpaque(false);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(campoBuscarCliente);
        toolbar.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbar.add(containerBotoesToolbar);

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

    /**
     * <span class="in">Buscar por nome</span>
     */
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
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                texto, bg, fg, borda, RAIO_COMPONENTE, preencherFundo
        );
        botao.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "A ação \"" + texto + "\" está reservada para a próxima etapa.",
                "Módulo reservado",
                JOptionPane.INFORMATION_MESSAGE
        ));
        return botao;
    }

    private JButton criarBotaoNovoCliente() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo cliente", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
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

    /**
     * <div class="card" style="padding:0"><table>...</table></div>
     */
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

    /**
     * Card lateral "Novo cliente" fiel a ClienteNovo.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Novo cliente</b>
        JLabel titulo = new JLabel("Novo cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome * (máx. 100)</span><div class="in" style="min-width:0"></div></div>
        campoNomeNovo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome * (máx. 100)", campoNomeNovo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto * (máx. 30)</span><div class="in" style="min-width:0"></div></div>
        campoContactoNovo = new CampoFormularioArredondado("");
        JPanel blocoContacto = criarCampoComLabel("Contacto * (máx. 30)", campoContactoNovo);
        fixarLargura(blocoContacto, larguraInterna, 60);
        card.add(blocoContacto);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <span class="k" style="color:#F5B638">+ Adicionar contacto</span>
        linkAdicionarContactoNovo = new JLabel("+ Adicionar contacto");
        linkAdicionarContactoNovo.setFont(UiStyle.fonte(12, Font.PLAIN));
        linkAdicionarContactoNovo.setForeground(UiStyle.ACCENT);
        linkAdicionarContactoNovo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkAdicionarContactoNovo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkAdicionarContactoNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        ClientesPanel.this,
                        "Por favor, guarde o cliente primeiro. ApÃ³s salvar, selecione-o na tabela para adicionar contactos adicionais.",
                        "Adicionar Contacto",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        card.add(linkAdicionarContactoNovo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovo.addActionListener(e -> {
            String nome = (campoNomeNovo != null) ? campoNomeNovo.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String contacto = (campoContactoNovo != null) ? campoContactoNovo.getText().trim() : "";
            Cliente c = new Cliente();
            c.setNome(nome);
            if (!contacto.isEmpty()) {
                Contacto cont = new Contacto();
                cont.setContacto(contacto);
                c.setContacto(Arrays.asList(cont));
            }

            AsyncTask.executar(
                () -> {
                    clienteBO.salvar(c);
                    return null;
                },
                sucesso -> {
                    JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    if (campoNomeNovo != null) campoNomeNovo.setText("");
                    if (campoContactoNovo != null) campoContactoNovo.setText("");
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
        botaoCancelarNovo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovo);
        card.add(acoes);

        return card;
    }

    /**
     * Card lateral "Editar cliente" fiel a ClienteEditar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Editar cliente</b>
        JLabel titulo = new JLabel("Editar cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome * (máx. 100)</span><div class="in" style="min-width:0">Ana Macie</div></div>
        campoNomeEditar = new CampoFormularioArredondado("Ana Macie");
        JPanel blocoNome = criarCampoComLabel("Nome * (máx. 100)", campoNomeEditar);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto 1 *</span><div class="in" style="min-width:0">84 123 4567</div></div>
        campoContacto1Editar = new CampoFormularioArredondado("84 123 4567");
        JPanel blocoContacto1 = criarCampoComLabel("Contacto 1 *", campoContacto1Editar);
        fixarLargura(blocoContacto1, larguraInterna, 60);
        card.add(blocoContacto1);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Contacto 2</span><div class="in" style="min-width:0">82 000 1234</div></div>
        campoContacto2Editar = new CampoFormularioArredondado("82 000 1234");
        JPanel blocoContacto2 = criarCampoComLabel("Contacto 2", campoContacto2Editar);
        fixarLargura(blocoContacto2, larguraInterna, 60);
        card.add(blocoContacto2);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <span class="k" style="color:#F5B638">+ Adicionar contacto</span>
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
                    JOptionPane.showMessageDialog(ClientesPanel.this, "Identificador de cliente invÃ¡lido.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        card.add(linkAdicionarContactoEditar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesEditar = new BotaoToolbarArredondado(
                "Guardar alteraÃ§Ãµes", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
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
                List<Contacto> listaCont = new ArrayList<>();
                if (!c1.isEmpty()) {
                    Contacto cont1 = new Contacto();
                    cont1.setContacto(c1);
                    listaCont.add(cont1);
                }
                c.setContacto(listaCont);

                final Cliente fCli = c;
                botaoGuardarAlteracoesEditar.setEnabled(false);
                AsyncTask.executar(
                    () -> {
                        clienteBO.atualizar(fCli);
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
                JOptionPane.showMessageDialog(this, "Identificador de cliente invÃ¡lido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        botaoCancelarEditar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        botaoEliminarEditar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditar.addActionListener(e -> eliminarClienteSelecionado());
        acoes.add(botaoGuardarAlteracoesEditar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarEditar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoEliminarEditar);
        card.add(acoes);

        return card;
    }

    /**
     * Card lateral "Informações do cliente" fiel a ClienteInfo.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32; // 388px

        // <b>Informações do cliente</b>
        JLabel titulo = new JLabel("Informações do cliente");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="font-size:14px;font-weight:600">Ana Macie</div>
        labelNomeInformacoes = new JLabel("Ana Macie");
        labelNomeInformacoes.setFont(UiStyle.fonteSemiBold(14));
        labelNomeInformacoes.setForeground(UiStyle.TEXT);
        labelNomeInformacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelNomeInformacoes);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm">Total de compras (3.350,00 MT) | Última compra (03/11/2026)</div>
        campoTotalComprasInformacoes = new CampoFormularioArredondado("3.350,00 MT");
        campoTotalComprasInformacoes.setEditable(false);
        campoUltimaCompraInformacoes = new CampoFormularioArredondado("03/11/2026");
        campoUltimaCompraInformacoes.setEditable(false);
        JPanel linhaResumo = new JPanel(new GridLayout(1, 2, 12, 0));
        linhaResumo.setOpaque(false);
        linhaResumo.add(criarCampoComLabel("Total de compras", campoTotalComprasInformacoes));
        linhaResumo.add(criarCampoComLabel("Última compra", campoUltimaCompraInformacoes));
        fixarLargura(linhaResumo, larguraInterna, 60);
        card.add(linhaResumo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <span class="k">Histórico de vendas</span>
        JLabel labelHistorico = new JLabel("Histórico de vendas");
        labelHistorico.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelHistorico.setForeground(UiStyle.MUTED);
        labelHistorico.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelHistorico);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="mt"><table>...</table></div>
        tabelaHistoricoVendasInformacoes = criarMiniTabelaHistoricoVendas();
        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.add(tabelaHistoricoVendasInformacoes.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(tabelaHistoricoVendasInformacoes, BorderLayout.CENTER);
        int alturaTabela = 29 + (DADOS_HISTORICO_VENDAS.length * 30);
        fixarLargura(tabelaWrapper, larguraInterna, alturaTabela);
        card.add(tabelaWrapper);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt s">Editar</span><span class="bt s">Fechar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoEditarInformacoes = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoEditarInformacoes.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.EDITAR_CLIENTE));

        botaoFecharInformacoes = new BotaoToolbarArredondado(
                "Fechar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoFecharInformacoes.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoEditarInformacoes);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoFecharInformacoes);
        card.add(acoes);

        return card;
    }

    private JTable criarMiniTabelaHistoricoVendas() {
        DefaultTableModel modelo = new DefaultTableModel(DADOS_HISTORICO_VENDAS, COLUNAS_HISTORICO_VENDAS) {
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
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o cliente #" + idStr + "?", "Confirmar ExclusÃ£o", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        try {
            Long idCliente = Long.parseLong(idStr);
            AsyncTask.executar(
                () -> {
                    clienteBO.eliminar(idCliente);
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
            JOptionPane.showMessageDialog(this, "Identificador de cliente invÃ¡lido.", "Erro", JOptionPane.ERROR_MESSAGE);
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

        String nome = String.valueOf(modeloTabela.getValueAt(row, 1));
        String totalCompras = String.valueOf(modeloTabela.getValueAt(row, 3));
        String ultimaCompra = String.valueOf(modeloTabela.getValueAt(row, 4));

        labelNomeInformacoes.setText(nome);
        campoTotalComprasInformacoes.setText(totalCompras);
        campoUltimaCompraInformacoes.setText(ultimaCompra);
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

        campoNomeEditar.setText(nome);
        campoContacto1Editar.setText(contacto1);
        if ("1".equals(id)) {
            campoContacto2Editar.setText("82 000 1234");
        } else if ("2".equals(id)) {
            campoContacto2Editar.setText("84 000 5678");
        } else {
            campoContacto2Editar.setText("82 000 9012");
        }
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
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
        List<Object[]> fonteDados = (dadosClientesCarregados != null) ? dadosClientesCarregados : Arrays.asList(DADOS_INICIAIS);

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo)) {
                modeloTabela.addRow(linha);
            }
        }
    }

    /**
     * th { text-align:left; color:#A3ADC2; font-weight:600; padding:11px 14px; border-bottom:1px solid #2A3040 }
     */
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

    /**
     * td { padding:11px 14px; border-bottom:1px solid #242B3B }
     */
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

    /**
     * .mt th { padding:6px 8px; font-size:12px; color:#A3ADC2; font-weight:600; border-bottom:1px solid #2A3040 }
     */
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

    /**
     * .mt td { padding:6px 8px; font-size:12px; border-bottom:1px solid #242B3B }
     */
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

    /**
     * Renderiza os chips da coluna Estado do histórico de vendas:
     * PENDENTE (.ch.y), REEMBOLSADA (.ch.b), CONCLUÍDA (.ch.g), ANULADA (.ch.r).
     */
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

    /**
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Nome * (máx. 100), Contacto * (máx. 30)).
     */
    private static final class CampoFormularioArredondado extends JTextField {
        private static final long serialVersionUID = 1L;

        CampoFormularioArredondado(String valorInicial) {
            super(valorInicial);
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

    /**
     * Campo de texto estilo .in ("Buscar por nome").
     */
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

    /**
     * Botão da toolbar (.bt / .bt.s / .bt.d) com padding 9px 16px, altura 38px e border-radius 8px.
     */
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
            Dimension pref = getPreferredSize();
            setPreferredSize(new Dimension(pref.width, ALTURA_INPUT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;

                if (preencherFundo && corFundo != null) {
                    Color atual = corFundo;
                    if (getModel().isPressed()) {
                        atual = corFundo.darker();
                    } else if (getModel().isRollover()) {
                        atual = new Color(
                                Math.min(255, corFundo.getRed() + 10),
                                Math.min(255, corFundo.getGreen() + 10),
                                Math.min(255, corFundo.getBlue() + 10)
                        );
                    }
                    g2.setColor(atual);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0x4F, 0x1B, 0x1B, 90));
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
                }

                if (corBorda != null) {
                    g2.setColor(corBorda);
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1f, h - 1f, arco, arco));
                }
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Card da tabela com padding:0, background #1B2030, border 1px #2A3040 e border-radius 14px.
     */
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
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arco, arco));
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
                int w = getWidth();
                int h = getHeight();
                int arco = raio * 2;
                g2.setColor(corBorda);
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
            () -> clienteBO.listarTodos(),
            clientes -> {
                this.listaClientesEntidades = clientes;
                if (clientes != null && !clientes.isEmpty()) {
                    List<Object[]> linhas = new ArrayList<>();
                    for (Cliente c : clientes) {
                        String idStr = String.valueOf(c.getIdCliente());
                        String nomeStr = c.getNome() != null ? c.getNome() : "";
                        String contStr = (c.getContacto() != null && !c.getContacto().isEmpty() && c.getContacto().get(0) != null)
                                ? c.getContacto().get(0).getContacto() : "-";
                        String totStr = "0,00 MT";
                        String ultStr = "-";
                        linhas.add(new Object[] {idStr, nomeStr, contStr, totStr, ultStr});
                    }
                    this.dadosClientesCarregados = linhas;
                }
                atualizarTabelaFiltrada();
            },
            erro -> {
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
        if (tabela != null) {
            tabela.setBackground(UiStyle.SURFACE);
            tabela.setForeground(UiStyle.TEXT);
            tabela.setGridColor(UiStyle.TABLE_ROW_BORDER);
            tabela.getTableHeader().setBackground(UiStyle.SURFACE);
            tabela.getTableHeader().setForeground(UiStyle.MUTED);
        }
        repaint();
    }
}
