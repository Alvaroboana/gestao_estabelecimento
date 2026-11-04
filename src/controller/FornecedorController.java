package controller;

import java.util.ArrayList;
import java.util.List;
import model.dao.ContactoDAO;
import model.dao.FornecedorDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Contacto;
import model.vo.Fornecedor;
import util.ValidadorObjeto;
import util.ValidadorTexto;

/**
 * Controller responsável pela gestão do cadastro de fornecedores e seus contactos.
 */
public class FornecedorController {

    private final FornecedorDAO fornecedorDAO;
    private final ContactoDAO contactoDAO;

    public FornecedorController() {
        this(new FornecedorDAO(), new ContactoDAO());
    }

    public FornecedorController(FornecedorDAO fornecedorDAO, ContactoDAO contactoDAO) {
        ValidadorObjeto.validarNaoNulo(fornecedorDAO, "FornecedorDAO");
        ValidadorObjeto.validarNaoNulo(contactoDAO, "ContactoDAO");
        this.fornecedorDAO = fornecedorDAO;
        this.contactoDAO = contactoDAO;
    }

    /**
     * Retorna a lista de todos os fornecedores cadastrados.
     *
     * @return Lista de fornecedores
     */
    public List<Fornecedor> listarFornecedores() {
        return fornecedorDAO.listarTodos();
    }

    /**
     * Alias para listarFornecedores.
     *
     * @return Lista de fornecedores
     */
    public List<Fornecedor> listarTodos() {
        return listarFornecedores();
    }

    /**
     * Localiza um fornecedor pelo seu identificador primário.
     *
     * @param id Identificador do fornecedor
     * @return Fornecedor encontrado
     */
    public Fornecedor buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException("idFornecedor deve ser um identificador positivo.");
        }
        Fornecedor fornecedor = fornecedorDAO.buscarPorId(id);
        if (fornecedor == null) {
            throw new EntidadeNaoEncontradaException("Fornecedor não encontrado.");
        }
        return fornecedor;
    }

    /**
     * Salva um novo fornecedor no sistema.
     *
     * @param fornecedor Objeto Fornecedor a persistir
     */
    public void salvar(Fornecedor fornecedor) {
        ValidadorObjeto.validarNaoNulo(fornecedor, "Fornecedor");
        ValidadorTexto.validarObrigatorioComTamanho(fornecedor.getNome(), 100, "Nome do fornecedor");
        fornecedorDAO.salvar(fornecedor);
    }

    /**
     * Atualiza os dados de um fornecedor existente.
     *
     * @param fornecedor Objeto Fornecedor atualizado
     */
    public void atualizar(Fornecedor fornecedor) {
        ValidadorObjeto.validarNaoNulo(fornecedor, "Fornecedor");
        if (fornecedor.getIdFornecedor() == null || fornecedor.getIdFornecedor() <= 0) {
            throw new DadoInvalidoException("idFornecedor deve ser positivo.");
        }
        ValidadorTexto.validarObrigatorioComTamanho(fornecedor.getNome(), 100, "Nome do fornecedor");
        fornecedorDAO.atualizar(fornecedor);
    }

    /**
     * Elimina um fornecedor pelo seu identificador.
     *
     * @param id Identificador do fornecedor
     */
    public void eliminar(Long id) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException("idFornecedor deve ser positivo.");
        }
        fornecedorDAO.eliminar(id);
    }

    /**
     * Adiciona um contacto extra a uma pessoa/fornecedor persistido.
     *
     * @param idPessoa Identificador da pessoa do fornecedor
     * @param contacto Valor do contacto a adicionar
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
     * Adiciona um contacto extra diretamente a uma instância de Fornecedor.
     *
     * @param fornecedor Fornecedor alvo
     * @param contacto   Valor do contacto
     * @return Contacto criado e persistido
     */
    public Contacto adicionarContactoExtra(Fornecedor fornecedor, String contacto) {
        ValidadorObjeto.validarNaoNulo(fornecedor, "Fornecedor");
        Long idPessoa = fornecedor.getIdPessoa() != null ? fornecedor.getIdPessoa() : fornecedor.getIdFornecedor();
        Contacto c = adicionarContactoExtra(idPessoa, contacto);
        if (fornecedor.getContacto() == null) {
            fornecedor.setContacto(new ArrayList<>());
        }
        fornecedor.getContacto().add(c);
        return c;
    }

    /**
     * Lista todos os contactos de um fornecedor a partir do identificador de pessoa.
     *
     * @param idPessoa Identificador da pessoa associada ao fornecedor
     * @return Lista de contactos
     */
    public List<Contacto> listarContactos(Long idPessoa) {
        return contactoDAO.buscarPorPessoa(idPessoa);
    }
}
