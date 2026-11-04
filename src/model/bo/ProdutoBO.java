package model.bo;

import java.util.List;

import model.dao.ProdutoDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.EstadoProduto;
import model.vo.Perfil;
import model.vo.Produto;
import model.vo.Utilizador;
import util.ValidadorNumero;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class ProdutoBO {
    private final ProdutoDAO dao;

    public ProdutoBO() {
        this(new ProdutoDAO());
    }

    public ProdutoBO(ProdutoDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de produto");
        this.dao = dao;
    }

    public void salvar(Produto produto) {
        validar(produto, false);
        dao.salvar(produto);
    }

    public void salvar(Produto produto, Utilizador utilizador) {
        exigirGestao(utilizador);
        salvar(produto);
    }

    public void atualizar(Produto produto) {
        validar(produto, true);
        dao.atualizar(produto);
    }

    public void atualizar(Produto produto, Utilizador utilizador) {
        exigirGestao(utilizador);
        atualizar(produto);
    }

    public void eliminar(Long id) {
        validarId(id, "idProduto");
        dao.eliminar(id);
    }

    public List<Produto> listarTodos() {
        return dao.listarTodos();
    }

    public Produto buscarPorId(Long id) {
        validarId(id, "idProduto");
        Produto produto = dao.buscarPorId(id);
        if (produto == null) {
            throw new EntidadeNaoEncontradaException("Produto nao encontrado.");
        }
        return produto;
    }

    public void descontinuar(Produto produto, Utilizador utilizador) {
        exigirGestao(utilizador);
        validar(produto, true);
        produto.setEstado(EstadoProduto.DESCONTINUADO);
        dao.atualizar(produto);
    }

    private void validar(Produto produto, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(produto, "Produto");
        if (exigirId) {
            validarId(produto.getIdProduto(), "idProduto");
        }
        ValidadorTexto.validarObrigatorioComTamanho(produto.getNome(), 100, "Nome");
        ValidadorTexto.validarObrigatorioComTamanho(produto.getUnidade(), 20, "Unidade");
        ValidadorNumero.validarNaoNulo(produto.getPreco(), "Preco");
        ValidadorNumero.validarNaoNegativo(produto.getPreco(), "Preco");
        ValidadorNumero.validarNaoNulo(produto.getQuantidadeMinima(), "Quantidade minima");
        ValidadorNumero.validarNaoNegativo(produto.getQuantidadeMinima(), "Quantidade minima");
        ValidadorObjeto.validarNaoNulo(produto.getCategoria(), "Categoria");
        validarId(produto.getCategoria().getIdCategoria(), "categoria.idCategoria");
        if (produto.getEstado() == null) {
            produto.setEstado(EstadoProduto.ATIVO);
        }
    }

    private void exigirGestao(Utilizador utilizador) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (!utilizador.podeGerirProdutos()) {
            throw new AcessoNegadoException("O utilizador nao pode gerir produtos.");
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
