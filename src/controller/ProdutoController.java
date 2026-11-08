package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.bo.CategoriaBO;
import model.bo.EstoqueBO;
import model.bo.ProdutoBO;
import model.vo.Categoria;
import model.vo.Estoque;
import model.vo.Fornecedor;
import model.vo.Produto;
import model.vo.Utilizador;
import util.ValidadorObjeto;

public class ProdutoController {

    private final ProdutoBO produtoBO;
    private final CategoriaBO categoriaBO;
    private final EstoqueBO estoqueBO;

    public ProdutoController() {
        this(new ProdutoBO(), new CategoriaBO(), new EstoqueBO());
    }

    public ProdutoController(ProdutoBO produtoBO, CategoriaBO categoriaBO) {
        this(produtoBO, categoriaBO, new EstoqueBO());
    }

    public ProdutoController(ProdutoBO produtoBO, CategoriaBO categoriaBO, EstoqueBO estoqueBO) {
        ValidadorObjeto.validarNaoNulo(produtoBO, "ProdutoBO");
        ValidadorObjeto.validarNaoNulo(categoriaBO, "CategoriaBO");
        ValidadorObjeto.validarNaoNulo(estoqueBO, "EstoqueBO");
        this.produtoBO = produtoBO;
        this.categoriaBO = categoriaBO;
        this.estoqueBO = estoqueBO;
    }

    public List<Produto> listarProdutos() {
        return produtoBO.listarTodos();
    }

    public List<Produto> listarTodos() {
        return listarProdutos();
    }

    public Produto buscarPorId(Long id) {
        return produtoBO.buscarPorId(id);
    }

    public Produto buscarPorCodigoBarras(String codigoBarras) {
        return produtoBO.buscarPorCodigoBarras(codigoBarras);
    }

    public Map<Long, LocalDate> obterProximaValidadeProdutos() {
        return produtoBO.obterProximaValidadePorProduto();
    }

    public void salvar(Produto produto) {
        produtoBO.salvar(produto);
    }

    public void salvar(Produto produto, Utilizador utilizador) {
        produtoBO.salvar(produto, utilizador);
    }

    public void atualizar(Produto produto) {
        produtoBO.atualizar(produto);
    }

    public void atualizar(Produto produto, Utilizador utilizador) {
        produtoBO.atualizar(produto, utilizador);
    }

    public void eliminar(Long id) {
        produtoBO.eliminar(id);
    }

    public void descontinuar(Produto produto, Utilizador utilizador) {
        produtoBO.descontinuar(produto, utilizador);
    }

    public void descontinuar(Long idProduto, Utilizador utilizador) {
        Produto produto = produtoBO.buscarPorId(idProduto);
        descontinuar(produto, utilizador);
    }

    public List<Categoria> listarCategorias() {
        return categoriaBO.listarTodos();
    }

    public Categoria criarCategoria(String nomeBruto) {
        return categoriaBO.criar(nomeBruto);
    }

    public void atualizarCategoria(Categoria categoria) {
        categoriaBO.atualizar(categoria);
    }

    public void eliminarCategoria(Long idCategoria) {
        categoriaBO.eliminar(idCategoria);
    }

    public void salvarLoteInicial(Estoque lote) {
        estoqueBO.salvar(lote);
    }

    public List<Fornecedor> listarFornecedoresDoProduto(Long idProduto) {
        return produtoBO.listarFornecedoresDoProduto(idProduto);
    }

    public void associarFornecedor(Long idProduto, Long idFornecedor) {
        produtoBO.associarFornecedor(idProduto, idFornecedor);
    }

    public void desassociarFornecedor(Long idProduto, Long idFornecedor) {
        produtoBO.desassociarFornecedor(idProduto, idFornecedor);
    }
}
