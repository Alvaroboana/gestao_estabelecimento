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
import java.awt.Frame;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import controller.UtilizadorController;
import model.vo.Bairro;
import model.vo.Cargo;
import model.vo.Categoria;
import model.vo.Cidade;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.EstadoFuncionario;
import model.vo.Fornecedor;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Pessoa;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import util.AsyncTask;

public class UtilizadoresPanel extends JPanel implements PainelRecarregavel {
    private static final long serialVersionUID = 1L;

    public enum ModoVisao {
        UTILIZADORES_PERMISSOES,
        TABELAS_AUXILIARES
    }

    public enum AbaAuxiliar {
        CARGOS("Cargos"),
        ENDERECOS("Endereços"),
        CONTACTOS("Contactos"),
        CATEGORIAS("Categorias");

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
        EDITAR_CONTACTO,
        NOVA_CIDADE,
        EDITAR_CIDADE,
        NOVO_BAIRRO,
        EDITAR_BAIRRO,
        NOVA_CATEGORIA,
        EDITAR_CATEGORIA
    }

    private static final String CARD_VISAO_PERMISSOES = "VISAO_PERMISSOES";
    private static final String CARD_VISAO_AUXILIARES = "VISAO_AUXILIARES";
    private static final String CARD_TABELA_CARGOS = "TABELA_CARGOS";
    private static final String CARD_TABELA_CONTACTOS = "TABELA_CONTACTOS";
    private static final String CARD_TABELA_ENDERECOS = "TABELA_ENDERECOS";
    private static final String CARD_TABELA_CATEGORIAS = "TABELA_CATEGORIAS";

    private static final int RAIO_CARD = 14;
    private static final int RAIO_COMPONENTE = 8;
    private static final int ALTURA_BOTAO = 38;
    private static final int LARGURA_MIN_INPUT = 200;
    private static final int LARGURA_CARD_LATERAL = 420;

    private static final String[] ABAS_AUXILIARES = {
        "Cargos", "Endereços", "Contactos", "Categorias"
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

    private static final String[] COLUNAS_TABELA_CARGOS_AUX = {
        "ID", "Nome", "Salário base", "Funcionários"
    };

    private static final String[] COLUNAS_TABELA_CONTACTOS_AUX = {
        "ID", "Valor", "Pessoa", "Tipo"
    };

    private static final String[] COLUNAS_TABELA_CIDADES_AUX = {
        "ID", "Nome da Cidade"
    };

    private static final String[] COLUNAS_TABELA_BAIRROS_AUX = {
        "ID", "Nome do Bairro", "Cidade"
    };

    private final CardLayout layoutVisao;
    private final JPanel containerVisao;

    private final JTable tabelaMatrizPermissoes;
    private final DefaultTableModel modeloTabelaCargos;
    private final JTable tabelaCargos;
    private final JButton botaoNovoCargo;
    private final DefaultTableModel modeloTabelaUtilizadores;
    private final JTable tabelaUtilizadores;
    private final JButton botaoNovoUtilizador;
    private final JButton botaoEditarUtilizador;
    private final JButton botaoSenhaUtilizador;
    private final JButton botaoEliminarUtilizador;

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
    private final CampoBuscaArredondado campoBuscarEndereco;
    private final JButton botaoNovaCidadeAuxiliar;
    private final JButton botaoEditarCidadeAuxiliar;
    private final JButton botaoEliminarCidadeAuxiliar;
    private final JButton botaoNovoBairroAuxiliar;
    private final JButton botaoEditarBairroAuxiliar;
    private final JButton botaoEliminarBairroAuxiliar;
    private final CampoBuscaArredondado campoBuscarCategoria;
    private final JButton botaoNovaCategoriaAuxiliar;
    private final JButton botaoEditarCategoriaAuxiliar;
    private final JButton botaoEliminarCategoriaAuxiliar;
    private final DefaultTableModel modeloTabelaCargosAux;
    private final JTable tabelaCargosAuxiliar;
    private final DefaultTableModel modeloTabelaContactosAux;
    private final JTable tabelaContactosAuxiliar;
    private final DefaultTableModel modeloTabelaCidadesAux;
    private final JTable tabelaCidadesAuxiliar;
    private final DefaultTableModel modeloTabelaBairrosAux;
    private final JTable tabelaBairrosAuxiliar;
    private final DefaultTableModel modeloTabelaCategoriasAux;
    private final JTable tabelaCategoriasAuxiliar;
    private JLabel labelTituloCidades;
    private JLabel labelTituloBairros;
    private final JPanel cardNovoCargo;
    private final JPanel cardEditarCargo;
    private final JPanel cardNovoContacto;
    private final JPanel cardEditarContacto;
    private final JPanel cardNovaCidade;
    private final JPanel cardEditarCidade;
    private final JPanel cardNovoBairro;
    private final JPanel cardEditarBairro;
    private final JPanel cardNovaCategoria;
    private final JPanel cardEditarCategoria;
    private final JPanel wrapperDireitaAuxiliar;
    private final JPanel wrapperDireitaPermissoes;
    private final JPanel cardNovoUtilizador;
    private final JPanel cardEditarUtilizador;
    private final JPanel cardRedefinirSenha;
    private BotaoSeletorFormulario seletorFuncionarioNovoUser;
    private JTextField campoUsernameNovoUser;
    private BotaoSeletorFormulario seletorPerfilNovoUser;
    private JTextField campoSenhaNovoUser;
    private JTextField campoUsernameEditarUser;
    private BotaoSeletorFormulario seletorPerfilEditarUser;
    private JLabel labelContaRedefinirSenha;
    private JTextField campoNovaSenhaUser;
    private JTextField campoNomeNovaCidade;
    private JTextField campoNomeEditarCidade;
    private BotaoSeletorFormulario seletorCidadeNovoBairro;
    private JTextField campoNomeNovoBairro;
    private BotaoSeletorFormulario seletorCidadeEditarBairro;
    private JTextField campoNomeEditarBairro;
    private JTextField campoNomeNovaCategoria;
    private JTextField campoNomeEditarCategoria;
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

    private final UtilizadorController utilizadorController;
    private List<Object[]> dadosCargosCarregados = new ArrayList<>();
    private List<Object[]> dadosContactosCarregados = new ArrayList<>();
    private List<Cidade> dadosCidadesCarregados = new ArrayList<>();
    private List<Bairro> dadosBairrosCarregados = new ArrayList<>();
    private List<Categoria> dadosCategoriasCarregadas = new ArrayList<>();
    private List<Utilizador> dadosUtilizadoresCarregados = new ArrayList<>();
    private List<Funcionario> listaFuncionariosReais = new ArrayList<>();
    private List<Cliente> listaClientesReais = new ArrayList<>();
    private List<Fornecedor> listaFornecedoresReais = new ArrayList<>();
    private List<Pessoa> listaPessoasReais = new ArrayList<>();

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
        this(visaoInicial, abaInicial, modoLateralInicial, new UtilizadorController());
    }

