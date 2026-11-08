package model.bo;

import java.util.List;
import model.dao.CidadeDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Cidade;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class CidadeBO {

    private static final int TAMANHO_MAXIMO_NOME = 60;

    private final CidadeDAO dao;

    public CidadeBO() {
        this(new CidadeDAO());
    }

    public CidadeBO(CidadeDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de cidade");
        this.dao = dao;
    }

    public void salvar(Cidade cidade) {
        validar(cidade, false);
        dao.salvar(cidade);
    }

    public void atualizar(Cidade cidade) {
        validar(cidade, true);
        dao.atualizar(cidade);
    }

    public void eliminar(Long id) {
        validarId(id, "idCidade");
        dao.eliminar(id);
    }

    public List<Cidade> listarTodos() {
        return dao.listarTodos();
    }

    public Cidade buscarPorId(Long id) {
        validarId(id, "idCidade");
        Cidade cidade = dao.buscarPorId(id);
        if (cidade == null) {
            throw new EntidadeNaoEncontradaException("Cidade nao encontrada.");
        }
        return cidade;
    }

    private void validar(Cidade cidade, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(cidade, "Cidade");
        if (exigirId) {
            validarId(cidade.getIdCidade(), "idCidade");
        }
        ValidadorTexto.validarObrigatorioComTamanho(cidade.getNome(), TAMANHO_MAXIMO_NOME, "Nome da cidade");
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
