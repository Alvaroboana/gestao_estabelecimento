package controller;

import java.util.List;
import model.bo.EstoqueBO;
import model.exception.DadoInvalidoException;
import model.exception.EstoqueInsuficienteException;
import model.vo.Estoque;
import util.ValidadorObjeto;

/**
 * Controller responsável pela movimentação de estoque, controle de lotes,
 * entradas, reposições e retiradas com validação de saldo.
 */
public class EstoqueController {

    private final EstoqueBO estoqueBO;

    public EstoqueController() {
        this(new EstoqueBO());
    }

    public EstoqueController(EstoqueBO estoqueBO) {
        ValidadorObjeto.validarNaoNulo(estoqueBO, "EstoqueBO");
        this.estoqueBO = estoqueBO;
    }

    /**
     * Retorna a lista de todos os lotes de estoque registrados.
     *
     * @return Lista de lotes
     */
    public List<Estoque> listarLotes() {
        return estoqueBO.listarTodos();
    }

    /**
     * Alias para listarLotes.
     *
     * @return Lista de lotes
     */
    public List<Estoque> listarTodos() {
        return listarLotes();
    }

    /**
     * Localiza um lote de estoque pelo seu identificador primário.
     *
     * @param id Identificador do lote de estoque
     * @return Lote encontrado
     */
    public Estoque buscarPorId(Long id) {
        return estoqueBO.buscarPorId(id);
    }

    /**
     * Busca todos os lotes cadastrados para um determinado produto.
     *
     * @param idProduto Identificador do produto
     * @return Lista de lotes do produto
     */
    public List<Estoque> buscarPorProduto(Long idProduto) {
        return estoqueBO.buscarPorProduto(idProduto);
    }

    /**
     * Dá entrada de um novo lote no estoque.
     *
     * @param lote Objeto Estoque com as informações do lote a cadastrar
     */
    public void darEntrada(Estoque lote) {
        estoqueBO.salvar(lote);
    }

    /**
     * Atualiza as informações de um lote existente.
     *
     * @param lote Objeto Estoque com dados atualizados
     */
    public void atualizar(Estoque lote) {
        estoqueBO.atualizar(lote);
    }

    /**
     * Repõe estoque de um produto no lote mais recente ou existente.
     *
     * @param idProduto  Identificador do produto
     * @param quantidade Quantidade a adicionar
     */
    public void repor(Long idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade de reposição deve ser positiva.");
        }
        estoqueBO.reporEstoque(idProduto, quantidade);
    }

    /**
     * Retira uma quantidade de itens de um lote específico após validar o saldo disponível.
     *
     * @param idEstoque  Identificador do lote
     * @param quantidade Quantidade a retirar
     */
    public void retirar(Long idEstoque, int quantidade) {
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade a retirar deve ser positiva.");
        }
        Estoque lote = estoqueBO.buscarPorId(idEstoque);
        int saldoAtual = lote.getQuantidade() != null ? lote.getQuantidade() : 0;
        if (saldoAtual < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Saldo insuficiente no lote: disponível " + saldoAtual + ", solicitado " + quantidade + ".");
        }
        lote.setQuantidade(saldoAtual - quantidade);
        estoqueBO.atualizar(lote);
    }

    /**
     * Retira quantidade do produto baixando pelos lotes com base na estratégia FEFO,
     * validando previamente se o saldo total somado dos lotes é suficiente.
     *
     * @param idProduto  Identificador do produto
     * @param quantidade Quantidade a retirar
     */
    public void retirarPorProduto(Long idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade a retirar deve ser positiva.");
        }
        List<Estoque> lotes = estoqueBO.buscarPorProduto(idProduto);
        int saldoTotal = 0;
        if (lotes != null) {
            for (Estoque l : lotes) {
                if (l != null && l.getQuantidade() != null) {
                    saldoTotal += l.getQuantidade();
                }
            }
        }
        if (saldoTotal < quantidade) {
            throw new EstoqueInsuficienteException(
                    "Saldo total insuficiente para o produto: disponível " + saldoTotal + ", solicitado " + quantidade + ".");
        }
        estoqueBO.baixarEstoqueFEFO(idProduto, quantidade);
    }

    /**
     * Elimina um lote de estoque pelo seu identificador primário.
     *
     * @param id Identificador do lote a eliminar
     */
    public void eliminarLote(Long id) {
        estoqueBO.eliminar(id);
    }

    /**
     * Alias para eliminarLote.
     *
     * @param id Identificador do lote a eliminar
     */
    public void eliminar(Long id) {
        eliminarLote(id);
    }
}
