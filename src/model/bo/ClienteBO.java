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
        Cliente atual = dao.buscarPorId(cliente.getIdCliente());
        if (atual != null && ehConsumidorFinal(atual.getNome())
                && !ehConsumidorFinal(cliente.getNome())) {
            throw new DadoInvalidoException("O cliente padrão 'Consumidor Final' não pode ser renomeado.");
        }
        dao.atualizar(cliente);
    }

    public void eliminar(Long id) {
        validarId(id, "idCliente");
        Cliente atual = dao.buscarPorId(id);
        if (atual != null && ehConsumidorFinal(atual.getNome())) {
            throw new DadoInvalidoException("O cliente padrão 'Consumidor Final' é protegido e não pode ser eliminado.");
        }
        try {
            dao.eliminar(id);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            if (msg.contains("foreign key") || msg.contains("constraint") || msg.contains("venda")) {
                throw new DadoInvalidoException("Não é possível eliminar este cliente porque já existem vendas associadas a ele.");
            }
            throw ex;
        }
    }

    private boolean ehConsumidorFinal(String nome) {
        if (nome == null) return false;
        String n = nome.trim();
        return "Consumidor Final".equalsIgnoreCase(n) || "Consumidor".equalsIgnoreCase(n);
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
