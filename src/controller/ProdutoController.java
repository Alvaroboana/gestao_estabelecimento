package controller;

import java.util.List;
import model.bo.ProdutoBO;
import model.dao.CategoriaDAO;
import model.dao.ProdutoDAO;
import model.vo.Categoria;
import model.vo.Fornecedor;
import model.vo.Produto;
import model.vo.Utilizador;
import util.ValidadorObjeto;

/**
 * Controller responsável pelas operações de cadastro, atualização, descontinuação,
 * categorização e associação de fornecedores a produtos.
 */
public class ProdutoController {

    private final ProdutoBO produtoBO;
    private final ProdutoDAO produtoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProdutoController() {
        this(new ProdutoBO(), new ProdutoDAO(), new CategoriaDAO());
    }

    public ProdutoController(ProdutoBO produtoBO, ProdutoDAO produtoDAO) {
        this(produtoBO, produtoDAO, new CategoriaDAO());
    }

    public ProdutoController(ProdutoBO produtoBO, ProdutoDAO produtoDAO, CategoriaDAO categoriaDAO) {
        ValidadorObjeto.validarNaoNulo(produtoBO, "ProdutoBO");
        ValidadorObjeto.validarNaoNulo(produtoDAO, "ProdutoDAO");
        ValidadorObjeto.validarNaoNulo(categoriaDAO, "CategoriaDAO");
        this.produtoBO = produtoBO;
        this.produtoDAO = produtoDAO;
        this.categoriaDAO = categoriaDAO;
    }

    /**
     * Retorna a lista de todos os produtos do catálogo.
     *
     * @return Lista de produtos
     */
    public List<Produto> listarProdutos() {
        return produtoBO.listarTodos();
    }

    /**
     * Alias para listarProdutos.
     *
     * @return Lista de produtos
     */
    public List<Produto> listarTodos() {
        return listarProdutos();
    }

    /**
     * Localiza um produto pelo seu identificador primário.
     *
     * @param id Identificador do produto
     * @return Produto correspondente
     */
    public Produto buscarPorId(Long id) {
        return produtoBO.buscarPorId(id);
    }

    /**
     * Salva um novo produto no catálogo.
     *
     * @param produto Produto a persistir
     */
    public void salvar(Produto produto) {
        produtoBO.salvar(produto);
    }

    /**
     * Salva um novo produto validando permissões de gestão do utilizador.
     *
     * @param produto    Produto a persistir
     * @param utilizador Utilizador responsável
     */
    public void salvar(Produto produto, Utilizador utilizador) {
        produtoBO.salvar(produto, utilizador);
    }

    /**
     * Atualiza os dados de um produto existente.
     *
     * @param produto Produto com informações atualizadas
     */
    public void atualizar(Produto produto) {
        produtoBO.atualizar(produto);
    }

    /**
     * Atualiza um produto existente validando permissões do utilizador.
     *
     * @param produto    Produto a atualizar
     * @param utilizador Utilizador operador
     */
    public void atualizar(Produto produto, Utilizador utilizador) {
        produtoBO.atualizar(produto, utilizador);
    }

    /**
     * Remove um produto do sistema.
     *
     * @param id Identificador do produto a eliminar
     */
    public void eliminar(Long id) {
        produtoBO.eliminar(id);
    }

    /**
     * Descontinua o produto, impedindo que continue a ser vendido.
     *
     * @param produto    Produto a descontinuar
     * @param utilizador Utilizador operador com permissão de gestão
     */
    public void descontinuar(Produto produto, Utilizador utilizador) {
        produtoBO.descontinuar(produto, utilizador);
    }

    /**
     * Descontinua o produto pelo identificador.
     *
     * @param idProduto  Identificador do produto
     * @param utilizador Utilizador operador
     */
    public void descontinuar(Long idProduto, Utilizador utilizador) {
        Produto produto = produtoBO.buscarPorId(idProduto);
        descontinuar(produto, utilizador);
    }

    /**
     * Lista todas as categorias de produtos disponíveis.
     *
     * @return Lista de categorias
     */
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodos();
    }

    /**
     * Lista todos os fornecedores associados a um produto específico.
     *
     * @param idProduto Identificador do produto
     * @return Lista de fornecedores associados
     */
    public List<Fornecedor> listarFornecedoresDoProduto(Long idProduto) {
        return produtoBO.listarFornecedoresDoProduto(idProduto);
    }

    /**
     * Associa um fornecedor ao produto.
     *
     * @param idProduto    Identificador do produto
     * @param idFornecedor Identificador do fornecedor
     */
    public void associarFornecedor(Long idProduto, Long idFornecedor) {
        produtoBO.associarFornecedor(idProduto, idFornecedor);
    }

    /**
     * Remove o vínculo de fornecimento entre um fornecedor e um produto.
     *
     * @param idProduto    Identificador do produto
     * @param idFornecedor Identificador do fornecedor
     */
    public void desassociarFornecedor(Long idProduto, Long idFornecedor) {
        produtoBO.desassociarFornecedor(idProduto, idFornecedor);
    }
}
