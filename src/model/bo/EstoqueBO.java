package model.bo;

import java.time.LocalDate;
import java.util.List;

import model.dao.EstoqueDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.EstadoFuncionario;
import model.vo.Estoque;
import model.vo.Funcionario;
import model.vo.Produto;
import util.ValidadorData;
import util.ValidadorNumero;
import util.ValidadorObjeto;

public class EstoqueBO {
    private final EstoqueDAO dao;

    public EstoqueBO() {
        this(new EstoqueDAO());
    }

    public EstoqueBO(EstoqueDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de estoque");
        this.dao = dao;
    }

    public void salvar(Estoque estoque) {
        validar(estoque, false);
        dao.salvar(estoque);
    }

    public void atualizar(Estoque estoque) {
        validar(estoque, true);
        dao.atualizar(estoque);
    }

    public void eliminar(Long id) {
        validarId(id, "idEstoque");
        dao.eliminar(id);
    }

    public List<Estoque> listarTodos() {
        return dao.listarTodos();
    }

    public Estoque buscarPorId(Long id) {
        validarId(id, "idEstoque");
        Estoque estoque = dao.buscarPorId(id);
        if (estoque == null) {
            throw new EntidadeNaoEncontradaException("Estoque nao encontrado.");
        }
        return estoque;
    }

    public List<Estoque> buscarPorProduto(Long idProduto) {
        validarId(idProduto, "idProduto");
        return dao.buscarPorProduto(idProduto);
    }

    private void validar(Estoque estoque, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(estoque, "Estoque");
        if (exigirId) {
            validarId(estoque.getIdEstoque(), "idEstoque");
        }
        ValidadorNumero.validarNaoNulo(estoque.getQuantidade(), "Quantidade");
        ValidadorNumero.validarNaoNegativo(estoque.getQuantidade(), "Quantidade");
        ValidadorObjeto.validarNaoNulo(estoque.getProduto(), "Produto");
        ValidadorObjeto.validarNaoNulo(estoque.getFuncionario(), "Funcionario");
        validarId(estoque.getProduto().getIdProduto(), "produto.idProduto");
        validarId(estoque.getFuncionario().getIdFuncionario(), "funcionario.idFuncionario");
        if (estoque.getFuncionario().getEstado() != EstadoFuncionario.ATIVO) {
            throw new DadoInvalidoException("O funcionario responsavel deve estar ativo.");
        }

        LocalDate entrada = estoque.getDataEntrada();
        if (entrada != null) {
            ValidadorData.validarNaoFutura(entrada, "Data de entrada");
        }
        if (estoque.getDataValidade() != null && entrada != null) {
            ValidadorData.validarOrdem(entrada, estoque.getDataValidade(),
                    "Data de entrada", "Data de validade");
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
