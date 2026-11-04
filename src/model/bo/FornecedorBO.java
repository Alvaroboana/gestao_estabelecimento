package model.bo;

import java.util.List;

import model.dao.FornecedorDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Contacto;
import model.vo.Fornecedor;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class FornecedorBO {
    private final FornecedorDAO dao;

    public FornecedorBO() {
        this(new FornecedorDAO());
    }

    public FornecedorBO(FornecedorDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de fornecedor");
        this.dao = dao;
    }

    public void salvar(Fornecedor fornecedor) {
        validar(fornecedor, false);
        dao.salvar(fornecedor);
    }

    public void atualizar(Fornecedor fornecedor) {
        validar(fornecedor, true);
        dao.atualizar(fornecedor);
    }

    public void eliminar(Long id) {
        validarId(id, "idFornecedor");
        dao.eliminar(id);
    }

    public List<Fornecedor> listarTodos() {
        return dao.listarTodos();
    }

    public Fornecedor buscarPorId(Long id) {
        validarId(id, "idFornecedor");
        Fornecedor fornecedor = dao.buscarPorId(id);
        if (fornecedor == null) {
            throw new EntidadeNaoEncontradaException("Fornecedor nao encontrado.");
        }
        return fornecedor;
    }

    private void validar(Fornecedor fornecedor, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(fornecedor, "Fornecedor");
        if (exigirId) {
            validarId(fornecedor.getIdFornecedor(), "idFornecedor");
        }
        ValidadorTexto.validarObrigatorioComTamanho(fornecedor.getNome(), 100, "Nome");
        if (fornecedor.getContacto() != null) {
            for (Contacto contacto : fornecedor.getContacto()) {
                if (contacto != null) {
                    ValidadorTexto.validarContacto(contacto.getContacto(), "Contacto");
                }
            }
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
