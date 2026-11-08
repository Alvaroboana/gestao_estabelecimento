package model.bo;

import java.util.List;
import model.dao.EnderecoDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Endereco;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class EnderecoBO {

    private final EnderecoDAO dao;

    public EnderecoBO() {
        this(new EnderecoDAO());
    }

    public EnderecoBO(EnderecoDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de endereco");
        this.dao = dao;
    }

    public void salvarComPessoa(Endereco endereco, Long idPessoa) {
        validar(endereco, false);
        validarId(idPessoa, "idPessoa");
        dao.salvarComPessoa(endereco, idPessoa);
    }

    public void atualizar(Endereco endereco) {
        validar(endereco, true);
        dao.atualizar(endereco);
    }

    public void eliminar(Long id) {
        validarId(id, "idEndereco");
        dao.eliminar(id);
    }

    public List<Endereco> listarTodos() {
        return dao.listarTodos();
    }

    public Endereco buscarPorId(Long id) {
        validarId(id, "idEndereco");
        Endereco end = dao.buscarPorId(id);
        if (end == null) {
            throw new EntidadeNaoEncontradaException("Endereco nao encontrado.");
        }
        return end;
    }

    public Endereco buscarPorPessoa(Long idPessoa) {
        validarId(idPessoa, "idPessoa");
        return dao.buscarPorPessoa(idPessoa);
    }

    private void validar(Endereco endereco, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(endereco, "Endereco");
        if (exigirId) {
            validarId(endereco.getIdEndereco(), "idEndereco");
        }
        ValidadorTexto.validarObrigatorioComTamanho(endereco.getQuarteirao(), 30, "Quarteirao");
        ValidadorTexto.validarObrigatorioComTamanho(endereco.getRua(), 100, "Rua");
        ValidadorTexto.validarObrigatorioComTamanho(endereco.getNumeroCasa(), 20, "Numero da casa");
        ValidadorObjeto.validarNaoNulo(endereco.getBairro(), "Bairro");
        validarId(endereco.getBairro().getIdBairro(), "bairro.idBairro");
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