    public UtilizadoresPanel(ModoVisao visaoInicial, AbaAuxiliar abaInicial, ModoPainelLateral modoLateralInicial, UtilizadorController utilizadorController) {
        UiStyle.instalarTemaEscuro();
        this.utilizadorController = (utilizadorController != null) ? utilizadorController : new UtilizadorController();
        this.modoVisao = visaoInicial != null ? visaoInicial : ModoVisao.TABELAS_AUXILIARES;
        this.abaAuxiliarAtiva = abaInicial != null ? abaInicial.getRotulo() : AbaAuxiliar.CARGOS.getRotulo();
        this.modoLateral = modoLateralInicial != null ? modoLateralInicial : ModoPainelLateral.NOVO_CONTACTO;

        setLayout(new BorderLayout());
        setBackground(UiStyle.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        this.tabelaMatrizPermissoes = criarTabelaMatrizPermissoes();
        this.modeloTabelaCargos = criarModeloTabelaCargos();
        this.tabelaCargos = criarTabelaCargos(this.modeloTabelaCargos);
        this.botaoNovoCargo = criarBotaoNovoCargo();
        this.modeloTabelaUtilizadores = new DefaultTableModel(
                new String[] {"ID", "Username", "Funcionário", "Perfil", "Estado Func."}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tabelaUtilizadores = criarTabelaCargosAuxiliar(this.modeloTabelaUtilizadores);
        this.botaoNovoUtilizador = new BotaoToolbarArredondado(
                "Nova conta", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoNovoUtilizador.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        this.botaoNovoUtilizador.setIconTextGap(6);
        this.botaoNovoUtilizador.addActionListener(e -> acaoNovoUtilizador());
        this.botaoEditarUtilizador = new BotaoToolbarArredondado(
                "Alterar perfil / username", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoEditarUtilizador.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        this.botaoEditarUtilizador.setIconTextGap(6);
        this.botaoEditarUtilizador.addActionListener(e -> acaoEditarUtilizador());
        this.botaoSenhaUtilizador = new BotaoToolbarArredondado(
                "Redefinir senha", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoSenhaUtilizador.addActionListener(e -> acaoRedefinirSenhaUtilizador());
        this.botaoEliminarUtilizador = new BotaoToolbarArredondado(
                "Eliminar conta", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarUtilizador.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarUtilizador.setIconTextGap(6);
        this.botaoEliminarUtilizador.addActionListener(e -> acaoEliminarUtilizador());

        this.containerToolbarAuxiliar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
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
                return new Dimension(larguraDisp, Math.max(ALTURA_BOTAO, y));
            }
        };
        this.containerToolbarAuxiliar.setOpaque(false);
        this.containerToolbarAuxiliar.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.containerToolbarAuxiliar.setBorder(new EmptyBorder(-6, -8, -6, 0));
        this.layoutTabelasAuxiliares = new CardLayout();
        this.containerTabelasAuxiliares = new JPanel(this.layoutTabelasAuxiliares);
        this.containerTabelasAuxiliares.setOpaque(false);

        this.campoBuscarCargo = criarCampoBuscarCargo();
        this.botaoNovoCargoAuxiliar = criarBotaoNovoCargoAuxiliar();
        this.botaoEditarCargoAuxiliar = criarBotaoEditarCargoAuxiliar();
        this.botaoEliminarCargoAuxiliar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarCargoAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarCargoAuxiliar.setIconTextGap(6);
        this.botaoEliminarCargoAuxiliar.addActionListener(e -> acaoEliminarCargoSelecionado());
        this.campoBuscarContacto = criarCampoBuscarContacto();
        this.botaoNovoContactoAuxiliar = criarBotaoNovoContactoAuxiliar();
        this.botaoEditarContactoAuxiliar = criarBotaoEditarContactoAuxiliar();
        this.botaoEliminarContactoAuxiliar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarContactoAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarContactoAuxiliar.setIconTextGap(6);
        this.botaoEliminarContactoAuxiliar.addActionListener(e -> acaoEliminarContactoSelecionado());

        this.campoBuscarEndereco = criarCampoBuscarEndereco();
        this.botaoNovaCidadeAuxiliar = new BotaoToolbarArredondado(
                "Nova cidade", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoNovaCidadeAuxiliar.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        this.botaoNovaCidadeAuxiliar.setIconTextGap(6);
        this.botaoNovaCidadeAuxiliar.addActionListener(e -> acaoNovaCidade());
        this.botaoEditarCidadeAuxiliar = new BotaoToolbarArredondado(
                "Editar cidade", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoEditarCidadeAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        this.botaoEditarCidadeAuxiliar.setIconTextGap(6);
        this.botaoEditarCidadeAuxiliar.addActionListener(e -> acaoEditarCidade());
        this.botaoEliminarCidadeAuxiliar = new BotaoToolbarArredondado(
                "Eliminar cidade", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarCidadeAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarCidadeAuxiliar.setIconTextGap(6);
        this.botaoEliminarCidadeAuxiliar.addActionListener(e -> acaoEliminarCidade());

        this.botaoNovoBairroAuxiliar = new BotaoToolbarArredondado(
                "Novo bairro", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoNovoBairroAuxiliar.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        this.botaoNovoBairroAuxiliar.setIconTextGap(6);
        this.botaoNovoBairroAuxiliar.addActionListener(e -> acaoNovoBairro());
        this.botaoEditarBairroAuxiliar = new BotaoToolbarArredondado(
                "Editar bairro", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoEditarBairroAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        this.botaoEditarBairroAuxiliar.setIconTextGap(6);
        this.botaoEditarBairroAuxiliar.addActionListener(e -> acaoEditarBairro());
        this.botaoEliminarBairroAuxiliar = new BotaoToolbarArredondado(
                "Eliminar bairro", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarBairroAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarBairroAuxiliar.setIconTextGap(6);
        this.botaoEliminarBairroAuxiliar.addActionListener(e -> acaoEliminarBairro());

        this.campoBuscarCategoria = criarCampoBuscarCategoria();
        this.botaoNovaCategoriaAuxiliar = new BotaoToolbarArredondado(
                "Nova categoria", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        this.botaoNovaCategoriaAuxiliar.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        this.botaoNovaCategoriaAuxiliar.setIconTextGap(6);
        this.botaoNovaCategoriaAuxiliar.addActionListener(e -> acaoNovaCategoria());
        this.botaoEditarCategoriaAuxiliar = new BotaoToolbarArredondado(
                "Editar categoria", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        this.botaoEditarCategoriaAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        this.botaoEditarCategoriaAuxiliar.setIconTextGap(6);
        this.botaoEditarCategoriaAuxiliar.addActionListener(e -> acaoEditarCategoria());
        this.botaoEliminarCategoriaAuxiliar = new BotaoToolbarArredondado(
                "Eliminar categoria", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        this.botaoEliminarCategoriaAuxiliar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        this.botaoEliminarCategoriaAuxiliar.setIconTextGap(6);
        this.botaoEliminarCategoriaAuxiliar.addActionListener(e -> acaoEliminarCategoria());

        this.modeloTabelaCargosAux = criarModeloTabelaCargosAux();
        this.tabelaCargosAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaCargosAux);
        this.modeloTabelaContactosAux = criarModeloTabelaContactosAux();
        this.tabelaContactosAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaContactosAux);

        this.modeloTabelaCidadesAux = criarModeloTabelaCidadesAux();
        this.tabelaCidadesAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaCidadesAux);
        this.modeloTabelaBairrosAux = criarModeloTabelaBairrosAux();
        this.tabelaBairrosAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaBairrosAux);
        this.modeloTabelaCategoriasAux = new DefaultTableModel(new String[] {"ID", "Nome da Categoria"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tabelaCategoriasAuxiliar = criarTabelaCargosAuxiliar(this.modeloTabelaCategoriasAux);
        if (this.tabelaCategoriasAuxiliar.getColumnModel().getColumnCount() > 0) {
            this.tabelaCategoriasAuxiliar.getColumnModel().getColumn(0).setMaxWidth(80);
            this.tabelaCategoriasAuxiliar.getColumnModel().getColumn(0).setPreferredWidth(60);
        }
        this.tabelaCategoriasAuxiliar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelaCategoriasAuxiliar.getSelectedRow() >= 0) {
                    acaoEditarCategoria();
                }
            }
        });

        if (this.tabelaCidadesAuxiliar.getColumnModel().getColumnCount() > 0) {
            this.tabelaCidadesAuxiliar.getColumnModel().getColumn(0).setMaxWidth(60);
            this.tabelaCidadesAuxiliar.getColumnModel().getColumn(0).setPreferredWidth(50);
        }
        if (this.tabelaBairrosAuxiliar.getColumnModel().getColumnCount() > 1) {
            this.tabelaBairrosAuxiliar.getColumnModel().getColumn(0).setMaxWidth(60);
            this.tabelaBairrosAuxiliar.getColumnModel().getColumn(0).setPreferredWidth(50);
            this.tabelaBairrosAuxiliar.getColumnModel().getColumn(2).setPreferredWidth(140);
        }

        this.tabelaCidadesAuxiliar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                atualizarTabelaBairrosFiltrada();
                sincronizarDadosCidadeParaEdicao();
            }
        });
        this.tabelaBairrosAuxiliar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosBairroParaEdicao();
            }
        });
        this.tabelaCategoriasAuxiliar.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosCategoriaParaEdicao();
            }
        });
        this.tabelaUtilizadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                sincronizarDadosUtilizadorParaEdicao();
            }
        });

        this.tabelaCidadesAuxiliar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelaCidadesAuxiliar.getSelectedRow() >= 0) {
                    acaoEditarCidade();
                }
            }
        });
        this.tabelaBairrosAuxiliar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabelaBairrosAuxiliar.getSelectedRow() >= 0) {
                    acaoEditarBairro();
                }
            }
        });

        this.cardNovoCargo = criarCardNovoCargo();
        this.cardEditarCargo = criarCardEditarCargo();
        this.cardNovoContacto = criarCardNovoContacto();
        this.cardEditarContacto = criarCardEditarContacto();
        this.cardNovaCidade = criarCardNovaCidade();
        this.cardEditarCidade = criarCardEditarCidade();
        this.cardNovoBairro = criarCardNovoBairro();
        this.cardEditarBairro = criarCardEditarBairro();
        this.cardNovaCategoria = criarCardNovaCategoria();
        this.cardEditarCategoria = criarCardEditarCategoria();
        this.wrapperDireitaAuxiliar = new JPanel(new BorderLayout());
        this.wrapperDireitaAuxiliar.setOpaque(false);
        this.wrapperDireitaPermissoes = new JPanel(new BorderLayout());
        this.wrapperDireitaPermissoes.setOpaque(false);
        this.wrapperDireitaPermissoes.setVisible(false);
        this.cardNovoUtilizador = criarCardNovoUtilizador();
        this.cardEditarUtilizador = criarCardEditarUtilizador();
        this.cardRedefinirSenha = criarCardRedefinirSenha();

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
        atualizarTabelaCidadesFiltrada();
        atualizarTabelaBairrosFiltrada();
        selecionarAbaAuxiliar(this.abaAuxiliarAtiva);
        definirModoPainelLateral(this.modoLateral);
        definirModoVisao(this.modoVisao);
        recarregarDados();
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
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardNovoCargo), BorderLayout.CENTER);
            tabelaCargosAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CARGO) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardEditarCargo), BorderLayout.CENTER);
            if (tabelaCargosAuxiliar.getRowCount() > 0 && tabelaCargosAuxiliar.getSelectedRow() < 0) {
                tabelaCargosAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosCargoParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.NOVO_CONTACTO) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardNovoContacto), BorderLayout.CENTER);
            tabelaContactosAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CONTACTO) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardEditarContacto), BorderLayout.CENTER);
            if (tabelaContactosAuxiliar.getRowCount() > 0 && tabelaContactosAuxiliar.getSelectedRow() < 0) {
                tabelaContactosAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosContactoParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.NOVA_CIDADE) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardNovaCidade), BorderLayout.CENTER);
            tabelaCidadesAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CIDADE) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardEditarCidade), BorderLayout.CENTER);
            if (tabelaCidadesAuxiliar.getRowCount() > 0 && tabelaCidadesAuxiliar.getSelectedRow() < 0) {
                tabelaCidadesAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosCidadeParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.NOVO_BAIRRO) {
            atualizarOpcoesCidadesNosBairros();
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardNovoBairro), BorderLayout.CENTER);
            tabelaBairrosAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_BAIRRO) {
            atualizarOpcoesCidadesNosBairros();
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardEditarBairro), BorderLayout.CENTER);
            if (tabelaBairrosAuxiliar.getRowCount() > 0 && tabelaBairrosAuxiliar.getSelectedRow() < 0) {
                tabelaBairrosAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosBairroParaEdicao();
        } else if (this.modoLateral == ModoPainelLateral.NOVA_CATEGORIA) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardNovaCategoria), BorderLayout.CENTER);
            tabelaCategoriasAuxiliar.clearSelection();
        } else if (this.modoLateral == ModoPainelLateral.EDITAR_CATEGORIA) {
            wrapperDireitaAuxiliar.add(UiStyle.envolverCardLateralComScroll(cardEditarCategoria), BorderLayout.CENTER);
            if (tabelaCategoriasAuxiliar.getRowCount() > 0 && tabelaCategoriasAuxiliar.getSelectedRow() < 0) {
                tabelaCategoriasAuxiliar.setRowSelectionInterval(0, 0);
            }
            sincronizarDadosCategoriaParaEdicao();
        } else {
            tabelaCargosAuxiliar.clearSelection();
            tabelaContactosAuxiliar.clearSelection();
        }

        UiStyle.atualizarComponentesTema(wrapperDireitaAuxiliar);
        revalidate();
        repaint();
    }

    public final void selecionarAbaAuxiliar(AbaAuxiliar aba) {
        if (aba != null) {
            selecionarAbaAuxiliar(aba.getRotulo());
        }
    }

    public final void selecionarAbaAuxiliar(String nomeAba) {
        if ("Endereços".equalsIgnoreCase(nomeAba) || "Enderecos".equalsIgnoreCase(nomeAba)) {
            this.abaAuxiliarAtiva = "Endereços";
        } else if ("Contactos".equalsIgnoreCase(nomeAba)) {
            this.abaAuxiliarAtiva = "Contactos";
        } else if ("Categorias".equalsIgnoreCase(nomeAba)) {
            this.abaAuxiliarAtiva = "Categorias";
        } else {
            this.abaAuxiliarAtiva = "Cargos";
        }
        for (BotaoAbaPilula botao : botoesAbasAuxiliares) {
            botao.setAtiva(botao.getText().equalsIgnoreCase(this.abaAuxiliarAtiva));
        }

        containerToolbarAuxiliar.removeAll();
        if ("Contactos".equalsIgnoreCase(this.abaAuxiliarAtiva)) {
            containerToolbarAuxiliar.add(campoBuscarContacto);
            containerToolbarAuxiliar.add(botaoNovoContactoAuxiliar);
            containerToolbarAuxiliar.add(botaoEditarContactoAuxiliar);
            containerToolbarAuxiliar.add(botaoEliminarContactoAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_CONTACTOS);
            if (this.modoLateral != ModoPainelLateral.NOVO_CONTACTO && this.modoLateral != ModoPainelLateral.EDITAR_CONTACTO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else if ("Endereços".equalsIgnoreCase(this.abaAuxiliarAtiva)) {
            containerToolbarAuxiliar.add(campoBuscarEndereco);
            containerToolbarAuxiliar.add(botaoNovaCidadeAuxiliar);
            containerToolbarAuxiliar.add(botaoEditarCidadeAuxiliar);
            containerToolbarAuxiliar.add(botaoEliminarCidadeAuxiliar);
            containerToolbarAuxiliar.add(botaoNovoBairroAuxiliar);
            containerToolbarAuxiliar.add(botaoEditarBairroAuxiliar);
            containerToolbarAuxiliar.add(botaoEliminarBairroAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_ENDERECOS);
            if (this.modoLateral != ModoPainelLateral.NOVA_CIDADE && this.modoLateral != ModoPainelLateral.EDITAR_CIDADE
                    && this.modoLateral != ModoPainelLateral.NOVO_BAIRRO && this.modoLateral != ModoPainelLateral.EDITAR_BAIRRO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else if ("Categorias".equalsIgnoreCase(this.abaAuxiliarAtiva)) {
            containerToolbarAuxiliar.add(campoBuscarCategoria);
            containerToolbarAuxiliar.add(botaoNovaCategoriaAuxiliar);
            containerToolbarAuxiliar.add(botaoEditarCategoriaAuxiliar);
            containerToolbarAuxiliar.add(botaoEliminarCategoriaAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_CATEGORIAS);
            if (this.modoLateral != ModoPainelLateral.NOVA_CATEGORIA && this.modoLateral != ModoPainelLateral.EDITAR_CATEGORIA) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        } else {
            containerToolbarAuxiliar.add(campoBuscarCargo);
            containerToolbarAuxiliar.add(botaoNovoCargoAuxiliar);
            containerToolbarAuxiliar.add(botaoEditarCargoAuxiliar);
            containerToolbarAuxiliar.add(botaoEliminarCargoAuxiliar);
            layoutTabelasAuxiliares.show(containerTabelasAuxiliares, CARD_TABELA_CARGOS);
            if (this.modoLateral != ModoPainelLateral.NOVO_CARGO && this.modoLateral != ModoPainelLateral.EDITAR_CARGO) {
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
            }
        }

        revalidate();
        repaint();
    }
    private JPanel criarVisaoUtilizadoresPermissoes() {
        JPanel visao = new JPanel(new BorderLayout(0, 16));
        visao.setOpaque(false);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JLabel titulo = new JLabel("Utilizadores e permissões");
        UiStyle.aplicarTexto(titulo, 24, true);
        BotaoToolbarArredondado btnIrAuxiliares = new BotaoToolbarArredondado(
                "Tabelas auxiliares", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnIrAuxiliares.addActionListener(e -> definirModoVisao(ModoVisao.TABELAS_AUXILIARES));
        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(btnIrAuxiliares, BorderLayout.EAST);

        JPanel corpoPermissoes = new JPanel(new BorderLayout(16, 0));
        corpoPermissoes.setOpaque(false);
        corpoPermissoes.add(criarCorpoPrincipal(), BorderLayout.CENTER);
        corpoPermissoes.add(wrapperDireitaPermissoes, BorderLayout.EAST);

        visao.add(cabecalho, BorderLayout.NORTH);
        visao.add(corpoPermissoes, BorderLayout.CENTER);
        return visao;
    }

    private JPanel criarVisaoTabelasAuxiliares() {
        JPanel visao = new JPanel(new BorderLayout(0, 16));
        visao.setOpaque(false);

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setOpaque(false);

        JPanel linhaTitulo = new JPanel(new BorderLayout());
        linhaTitulo.setOpaque(false);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("Tabelas auxiliares");
        UiStyle.aplicarTexto(titulo, 24, true);
        BotaoToolbarArredondado btnIrPermissoes = new BotaoToolbarArredondado(
                "Utilizadores e permissões", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnIrPermissoes.addActionListener(e -> definirModoVisao(ModoVisao.UTILIZADORES_PERMISSOES));
        linhaTitulo.add(titulo, BorderLayout.WEST);
        linhaTitulo.add(btnIrPermissoes, BorderLayout.EAST);
        topo.add(linhaTitulo);
        topo.add(Box.createRigidArea(new Dimension(0, 16)));

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
        containerTabelasAuxiliares.add(criarCardEnderecosAuxiliar(), CARD_TABELA_ENDERECOS);
        containerTabelasAuxiliares.add(criarCardTabelaAuxiliar(tabelaCategoriasAuxiliar), CARD_TABELA_CATEGORIAS);

        JPanel corpo = new JPanel(new BorderLayout(16, 0));
        corpo.setOpaque(false);
        corpo.add(containerTabelasAuxiliares, BorderLayout.CENTER);
        corpo.add(wrapperDireitaAuxiliar, BorderLayout.EAST);

        visao.add(topo, BorderLayout.NORTH);
        visao.add(corpo, BorderLayout.CENTER);
        return visao;
    }

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
        gbcEspacador.insets = new Insets(16, 0, 0, 0);
        container.add(criarCardContasUtilizadores(), gbcEspacador);

        return container;
    }

    private JPanel criarCardContasUtilizadores() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel topo = new JPanel(new BorderLayout(12, 0));
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Contas de utilizadores");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(botaoNovoUtilizador);
        botoes.add(botaoEditarUtilizador);
        botoes.add(botaoSenhaUtilizador);
        botoes.add(botaoEliminarUtilizador);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);
        card.add(topo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tabelaUtilizadores);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(UiStyle.SURFACE);

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.add(tabelaUtilizadores.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(scroll, BorderLayout.CENTER);
        card.add(tabelaWrapper, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardMatrizPermissoes() {
        PainelCard card = new PainelCard(UiStyle.SURFACE, UiStyle.BORDER, RAIO_CARD);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

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

        JLabel titulo = new JLabel("Cargos");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel barraBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBotoes.setOpaque(false);
        barraBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraBotoes.add(botaoNovoCargo);
        card.add(barraBotoes);

        JPanel tabelaWrapper = new JPanel(new BorderLayout());
        tabelaWrapper.setOpaque(false);
        tabelaWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane scrollCargos = new JScrollPane(tabelaCargos);
        scrollCargos.setBorder(BorderFactory.createEmptyBorder());
        scrollCargos.setOpaque(false);
        scrollCargos.getViewport().setOpaque(false);
        scrollCargos.getViewport().setBackground(UiStyle.SURFACE);
        tabelaWrapper.add(tabelaCargos.getTableHeader(), BorderLayout.NORTH);
        tabelaWrapper.add(scrollCargos, BorderLayout.CENTER);
        card.add(tabelaWrapper);

        return card;
    }

    private JButton criarBotaoNovoCargo() {
        BotaoToolbarArredondado botao = new BotaoToolbarArredondado(
                "Novo cargo", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.icone(MaterialDesignP.PLUS, 15, UiStyle.ACCENT_TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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
        botao.setIcon(UiStyle.iconeDinamico(MaterialDesignP.PENCIL_OUTLINE, 15, () -> UiStyle.TEXT));
        botao.setIconTextGap(6);
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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Novo contacto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoValorNovoContacto = new CampoFormularioArredondado("");
        JPanel blocoValor = criarCampoComLabel("Valor * (máx. 30)", campoValorNovoContacto);
        fixarLargura(blocoValor, larguraInterna, 60);
        card.add(blocoValor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorTipoNovoContacto = new BotaoSeletorFormulario("Cliente", new String[] {"Cliente", "Fornecedor", "Funcionário"});
        seletorPessoaNovoContacto = new BotaoSeletorFormulario("Ana Macie", new String[] {"Ana Macie", "João Sitoe", "Maria Nhaca", "Distribuidora Sol", "Carlos Mabjaia"});
        seletorTipoNovoContacto.setAoSelecionar(() -> atualizarPessoasPorTipo(seletorTipoNovoContacto, seletorPessoaNovoContacto, null));
        JPanel linhaTipoPessoa = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        linhaTipoPessoa.setOpaque(false);
        linhaTipoPessoa.add(criarCampoComLabel("Tipo *", seletorTipoNovoContacto));
        linhaTipoPessoa.add(criarCampoComLabel("Pessoa *", seletorPessoaNovoContacto));
        fixarLargura(linhaTipoPessoa, larguraInterna, 60);
        card.add(linhaTipoPessoa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovoContacto = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovoContacto.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarNovoContacto.setIconTextGap(6);
        botaoGuardarNovoContacto.addActionListener(e -> acaoGuardarNovoContacto());
        botaoCancelarNovoContacto = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovoContacto.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarNovoContacto.setIconTextGap(6);
        botaoCancelarNovoContacto.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovoContacto);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovoContacto);
        card.add(acoes);

        return card;
    }

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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar contacto");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoValorEditarContacto = new CampoFormularioArredondado("");
        JPanel blocoValor = criarCampoComLabel("Valor * (máx. 30)", campoValorEditarContacto);
        fixarLargura(blocoValor, larguraInterna, 60);
        card.add(blocoValor);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorTipoEditarContacto = new BotaoSeletorFormulario("Cliente", new String[] {"Cliente", "Fornecedor", "Funcionário"});
        seletorPessoaEditarContacto = new BotaoSeletorFormulario("Ana Macie", new String[] {"Ana Macie", "João Sitoe", "Maria Nhaca", "Distribuidora Sol", "Carlos Mabjaia"});
        seletorTipoEditarContacto.setAoSelecionar(() -> atualizarPessoasPorTipo(seletorTipoEditarContacto, seletorPessoaEditarContacto, null));
        JPanel linhaTipoPessoa = new JPanel(new java.awt.GridLayout(1, 2, 12, 0));
        linhaTipoPessoa.setOpaque(false);
        linhaTipoPessoa.add(criarCampoComLabel("Tipo *", seletorTipoEditarContacto));
        linhaTipoPessoa.add(criarCampoComLabel("Pessoa *", seletorPessoaEditarContacto));
        fixarLargura(linhaTipoPessoa, larguraInterna, 60);
        card.add(linhaTipoPessoa);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesContacto = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoesContacto.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoesContacto.setIconTextGap(5);
        botaoGuardarAlteracoesContacto.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoGuardarAlteracoesContacto.addActionListener(e -> acaoGuardarAlteracoesContacto());
        botaoCancelarEditarContacto = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditarContacto.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarEditarContacto.setIconTextGap(5);
        botaoCancelarEditarContacto.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoCancelarEditarContacto.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditarContacto = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditarContacto.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        botaoEliminarEditarContacto.setIconTextGap(5);
        botaoEliminarEditarContacto.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoEliminarEditarContacto.addActionListener(e -> acaoEliminarContactoSelecionado());

        acoes.add(botaoGuardarAlteracoesContacto);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoCancelarEditarContacto);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
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
        seletorTipoEditarContacto.setText(tipo);
        atualizarPessoasPorTipo(seletorTipoEditarContacto, seletorPessoaEditarContacto, pessoa);
    }

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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Novo cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovoCargo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeNovoCargo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoSalarioBaseNovoCargo = new CampoFormularioArredondado("");
        JPanel blocoSalario = criarCampoComLabel("Salário base (MT) *  (≥ 0)", campoSalarioBaseNovoCargo);
        fixarLargura(blocoSalario, larguraInterna, 60);
        card.add(blocoSalario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarNovoCargo = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarNovoCargo.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarNovoCargo.setIconTextGap(6);
        botaoGuardarNovoCargo.addActionListener(e -> acaoGuardarNovoCargo());
        botaoCancelarNovoCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarNovoCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarNovoCargo.setIconTextGap(6);
        botaoCancelarNovoCargo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(botaoGuardarNovoCargo);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(botaoCancelarNovoCargo);
        card.add(acoes);

        return card;
    }

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

        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar cargo");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditarCargo = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome *", campoNomeEditarCargo);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoSalarioBaseEditarCargo = new CampoFormularioArredondado("");
        JPanel blocoSalario = criarCampoComLabel("Salário base (MT) *  (≥ 0)", campoSalarioBaseEditarCargo);
        fixarLargura(blocoSalario, larguraInterna, 60);
        card.add(blocoSalario);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelAvisoFuncionariosCargo = new JLabel("1 funcionário neste cargo. Só pode ser eliminado sem funcionários.");
        labelAvisoFuncionariosCargo.setFont(UiStyle.fonte(12, Font.PLAIN));
        labelAvisoFuncionariosCargo.setForeground(UiStyle.MUTED);
        labelAvisoFuncionariosCargo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelAvisoFuncionariosCargo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        botaoGuardarAlteracoesCargo = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        botaoGuardarAlteracoesCargo.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        botaoGuardarAlteracoesCargo.setIconTextGap(5);
        botaoGuardarAlteracoesCargo.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoGuardarAlteracoesCargo.addActionListener(e -> acaoGuardarAlteracoesCargo());
        botaoCancelarEditarCargo = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        botaoCancelarEditarCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        botaoCancelarEditarCargo.setIconTextGap(5);
        botaoCancelarEditarCargo.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoCancelarEditarCargo.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));
        botaoEliminarEditarCargo = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        botaoEliminarEditarCargo.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        botaoEliminarEditarCargo.setIconTextGap(5);
        botaoEliminarEditarCargo.setBorder(new EmptyBorder(9, 10, 9, 10));
        botaoEliminarEditarCargo.addActionListener(e -> acaoEliminarCargoSelecionado());

        acoes.add(botaoGuardarAlteracoesCargo);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(botaoCancelarEditarCargo);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
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
        List<Object[]> fonteDados = (dadosCargosCarregados != null) ? dadosCargosCarregados : new ArrayList<>();

        for (Object[] linha : fonteDados) {
            String nome = String.valueOf(linha[1]).toLowerCase();
            if (termo.isEmpty() || nome.contains(termo)) {
                modeloTabelaCargosAux.addRow(linha);
            }
        }
    }

    private void atualizarTabelaContactosFiltrada() {
        String termo = campoBuscarContacto.getTextoReal().trim().toLowerCase();
        modeloTabelaContactosAux.setRowCount(0);
        List<Object[]> fonteDados = (dadosContactosCarregados != null) ? dadosContactosCarregados : new ArrayList<>();

        for (Object[] linha : fonteDados) {
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

    private DefaultTableModel criarModeloTabelaCargos() {
        return new DefaultTableModel(new Object[0][COLUNAS_CARGOS.length], COLUNAS_CARGOS) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable criarTabelaCargos(DefaultTableModel modelo) {
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
            boolean permitido = "Sim".equalsIgnoreCase(getText() != null ? getText().trim() : "");
            setForeground(permitido ? UiStyle.CHIP_GREEN_FG : UiStyle.ERROR_TEXT);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = getHeight();
                g2.setColor(permitido ? UiStyle.CHIP_GREEN_BG : UiStyle.ERROR_BG);
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
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, ALTURA_BOTAO);
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
                setForeground(ativa ? UiStyle.ACCENT_TEXT : UiStyle.MUTED);
                g2.setColor(ativa ? UiStyle.ACCENT : UiStyle.TAB_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private static final class BotaoSeletorFormulario extends JButton {
        private static final long serialVersionUID = 1L;
        private String[] opcoes;
        private Runnable aoSelecionar;

        BotaoSeletorFormulario(String textoInicial, String[] opcoes) {
            super(textoInicial);
            this.opcoes = opcoes;
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

            addActionListener(e -> {
                if (this.opcoes != null && this.opcoes.length > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.setBackground(UiStyle.SURFACE);
                    menu.setBorder(BorderFactory.createLineBorder(UiStyle.BORDER));
                    for (String opcao : this.opcoes) {
                        JMenuItem item = new JMenuItem(opcao);
                        item.setFont(UiStyle.fonte(13, Font.PLAIN));
                        item.setBackground(UiStyle.SURFACE);
                        item.setForeground(UiStyle.TEXT);
                        item.addActionListener(ev -> {
                            setText(opcao);
                            if (this.aoSelecionar != null) {
                                this.aoSelecionar.run();
                            }
                        });
                        menu.add(item);
                    }
                    UiStyle.mostrarPopupSincronizado(menu, this);
                }
            });
        }

        void setOpcoes(String[] novasOpcoes) {
            this.opcoes = novasOpcoes;
        }

        void setAoSelecionar(Runnable callback) {
            this.aoSelecionar = callback;
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

    private void acaoGuardarNovoCargo() {
        String nome = campoNomeNovoCargo.getText().trim();
        String salStr = campoSalarioBaseNovoCargo.getText().trim().replace(",", ".");
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do cargo é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salario = 0.0;
        try {
            salario = salStr.isEmpty() ? 0.0 : Double.parseDouble(salStr);
            if (salario < 0) {
                JOptionPane.showMessageDialog(this, "O salário base deve ser maior ou igual a zero.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salário base inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cargo c = new Cargo();
        c.setNome(nome);
        c.setSalarioBase(salario);
        AsyncTask.executar(
            () -> {
                utilizadorController.salvarCargo(c);
                return c;
            },
            salvo -> {
                JOptionPane.showMessageDialog(this, "Cargo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                campoNomeNovoCargo.setText("");
                campoSalarioBaseNovoCargo.setText("");
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao salvar cargo: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void acaoGuardarAlteracoesCargo() {
        int row = tabelaCargosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaCargosAux.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um cargo para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabelaCargosAux.getValueAt(row, 0)));
        String nome = campoNomeEditarCargo.getText().trim();
        String salStr = campoSalarioBaseEditarCargo.getText().trim().replaceAll("[^0-9.,]", "").replace(",", ".");
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do cargo é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double salario = 0.0;
        try {
            salario = salStr.isEmpty() ? 0.0 : Double.parseDouble(salStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salário base inválido.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cargo c = new Cargo();
        c.setIdCargo(id);
        c.setNome(nome);
        c.setSalarioBase(salario);
        AsyncTask.executar(
            () -> {
                utilizadorController.atualizarCargo(c);
                return true;
            },
            res -> {
                JOptionPane.showMessageDialog(this, "Cargo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao atualizar cargo: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void acaoEliminarCargoSelecionado() {
        int row = tabelaCargosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaCargosAux.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um cargo para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabelaCargosAux.getValueAt(row, 0)));
        String nome = String.valueOf(modeloTabelaCargosAux.getValueAt(row, 1));
        int op = JOptionPane.showConfirmDialog(this, "Deseja realmente eliminar o cargo '" + nome + "'?", "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            AsyncTask.executar(
                () -> {
                    utilizadorController.eliminarCargo(id);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Cargo eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> JOptionPane.showMessageDialog(this, "Erro ao eliminar cargo: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    private void acaoGuardarNovoContacto() {
        String valor = campoValorNovoContacto.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O valor do contacto é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Contacto c = new Contacto();
        c.setContacto(valor);
        String nomePessoa = seletorPessoaNovoContacto != null
                ? seletorPessoaNovoContacto.getText().replace(" ▾", "").replace("▾", "").trim()
                : "";
        Long idPessoa = null;
        if (listaPessoasReais != null) {
            for (Pessoa p : listaPessoasReais) {
                if (p.getNome() != null && p.getNome().equalsIgnoreCase(nomePessoa)) {
                    idPessoa = p.getIdPessoa();
                    break;
                }
            }
        }
        if (idPessoa == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma pessoa cadastrada para associar ao contacto.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final Long idPessoaFinal = idPessoa;
        AsyncTask.executar(
            () -> {
                utilizadorController.salvarContactoComPessoa(c, idPessoaFinal);
                return c;
            },
            salvo -> {
                JOptionPane.showMessageDialog(this, "Contacto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                campoValorNovoContacto.setText("");
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao salvar contacto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void acaoGuardarAlteracoesContacto() {
        int row = tabelaContactosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaContactosAux.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um contacto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabelaContactosAux.getValueAt(row, 0)));
        String valor = campoValorEditarContacto.getText().trim();
        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O valor do contacto é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Contacto c = new Contacto();
        c.setIdContacto(id);
        c.setContacto(valor);
        AsyncTask.executar(
            () -> {
                utilizadorController.atualizarContacto(c);
                return true;
            },
            res -> {
                JOptionPane.showMessageDialog(this, "Contacto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                definirModoPainelLateral(ModoPainelLateral.NENHUM);
                recarregarDados();
            },
            erro -> JOptionPane.showMessageDialog(this, "Erro ao atualizar contacto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void acaoEliminarContactoSelecionado() {
        int row = tabelaContactosAuxiliar.getSelectedRow();
        if (row < 0 || row >= modeloTabelaContactosAux.getRowCount()) {
            JOptionPane.showMessageDialog(this, "Selecione um contacto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.valueOf(String.valueOf(modeloTabelaContactosAux.getValueAt(row, 0)));
        String valor = String.valueOf(modeloTabelaContactosAux.getValueAt(row, 1));
        int op = JOptionPane.showConfirmDialog(this, "Deseja realmente eliminar o contacto '" + valor + "'?", "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            AsyncTask.executar(
                () -> {
                    utilizadorController.eliminarContacto(id);
                    return true;
                },
                res -> {
                    JOptionPane.showMessageDialog(this, "Contacto eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                erro -> JOptionPane.showMessageDialog(this, "Erro ao eliminar contacto: " + erro.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    @Override
    public void recarregarDados() {
        AsyncTask.executar(
            () -> {
                List<Cargo> cargos = utilizadorController.listarCargos();
                List<Funcionario> funcs = new ArrayList<>();
                try {
                    funcs = utilizadorController.listarFuncionarios();
                } catch (Exception ignored) {}
                return new Object[] { cargos, funcs };
            },
            resultado -> {
                Object[] arr = (Object[]) resultado;
                @SuppressWarnings("unchecked")
                List<Cargo> cargos = (List<Cargo>) arr[0];
                @SuppressWarnings("unchecked")
                List<Funcionario> funcs = (List<Funcionario>) arr[1];
                List<Object[]> linhas = new ArrayList<>();
                modeloTabelaCargos.setRowCount(0);
                if (cargos != null) {
                    for (Cargo c : cargos) {
                        String idStr = String.valueOf(c.getIdCargo());
                        String nomeStr = c.getNome() != null ? c.getNome() : "";
                        String salStr = String.format(Locale.GERMANY, "%,.2f MT", c.getSalarioBase() != null ? c.getSalarioBase() : 0.0);
                        int qtdFunc = 0;
                        if (funcs != null && c.getIdCargo() != null) {
                            for (Funcionario f : funcs) {
                                if (f.getCargo() != null && c.getIdCargo().equals(f.getCargo().getIdCargo())) {
                                    qtdFunc++;
                                }
                            }
                        }
                        String funcStr = String.valueOf(qtdFunc);
                        linhas.add(new Object[] {idStr, nomeStr, salStr, funcStr});
                        modeloTabelaCargos.addRow(new Object[] {nomeStr, salStr, funcStr});
                    }
                }
                this.dadosCargosCarregados = linhas;
                atualizarTabelaCargosFiltrada();
            },
            erro -> {
                this.dadosCargosCarregados = new ArrayList<>();
                modeloTabelaCargos.setRowCount(0);
                atualizarTabelaCargosFiltrada();
            }
        );

        AsyncTask.executar(
            () -> {
                List<Cliente> clientes = new ArrayList<>();
                List<Fornecedor> fornecedores = new ArrayList<>();
                List<Funcionario> funcs = new ArrayList<>();
                try { clientes = utilizadorController.listarClientes(); } catch (Exception ignored) {}
                try { fornecedores = utilizadorController.listarFornecedores(); } catch (Exception ignored) {}
                try { funcs = utilizadorController.listarFuncionarios(); } catch (Exception ignored) {}
                return new Object[] { clientes, fornecedores, funcs };
            },
            resultado -> {
                Object[] arr = (Object[]) resultado;
                @SuppressWarnings("unchecked")
                List<Cliente> clientes = (List<Cliente>) arr[0];
                @SuppressWarnings("unchecked")
                List<Fornecedor> fornecedores = (List<Fornecedor>) arr[1];
                @SuppressWarnings("unchecked")
                List<Funcionario> funcs = (List<Funcionario>) arr[2];
                List<Object[]> linhas = new ArrayList<>();
                List<Pessoa> pessoas = new ArrayList<>();
                if (clientes != null) {
                    for (Cliente cli : clientes) {
                        pessoas.add(cli);
                        if (cli.getContacto() != null) {
                            for (Contacto c : cli.getContacto()) {
                                linhas.add(new Object[] {
                                    String.valueOf(c.getIdContacto()),
                                    c.getContacto() != null ? c.getContacto() : "",
                                    cli.getNome() != null ? cli.getNome() : "-",
                                    "Cliente"
                                });
                            }
                        }
                    }
                }
                if (fornecedores != null) {
                    for (Fornecedor forn : fornecedores) {
                        pessoas.add(forn);
                        if (forn.getContacto() != null) {
                            for (Contacto c : forn.getContacto()) {
                                linhas.add(new Object[] {
                                    String.valueOf(c.getIdContacto()),
                                    c.getContacto() != null ? c.getContacto() : "",
                                    forn.getNome() != null ? forn.getNome() : "-",
                                    "Fornecedor"
                                });
                            }
                        }
                    }
                }
                if (funcs != null) {
                    for (Funcionario f : funcs) {
                        pessoas.add(f);
                        if (f.getContacto() != null) {
                            for (Contacto c : f.getContacto()) {
                                linhas.add(new Object[] {
                                    String.valueOf(c.getIdContacto()),
                                    c.getContacto() != null ? c.getContacto() : "",
                                    f.getNome() != null ? f.getNome() : "-",
                                    "Funcionário"
                                });
                            }
                        }
                    }
                }
                this.listaPessoasReais = pessoas;
                this.listaClientesReais = clientes != null ? clientes : new ArrayList<>();
                this.listaFornecedoresReais = fornecedores != null ? fornecedores : new ArrayList<>();
                this.listaFuncionariosReais = funcs != null ? funcs : new ArrayList<>();
                this.dadosContactosCarregados = linhas;
                atualizarPessoasPorTipo(seletorTipoNovoContacto, seletorPessoaNovoContacto,
                        seletorPessoaNovoContacto != null ? seletorPessoaNovoContacto.getText() : null);
                atualizarPessoasPorTipo(seletorTipoEditarContacto, seletorPessoaEditarContacto,
                        seletorPessoaEditarContacto != null ? seletorPessoaEditarContacto.getText() : null);
                atualizarTabelaContactosFiltrada();
            },
            erro -> {
                this.dadosContactosCarregados = new ArrayList<>();
                atualizarTabelaContactosFiltrada();
            }
        );
        AsyncTask.executar(
            () -> {
                List<Cidade> cids = utilizadorController.listarCidades();
                List<Bairro> bairs = utilizadorController.listarBairros();
                List<Categoria> cats = new ArrayList<>();
                List<Utilizador> users = new ArrayList<>();
                try { cats = utilizadorController.listarCategorias(); } catch (Exception ignored) {}
                try { users = utilizadorController.listarUtilizadores(); } catch (Exception ignored) {}
                return new Object[] { cids, bairs, cats, users };
            },
            res -> {
                Object[] arr = (Object[]) res;
                @SuppressWarnings("unchecked")
                List<Cidade> cids = (List<Cidade>) arr[0];
                @SuppressWarnings("unchecked")
                List<Bairro> bairs = (List<Bairro>) arr[1];
                @SuppressWarnings("unchecked")
                List<Categoria> cats = (List<Categoria>) arr[2];
                @SuppressWarnings("unchecked")
                List<Utilizador> users = (List<Utilizador>) arr[3];
                this.dadosCidadesCarregados = cids != null ? cids : new ArrayList<>();
                this.dadosBairrosCarregados = bairs != null ? bairs : new ArrayList<>();
                this.dadosCategoriasCarregadas = cats != null ? cats : new ArrayList<>();
                this.dadosUtilizadoresCarregados = users != null ? users : new ArrayList<>();
                atualizarTabelaCidadesFiltrada();
                atualizarTabelaBairrosFiltrada();
                atualizarTabelaCategoriasFiltrada();
                atualizarTabelaUtilizadores();
            },
            erro -> {
                this.dadosCidadesCarregados = new ArrayList<>();
                this.dadosBairrosCarregados = new ArrayList<>();
                this.dadosCategoriasCarregadas = new ArrayList<>();
                this.dadosUtilizadoresCarregados = new ArrayList<>();
                atualizarTabelaCidadesFiltrada();
                atualizarTabelaBairrosFiltrada();
                atualizarTabelaCategoriasFiltrada();
                atualizarTabelaUtilizadores();
            }
        );
    }

    @Override
    public void aplicarPermissoes(Utilizador utilizador) {
    }

    @Override
    public void aplicarTema() {
        setBackground(UiStyle.BACKGROUND);
        UiStyle.atualizarComponentesTema(this);
        UiStyle.atualizarComponentesTema(cardNovoCargo);
        UiStyle.atualizarComponentesTema(cardEditarCargo);
        UiStyle.atualizarComponentesTema(cardNovoContacto);
        UiStyle.atualizarComponentesTema(cardEditarContacto);
        UiStyle.atualizarComponentesTema(cardNovaCidade);
        UiStyle.atualizarComponentesTema(cardEditarCidade);
        UiStyle.atualizarComponentesTema(cardNovoBairro);
        UiStyle.atualizarComponentesTema(cardEditarBairro);
        UiStyle.atualizarComponentesTema(cardNovaCategoria);
        UiStyle.atualizarComponentesTema(cardEditarCategoria);
        UiStyle.atualizarComponentesTema(cardNovoUtilizador);
        UiStyle.atualizarComponentesTema(cardEditarUtilizador);
        UiStyle.atualizarComponentesTema(cardRedefinirSenha);
    }

    private void atualizarPessoasPorTipo(
            BotaoSeletorFormulario seletorTipo,
            BotaoSeletorFormulario seletorPessoa,
            String pessoaPreferida) {
        if (seletorTipo == null || seletorPessoa == null) {
            return;
        }
        String tipo = seletorTipo.getText() != null ? seletorTipo.getText().trim() : "Cliente";
        List<String> nomes = new ArrayList<>();
        if ("Fornecedor".equalsIgnoreCase(tipo)) {
            if (listaFornecedoresReais != null) {
                for (Fornecedor f : listaFornecedoresReais) {
                    if (f != null && f.getNome() != null) nomes.add(f.getNome());
                }
            }
        } else if ("Funcionário".equalsIgnoreCase(tipo) || "Funcionario".equalsIgnoreCase(tipo)) {
            if (listaFuncionariosReais != null) {
                for (Funcionario f : listaFuncionariosReais) {
                    if (f != null && f.getNome() != null) nomes.add(f.getNome());
                }
            }
        } else {
            if (listaClientesReais != null) {
                for (Cliente c : listaClientesReais) {
                    if (c != null && c.getNome() != null) nomes.add(c.getNome());
                }
            }
        }
        String[] opcoes = nomes.toArray(new String[0]);
        seletorPessoa.setOpcoes(opcoes);
        String prefLimpa = pessoaPreferida != null ? UiStyle.removerMarcadorSeta(pessoaPreferida).trim() : null;
        if (prefLimpa != null && nomes.stream().anyMatch(n -> n.equalsIgnoreCase(prefLimpa))) {
            seletorPessoa.setText(prefLimpa);
        } else if (opcoes.length > 0) {
            seletorPessoa.setText(opcoes[0]);
        } else {
            seletorPessoa.setText("Sem registos");
        }
    }

    private CampoBuscaArredondado criarCampoBuscarCategoria() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar categoria");
        campo.setPreferredSize(new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO));
        campo.setMinimumSize(new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO));
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarTabelaCategoriasFiltrada(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarTabelaCategoriasFiltrada(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarTabelaCategoriasFiltrada(); }
        });
        return campo;
    }

    private void atualizarTabelaCategoriasFiltrada() {
        if (modeloTabelaCategoriasAux == null) return;
        modeloTabelaCategoriasAux.setRowCount(0);
        if (dadosCategoriasCarregadas == null) return;
        String termo = (campoBuscarCategoria != null) ? campoBuscarCategoria.getTextoReal().trim().toLowerCase() : "";
        for (Categoria c : dadosCategoriasCarregadas) {
            String idStr = String.valueOf(c.getIdCategoria());
            String nomeStr = c.getNome() != null ? c.getNome() : "";
            if (termo.isEmpty() || nomeStr.toLowerCase().contains(termo) || idStr.contains(termo)) {
                modeloTabelaCategoriasAux.addRow(new Object[] {idStr, nomeStr});
            }
        }
    }

    private void acaoNovaCategoria() {
        if (modoLateral == ModoPainelLateral.NOVA_CATEGORIA) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            if (campoNomeNovaCategoria != null) {
                campoNomeNovaCategoria.setText("");
            }
            definirModoPainelLateral(ModoPainelLateral.NOVA_CATEGORIA);
        }
    }

    private void acaoEditarCategoria() {
        if (modoLateral == ModoPainelLateral.EDITAR_CATEGORIA) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            definirModoPainelLateral(ModoPainelLateral.EDITAR_CATEGORIA);
        }
    }

    private void acaoEliminarCategoria() {
        int row = (tabelaCategoriasAuxiliar != null) ? tabelaCategoriasAuxiliar.getSelectedRow() : -1;
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.parseLong(String.valueOf(tabelaCategoriasAuxiliar.getValueAt(row, 0)));
        String nome = String.valueOf(tabelaCategoriasAuxiliar.getValueAt(row, 1));
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Deseja eliminar a categoria \"" + nome + "\"?\n(Nota: categorias com produtos associados não podem ser eliminadas.)",
                "Confirmar Eliminação",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;
        AsyncTask.executar(
            () -> {
                utilizadorController.eliminarCategoria(id);
                return true;
            },
            ok -> {
                JOptionPane.showMessageDialog(this, "Categoria eliminada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                recarregarDados();
            },
            err -> JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível eliminar a categoria \"" + nome + "\".\nVerifique se existem produtos vinculados (restrição RESTRICT).\nDetalhe: " + err.getMessage(),
                    "Erro ao Eliminar Categoria",
                    JOptionPane.ERROR_MESSAGE
            )
        );
    }

    private void atualizarTabelaUtilizadores() {
        if (modeloTabelaUtilizadores == null) return;
        modeloTabelaUtilizadores.setRowCount(0);
        if (dadosUtilizadoresCarregados == null) return;
        for (Utilizador u : dadosUtilizadoresCarregados) {
            String idStr = String.valueOf(u.getIdUtilizador());
            String uName = u.getUsername() != null ? u.getUsername() : "";
            String fNome = (u.getFuncionario() != null && u.getFuncionario().getNome() != null)
                    ? u.getFuncionario().getNome() : "-";
            String perf = u.getPerfil() != null ? u.getPerfil().name() : "VENDEDOR";
            String est = (u.getFuncionario() != null && u.getFuncionario().getEstado() != null)
                    ? u.getFuncionario().getEstado().name() : "ATIVO";
            modeloTabelaUtilizadores.addRow(new Object[] {idStr, uName, fNome, perf, est});
        }
    }

    private void acaoNovoUtilizador() {
        List<Funcionario> disponiveis = obterFuncionariosDisponiveisParaConta();
        if (disponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Todos os funcionários ativos já possuem conta de utilizador ou não há funcionários ativos cadastrados.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        String[] nomesFuncs = disponiveis.stream().map(Funcionario::getNome).toArray(String[]::new);
        if (seletorFuncionarioNovoUser != null) {
            seletorFuncionarioNovoUser.setOpcoes(nomesFuncs);
            seletorFuncionarioNovoUser.setText(nomesFuncs[0]);
        }
        if (campoUsernameNovoUser != null) {
            campoUsernameNovoUser.setText("");
        }
        if (campoSenhaNovoUser != null) {
            campoSenhaNovoUser.setText("Mudar@123");
        }
        exibirCardLateralPermissoes(cardNovoUtilizador);
    }

    private void acaoEditarUtilizador() {
        if (tabelaUtilizadores != null && tabelaUtilizadores.getRowCount() > 0 && tabelaUtilizadores.getSelectedRow() < 0) {
            tabelaUtilizadores.setRowSelectionInterval(0, 0);
        }
        if (tabelaUtilizadores == null || tabelaUtilizadores.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta de utilizador na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sincronizarDadosUtilizadorParaEdicao();
        exibirCardLateralPermissoes(cardEditarUtilizador);
    }

    private void acaoRedefinirSenhaUtilizador() {
        if (tabelaUtilizadores != null && tabelaUtilizadores.getRowCount() > 0 && tabelaUtilizadores.getSelectedRow() < 0) {
            tabelaUtilizadores.setRowSelectionInterval(0, 0);
        }
        if (tabelaUtilizadores == null || tabelaUtilizadores.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta de utilizador na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sincronizarDadosUtilizadorParaEdicao();
        if (campoNovaSenhaUser != null) {
            campoNovaSenhaUser.setText("");
        }
        exibirCardLateralPermissoes(cardRedefinirSenha);
    }

    private void acaoEliminarUtilizador() {
        int row = (tabelaUtilizadores != null) ? tabelaUtilizadores.getSelectedRow() : -1;
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma conta de utilizador na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long idU = Long.parseLong(String.valueOf(tabelaUtilizadores.getValueAt(row, 0)));
        String uName = String.valueOf(tabelaUtilizadores.getValueAt(row, 1));
        Utilizador logado = SessaoUtilizador.getUtilizador();
        if (logado != null && idU.equals(logado.getIdUtilizador())) {
            JOptionPane.showMessageDialog(this, "Não é possível eliminar a sua própria conta em sessão ativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(
                this,
                "Deseja eliminar a conta de acesso \"" + uName + "\"?\n(O registo do funcionário será mantido.)",
                "Confirmar Eliminação de Conta",
                JOptionPane.YES_NO_OPTION
        );
        if (opt != JOptionPane.YES_OPTION) return;
        AsyncTask.executar(
            () -> {
                utilizadorController.eliminar(idU);
                return true;
            },
            ok -> {
                JOptionPane.showMessageDialog(this, "Conta de utilizador eliminada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                recarregarDados();
            },
            err -> JOptionPane.showMessageDialog(this, "Erro ao eliminar conta: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private CampoBuscaArredondado criarCampoBuscarEndereco() {
        CampoBuscaArredondado campo = new CampoBuscaArredondado("Buscar endereço / cidade / bairro");
        campo.setPreferredSize(new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO));
        campo.setMinimumSize(new Dimension(LARGURA_MIN_INPUT, ALTURA_BOTAO));
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarTabelaCidadesFiltrada(); atualizarTabelaBairrosFiltrada(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarTabelaCidadesFiltrada(); atualizarTabelaBairrosFiltrada(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarTabelaCidadesFiltrada(); atualizarTabelaBairrosFiltrada(); }
        });
        return campo;
    }

    private DefaultTableModel criarModeloTabelaCidadesAux() {
        return new DefaultTableModel(new String[]{"ID", "Nome da Cidade"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private DefaultTableModel criarModeloTabelaBairrosAux() {
        return new DefaultTableModel(new String[]{"ID", "Nome do Bairro", "Cidade"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JPanel criarCardEnderecosAuxiliar() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 16, 0));
        painel.setOpaque(false);

        JPanel painelCidades = new JPanel(new BorderLayout(0, 8));
        painelCidades.setOpaque(false);
        labelTituloCidades = new JLabel("Cidades");
        labelTituloCidades.setFont(UiStyle.fonteSemiBold(14));
        labelTituloCidades.setForeground(UiStyle.TEXT);
        painelCidades.add(labelTituloCidades, BorderLayout.NORTH);
        painelCidades.add(criarCardTabelaAuxiliar(tabelaCidadesAuxiliar), BorderLayout.CENTER);

        JPanel painelBairros = new JPanel(new BorderLayout(0, 8));
        painelBairros.setOpaque(false);
        labelTituloBairros = new JLabel("Bairros");
        labelTituloBairros.setFont(UiStyle.fonteSemiBold(14));
        labelTituloBairros.setForeground(UiStyle.TEXT);
        painelBairros.add(labelTituloBairros, BorderLayout.NORTH);
        painelBairros.add(criarCardTabelaAuxiliar(tabelaBairrosAuxiliar), BorderLayout.CENTER);

        painel.add(painelCidades);
        painel.add(painelBairros);
        return painel;
    }

    private void atualizarTabelaCidadesFiltrada() {
        if (modeloTabelaCidadesAux == null) return;
        modeloTabelaCidadesAux.setRowCount(0);
        if (dadosCidadesCarregados == null) return;
        String termo = (campoBuscarEndereco != null) ? campoBuscarEndereco.getTextoReal().trim().toLowerCase() : "";
        for (Cidade c : dadosCidadesCarregados) {
            String idStr = String.valueOf(c.getIdCidade());
            String nomeStr = c.getNome() != null ? c.getNome() : "";
            if (termo.isEmpty() || nomeStr.toLowerCase().contains(termo) || idStr.contains(termo)) {
                modeloTabelaCidadesAux.addRow(new Object[]{idStr, nomeStr});
            }
        }
    }

    private void atualizarTabelaBairrosFiltrada() {
        if (modeloTabelaBairrosAux == null) return;
        modeloTabelaBairrosAux.setRowCount(0);
        if (dadosBairrosCarregados == null) return;
        String termo = (campoBuscarEndereco != null) ? campoBuscarEndereco.getTextoReal().trim().toLowerCase() : "";
        Long idCidadeFiltro = null;
        if (tabelaCidadesAuxiliar != null && tabelaCidadesAuxiliar.getSelectedRow() >= 0) {
            try {
                idCidadeFiltro = Long.parseLong(String.valueOf(tabelaCidadesAuxiliar.getValueAt(tabelaCidadesAuxiliar.getSelectedRow(), 0)));
            } catch (Exception ex) {}
        }
        for (Bairro b : dadosBairrosCarregados) {
            if (idCidadeFiltro != null && b.getCidade() != null && !idCidadeFiltro.equals(b.getCidade().getIdCidade())) {
                continue;
            }
            String idStr = String.valueOf(b.getIdBairro());
            String nomeStr = b.getNome() != null ? b.getNome() : "";
            String cidNome = (b.getCidade() != null && b.getCidade().getNome() != null) ? b.getCidade().getNome() : "-";
            if (termo.isEmpty() || nomeStr.toLowerCase().contains(termo) || cidNome.toLowerCase().contains(termo) || idStr.contains(termo)) {
                modeloTabelaBairrosAux.addRow(new Object[]{idStr, nomeStr, cidNome});
            }
        }
    }

    private void acaoNovaCidade() {
        if (modoLateral == ModoPainelLateral.NOVA_CIDADE) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            if (campoNomeNovaCidade != null) {
                campoNomeNovaCidade.setText("");
            }
            definirModoPainelLateral(ModoPainelLateral.NOVA_CIDADE);
        }
    }

    private void acaoEditarCidade() {
        if (modoLateral == ModoPainelLateral.EDITAR_CIDADE) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            definirModoPainelLateral(ModoPainelLateral.EDITAR_CIDADE);
        }
    }

    private void acaoEliminarCidade() {
        int row = (tabelaCidadesAuxiliar != null) ? tabelaCidadesAuxiliar.getSelectedRow() : -1;
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma cidade na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.parseLong(String.valueOf(tabelaCidadesAuxiliar.getValueAt(row, 0)));
        String nome = String.valueOf(tabelaCidadesAuxiliar.getValueAt(row, 1));
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar a cidade \"" + nome + "\"?", "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        AsyncTask.executar(
            () -> {
                utilizadorController.eliminarCidade(id);
                return true;
            },
            ok -> {
                JOptionPane.showMessageDialog(this, "Cidade eliminada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                recarregarDados();
            },
            err -> JOptionPane.showMessageDialog(this, "Erro ao eliminar cidade: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private void acaoNovoBairro() {
        if (dadosCidadesCarregados == null || dadosCidadesCarregados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre uma cidade primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (modoLateral == ModoPainelLateral.NOVO_BAIRRO) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            if (campoNomeNovoBairro != null) {
                campoNomeNovoBairro.setText("");
            }
            definirModoPainelLateral(ModoPainelLateral.NOVO_BAIRRO);
        }
    }

    private void acaoEditarBairro() {
        if (modoLateral == ModoPainelLateral.EDITAR_BAIRRO) {
            definirModoPainelLateral(ModoPainelLateral.NENHUM);
        } else {
            definirModoPainelLateral(ModoPainelLateral.EDITAR_BAIRRO);
        }
    }

    private void acaoEliminarBairro() {
        int row = (tabelaBairrosAuxiliar != null) ? tabelaBairrosAuxiliar.getSelectedRow() : -1;
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um bairro na tabela para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = Long.parseLong(String.valueOf(tabelaBairrosAuxiliar.getValueAt(row, 0)));
        String nome = String.valueOf(tabelaBairrosAuxiliar.getValueAt(row, 1));
        int opt = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja eliminar o bairro \"" + nome + "\"?", "Confirmar Eliminação", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;
        AsyncTask.executar(
            () -> {
                utilizadorController.eliminarBairro(id);
                return true;
            },
            ok -> {
                JOptionPane.showMessageDialog(this, "Bairro eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                recarregarDados();
            },
            err -> JOptionPane.showMessageDialog(this, "Erro ao eliminar bairro: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }

    private PainelCard criarBaseCardLateral() {
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
        return card;
    }

    private List<Funcionario> obterFuncionariosDisponiveisParaConta() {
        List<Funcionario> disponiveis = new ArrayList<>();
        if (listaFuncionariosReais != null) {
            for (Funcionario f : listaFuncionariosReais) {
                if (f == null || f.getIdFuncionario() == null || f.getEstado() != EstadoFuncionario.ATIVO) {
                    continue;
                }
                boolean jaTem = false;
                if (dadosUtilizadoresCarregados != null) {
                    for (Utilizador u : dadosUtilizadoresCarregados) {
                        if (u.getFuncionario() != null && f.getIdFuncionario().equals(u.getFuncionario().getIdFuncionario())) {
                            jaTem = true;
                            break;
                        }
                    }
                }
                if (!jaTem) {
                    disponiveis.add(f);
                }
            }
        }
        return disponiveis;
    }

    private void exibirCardLateralPermissoes(JPanel card) {
        wrapperDireitaPermissoes.removeAll();
        wrapperDireitaPermissoes.setVisible(card != null);
        if (card != null) {
            UiStyle.atualizarComponentesTema(card);
            wrapperDireitaPermissoes.add(UiStyle.envolverCardLateralComScroll(card), BorderLayout.CENTER);
        }
        UiStyle.atualizarComponentesTema(wrapperDireitaPermissoes);
        revalidate();
        repaint();
    }

    private void fecharCardLateralPermissoes() {
        exibirCardLateralPermissoes(null);
    }

    private JPanel criarCardNovoUtilizador() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Nova conta de utilizador");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorFuncionarioNovoUser = new BotaoSeletorFormulario("Selecione", new String[0]);
        JPanel blocoFunc = criarCampoComLabel("Funcionário ativo *", seletorFuncionarioNovoUser);
        fixarLargura(blocoFunc, larguraInterna, 60);
        card.add(blocoFunc);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoUsernameNovoUser = new CampoFormularioArredondado("");
        JPanel blocoUser = criarCampoComLabel("Username *", campoUsernameNovoUser);
        fixarLargura(blocoUser, larguraInterna, 60);
        card.add(blocoUser);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorPerfilNovoUser = new BotaoSeletorFormulario("VENDEDOR", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel blocoPerfil = criarCampoComLabel("Perfil *", seletorPerfilNovoUser);
        fixarLargura(blocoPerfil, larguraInterna, 60);
        card.add(blocoPerfil);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoSenhaNovoUser = new CampoFormularioArredondado("");
        campoSenhaNovoUser.setText("Mudar@123");
        JPanel blocoSenha = criarCampoComLabel("Senha inicial *", campoSenhaNovoUser);
        fixarLargura(blocoSenha, larguraInterna, 60);
        card.add(blocoSenha);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(6);
        btnGuardar.addActionListener(e -> {
            List<Funcionario> disponiveis = obterFuncionariosDisponiveisParaConta();
            String nomeFunc = seletorFuncionarioNovoUser.getText().trim();
            Funcionario fEscolhido = disponiveis.stream()
                    .filter(f -> f.getNome() != null && f.getNome().equalsIgnoreCase(nomeFunc))
                    .findFirst().orElse(disponiveis.isEmpty() ? null : disponiveis.get(0));
            if (fEscolhido == null) {
                JOptionPane.showMessageDialog(this, "Selecione um funcionário ativo disponível.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String username = campoUsernameNovoUser.getText() != null ? campoUsernameNovoUser.getText().trim() : "";
            String senha = campoSenhaNovoUser.getText() != null ? campoSenhaNovoUser.getText().trim() : "";
            if (username.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o username e a senha inicial.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Perfil perfil = Perfil.valueOf(seletorPerfilNovoUser.getText().trim());
            Utilizador novo = new Utilizador();
            novo.setFuncionario(fEscolhido);
            novo.setUsername(username);
            novo.setPerfil(perfil);
            novo.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(senha, org.mindrot.jbcrypt.BCrypt.gensalt()));
            AsyncTask.executar(
                () -> {
                    utilizadorController.salvar(novo);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Conta de utilizador criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    fecharCardLateralPermissoes();
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao criar utilizador: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> fecharCardLateralPermissoes());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnCancelar);
        card.add(acoes);
        return card;
    }

    private JPanel criarCardEditarUtilizador() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar conta de utilizador");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoUsernameEditarUser = new CampoFormularioArredondado("");
        JPanel blocoUser = criarCampoComLabel("Username *", campoUsernameEditarUser);
        fixarLargura(blocoUser, larguraInterna, 60);
        card.add(blocoUser);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorPerfilEditarUser = new BotaoSeletorFormulario("VENDEDOR", new String[] {"VENDEDOR", "GERENTE", "ADMIN"});
        JPanel blocoPerfil = criarCampoComLabel("Perfil *", seletorPerfilEditarUser);
        fixarLargura(blocoPerfil, larguraInterna, 60);
        card.add(blocoPerfil);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(5);
        btnGuardar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnGuardar.addActionListener(e -> {
            int row = (tabelaUtilizadores != null) ? tabelaUtilizadores.getSelectedRow() : -1;
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma conta de utilizador na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long idU = Long.parseLong(String.valueOf(tabelaUtilizadores.getValueAt(row, 0)));
            Utilizador alvo = dadosUtilizadoresCarregados.stream()
                    .filter(u -> idU.equals(u.getIdUtilizador()))
                    .findFirst().orElse(null);
            if (alvo == null) return;
            String novoUser = campoUsernameEditarUser.getText() != null ? campoUsernameEditarUser.getText().trim() : "";
            if (novoUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O username não pode estar vazio.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            alvo.setUsername(novoUser);
            alvo.setPerfil(Perfil.valueOf(seletorPerfilEditarUser.getText().trim()));
            AsyncTask.executar(
                () -> {
                    utilizadorController.atualizar(alvo);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Conta de utilizador atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    fecharCardLateralPermissoes();
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao atualizar utilizador: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(5);
        btnCancelar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnCancelar.addActionListener(e -> fecharCardLateralPermissoes());

        BotaoToolbarArredondado btnEliminar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        btnEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        btnEliminar.setIconTextGap(5);
        btnEliminar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnEliminar.addActionListener(e -> acaoEliminarUtilizador());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnCancelar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnEliminar);
        card.add(acoes);
        return card;
    }

    private JPanel criarCardRedefinirSenha() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Redefinir senha");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        labelContaRedefinirSenha = new JLabel("Conta: -");
        labelContaRedefinirSenha.setFont(UiStyle.fonte(13, Font.PLAIN));
        labelContaRedefinirSenha.setForeground(UiStyle.MUTED);
        labelContaRedefinirSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(labelContaRedefinirSenha);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNovaSenhaUser = new CampoFormularioArredondado("");
        JPanel blocoSenha = criarCampoComLabel("Nova senha *", campoNovaSenhaUser);
        fixarLargura(blocoSenha, larguraInterna, 60);
        card.add(blocoSenha);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar senha", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(6);
        btnGuardar.addActionListener(e -> {
            int row = (tabelaUtilizadores != null) ? tabelaUtilizadores.getSelectedRow() : -1;
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma conta de utilizador na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long idU = Long.parseLong(String.valueOf(tabelaUtilizadores.getValueAt(row, 0)));
            String novaSenha = campoNovaSenhaUser.getText() != null ? campoNovaSenhaUser.getText().trim() : "";
            if (novaSenha.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A nova senha não pode estar vazia.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AsyncTask.executar(
                () -> {
                    Utilizador operador = SessaoUtilizador.getUtilizador();
                    if (operador != null && operador.getPerfil() == Perfil.ADMIN) {
                        utilizadorController.alterarSenhaAdmin(idU, novaSenha, operador);
                    } else {
                        Utilizador u = utilizadorController.buscarPorId(idU);
                        u.setPasswordHash(org.mindrot.jbcrypt.BCrypt.hashpw(novaSenha, org.mindrot.jbcrypt.BCrypt.gensalt()));
                        utilizadorController.atualizar(u);
                    }
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Senha redefinida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    campoNovaSenhaUser.setText("");
                    fecharCardLateralPermissoes();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao redefinir senha: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> fecharCardLateralPermissoes());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnCancelar);
        card.add(acoes);
        return card;
    }

    private void sincronizarDadosUtilizadorParaEdicao() {
        if (tabelaUtilizadores == null || tabelaUtilizadores.getRowCount() == 0) {
            return;
        }
        int row = tabelaUtilizadores.getSelectedRow();
        if (row < 0 || row >= tabelaUtilizadores.getRowCount()) {
            row = 0;
        }
        String uName = String.valueOf(tabelaUtilizadores.getValueAt(row, 1));
        String perf = String.valueOf(tabelaUtilizadores.getValueAt(row, 3));
        if (campoUsernameEditarUser != null) {
            campoUsernameEditarUser.setText(uName);
        }
        if (seletorPerfilEditarUser != null) {
            seletorPerfilEditarUser.setText(perf);
        }
        if (labelContaRedefinirSenha != null) {
            labelContaRedefinirSenha.setText("Conta selecionada: " + uName);
        }
    }

    private JPanel criarCardNovaCidade() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Nova cidade");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovaCidade = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome da cidade *", campoNomeNovaCidade);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(6);
        btnGuardar.addActionListener(e -> {
            String nome = campoNomeNovaCidade.getText() != null ? campoNomeNovaCidade.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome da cidade é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cidade c = new Cidade();
            c.setNome(nome);
            AsyncTask.executar(
                () -> {
                    utilizadorController.salvarCidade(c);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Cidade cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    campoNomeNovaCidade.setText("");
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao salvar cidade: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnCancelar);
        card.add(acoes);
        return card;
    }

    private JPanel criarCardEditarCidade() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar cidade");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditarCidade = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome da cidade *", campoNomeEditarCidade);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(5);
        btnGuardar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnGuardar.addActionListener(e -> {
            int row = (tabelaCidadesAuxiliar != null) ? tabelaCidadesAuxiliar.getSelectedRow() : -1;
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma cidade na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = Long.parseLong(String.valueOf(tabelaCidadesAuxiliar.getValueAt(row, 0)));
            String novoNome = campoNomeEditarCidade.getText() != null ? campoNomeEditarCidade.getText().trim() : "";
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome da cidade é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cidade c = new Cidade();
            c.setIdCidade(id);
            c.setNome(novoNome);
            AsyncTask.executar(
                () -> {
                    utilizadorController.atualizarCidade(c);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Cidade atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao atualizar cidade: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(5);
        btnCancelar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado btnEliminar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        btnEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        btnEliminar.setIconTextGap(5);
        btnEliminar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnEliminar.addActionListener(e -> acaoEliminarCidade());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnCancelar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnEliminar);
        card.add(acoes);
        return card;
    }

    private void sincronizarDadosCidadeParaEdicao() {
        if (campoNomeEditarCidade == null || tabelaCidadesAuxiliar == null || tabelaCidadesAuxiliar.getRowCount() == 0) {
            return;
        }
        int row = tabelaCidadesAuxiliar.getSelectedRow();
        if (row < 0 || row >= tabelaCidadesAuxiliar.getRowCount()) {
            row = 0;
        }
        campoNomeEditarCidade.setText(String.valueOf(tabelaCidadesAuxiliar.getValueAt(row, 1)));
    }

    private void atualizarOpcoesCidadesNosBairros() {
        String[] nomesCidades = (dadosCidadesCarregados != null)
                ? dadosCidadesCarregados.stream().map(Cidade::getNome).toArray(String[]::new)
                : new String[0];
        if (seletorCidadeNovoBairro != null) {
            seletorCidadeNovoBairro.setOpcoes(nomesCidades);
            if (nomesCidades.length > 0) {
                seletorCidadeNovoBairro.setText(nomesCidades[0]);
            }
        }
        if (seletorCidadeEditarBairro != null) {
            seletorCidadeEditarBairro.setOpcoes(nomesCidades);
        }
    }

    private JPanel criarCardNovoBairro() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Novo bairro");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorCidadeNovoBairro = new BotaoSeletorFormulario("Matola", new String[] {"Matola"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", seletorCidadeNovoBairro);
        fixarLargura(blocoCidade, larguraInterna, 60);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovoBairro = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome do bairro *", campoNomeNovoBairro);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(6);
        btnGuardar.addActionListener(e -> {
            String cidadeEscolhida = seletorCidadeNovoBairro.getText().trim();
            Cidade cid = (dadosCidadesCarregados != null)
                    ? dadosCidadesCarregados.stream().filter(c -> c.getNome() != null && c.getNome().equalsIgnoreCase(cidadeEscolhida)).findFirst().orElse(null)
                    : null;
            if (cid == null && dadosCidadesCarregados != null && !dadosCidadesCarregados.isEmpty()) {
                cid = dadosCidadesCarregados.get(0);
            }
            if (cid == null) {
                JOptionPane.showMessageDialog(this, "Selecione uma cidade válida.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nomeBairro = campoNomeNovoBairro.getText() != null ? campoNomeNovoBairro.getText().trim() : "";
            if (nomeBairro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome do bairro é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Bairro b = new Bairro();
            b.setNome(nomeBairro);
            b.setCidade(cid);
            AsyncTask.executar(
                () -> {
                    utilizadorController.salvarBairro(b);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Bairro cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    campoNomeNovoBairro.setText("");
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao salvar bairro: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnCancelar);
        card.add(acoes);
        return card;
    }

    private JPanel criarCardEditarBairro() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar bairro");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        seletorCidadeEditarBairro = new BotaoSeletorFormulario("Matola", new String[] {"Matola"});
        JPanel blocoCidade = criarCampoComLabel("Cidade *", seletorCidadeEditarBairro);
        fixarLargura(blocoCidade, larguraInterna, 60);
        card.add(blocoCidade);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditarBairro = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome do bairro *", campoNomeEditarBairro);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(5);
        btnGuardar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnGuardar.addActionListener(e -> {
            int row = (tabelaBairrosAuxiliar != null) ? tabelaBairrosAuxiliar.getSelectedRow() : -1;
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione um bairro na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = Long.parseLong(String.valueOf(tabelaBairrosAuxiliar.getValueAt(row, 0)));
            String novoNome = campoNomeEditarBairro.getText() != null ? campoNomeEditarBairro.getText().trim() : "";
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome do bairro é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String nomeCidade = seletorCidadeEditarBairro.getText().trim();
            Cidade cid = (dadosCidadesCarregados != null)
                    ? dadosCidadesCarregados.stream().filter(c -> c.getNome() != null && c.getNome().equalsIgnoreCase(nomeCidade)).findFirst().orElse(null)
                    : null;
            if (cid == null && dadosBairrosCarregados != null) {
                Bairro bExistente = dadosBairrosCarregados.stream()
                        .filter(item -> item.getIdBairro() != null && item.getIdBairro().equals(id))
                        .findFirst().orElse(null);
                if (bExistente != null) {
                    cid = bExistente.getCidade();
                }
            }
            Bairro b = new Bairro();
            b.setIdBairro(id);
            b.setNome(novoNome);
            b.setCidade(cid);
            AsyncTask.executar(
                () -> {
                    utilizadorController.atualizarBairro(b);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Bairro atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao atualizar bairro: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(5);
        btnCancelar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado btnEliminar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        btnEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        btnEliminar.setIconTextGap(5);
        btnEliminar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnEliminar.addActionListener(e -> acaoEliminarBairro());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnCancelar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnEliminar);
        card.add(acoes);
        return card;
    }

    private void sincronizarDadosBairroParaEdicao() {
        if (campoNomeEditarBairro == null || tabelaBairrosAuxiliar == null || tabelaBairrosAuxiliar.getRowCount() == 0) {
            return;
        }
        int row = tabelaBairrosAuxiliar.getSelectedRow();
        if (row < 0 || row >= tabelaBairrosAuxiliar.getRowCount()) {
            row = 0;
        }
        String nomeBairro = String.valueOf(tabelaBairrosAuxiliar.getValueAt(row, 1));
        String nomeCid = String.valueOf(tabelaBairrosAuxiliar.getValueAt(row, 2));
        campoNomeEditarBairro.setText(nomeBairro);
        if (seletorCidadeEditarBairro != null && !"-".equals(nomeCid)) {
            seletorCidadeEditarBairro.setText(nomeCid);
        }
    }

    private JPanel criarCardNovaCategoria() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Nova categoria");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeNovaCategoria = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome da categoria *", campoNomeNovaCategoria);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(6);
        btnGuardar.addActionListener(e -> {
            String nome = campoNomeNovaCategoria.getText() != null ? campoNomeNovaCategoria.getText().trim() : "";
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome da categoria é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            AsyncTask.executar(
                () -> utilizadorController.criarCategoria(nome),
                criada -> {
                    JOptionPane.showMessageDialog(this, "Categoria cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    campoNomeNovaCategoria.setText("");
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao criar categoria: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(6);
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(10, 0)));
        acoes.add(btnCancelar);
        card.add(acoes);
        return card;
    }

    private JPanel criarCardEditarCategoria() {
        PainelCard card = criarBaseCardLateral();
        int larguraInterna = LARGURA_CARD_LATERAL - 32;

        JLabel titulo = new JLabel("Editar categoria");
        titulo.setFont(UiStyle.fonte(13, Font.BOLD));
        titulo.setForeground(UiStyle.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        campoNomeEditarCategoria = new CampoFormularioArredondado("");
        JPanel blocoNome = criarCampoComLabel("Nome da categoria *", campoNomeEditarCategoria);
        fixarLargura(blocoNome, larguraInterna, 60);
        card.add(blocoNome);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        acoes.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotaoToolbarArredondado btnGuardar = new BotaoToolbarArredondado(
                "Guardar alterações", UiStyle.ACCENT, UiStyle.ACCENT_TEXT, null, RAIO_COMPONENTE, true
        );
        btnGuardar.setIcon(UiStyle.icone(MaterialDesignC.CHECK, 15, UiStyle.ACCENT_TEXT));
        btnGuardar.setIconTextGap(5);
        btnGuardar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnGuardar.addActionListener(e -> {
            int row = (tabelaCategoriasAuxiliar != null) ? tabelaCategoriasAuxiliar.getSelectedRow() : -1;
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long id = Long.parseLong(String.valueOf(tabelaCategoriasAuxiliar.getValueAt(row, 0)));
            String novoNome = campoNomeEditarCategoria.getText() != null ? campoNomeEditarCategoria.getText().trim() : "";
            if (novoNome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome da categoria é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Categoria cat = new Categoria();
            cat.setIdCategoria(id);
            cat.setNome(novoNome);
            AsyncTask.executar(
                () -> {
                    utilizadorController.atualizarCategoria(cat);
                    return true;
                },
                ok -> {
                    JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    definirModoPainelLateral(ModoPainelLateral.NENHUM);
                    recarregarDados();
                },
                err -> JOptionPane.showMessageDialog(this, "Erro ao atualizar categoria: " + err.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE)
            );
        });

        BotaoToolbarArredondado btnCancelar = new BotaoToolbarArredondado(
                "Cancelar", UiStyle.SECONDARY_BG, UiStyle.TEXT, UiStyle.INPUT_BORDER, RAIO_COMPONENTE, true
        );
        btnCancelar.setIcon(UiStyle.iconeDinamico(MaterialDesignC.CLOSE, 15, () -> UiStyle.TEXT));
        btnCancelar.setIconTextGap(5);
        btnCancelar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnCancelar.addActionListener(e -> definirModoPainelLateral(ModoPainelLateral.NENHUM));

        BotaoToolbarArredondado btnEliminar = new BotaoToolbarArredondado(
                "Eliminar", new Color(0, 0, 0, 0), UiStyle.ERROR_TEXT, UiStyle.DANGER_BORDER, RAIO_COMPONENTE, false
        );
        btnEliminar.setIcon(UiStyle.iconeDinamico(MaterialDesignT.TRASH_CAN_OUTLINE, 15, () -> UiStyle.DANGER_TEXT));
        btnEliminar.setIconTextGap(5);
        btnEliminar.setBorder(new EmptyBorder(9, 10, 9, 10));
        btnEliminar.addActionListener(e -> acaoEliminarCategoria());

        acoes.add(btnGuardar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnCancelar);
        acoes.add(Box.createRigidArea(new Dimension(6, 0)));
        acoes.add(btnEliminar);
        card.add(acoes);
        return card;
    }

    private void sincronizarDadosCategoriaParaEdicao() {
        if (campoNomeEditarCategoria == null || tabelaCategoriasAuxiliar == null || tabelaCategoriasAuxiliar.getRowCount() == 0) {
            return;
        }
        int row = tabelaCategoriasAuxiliar.getSelectedRow();
        if (row < 0 || row >= tabelaCategoriasAuxiliar.getRowCount()) {
            row = 0;
        }
        campoNomeEditarCategoria.setText(String.valueOf(tabelaCategoriasAuxiliar.getValueAt(row, 1)));
    }
}
