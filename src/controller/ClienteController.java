package controller;

import java.util.ArrayList;
import java.util.List;
import model.bo.ClienteBO;
import model.dao.ContactoDAO;
import model.vo.Cliente;
import model.vo.Contacto;
import util.ValidadorObjeto;
import util.ValidadorTexto;

/**
 * Controller responsável pela gestão do cadastro de clientes e seus contactos.
 */
public class ClienteController {

    private final ClienteBO clienteBO;
    private final ContactoDAO contactoDAO;

    public ClienteController() {
        this(new ClienteBO(), new ContactoDAO());
    }

    public ClienteController(ClienteBO clienteBO, ContactoDAO contactoDAO) {
        ValidadorObjeto.validarNaoNulo(clienteBO, "ClienteBO");
        ValidadorObjeto.validarNaoNulo(contactoDAO, "ContactoDAO");
        this.clienteBO = clienteBO;
        this.contactoDAO = contactoDAO;
    }

    /**
     * Retorna a lista de todos os clientes cadastrados.
     *
     * @return Lista de clientes
     */
    public List<Cliente> listarClientes() {
        return clienteBO.listarTodos();
    }

    /**
     * Alias para listarClientes.
     *
     * @return Lista de clientes
     */
    public List<Cliente> listarTodos() {
        return listarClientes();
    }

    /**
     * Localiza um cliente pelo seu identificador primário.
     *
     * @param id Identificador do cliente
     * @return Cliente encontrado
     */
    public Cliente buscarPorId(Long id) {
        return clienteBO.buscarPorId(id);
    }

    /**
     * Salva um novo cliente no sistema.
     *
     * @param cliente Objeto Cliente com os dados
     */
    public void salvar(Cliente cliente) {
        clienteBO.salvar(cliente);
    }

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param cliente Objeto Cliente com dados atualizados
     */
    public void atualizar(Cliente cliente) {
        clienteBO.atualizar(cliente);
    }

    /**
     * Elimina um cliente do sistema pelo seu identificador.
     *
     * @param id Identificador do cliente
     */
    public void eliminar(Long id) {
        clienteBO.eliminar(id);
    }

    /**
     * Adiciona um contacto extra a uma pessoa/cliente persistido.
     *
     * @param idPessoa Identificador de pessoa correspondente ao cliente
     * @param contacto Valor do contacto (telefone ou email)
     * @return Contacto criado e persistido
     */
    public Contacto adicionarContactoExtra(Long idPessoa, String contacto) {
        ValidadorObjeto.validarNaoNulo(idPessoa, "idPessoa");
        ValidadorTexto.validarContacto(contacto, "Contacto");

        Contacto c = new Contacto();
        c.setContacto(contacto);
        contactoDAO.salvarComPessoa(c, idPessoa);
        return c;
    }

    /**
     * Adiciona um contacto extra diretamente a uma instância de Cliente.
     *
     * @param cliente  Cliente alvo
     * @param contacto Valor do contacto a adicionar
     * @return Contacto criado e persistido
     */
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

    /**
     * Lista todos os contactos associados a um determinado cliente/pessoa.
     *
     * @param idPessoa Identificador da pessoa
     * @return Lista de contactos
     */
    public List<Contacto> listarContactos(Long idPessoa) {
        return contactoDAO.buscarPorPessoa(idPessoa);
    }
}
