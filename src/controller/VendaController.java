package controller;

import java.util.List;
import model.bo.EstoqueBO;
import model.bo.ReembolsoBO;
import model.bo.VendaBO;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Reembolso;
import model.vo.Utilizador;
import model.vo.Venda;
import util.ValidadorObjeto;

/**
 * Controller responsável pela gestão do ciclo de vida das vendas,
 * baixa de estoque (FEFO) e processamento de reembolsos.
 */
public class VendaController {

    private final VendaBO vendaBO;
    private final EstoqueBO estoqueBO;
    private final ReembolsoBO reembolsoBO;

    public VendaController() {
        this(new VendaBO(), new EstoqueBO(), new ReembolsoBO());
    }

    public VendaController(VendaBO vendaBO, EstoqueBO estoqueBO, ReembolsoBO reembolsoBO) {
        ValidadorObjeto.validarNaoNulo(vendaBO, "VendaBO");
        ValidadorObjeto.validarNaoNulo(estoqueBO, "EstoqueBO");
        ValidadorObjeto.validarNaoNulo(reembolsoBO, "ReembolsoBO");
        this.vendaBO = vendaBO;
        this.estoqueBO = estoqueBO;
        this.reembolsoBO = reembolsoBO;
    }

    /**
     * Retorna a lista de todas as vendas cadastradas.
     *
     * @return Lista de vendas
     */
    public List<Venda> listarVendas() {
        return vendaBO.listarTodos();
    }

    /**
     * Alias para listagem de vendas.
     *
     * @return Lista de vendas
     */
    public List<Venda> listarTodos() {
        return listarVendas();
    }

    /**
     * Localiza uma venda pelo seu identificador primário.
     *
     * @param id Identificador da venda
     * @return Venda encontrada
     */
    public Venda buscarPorId(Long id) {
        return vendaBO.buscarPorId(id);
    }

    /**
     * Salva uma venda em estado de rascunho (PENDENTE).
     *
     * @param venda Venda a ser guardada como rascunho
     */
    public void salvarRascunho(Venda venda) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (venda.getEstado() == null) {
            venda.setEstado(EstadoVenda.PENDENTE);
        }
        vendaBO.salvar(venda);
    }

    /**
     * Salva uma venda em estado de rascunho validando o operador.
     *
     * @param venda    Venda a ser guardada como rascunho
     * @param operador Utilizador autenticado realizando a ação
     */
    public void salvarRascunho(Venda venda, Utilizador operador) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (venda.getEstado() == null) {
            venda.setEstado(EstadoVenda.PENDENTE);
        }
        vendaBO.salvar(venda, operador);
    }

    /**
     * Conclui a venda, persiste sua conclusão e efetua a baixa automática
     * nos lotes de estoque seguindo a estratégia FEFO (First Expired, First Out).
     *
     * @param venda Venda a concluir
     */
    public void concluirVenda(Venda venda) {
        concluirVenda(venda, null);
    }

    /**
     * Conclui a venda com operador, persiste sua conclusão e efetua a baixa automática
     * nos lotes de estoque seguindo a estratégia FEFO (First Expired, First Out).
     *
     * @param venda    Venda a concluir
     * @param operador Utilizador operador
     */
    public void concluirVenda(Venda venda, Utilizador operador) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (operador != null) {
            vendaBO.concluirEGuardar(venda, operador);
        } else {
            vendaBO.concluirEGuardar(venda);
        }

        if (venda.getItemVendido() != null) {
            for (ItemVendido item : venda.getItemVendido()) {
                if (item != null && item.getProduto() != null && item.getProduto().getIdProduto() != null) {
                    int qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
                    if (qtd > 0) {
                        estoqueBO.baixarEstoqueFEFO(item.getProduto().getIdProduto(), qtd);
                    }
                }
            }
        }
    }

    /**
     * Anula uma venda concluída ou pendente, exigindo permissão do operador.
     *
     * @param venda    Venda a ser anulada
     * @param operador Utilizador com permissão para anulação
     */
    public void anularVenda(Venda venda, Utilizador operador) {
        vendaBO.anular(venda, operador);
    }

    /**
     * Anula uma venda pelo identificador primário.
     *
     * @param idVenda  Identificador da venda
     * @param operador Utilizador com permissão para anulação
     */
    public void anularVenda(Long idVenda, Utilizador operador) {
        Venda venda = vendaBO.buscarPorId(idVenda);
        anularVenda(venda, operador);
    }

    /**
     * Processa a solicitação de reembolso de um item vendido, persistindo o registo
     * e repondo a quantidade devolvida no estoque de produtos.
     *
     * @param item        Item vendido a ser reembolsado
     * @param quantidade  Quantidade a devolver
     * @param motivo      Motivo do reembolso
     * @param funcionario Funcionário responsável pelo atendimento
     * @return O registo de Reembolso persistido
     */
    public Reembolso processarReembolso(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        Reembolso reembolso = reembolsoBO.criarEGuardar(item, quantidade, motivo, funcionario);
        if (item != null && item.getProduto() != null && item.getProduto().getIdProduto() != null) {
            estoqueBO.reporEstoque(item.getProduto().getIdProduto(), quantidade);
        }
        return reembolso;
    }

    /**
     * Adiciona um item à venda em construção.
     *
     * @param venda      Venda alvo
     * @param produto    Produto a adicionar
     * @param quantidade Quantidade vendida
     * @param desconto   Desconto aplicado (ou null)
     * @return ItemVendido adicionado
     */
    public ItemVendido adicionarItem(Venda venda, Produto produto, int quantidade, Double desconto) {
        return vendaBO.adicionarItem(venda, produto, quantidade, desconto);
    }

    /**
     * Lista todos os reembolsos registrados no sistema.
     *
     * @return Lista de reembolsos
     */
    public List<Reembolso> listarReembolsos() {
        return reembolsoBO.listarTodos();
    }

    /**
     * Busca reembolsos vinculados a um item específico.
     *
     * @param idItem Identificador do item vendido
     * @return Lista de reembolsos daquele item
     */
    public List<Reembolso> buscarReembolsosPorItem(Long idItem) {
        return reembolsoBO.buscarPorItem(idItem);
    }
}
