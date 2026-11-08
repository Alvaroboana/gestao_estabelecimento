package controller;

import java.util.ArrayList;
import java.util.List;
import model.bo.ClienteBO;
import model.bo.ContactoBO;
import model.bo.VendaBO;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.Venda;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class ClienteController {

    private final ClienteBO clienteBO;
    private final ContactoBO contactoBO;
    private final VendaBO vendaBO;

    public ClienteController() {
        this(new ClienteBO(), new ContactoBO(), new VendaBO());
    }

    public ClienteController(ClienteBO clienteBO, ContactoBO contactoBO) {
        this(clienteBO, contactoBO, new VendaBO());
    }

    public ClienteController(ClienteBO clienteBO, ContactoBO contactoBO, VendaBO vendaBO) {
        ValidadorObjeto.validarNaoNulo(clienteBO, "ClienteBO");
        ValidadorObjeto.validarNaoNulo(contactoBO, "ContactoBO");
        ValidadorObjeto.validarNaoNulo(vendaBO, "VendaBO");
        this.clienteBO = clienteBO;
        this.contactoBO = contactoBO;
        this.vendaBO = vendaBO;
    }

    public List<Cliente> listarClientes() {
        return clienteBO.listarTodos();
    }

    public List<Cliente> listarTodos() {
        return listarClientes();
    }

    public Cliente buscarPorId(Long id) {
        return clienteBO.buscarPorId(id);
    }

    public void salvar(Cliente cliente) {
        clienteBO.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        clienteBO.atualizar(cliente);
    }

    public void eliminar(Long id) {
        clienteBO.eliminar(id);
    }

    public Contacto adicionarContactoExtra(Long idPessoa, String contacto) {
        ValidadorObjeto.validarNaoNulo(idPessoa, "idPessoa");
        ValidadorTexto.validarContacto(contacto, "Contacto");

        Contacto c = new Contacto();
        c.setContacto(contacto);
        contactoBO.salvarComPessoa(c, idPessoa);
        return c;
    }

    public Contacto adicionarContactoExtra(Cliente cliente, String contacto) {
        ValidadorObjeto.validarNaoNulo(cliente, "Cliente");
        Long idPessoa = cliente.getIdPessoa() != null ? cliente.getIdPessoa() : cliente.getIdCliente();
        Contacto c = adicionarContactoExtra(idPessoa, contacto);
        if (cliente.getContacto() == null) {
            cliente.setContacto(new ArrayList<>());
        }
        cliente.getContacto().add(c);
        return c;
    }

    public List<Contacto> listarContactos(Long idPessoa) {
        return contactoBO.buscarPorPessoa(idPessoa);
    }

    public List<Venda> listarVendas() {
        return vendaBO.listarTodos();
    }
}
