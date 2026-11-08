package model.bo;

import java.util.ArrayList;
import java.util.List;
import model.dao.BairroDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Bairro;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class BairroBO {

    private static final int TAMANHO_MAXIMO_NOME = 60;

    private final BairroDAO dao;

    public BairroBO() {
        this(new BairroDAO());
    }

    public BairroBO(BairroDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de bairro");
        this.dao = dao;
    }

    public void salvar(Bairro bairro) {
        validar(bairro, false);
        dao.salvar(bairro);
    }

    public void atualizar(Bairro bairro) {
        validar(bairro, true);
        dao.atualizar(bairro);
    }

    public void eliminar(Long id) {
        validarId(id, "idBairro");
        dao.eliminar(id);
    }

    public List<Bairro> listarTodos() {
        return dao.listarTodos();
    }

    public List<Bairro> listarPorCidade(Long idCidade) {
        validarId(idCidade, "idCidade");
        List<Bairro> todos = dao.listarTodos();
        List<Bairro> filtrados = new ArrayList<>();
        if (todos != null) {
            for (Bairro b : todos) {
                if (b != null && b.getCidade() != null && idCidade.equals(b.getCidade().getIdCidade())) {
                    filtrados.add(b);
                }
            }
        }
        return filtrados;
    }

    public Bairro buscarPorId(Long id) {
        validarId(id, "idBairro");
        Bairro bairro = dao.buscarPorId(id);
        if (bairro == null) {
            throw new EntidadeNaoEncontradaException("Bairro nao encontrado.");
        }
        return bairro;
    }

    private void validar(Bairro bairro, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(bairro, "Bairro");
        if (exigirId) {
            validarId(bairro.getIdBairro(), "idBairro");
        }
        ValidadorTexto.validarObrigatorioComTamanho(bairro.getNome(), TAMANHO_MAXIMO_NOME, "Nome do bairro");
        ValidadorObjeto.validarNaoNulo(bairro.getCidade(), "Cidade do bairro");
        validarId(bairro.getCidade().getIdCidade(), "cidade.idCidade");
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
