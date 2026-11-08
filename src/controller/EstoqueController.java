package controller;

import java.util.List;
import model.bo.EstoqueBO;
import model.bo.ProdutoBO;
import model.exception.DadoInvalidoException;
import model.exception.EstoqueInsuficienteException;
import model.vo.Estoque;
import model.vo.Produto;
import util.ValidadorObjeto;

public class EstoqueController {

    private final EstoqueBO estoqueBO;
    private final ProdutoBO produtoBO;

    public EstoqueController() {
        this(new EstoqueBO(), new ProdutoBO());
    }

    public EstoqueController(EstoqueBO estoqueBO) {
        this(estoqueBO, new ProdutoBO());
    }

    public EstoqueController(EstoqueBO estoqueBO, ProdutoBO produtoBO) {
        ValidadorObjeto.validarNaoNulo(estoqueBO, "EstoqueBO");
        ValidadorObjeto.validarNaoNulo(produtoBO, "ProdutoBO");
        this.estoqueBO = estoqueBO;
        this.produtoBO = produtoBO;
    }

    public List<Estoque> listarLotes() {
        return estoqueBO.listarTodos();
    }

    public List<Estoque> listarTodos() {
        return listarLotes();
    }

    public Estoque buscarPorId(Long id) {
        return estoqueBO.buscarPorId(id);
    }

    public List<Estoque> buscarPorProduto(Long idProduto) {
        return estoqueBO.buscarPorProduto(idProduto);
    }

    public void darEntrada(Estoque lote) {
        estoqueBO.salvar(lote);
    }

    public void salvar(Estoque lote) {
        darEntrada(lote);
    }

    public void atualizar(Estoque lote) {
        estoqueBO.atualizar(lote);
    }

    public void repor(Long idProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade de reposição deve ser positiva.");
        }
        estoqueBO.reporEstoque(idProduto, quantidade);
    }

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

    public void eliminarLote(Long id) {
        estoqueBO.eliminar(id);
    }

    public void eliminar(Long id) {
        eliminarLote(id);
    }

    public List<Produto> listarProdutos() {
        return produtoBO.listarTodos();
    }
}
