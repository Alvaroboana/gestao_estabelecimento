package model.bo;

import java.util.List;

import model.dao.ClienteDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Cliente;
import model.vo.Contacto;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class ClienteBO {
    private final ClienteDAO dao;

    public ClienteBO() {
        this(new ClienteDAO());
    }

    public ClienteBO(ClienteDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de cliente");
        this.dao = dao;
    }

    public void salvar(Cliente cliente) {
        validar(cliente, false);
        dao.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        validar(cliente, true);
        dao.atualizar(cliente);
    }

    public void eliminar(Long id) {
        validarId(id, "idCliente");
        dao.eliminar(id);
    }

    public List<Cliente> listarTodos() {
        return dao.listarTodos();
    }

    public Cliente buscarPorId(Long id) {
        validarId(id, "idCliente");
        Cliente cliente = dao.buscarPorId(id);
        if (cliente == null) {
            throw new EntidadeNaoEncontradaException("Cliente nao encontrado.");
        }
        return cliente;
    }

    private void validar(Cliente cliente, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(cliente, "Cliente");
        if (exigirId) {
            validarId(cliente.getIdCliente(), "idCliente");
        }
        ValidadorTexto.validarObrigatorioComTamanho(cliente.getNome(), 100, "Nome");
        if (cliente.getContacto() != null) {
            for (Contacto contacto : cliente.getContacto()) {
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
