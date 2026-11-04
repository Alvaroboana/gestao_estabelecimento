package view;

import java.awt.BorderLayout;
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
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.bo.RelatorioBO;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.AsyncTask;

/**
 * Painel Início (Dashboard) em Java Swing fiel ao design HTML "Inicio.dc.html",
 * integrado assincronamente ao banco de dados com KPIs em tempo real e navegação rápida.
 */
public class DashboardPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    private static final int RAIO_CARD = 14;
    private static final int RAIO_BOTAO = 8;

    private final RelatorioBO relatorioBO;
    private Utilizador utilizadorLogado;

    private JLabel labelBoasVindas;
    private JButton btnNovaVenda;
    private JButton btnEntradaEstoque;
    private JButton btnNovoProduto;
    private JButton btnNovoCliente;
    private JButton btnVerRelatorios;

    private JLabel labelKpiVendasHoje;
    private JLabel labelKpiVendasPendentes;
    private JLabel labelKpiEstoqueBaixo;
    private JLabel labelKpiLotesExpirar;

    private DefaultTableModel modeloEstoqueBaixo;
    private DefaultTableModel modeloMaisVendidos;
    private JTable tabelaEstoqueBaixo;
    private JTable tabelaMaisVendidos;

    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(Utilizador utilizador) {
        this.relatorioBO = new RelatorioBO();
        this.utilizadorLogado = utilizador;

        setLayout(new BorderLayout(0, 16));
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(criarCabecalho(), BorderLayout.NORTH);
        add(criarConteudo(), BorderLayout.CENTER);

        aplicarPermissoes(utilizador);
        recarregarDados();
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        String nome = (utilizadorLogado != null && utilizadorLogado.getUsername() != null)
                ? utilizadorLogado.getUsername()
                : "admin";

        this.labelBoasVindas = new JLabel("Bem-vindo, " + nome);
        UiStyle.aplicarTexto(labelBoasVindas, 24, true);
        labelBoasVindas.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.add(labelBoasVindas);
        cabecalho.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnNovaVenda = botaoAcao("+ Nova venda", true, () -> navegarParaPainel(MainFrame.PAINEL_VENDAS, VendasPanel.ModoPainelLateral.NOVA_VENDA));
        btnEntradaEstoque = botaoAcao("Dar entrada no estoque", false, () -> navegarParaPainel(MainFrame.PAINEL_ESTOQUE, EstoquePanel.ModoPainelLateral.DAR_ENTRADA));
        btnNovoProduto = botaoAcao("Novo produto", false, () -> navegarParaPainel(MainFrame.PAINEL_PRODUTOS, ProdutosPanel.ModoPainelLateral.NOVO_PRODUTO));
        btnNovoCliente = botaoAcao("Novo cliente", false, () -> navegarParaPainel(MainFrame.PAINEL_CLIENTES, ClientesPanel.ModoPainelLateral.NOVO_CLIENTE));
        btnVerRelatorios = botaoAcao("Ver relatórios", false, () -> navegarParaPainel(MainFrame.PAINEL_RELATORIOS, null));

        acoes.add(btnNovaVenda);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnEntradaEstoque);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnNovoProduto);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnNovoCliente);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnVerRelatorios);
        cabecalho.add(acoes);

        return cabecalho;
    }

    private void navegarParaPainel(String idPainel, Object parametro) {
        Component root = SwingUtilities.getWindowAncestor(this);
        if (root instanceof MainFrame) {
            MainFrame mf = (MainFrame) root;
            mf.mostrarPainel(idPainel);

            JPanel c = mf.getPainelPorId(idPainel);
            if (c != null) {
                if (idPainel.equals(MainFrame.PAINEL_VENDAS) && c instanceof VendasPanel && parametro instanceof VendasPanel.ModoPainelLateral) {
                    ((VendasPanel) c).abrirModoLateral((VendasPanel.ModoPainelLateral) parametro);
                } else if (idPainel.equals(MainFrame.PAINEL_ESTOQUE) && c instanceof EstoquePanel && parametro instanceof EstoquePanel.ModoPainelLateral) {
                    ((EstoquePanel) c).abrirModoLateral((EstoquePanel.ModoPainelLateral) parametro);
                } else if (idPainel.equals(MainFrame.PAINEL_PRODUTOS) && c instanceof ProdutosPanel && parametro instanceof ProdutosPanel.ModoPainelLateral) {
                    ((ProdutosPanel) c).abrirModoLateral((ProdutosPanel.ModoPainelLateral) parametro);
                } else if (idPainel.equals(MainFrame.PAINEL_CLIENTES) && c instanceof ClientesPanel && parametro instanceof ClientesPanel.ModoPainelLateral) {
                    ((ClientesPanel) c).abrirModoLateral((ClientesPanel.ModoPainelLateral) parametro);
                }
            }
        }
    }

    private JButton botaoAcao(String texto, boolean primario, Runnable acao) {
        Color bg = primario ? UiStyle.ACCENT : UiStyle.SECONDARY_BG;
        Color fg = primario ? UiStyle.ACCENT_TEXT : UiStyle.TEXT;
        Color borda = primario ? null : UiStyle.INPUT_BORDER;

        BotaoAcaoArredondado botao = new BotaoAcaoArredondado(texto, bg, fg, borda, RAIO_BOTAO);
        botao.addActionListener(e -> {
            if (acao != null) acao.run();
        });
        return botao;
    }

    private JPanel criarConteudo() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 16));
        conteudo.setOpaque(false);

        JPanel indicadores = new JPanel(new GridLayout(1, 4, 16, 0));
        indicadores.setOpaque(false);

        this.labelKpiVendasHoje = new JLabel("0");
        this.labelKpiVendasPendentes = new JLabel("0");
        this.labelKpiEstoqueBaixo = new JLabel("0");
        this.labelKpiLotesExpirar = new JLabel("0");

        indicadores.add(cartaoKpi("Vendas de hoje", labelKpiVendasHoje));
        indicadores.add(cartaoKpi("Vendas pendentes", labelKpiVendasPendentes));
        indicadores.add(cartaoKpi("Produtos com estoque baixo", labelKpiEstoqueBaixo));
        indicadores.add(cartaoKpi("Produtos a expirar (7 dias)", labelKpiLotesExpirar));
        conteudo.add(indicadores, BorderLayout.NORTH);

        JPanel tabelas = new JPanel(new GridLayout(1, 2, 16, 0));
        tabelas.setOpaque(false);
        tabelas.add(criarCardTabelaEstoqueBaixo());
        tabelas.add(criarCardTabelaProdutosMaisVendidos());
        conteudo.add(tabelas, BorderLayout.CENTER);

        return conteudo;
    }

    private JPanel cartaoKpi(String rotulo, JLabel labelValor) {
        PainelCard card = new PainelCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel labelRotulo = new JLabel(rotulo);
        labelRotulo.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelRotulo.setForeground(UiStyle.MUTED);

        labelValor.setFont(UiStyle.fonte(28, Font.BOLD));
        labelValor.setForeground(UiStyle.TEXT);

        card.add(labelRotulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(labelValor);
        return card;
    }

    private JPanel criarCardTabelaEstoqueBaixo() {
        PainelCard card = new PainelCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel titulo = new JLabel("Estoque baixo");
        UiStyle.aplicarTexto(titulo, 16, true);
        card.add(titulo, BorderLayout.NORTH);

        String[] colunas = {"Produto", "Mínimo", "Atual"};
        this.modeloEstoqueBaixo = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tabelaEstoqueBaixo = criarTabelaCustomizada(modeloEstoqueBaixo);
        card.add(criarScrollPaneTabela(tabelaEstoqueBaixo), BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardTabelaProdutosMaisVendidos() {
        PainelCard card = new PainelCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel titulo = new JLabel("Produtos mais vendidos");
        UiStyle.aplicarTexto(titulo, 16, true);
        card.add(titulo, BorderLayout.NORTH);

        String[] colunas = {"Produto", "Categoria", "Qtd", "Total"};
        this.modeloMaisVendidos = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tabelaMaisVendidos = criarTabelaCustomizada(modeloMaisVendidos);
        card.add(criarScrollPaneTabela(tabelaMaisVendidos), BorderLayout.CENTER);
        return card;
    }

    private JTable criarTabelaCustomizada(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(UiStyle.fonte(13, Font.PLAIN));
        table.setForeground(UiStyle.TEXT);
        table.setBackground(UiStyle.SURFACE);
        table.setSelectionBackground(UiStyle.TABLE_SELECTION_BG);
        table.setSelectionForeground(UiStyle.TEXT);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(UiStyle.TABLE_ROW_BORDER);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setFont(UiStyle.fonte(12, Font.BOLD));
        header.setForeground(UiStyle.MUTED);
        header.setBackground(UiStyle.SURFACE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER));

        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer();
        renderizador.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        table.setDefaultRenderer(Object.class, renderizador);

        return table;
    }

    private JScrollPane criarScrollPaneTabela(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UiStyle.SURFACE);
        scroll.setOpaque(false);
        return scroll;
    }

    @Override
    public void recarregarDados() {
        // Carregamento de KPIs e tabelas assincronamente via SwingWorker
        AsyncTask.executar(() -> {
            Map<String, Integer> kpis = relatorioBO.obterIndicadoresDashboard();
            List<Map<String, Object>> estoque = relatorioBO.obterEstoqueBaixoDashboard();
            List<Map<String, Object>> vendas = relatorioBO.obterProdutosMaisVendidosDashboard();
            return new DadosDashboard(kpis, estoque, vendas);
        }, dados -> {
            // Atualiza KPIs
            if (dados.kpis != null) {
                labelKpiVendasHoje.setText(String.valueOf(dados.kpis.getOrDefault("vendasHoje", 0)));
                labelKpiVendasPendentes.setText(String.valueOf(dados.kpis.getOrDefault("vendasPendentes", 0)));
                labelKpiEstoqueBaixo.setText(String.valueOf(dados.kpis.getOrDefault("estoqueBaixo", 0)));
                labelKpiLotesExpirar.setText(String.valueOf(dados.kpis.getOrDefault("lotesAExpirar", 0)));
            }

            // Atualiza tabela Estoque Baixo
            modeloEstoqueBaixo.setRowCount(0);
            if (dados.estoqueBaixo != null && !dados.estoqueBaixo.isEmpty()) {
                for (Map<String, Object> l : dados.estoqueBaixo) {
                    modeloEstoqueBaixo.addRow(new Object[]{
                            l.get("nome_produto"),
                            l.get("quantidade_minima"),
                            l.get("quantidade_atual")
                    });
                }
            }

            // Atualiza tabela Mais Vendidos
            modeloMaisVendidos.setRowCount(0);
            if (dados.maisVendidos != null && !dados.maisVendidos.isEmpty()) {
                for (Map<String, Object> l : dados.maisVendidos) {
                    modeloMaisVendidos.addRow(new Object[]{
                            l.get("nome_produto"),
                            l.get("nome_categoria"),
                            l.get("quantidade_vendida"),
                            String.format("%.2f MT", ((Number) l.getOrDefault("valor_vendido", 0.0)).doubleValue())
                    });
                }
            }
        }, err -> {
            // Se falhar (ex: MySQL desconectado), zera sem quebrar
            labelKpiVendasHoje.setText("0");
            labelKpiVendasPendentes.setText("0");
            labelKpiEstoqueBaixo.setText("0");
            labelKpiLotesExpirar.setText("0");
        });
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        this.utilizadorLogado = utilizador;

        String nome = (utilizador != null && utilizador.getUsername() != null)
                ? utilizador.getUsername()
                : "admin";
        if (labelBoasVindas != null) {
            labelBoasVindas.setText("Bem-vindo, " + nome);
        }

        Perfil perfil = (utilizador != null) ? utilizador.getPerfil() : Perfil.ADMIN;
        if (perfil == Perfil.VENDEDOR) {
            // Vendedor não pode criar produto diretamente nem acessar relatórios
            if (btnNovoProduto != null) btnNovoProduto.setVisible(false);
            if (btnVerRelatorios != null) btnVerRelatorios.setVisible(false);
        } else {
            if (btnNovoProduto != null) btnNovoProduto.setVisible(true);
            if (btnVerRelatorios != null) btnVerRelatorios.setVisible(true);
        }
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        if (labelBoasVindas != null) labelBoasVindas.setForeground(UiStyle.TEXT);

        if (tabelaEstoqueBaixo != null) {
            tabelaEstoqueBaixo.setBackground(UiStyle.SURFACE);
            tabelaEstoqueBaixo.setForeground(UiStyle.TEXT);
            tabelaEstoqueBaixo.setGridColor(UiStyle.TABLE_ROW_BORDER);
            tabelaEstoqueBaixo.getTableHeader().setBackground(UiStyle.SURFACE);
            tabelaEstoqueBaixo.getTableHeader().setForeground(UiStyle.MUTED);
        }
        if (tabelaMaisVendidos != null) {
            tabelaMaisVendidos.setBackground(UiStyle.SURFACE);
            tabelaMaisVendidos.setForeground(UiStyle.TEXT);
            tabelaMaisVendidos.setGridColor(UiStyle.TABLE_ROW_BORDER);
            tabelaMaisVendidos.getTableHeader().setBackground(UiStyle.SURFACE);
            tabelaMaisVendidos.getTableHeader().setForeground(UiStyle.MUTED);
        }
        repaint();
    }

    private static final class DadosDashboard {
        final Map<String, Integer> kpis;
        final List<Map<String, Object>> estoqueBaixo;
        final List<Map<String, Object>> maisVendidos;

        DadosDashboard(Map<String, Integer> kpis, List<Map<String, Object>> estoqueBaixo, List<Map<String, Object>> maisVendidos) {
            this.kpis = kpis;
            this.estoqueBaixo = estoqueBaixo;
            this.maisVendidos = maisVendidos;
        }
    }

    private static final class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;

        public PainelCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UiStyle.SURFACE);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RAIO_CARD, RAIO_CARD));
            g2.setColor(UiStyle.BORDER);
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, RAIO_CARD, RAIO_CARD));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static final class BotaoAcaoArredondado extends JButton {
        private static final long serialVersionUID = 1L;
        private final Color bg;
        private final Color borda;
        private final int raio;

        public BotaoAcaoArredondado(String texto, Color bg, Color fg, Color borda, int raio) {
            super(texto);
            this.bg = bg;
            this.borda = borda;
            this.raio = raio;
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(fg);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 16, 10, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), raio, raio));

            if (borda != null) {
                g2.setColor(borda);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, raio, raio));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
