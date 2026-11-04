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
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import model.vo.Perfil;
import model.vo.Utilizador;

/**
 * Painel Utilizadores / Tabelas auxiliares (Cargos e Contactos) em Java Swing fiel aos designs HTML:
 * - "ADMIN · Barra superior · Escuro — Utilizadores" (Utilizadores.dc.html)
 * - "ADMIN · Barra superior · Escuro — Cargo — novo" (CargoNovo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Cargo — editar" (CargoEditar.dc.html)
 * - "ADMIN · Barra superior · Escuro — Contacto — novo" (ContactoNovo.dc.html)
 * - "ADMIN · Barra superior · Escuro — Contacto — editar" (ContactoEditar.dc.html)
 */
public class UtilizadoresPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoVisao {
        UTILIZADORES_PERMISSOES,
        TABELAS_AUXILIARES
    }

    public enum AbaAuxiliar {
        CARGOS("Cargos"),
        ENDERECOS("Endereços"),
        CONTACTOS("Contactos");

        private final String rotulo;

        AbaAuxiliar(String rotulo) {
            this.rotulo = rotulo;
        }

        public String getRotulo() {
            return rotulo;
        }
    }

    public enum ModoPainelLateral {
        NENHUM,
        NOVO_CARGO,
        EDITAR_CARGO,
        NOVO_CONTACTO,
        EDITAR_CONTACTO
    }

    private static final String CARD_VISAO_PERMISSOES = "VISAO_PERMISSOES";
    private static final String CARD_VISAO_AUXILIARES = "VISAO_AUXILIARES";
    private static final String CARD_TABELA_CARGOS = "TABELA_CARGOS";
    private static final String CARD_TABELA_CONTACTOS = "TABELA_CONTACTOS";

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_BOTAO = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] ABAS_AUXILIARES = {
        "Cargos", "Endereços", "Contactos"
    };

    private static final String[] COLUNAS_MATRIZ = {
        "Funcionalidade", "ADMIN", "GERENTE", "VENDEDOR"
    };

    private static final Object[][] DADOS_MATRIZ = {
        {"Entrar no sistema", "Sim", "Sim", "Sim"},
        {"Gerir produtos", "Sim", "Sim", "Não"},
        {"Anular vendas", "Sim", "Sim", "Não"},
        {"Ver relatórios", "Sim", "Sim", "Não"},
        {"Gerir funcionários", "Sim", "Não", "Não"}
    };

    private static final String[] COLUNAS_CARGOS = {
        "Nome", "Salário base", "Func."
    };

    private static final Object[][] DADOS_CARGOS = {
        {"Gerente", "[valor]", "1"},
        {"Vendedor", "[valor]", "4"},
        {"Caixa", "[valor]", "2"}
    };

    private static final String[] COLUNAS_TABELA_CARGOS_AUX = {
        "ID", "Nome", "Salário base", "Funcionários"
    };

    private static final Object[][] DADOS_TABELA_CARGOS_AUX = {
        {"1", "Gerente", "[valor]", "1"},
        {"2", "Vendedor", "[valor]", "4"},
        {"3", "Caixa", "[valor]", "2"}
    };

    private static final String[] COLUNAS_TABELA_CONTACTOS_AUX = {
        "ID", "Valor", "Pessoa", "Tipo"
    };

    private static final Object[][] DADOS_TABELA_CONTACTOS_AUX = {
        {"1", "84 123 4567", "Ana Macie", "Cliente"},
        {"2", "84 000 1111", "Distribuidora Sol", "Fornecedor"},
        {"3", "82 555 0101", "Carlos Mabjaia", "Funcionário"}
    };

    private final CardLayout layoutVisao;
    private final JPanel containerVisao;

    // Componentes da visão "Utilizadores e permissões" (Utilizadores.dc.html)
    private final JTable tabelaMatrizPermissoes;
    private final JTable tabelaCargos;
    private final JButton botaoNovoCargo;

    // Componentes da visão "Tabelas auxiliares" (CargoNovo.dc.html, CargoEditar.dc.html, ContactoNovo.dc.html)
    private final List<BotaoAbaPilula> botoesAbasAuxiliares = new ArrayList<>();
    private final JPanel containerToolbarAuxiliar;
    private final CardLayout layoutTabelasAuxiliares;
    private final JPanel containerTabelasAuxiliares;
    private final CampoBuscaArredondado campoBuscarCargo;
    private final JButton botaoNovoCargoAuxiliar;
    private final JButton botaoEditarCargoAuxiliar;
    private final JButton botaoEliminarCargoAuxiliar;
    private final CampoBuscaArredondado campoBuscarContacto;
    private final JButton botaoNovoContactoAuxiliar;
    private final JButton botaoEditarContactoAuxiliar;
    private final JButton botaoEliminarContactoAuxiliar;
    private final DefaultTableModel modeloTabelaCargosAux;
    private final JTable tabelaCargosAuxiliar;
    private final DefaultTableModel modeloTabelaContactosAux;
    private final JTable tabelaContactosAuxiliar;
    private final JPanel cardNovoCargo;
    private final JPanel cardEditarCargo;
    private final JPanel cardNovoContacto;
    private final JPanel cardEditarContacto;
    private final JPanel wrapperDireitaAuxiliar;
    private JTextField campoNomeNovoCargo;
    private JTextField campoSalarioBaseNovoCargo;
    private JButton botaoGuardarNovoCargo;
    private JButton botaoCancelarNovoCargo;
    private JTextField campoNomeEditarCargo;
    private JTextField campoSalarioBaseEditarCargo;
    private JLabel labelAvisoFuncionariosCargo;
    private JButton botaoGuardarAlteracoesCargo;
    private JButton botaoCancelarEditarCargo;
    private JButton botaoEliminarEditarCargo;
    private JTextField campoValorNovoContacto;
    private BotaoSeletorFormulario seletorTipoNovoContacto;
    private BotaoSeletorFormulario seletorPessoaNovoContacto;
    private JButton botaoGuardarNovoContacto;
    private JButton botaoCancelarNovoContacto;
    private JTextField campoValorEditarContacto;
    private BotaoSeletorFormulario seletorTipoEditarContacto;
    private BotaoSeletorFormulario seletorPessoaEditarContacto;
    private JButton botaoGuardarAlteracoesContacto;
    private JButton botaoCancelarEditarContacto;
    private JButton botaoEliminarEditarContacto;

    private ModoVisao modoVisao;
    private String abaAuxiliarAtiva = "Cargos";
    private ModoPainelLateral modoLateral;

    public UtilizadoresPanel() {
        this(ModoVisao.TABELAS_AUXILIARES, AbaAuxiliar.CONTACTOS, ModoPainelLateral.EDITAR_CONTACTO);
    }

    public UtilizadoresPanel(ModoVisao visaoInicial) {
        this(
                visaoInicial != null ? visaoInicial : ModoVisao.UTILIZADORES_PERMISSOES,
                AbaAuxiliar.CARGOS,
                visaoInicial == ModoVisao.TABELAS_AUXILIARES ? ModoPainelLateral.NOVO_CARGO : ModoPainelLateral.NENHUM
        );
    }

    public UtilizadoresPanel(ModoVisao visaoInicial, ModoPainelLateral modoLateralInicial) {
        this(
                visaoInicial,
                (modoLateralInicial == ModoPainelLateral.NOVO_CONTACTO
                        || modoLateralInicial == ModoPainelLateral.EDITAR_CONTACTO)
                        ? AbaAuxiliar.CONTACTOS
                        : AbaAuxiliar.CARGOS,
                modoLateralInicial
        );
    }

    public UtilizadoresPanel(ModoVisao visaoInicial, AbaAuxiliar abaInicial, ModoPainelLateral modoLateralInicial) {
        UiStyle.instalarTemaEscuro();
        this.modoVisao = visaoInicial != null ? visaoInicial : ModoVisao.TABELAS_AUXILIARES;
        this.abaAuxiliarAtiva = abaInicial != null ? abaInicial.getRotulo() : AbaAuxiliar.CARGOS.getRotulo();
        this.modoLateral = modoLateralInicial != null ? modoLateralInicial : ModoPainelLateral.NOVO_CONTACTO;

        // .ct { padding:24px 28px; display:flex; flex-direction:column; gap:16px; flex:1 }
        setLayout(new BorderLayout());
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.tabelaMatrizPermissoes = criarTabelaMatrizPermissoes();
        this.tabelaCargos = criarTabelaCargos();
        this.botaoNovoCargo = criarBotaoNovoCargo();

        this.containerToolbarAuxiliar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.containerToolbarAuxiliar.setOpaque(false);
        this.containerToolbarAuxiliar.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.layoutTabelasAuxiliares = new CardLayout();
        this.containerTabelasAuxiliares = new JPanel(this.layoutTabelasAuxiliares);
        this.containerTabelasAuxiliares.setOpaque(false);

        this.campoBuscarCargo = criarCampoBuscarCargo();
        this.botaoNovoCargoAuxiliar = criarBotaoNovoCargoAuxiliar();
        this.botaoEditarCargoAuxiliar = criarBotaoEditarCargoAuxiliar();
        this.botaoEliminarCargoAuxiliar = criarBotaoAcaoReservada(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );
        this.campoBuscarContacto = criarCampoBuscarContacto();
        this.botaoNovoContactoAuxiliar = criarBotaoNovoContactoAuxiliar();
        this.botaoEditarContactoAuxiliar = criarBotaoEditarContactoAuxiliar();
        this.botaoEliminarContactoAuxiliar = criarBotaoAcaoReservada(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );

        this.modeloTabelaCargosAux = criarModeloTabelaCargosAux();
        this.tabelaCargosAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaCargosAux);
        this.modeloTabelaContactosAux = criarModeloTabelaContactosAux();
        this.tabelaContactosAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaContactosAux);

        this.cardNovoCargo = criarCardNovoCargo();
        this.cardEditarCargo = criarCardEditarCargo();
        this.cardNovoContacto = criarCardNovoContacto();
        this.cardEditarContacto = criarCardEditarContacto();
        this.wrapperDireitaAuxiliar = new JPanel(new BorderLayout());
        this.wrapperDireitaAuxiliar.setOpaque(false);

        this.tabelaCargosAuxiliar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosCargoParaEdicao();
            }
        });
        this.tabelaContactosAuxiliar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosContactoParaEdicao();
            }
        });

        this.layoutVisao = new CardLayout();
        this.containerVisao = new JPanel(this.layoutVisao);
        this.containerVisao.setOpaque(false);
        this.containerVisao.add(criarVisaoUtilizadoresPermissoes(), CARD_VISAO_PERMISSOES);
        this.containerVisao.add(criarVisaoTabelasAuxiliares(), CARD_VISAO_AUXILIARES);

        add(this.containerVisao, BorderLayout.CENTER);

        atualizarTabelaCargosFiltrada();
        atualizarTabelaContactosFiltrada();
        selecionarAbaAuxiliar(this.abaAuxiliarAtiva);
        definirModoPainelLateral(this.modoLateral);
        definirModoVisao(this.modoVisao);
    }

    public JTable getTabelaMatrizPermissoes() {
        return tabelaMatrizPermissoes;
    }

    public JTable getTabelaCargos() {
        return tabelaCargos;
    }

    public JButton getBotaoNovoCargo() {
        return botaoNovoCargo;
    }

    public JTextField getCampoBuscarCargo() {
        return campoBuscarCargo;
    }

    public JButton getBotaoNovoCargoAuxiliar() {
        return botaoNovoCargoAuxiliar;
    }

    public JButton getBotaoEditarCargoAuxiliar() {
        return botaoEditarCargoAuxiliar;
    }

    public JButton getBotaoEliminarCargoAuxiliar() {
        return botaoEliminarCargoAuxiliar;
    }

    public JTable getTabelaCargosAuxiliar() {
        return tabelaCargosAuxiliar;
    }

    public JTextField getCampoBuscarContacto() {
        return campoBuscarContacto;
    }

    public JButton getBotaoNovoContactoAuxiliar() {
        return botaoNovoContactoAuxiliar;
    }

    public JButton getBotaoEditarContactoAuxiliar() {
        return botaoEditarContactoAuxiliar;
    }

    public JButton getBotaoEliminarContactoAuxiliar() {
        return botaoEliminarContactoAuxiliar;
    }

    public JTable getTabelaContactosAuxiliar() {
        return tabelaContactosAuxiliar;
    }

    public JPanel getCardNovoCargo() {
        return cardNovoCargo;
    }

    public JPanel getCardEditarCargo() {
        return cardEditarCargo;
    }

    public JPanel getCardNovoContacto() {
        return cardNovoContacto;
    }

    public JPanel getCardEditarContacto() {
        return cardEditarContacto;
    }

    public JTextField getCampoNomeNovoCargo() {
        return campoNomeNovoCargo;
    }

    public JTextField getCampoSalarioBaseNovoCargo() {
        return campoSalarioBaseNovoCargo;
    }

    public JButton getBotaoGuardarNovoCargo() {
        return botaoGuardarNovoCargo;
    }

    public JButton getBotaoCancelarNovoCargo() {
        return botaoCancelarNovoCargo;
    }

    public JTextField getCampoNomeEditarCargo() {
        return campoNomeEditarCargo;
    }

    public JTextField getCampoSalarioBaseEditarCargo() {
        return campoSalarioBaseEditarCargo;
    }

    public JLabel getLabelAvisoFuncionariosCargo() {
        return labelAvisoFuncionariosCargo;
    }

    public JButton getBotaoGuardarAlteracoesCargo() {
        return botaoGuardarAlteracoesCargo;
    }

    public JButton getBotaoCancelarEditarCargo() {
        return botaoCancelarEditarCargo;
    }

    public JButton getBotaoEliminarEditarCargo() {
        return botaoEliminarEditarCargo;
    }

    public JTextField getCampoValorNovoContacto() {
        return campoValorNovoContacto;
    }

    public JButton getSeletorTipoNovoContacto() {
        return seletorTipoNovoContacto;
    }

    public JButton getSeletorPessoaNovoContacto() {
        return seletorPessoaNovoContacto;
    }

    public JButton getBotaoGuardarNovoContacto() {
        return botaoGuardarNovoContacto;
    }

    public JButton getBotaoCancelarNovoContacto() {
        return botaoCancelarNovoContacto;
    }

    public JTextField getCampoValorEditarContacto() {
        return campoValorEditarContacto;
    }

    public JButton getSeletorTipoEditarContacto() {
        return seletorTipoEditarContacto;
    }

    public JButton getSeletorPessoaEditarContacto() {
        return seletorPessoaEditarContacto;
    }

    public JButton getBotaoGuardarAlteracoesContacto() {
        return botaoGuardarAlteracoesContacto;
    }

    public JButton getBotaoCancelarEditarContacto() {
        return botaoCancelarEditarContacto;
    }

    public JButton getBotaoEliminarEditarContacto() {
        return botaoEliminarEditarContacto;
    }

    public ModoVisao getModoVisao() {
        return modoVisao;
    }

    public String getAbaAuxiliarAtiva() {
        return abaAuxiliarAtiva;
    }

    public ModoPainelLateral getModoPainelLateral() {
        return modoLateral;
    }

    public boolean isNovoCargoVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_CARGO;
    }

    public boolean isEditarCargoVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_CARGO;
    }

    public boolean isNovoContactoVisivel() {
        return modoLateral == ModoPainelLateral.NOVO_CONTACTO;
    }

    public boolean isEditarContactoVisivel() {
        return modoLateral == ModoPainelLateral.EDITAR_CONTACTO;
    }

    public final void definirModoVisao(ModoVisao novaVisao) {
        this.modoVisao = novaVisao != null ? novaVisao : ModoVisao.UTILIZADORES_PERMISSOES;
        if (this.modoVisao == ModoVisao.TABELAS_AUXILIARES) {
            layoutVisao.show(containerVisao, CARD_VISAO_AUXILIARES);
        } else {
            layoutVisao.show(containerVisao, CARD_VISAO_PERMISSOES);
        }
        revalidate();
        repaint();
    }

    public final void definirNovoCargoVisivel(boolean visivel) {
        definirModoPainelLateral(visivel ? ModoPainelLateral.NOVO_CARGO : ModoPainelLateral.NENHUM);
    }

    public final void exibirPainelEditarCargo() {
        definirModoPainelLateral(ModoPainelLateral.EDITAR_CARGO);
    }

    public final void definirNovoContactoVisivel(boolean visivel) {
        if (visivel) {
            selecionarAbaAuxiliar("Contactos");
            definirModoPainelLateral(ModoPainelLateral.NOVO_CONTACTO);
        } else {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        }
    }

    public final void exibirPainelEditarContacto() {
        selecionarAbaAuxiliar("Contactos");
        definirModoPainelLateral(ModoPainelLateral.EDITAR_CONTACTO);
    }

    public final void definirModoPainelLateral(ModoPainelLateral novoModo) {
        this.modoLateral = novoModo != null ? novoModo : ModoPainelLateral.NENHUM;
        boolean visivel = this.modoLateral != ModoPainelLateral.NENHUM;
        wrapperDireitaAuxiliar.removeAll();
        wrapperDireitaAuxiliar.setVisible(visivel);

        if (this.modoLateral == ModoPainelLateral.NOVO_CARGO) {
            wrapperDireitaAuxiliar.add(cardNovoCargo, BorderLayout.NORTH);
            tabelaCargosAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CARGO) {
            wrapperDireitaAuxiliar.add(cardEditarCargo, BorderLayout.NORTH);
            if (tabelaCargosAuxiliar.getRowCount() > 0 && tabelaCargosAuxiliar.getSelectedRow() < 0) {
                tabelaCargosAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosCargoParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.NOVO_CONTACTO) {
            wrapperDireitaAuxiliar.add(cardNovoContacto, BorderLayout.NORTH);
            tabelaContactosAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CONTACTO) {
            wrapperDireitaAuxiliar.add(cardEditarContacto, BorderLayout.NORTH);
            if (tabelaContactosAuxiliar.getRowCount() > 0 && tabelaContactosAuxiliar.getSelectedRow() < 0) {
                tabelaContactosAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosContactoParaEdicao();
        } else {
            tabelaCargosAuxiliar.clearSelection();
            tabelaContactosAuxiliar.clearSelection();
        }

        revalidate();
        repaint();
    }

    public final void selecionarAbaAuxiliar(AbaAuxiliar aba) {
        if (aba != null) {
            selecionarAbaAuxiliar(aba.getRotulo());
        }
    }

    public final void selecionarAbaAuxiliar(String nomeAba) {
        if (!"Cargos".equalsIgnoreCase(nomeAba) && !"Contactos".equalsIgnoreCase(nomeAba)) {
            JOptionPane.showMessageDialog(
                    this,
                    "A sub-aba \"" + nomeAba + "\" está reservada para a próxima etapa.",
                    "Módulo reservado",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        this.abaAuxiliarAtiva = "Contactos".equalsIgnoreCase(nomeAba) ? "Contactos" : "Cargos";
        for (BotaoAbaPilula botao : botoesAbasAuxiliares) {
            botao.setAtiva(botao.getText().equalsIgnoreCase(this.abaAuxiliarAtiva));
        }

        containerToolbarAuxiliar.removeAll();
        if ("Contactos".equalsIgnoreCase(this.abaAuxiliarAtiva)) {
            // Em ContactoNovo.dc.html: Buscar contacto | Novo contacto | Editar | Eliminar
            containerToolbarAuxiliar.add(campoBuscarContacto);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoNovoContactoAuxiliar);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoEditarContactoAuxiliar);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoEliminarContactoAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_CONTACTOS);
            if (this.modoLateral == ModoPainelLateral.NOVO_CARGO || this.modoLateral == ModoPainelLateral.EDITAR_CARGO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else {
            // Em CargoNovo.dc.html / CargoEditar.dc.html: Buscar cargo | Novo cargo | Editar | Eliminar
            containerToolbarAuxiliar.add(campoBuscarCargo);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoNovoCargoAuxiliar);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoEditarCargoAuxiliar);
            containerToolbarAuxiliar.add(Box.createRigidArea(new Dimension(10, 0)));
            containerToolbarAuxiliar.add(botaoEliminarCargoAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_CARGOS);
            if (this.modoLateral == ModoPainelLateral.NOVO_CONTACTO
                    || this.modoLateral == ModoPainelLateral.EDITAR_CONTACTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        }

        revalidate();
        repaint();
    }

    private JPanel criarVisaoUtilizadoresPermissoes() {
        JPanel visao = new JPanel(new BorderLayout(0, 16));
        visao.setOpaque(false);

        JLabel titulo = new JLabel("Utilizadores e permissões");
        UiStyle.aplicarTexto(titulo, 24, true);
        visao.add(titulo, BorderLayout.NORTH);
        visao.add(criarCorpoPrincipal(), BorderLayout.CENTER);
        return visao;
    }

    /**
     * Visão "Tabelas auxiliares" fiel a CargoNovo.dc.html:
     * <h1>Tabelas auxiliares</h1>
     * <div class="tb"><span class="tab on">Cargos</span><span class="tab">Endereços</span><span class="tab">Contactos</span></div>
     * <div class="tb"><span class="in">Buscar cargo</span><span class="bt">Novo cargo</span><span class="bt s">Editar</span><span class="bt d">Eliminar</span></div>
     * <div class="row" style="align-items:flex-start">...</div>
     */
    private JPanel criarVisaoTabelasAuxiliares() {
        JPanel visao = new JPanel(new BorderLayout(0, 16));
        visao.setOpaque(false);

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Tabelas auxiliares");
        UiStyle.aplicarTexto(titulo, 24, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.add(titulo);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

        // Barra de abas em pílula: Cargos | Endereços | Contactos
        JPanel barraAbas = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraAbas.setOpaque(false);
        barraAbas.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < ABAS_AUXILIARES.length; i++) {
            String nomeAba = ABAS_AUXILIARES[i];
            BotaoAbaPilula botaoAba = new BotaoAbaPilula(nomeAba, "Cargos".equals(nomeAba));
            botaoAba.addActionListener(e -> selecionarAbaAuxiliar(nomeAba));
            botoesAbasAuxiliares.add(botaoAba);
            if (i > 0) {
                barraAbas.add(Box.createRigidArea(new Dimension(10, 0)));
            }
            barraAbas.add(botaoAba);
        }
        topo.add(barraAbas);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));
        topo.add(containerToolbarAuxiliar);

        containerTabelasAuxiliares.add(criarCardTabelaAuxiliar(tabelaCargosAuxiliar), CARD_TABELA_CARGOS);
        containerTabelasAuxiliares.add(criarCardTabelaAuxiliar(tabelaContactosAuxiliar), CARD_TABELA_CONTACTOS);

        JPanel corpo = new JPanel(new BorderLayout(16, 0));
        corpo.setOpaque(false);
        corpo.add(containerTabelasAuxiliares, BorderLayout.CENTER);
        corpo.add(wrapperDireitaAuxiliar, BorderLayout.EAST);

        visao.add(topo, BorderLayout.NORTH);
        visao.add(corpo, BorderLayout.CENTER);
        return visao;
    }

    /**
     * <div class="row">
     *   <div class="card" style="flex:1.3">...</div>
     *   <div class="card" style="flex:1">...</div>
     * </div>
     */
    private JPanel criarCorpoPrincipal() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);

        GridBagConstraints gbcEsquerda = new GridBagConstraints();
        gbcEsquerda.gridx = 0;
        gbcEsquerda.gridy = 0;
        gbcEsquerda.weightx = 1.3;
        gbcEsquerda.weighty = 0;
        gbcEsquerda.fill = GridBagConstraints.BOTH;
        gbcEsquerda.anchor = GridBagConstraints.NORTHWEST;
        gbcEsquerda.insets = new Insets(0, 0, 0, 16);
        container.add(criarCardMatrizPermissoes(), gbcEsquerda);

        GridBagConstraints gbcDireita = new GridBagConstraints();
        gbcDireita.gridx = 1;
        gbcDireita.gridy = 0;
        gbcDireita.weightx = 1.0;
        gbcDireita.weighty = 0;
        gbcDireita.fill = GridBagConstraints.BOTH;
        gbcDireita.anchor = GridBagConstraints.NORTHEAST;
        container.add(criarCardCargos(), gbcDireita);

        GridBagConstraints gbcEspacador = new GridBagConstraints();
        gbcEspacador.gridx = 0;
        gbcEspacador.gridy = 1;
        gbcEspacador.gridwidth = 2;
        gbcEspacador.weightx = 1.0;
        gbcEspacador.weighty = 1.0;
        gbcEspacador.fill = GridBagConstraints.BOTH;
        JPanel espacador = new JPanel();
        espacador.setOpaque(false);
        container.add(espacador, gbcEspacador);

        return container;
    }

    private JPanel criarCardMatrizPermissoes() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // <b>Matriz de permissões</b>
        JLabel titulo = new JLabel("Matriz de permissões");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        card.add(titulo, BorderLayout.NORTH);

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.add(tabelaMatrizPermissoes.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(tabelaMatrizPermissoes, BorderLayout.CENTER);

        card.add(tabelaWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardCargos() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        // <b>Cargos</b>
        JLabel titulo = new JLabel("Cargos");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Novo cargo</span></div>
        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);
        barraBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraBotoes.add(botaoNovoCargo);
        card.add(barraBotoes);

        // <table><tr><th>Nome</th><th>Salário base</th><th>Func.</th></tr>...</table>
        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabelaWrapper.add(tabelaCargos.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(tabelaCargos, BorderLayout.CENTER);
        card.add(tabelaWrapper);

        return card;
    }

    private JButton criarBotaoNovoCargo() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo cargo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            selecionarAbaAuxiliar("Cargos");
            definirModoPainelLateral(ModoPainelLateral.NOVO_CARGO);
            definirModoVisao(ModoVisao.TABELAS_AUXILIARES);
        });
        return botao;
    }

    private JButton criarBotaoNovoCargoAuxiliar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo cargo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_CARGO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_CARGO);
            }
        });
        return botao;
    }

    private JButton criarBotaoNovoContactoAuxiliar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo contacto", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.NOVO_CONTACTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.NOVO_CONTACTO);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarCargoAuxiliar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_CARGO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_CARGO);
            }
        });
        return botao;
    }

    private JButton criarBotaoEditarContactoAuxiliar() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Editar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botao.addActionListener(e -> {
            if (modoLateral == ModoPainelLateral.EDITAR_CONTACTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            } else {
                definirModoPainelLateral(ModoPainelLateral.EDITAR_CONTACTO);
            }
        });
        return botao;
    }

    private JButton criarBotaoAcaoReservada(
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

    private CampoBuscaArredondado criarCampoBuscarCargo() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar cargo");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO);
        campo.setPreferredSize(dim);
        campo.setMinimumSize(dim);
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabelaCargosFiltrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabelaCargosFiltrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabelaCargosFiltrada();
            }
        });
        return campo;
    }

    private CampoBuscaArredondado criarCampoBuscarContacto() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar contacto");
        Dimension dim = new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO);
        campo.setPreferredSize(dim);
        campo.setMinimumSize(dim);
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabelaContactosFiltrada();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabelaContactosFiltrada();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabelaContactosFiltrada();
            }
        });
        return campo;
    }

    private DefaultTableModel criarModeloTabelaCargosAux() {
        return new DefaultTableModel(new Object[0][COLUNAS_TABELA_CARGOS_AUX.length], COLUNAS_TABELA_CARGOS_AUX) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel criarModeloTabelaContactosAux() {
        return new DefaultTableModel(new Object[0][COLUNAS_TABELA_CONTACTOS_AUX.length], COLUNAS_TABELA_CONTACTOS_AUX) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable criarTabelaCargosAuxiliar(DefaultTableModel modelo) {
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

    private JPanel criarCardTabelaAuxiliar(JTable tabela) {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
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
     * Card lateral "Novo contacto" fiel a ContactoNovo.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardNovoContacto() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
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

        // <b>Novo contacto</b>
        JLabel titulo = new JLabel("Novo contacto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Valor * (máx. 30)</span><div class="in" style="min-width:0"></div></div>
        campoValorNovoContacto = new CampoFormularioArredondado("");
        JPanel blocoValor = criarCampoComLabel("Valor * (máx. 30)", campoValorNovoContacto);
        fixarLargura(blocoValor, larguraInterna, 60);
        card.add(blocoValor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm">Tipo * (Cliente ▾) | Pessoa * (Ana Macie ▾)</div>
        seletorTipoNovoContacto = new BotaoSeletorFormulario("Cliente ▾", new String[] {"Cliente", "Fornecedor", "Funcionário"});
        seletorPessoaNovoContacto = new BotaoSeletorFormulario("Ana Macie ▾", new String[] {"Ana Macie", "João Sitoe", "Maria Nhaca", "Distribuidora Sol", "Carlos Mabjaia"});
        JPanel linhaTipoPessoa = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        linhaTipoPessoa.setOpaque(false);
        linhaTipoPessoa.add(criarCampoComLabel("Tipo *", seletorTipoNovoContacto));
        linhaTipoPessoa.add(criarCampoComLabel("Pessoa *", seletorPessoaNovoContacto));
        fixarLargura(linhaTipoPessoa, larguraInterna, 60);
        card.add(linhaTipoPessoa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovoContacto = criarBotaoAcaoReservada(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoCancelarNovoContacto = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovoContacto.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovoContacto);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovoContacto);
        card.add(acoes);

        return card;
    }

    /**
     * Card lateral "Editar contacto" fiel a ContactoEditar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardEditarContacto() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
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

        // <b>Editar contacto</b>
        JLabel titulo = new JLabel("Editar contacto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Valor * (máx. 30)</span><div class="in" style="min-width:0">84 123 4567</div></div>
        campoValorEditarContacto = new CampoFormularioArredondado("84 123 4567");
        JPanel blocoValor = criarCampoComLabel("Valor * (máx. 30)", campoValorEditarContacto);
        fixarLargura(blocoValor, larguraInterna, 60);
        card.add(blocoValor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="fm">Tipo * (Cliente ▾) | Pessoa * (Ana Macie ▾)</div>
        seletorTipoEditarContacto = new BotaoSeletorFormulario("Cliente ▾", new String[] {"Cliente", "Fornecedor", "Funcionário"});
        seletorPessoaEditarContacto = new BotaoSeletorFormulario("Ana Macie ▾", new String[] {"Ana Macie", "João Sitoe", "Maria Nhaca", "Distribuidora Sol", "Carlos Mabjaia"});
        JPanel linhaTipoPessoa = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        linhaTipoPessoa.setOpaque(false);
        linhaTipoPessoa.add(criarCampoComLabel("Tipo *", seletorTipoEditarContacto));
        linhaTipoPessoa.add(criarCampoComLabel("Pessoa *", seletorPessoaEditarContacto));
        fixarLargura(linhaTipoPessoa, larguraInterna, 60);
        card.add(linhaTipoPessoa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesContacto = criarBotaoAcaoReservada(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoCancelarEditarContacto = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditarContacto.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditarContacto = criarBotaoAcaoReservada(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );

        acoes.add(botaoGuardarAlteracoesContacto);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarEditarContacto);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoEliminarEditarContacto);
        card.add(acoes);

        return card;
    }

    private void sincronizarDadosContactoParaEdicao() {
        if (campoValorEditarContacto == null) {
            return;
        }
        int row = tabelaContactosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaContactosAux.getRowCount()) {
            row = 0;
        }
        if (modeloTabelaContactosAux.getRowCount() == 0) {
            return;
        }

        String valor = String.valueOf(modeloTabelaContactosAux.getValueAt(row, 1));
        String pessoa = String.valueOf(modeloTabelaContactosAux.getValueAt(row, 2));
        String tipo = String.valueOf(modeloTabelaContactosAux.getValueAt(row, 3));

        campoValorEditarContacto.setText(valor);
        seletorTipoEditarContacto.setText(tipo + " ▾");
        seletorPessoaEditarContacto.setText(pessoa + " ▾");
    }

    /**
     * Card lateral "Novo cargo" fiel a CargoNovo.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardNovoCargo() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
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

        // <b>Novo cargo</b>
        JLabel titulo = new JLabel("Novo cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0"></div></div>
        campoNomeNovoCargo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovoCargo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Salário base (MT) *  (≥ 0)</span><div class="in" style="min-width:0"></div></div>
        campoSalarioBaseNovoCargo = new CampoFormularioArredondado("");
        JPanel blocoSalario = criarCampoComLabel("Salário base (MT) *  (≥ 0)", campoSalarioBaseNovoCargo);
        fixarLargura(blocoSalario, larguraInterna, 60);
        card.add(blocoSalario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar</span><span class="bt s">Cancelar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovoCargo = criarBotaoAcaoReservada(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoCancelarNovoCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovoCargo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovoCargo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovoCargo);
        card.add(acoes);

        return card;
    }

    /**
     * Card lateral "Editar cargo" fiel a CargoEditar.dc.html:
     * <div class="card" style="width:420px;height:fit-content;display:flex;flex-direction:column;gap:12px">
     */
    private JPanel criarCardEditarCargo() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD) {
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

        // <b>Editar cargo</b>
        JLabel titulo = new JLabel("Editar cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Nome *</span><div class="in" style="min-width:0">Gerente</div></div>
        campoNomeEditarCargo = new CampoFormularioArredondado("Gerente");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditarCargo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div style="display:flex;flex-direction:column;gap:6px"><span class="k">Salário base (MT) *  (≥ 0)</span><div class="in" style="min-width:0">[valor]</div></div>
        campoSalarioBaseEditarCargo = new CampoFormularioArredondado("[valor]");
        JPanel blocoSalario = criarCampoComLabel("Salário base (MT) *  (≥ 0)", campoSalarioBaseEditarCargo);
        fixarLargura(blocoSalario, larguraInterna, 60);
        card.add(blocoSalario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="k">1 funcionário neste cargo. Só pode ser eliminado sem funcionários.</div>
        labelAvisoFuncionariosCargo = new JLabel("1 funcionário neste cargo. Só pode ser eliminado sem funcionários.");
        labelAvisoFuncionariosCargo.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelAvisoFuncionariosCargo.setForeground(UiStyle.MUTED);
        labelAvisoFuncionariosCargo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelAvisoFuncionariosCargo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // <div class="tb"><span class="bt">Guardar alterações</span><span class="bt s">Cancelar</span><span class="bt d">Eliminar</span></div>
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesCargo = criarBotaoAcaoReservada(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, true
        );
        botaoCancelarEditarCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditarCargo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditarCargo = criarBotaoAcaoReservada(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, false
        );

        acoes.add(botaoGuardarAlteracoesCargo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarEditarCargo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoEliminarEditarCargo);
        card.add(acoes);

        return card;
    }

    private void sincronizarDadosCargoParaEdicao() {
        if (campoNomeEditarCargo == null) {
            return;
        }
        int row = tabelaCargosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaCargosAux.getRowCount()) {
            row = 0;
        }
        if (modeloTabelaCargosAux.getRowCount() == 0) {
            return;
        }

        String nome = String.valueOf(modeloTabelaCargosAux.getValueAt(row, 1));
        String salario = String.valueOf(modeloTabelaCargosAux.getValueAt(row, 2));
        String qtdFunc = String.valueOf(modeloTabelaCargosAux.getValueAt(row, 3));

        campoNomeEditarCargo.setText(nome);
        campoSalarioBaseEditarCargo.setText(salario);
        String sufixo = "1".equals(qtdFunc) ? " funcionário neste cargo." : " funcionários neste cargo.";
        labelAvisoFuncionariosCargo.setText(qtdFunc + sufixo + " Só pode ser eliminado sem funcionários.");
    }

    private JPanel criarCampoComLabel(String textoLabel, JComponent campo) {
        JPanel bloco = new JPanel(new BorderLayout(0, 6));
        bloco.setOpaque(false);

        JLabel label = new JLabel(textoLabel);
        label.setFont(UiStyle.fonte(12, Font.PLAIN));
        label.setForeground(UiStyle.MUTED);

        Dimension dimCampo = new Dimension(0, ALTURA_BOTAO);
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

    private void atualizarTabelaCargosFiltrada() {
        String termo = campoBuscarCargo.getTextoReal().trim().toLowerCase();
        modeloTabelaCargosAux.setRowCount(0);

        for (Object[] linha : DADOS_TABELA_CARGOS_AUX) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo)) {
                modeloTabelaCargosAux.addRow(linha);
            }
        }
    }

    private void atualizarTabelaContactosFiltrada() {
        String termo = campoBuscarContacto.getTextoReal().trim().toLowerCase();
        modeloTabelaContactosAux.setRowCount(0);

        for (Object[] linha : DADOS_TABELA_CONTACTOS_AUX) {
            String valor = String.valueOf(linha[1]).toLowerCase();
            String pessoa = String.valueOf(linha[2]).toLowerCase();
            String tipo = String.valueOf(linha[3]).toLowerCase();
            if (termo.isEmpty() || valor.contains(termo) || pessoa.contains(termo) || tipo.contains(termo)) {
                modeloTabelaContactosAux.addRow(linha);
            }
        }
    }

    private JTable criarTabelaMatrizPermissoes() {
        DefaultTableModel modelo = new DefaultTableModel(DADOS_MATRIZ, COLUNAS_MATRIZ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable t = new JTable(modelo);
        t.setRowHeight(42);
        t.setFont(UiStyle.fonte(13, Font.PLAIN));
        t.setBackground(UiStyle.SURFACE);
        t.setForeground(UiStyle.TEXT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(UiStyle.TABLE_ROW_BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setRowSelectionAllowed(false);
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
        RenderizadorCelulaPermissao renderizadorPermissao = new RenderizadorCelulaPermissao();
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (i == 0) {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorTexto);
                t.getColumnModel().getColumn(i).setPreferredWidth(220);
            } else {
                t.getColumnModel().getColumn(i).setCellRenderer(renderizadorPermissao);
            }
        }
        return t;
    }

    private JTable criarTabelaCargos() {
        DefaultTableModel modelo = new DefaultTableModel(DADOS_CARGOS, COLUNAS_CARGOS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable t = new JTable(modelo);
        t.setRowHeight(42);
        t.setFont(UiStyle.fonte(13, Font.PLAIN));
        t.setBackground(UiStyle.SURFACE);
        t.setForeground(UiStyle.TEXT);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(UiStyle.TABLE_ROW_BORDER);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setRowSelectionAllowed(false);
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
     * Renderiza os chips de Permissão:
     * Sim (.ch.g: #12402A / #7FE3A8) e Não (.ch.r: #4F1B1B / #FF9C9C).
     */
    private static final class RenderizadorCelulaPermissao extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String texto = value != null ? value.toString() : "";
            boolean permitido = "Sim".equalsIgnoreCase(texto);

            Color bgChip = permitido ? UiStyle.CHIP_GREEN_BG : UiStyle.ERROR_BG;
            Color fgChip = permitido ? UiStyle.CHIP_GREEN_FG : UiStyle.ERROR_TEXT;

            JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            container.setOpaque(false);
            container.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UiStyle.TABLE_ROW_BORDER),
                    new EmptyBorder(8, 14, 8, 14)
            ));
            container.add(new ChipPermissao(texto, bgChip, fgChip));
            return container;
        }
    }

    private static final class ChipPermissao extends JLabel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;

        ChipPermissao(String texto, Color corFundo, Color corTexto) {
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
            setPreferredSize(new Dimension(pref.width, ALTURA_BOTAO));
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
     * Aba em pílula (.tab / .tab.on):
     * .tab { padding:7px 14px; border-radius:99px; font-size:13px; background:#232A3B; color:#A3ADC2 }
     * .tab.on { background:#F5B638; color:#12151C; font-weight:600 }
     */
    private static final class BotaoAbaPilula extends JButton {
        private static final long serialVersionUID = 1L;

        private boolean ativa;

        BotaoAbaPilula(String texto, boolean ativa) {
            super(texto);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(7, 14, 7, 14));
            setAtiva(ativa);
        }

        void setAtiva(boolean ativa) {
            this.ativa = ativa;
            if (ativa) {
                setFont(UiStyle.fonteSemiBold(13));
                setForeground(UiStyle.ACCENT_TEXT);
            } else {
                setFont(UiStyle.fonte(13, Font.PLAIN));
                setForeground(UiStyle.MUTED);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(ativa ? UiStyle.ACCENT : UiStyle.SECONDARY_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Seletor de formulário estilo .in com min-width:0 (ex: "Cliente ▾", "Ana Macie ▾").
     */
    private static final class BotaoSeletorFormulario extends JButton {
        private static final long serialVersionUID = 1L;

        BotaoSeletorFormulario(String textoInicial, String[] opcoes) {
            super(textoInicial);
            setFont(UiStyle.fonte(13, Font.PLAIN));
            setForeground(UiStyle.MUTED);
            setBackground(UiStyle.INPUT_BG);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 12, 0, 12));

            if (opcoes != null && opcoes.length > 0) {
                addActionListener(e -> {
                    JPopupMenu menu = new JPopupMenu();
                    menu.setBackground(UiStyle.SURFACE);
                    menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
                    for (String opcao : opcoes) {
                        JMenuItem item = new JMenuItem(opcao);
                        item.setFont(UiStyle.fonte(13, Font.PLAIN));
                        item.setBackground(UiStyle.SURFACE);
                        item.setForeground(UiStyle.TEXT);
                        item.addActionListener(ev -> setText(opcao + " ▾"));
                        menu.add(item);
                    }
                    menu.show(this, 0, getHeight() + 2);
                });
            }
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
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /**
     * Campo de texto de formulário estilo .in com min-width:0 (ex: Nome *, Salário base (MT) *  (≥ 0)).
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
     * Campo de texto estilo .in ("Buscar cargo").
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

    private static class PainelCard extends JPanel {
        private static final long serialVersionUID = 1L;

        private final Color corFundo;
        private final Color corBorda;
        private final int raio;

        PainelCard(Color corFundo, Color corBorda, int raio) {
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

    @Override
    public void recarregarDados() {
        atualizarTabelaCargosFiltrada();
        atualizarTabelaContactosFiltrada();
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
        // Acesso restrito exclusivamente ao perfil ADMIN
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        repaint();
    }
}
