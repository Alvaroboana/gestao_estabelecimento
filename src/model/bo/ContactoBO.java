package model.bo;

import java.util.List;
import model.dao.ContactoDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Contacto;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class ContactoBO {

    private final ContactoDAO dao;

    public ContactoBO() {
        this(new ContactoDAO());
    }

    public ContactoBO(ContactoDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de contacto");
        this.dao = dao;
    }

    public void salvarComPessoa(Contacto contacto, Long idPessoa) {
        ValidadorObjeto.validarNaoNulo(contacto, "Contacto");
        validarId(idPessoa, "idPessoa");
        ValidadorTexto.validarContacto(contacto.getContacto(), "Contacto");
        dao.salvarComPessoa(contacto, idPessoa);
    }

    public void atualizar(Contacto contacto) {
        ValidadorObjeto.validarNaoNulo(contacto, "Contacto");
        validarId(contacto.getIdContacto(), "idContacto");
        ValidadorTexto.validarContacto(contacto.getContacto(), "Contacto");
        dao.atualizar(contacto);
    }

    public void eliminar(Long id) {
        validarId(id, "idContacto");
        dao.eliminar(id);
    }

    public List<Contacto> listarTodos() {
        return dao.listarTodos();
    }

    public Contacto buscarPorId(Long id) {
        validarId(id, "idContacto");
        Contacto c = dao.buscarPorId(id);
        if (c == null) {
            throw new EntidadeNaoEncontradaException("Contacto nao encontrado.");
        }
        return c;
    }

    public List<Contacto> buscarPorPessoa(Long idPessoa) {
        validarId(idPessoa, "idPessoa");
        return dao.buscarPorPessoa(idPessoa);
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
