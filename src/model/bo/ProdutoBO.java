package model.bo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.dao.ProdutoDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.EstadoProduto;
import model.vo.Fornecedor;
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
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        validar(produto, false);
        dao.salvar(produto);
    }

    public void salvar(Produto produto, Utilizador utilizador) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        exigirGestao(utilizador);
        salvar(produto);
    }

    public void salvarCompleto(Produto produto, Long fornecedor, model.vo.Estoque lote) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        validar(produto, false);
        if (fornecedor != null) validarId(fornecedor, "idFornecedor");
        if (lote != null) {
            ValidadorNumero.validarPositivo(lote.getQuantidade(), "Quantidade inicial");
            ValidadorObjeto.validarNaoNulo(lote.getFuncionario(), "Funcionário");
            validarId(lote.getFuncionario().getIdFuncionario(), "idFuncionario");
            if (lote.getFuncionario().getEstado() != model.vo.EstadoFuncionario.ATIVO)
                throw new DadoInvalidoException("O funcionário deve estar activo.");
            if (lote.getDataValidade() != null && lote.getDataEntrada() != null && lote.getDataValidade().isBefore(lote.getDataEntrada()))
                throw new DadoInvalidoException("A validade não pode anteceder a entrada.");
        }
        dao.salvarCompleto(produto, fornecedor, lote);
    }

    public void atualizar(Produto produto) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        validar(produto, true);
        dao.atualizar(produto);
    }

    public void atualizar(Produto produto, Utilizador utilizador) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        exigirGestao(utilizador);
        atualizar(produto);
    }

    public void eliminar(Long id) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
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

    public Produto buscarPorCodigoBarras(String codigoBarras) {
        return dao.buscarPorCodigoBarras(codigoBarras);
    }

    public Map<Long, LocalDate> obterProximaValidadePorProduto() {
        return dao.obterProximaValidadePorProduto();
    }

    public List<Fornecedor> listarFornecedoresDoProduto(Long idProduto) {
        validarId(idProduto, "idProduto");
        return dao.listarFornecedoresDoProduto(idProduto);
    }

    public void associarFornecedor(Long idProduto, Long idFornecedor) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        validarId(idProduto, "idProduto");
        validarId(idFornecedor, "idFornecedor");
        dao.associarFornecedor(idProduto, idFornecedor);
    }

    public void desassociarFornecedor(Long idProduto, Long idFornecedor) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
        validarId(idProduto, "idProduto");
        validarId(idFornecedor, "idFornecedor");
        dao.desassociarFornecedor(idProduto, idFornecedor);
    }

    public void descontinuar(Produto produto, Utilizador utilizador) {
        infra.Autorizacao.exigir(model.vo.Perfil.GERENTE);
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
