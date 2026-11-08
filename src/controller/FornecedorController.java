package controller;

import java.util.ArrayList;
import java.util.List;
import model.bo.BairroBO;
import model.bo.CidadeBO;
import model.bo.ContactoBO;
import model.bo.FornecedorBO;
import model.vo.Bairro;
import model.vo.Cidade;
import model.vo.Contacto;
import model.vo.Fornecedor;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class FornecedorController {

    private final FornecedorBO fornecedorBO;
    private final ContactoBO contactoBO;
    private final CidadeBO cidadeBO;
    private final BairroBO bairroBO;

    public FornecedorController() {
        this(new FornecedorBO(), new ContactoBO(), new CidadeBO(), new BairroBO());
    }

    public FornecedorController(FornecedorBO fornecedorBO, ContactoBO contactoBO) {
        this(fornecedorBO, contactoBO, new CidadeBO(), new BairroBO());
    }

    public FornecedorController(
            FornecedorBO fornecedorBO,
            ContactoBO contactoBO,
            CidadeBO cidadeBO,
            BairroBO bairroBO) {
        ValidadorObjeto.validarNaoNulo(fornecedorBO, "FornecedorBO");
        ValidadorObjeto.validarNaoNulo(contactoBO, "ContactoBO");
        ValidadorObjeto.validarNaoNulo(cidadeBO, "CidadeBO");
        ValidadorObjeto.validarNaoNulo(bairroBO, "BairroBO");
        this.fornecedorBO = fornecedorBO;
        this.contactoBO = contactoBO;
        this.cidadeBO = cidadeBO;
        this.bairroBO = bairroBO;
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorBO.listarTodos();
    }

    public List<Fornecedor> listarTodos() {
        return listarFornecedores();
    }

    public Fornecedor buscarPorId(Long id) {
        return fornecedorBO.buscarPorId(id);
    }

    public void salvar(Fornecedor fornecedor) {
        fornecedorBO.salvar(fornecedor);
    }

    public void atualizar(Fornecedor fornecedor) {
        fornecedorBO.atualizar(fornecedor);
    }

    public void eliminar(Long id) {
        fornecedorBO.eliminar(id);
    }

    public Contacto adicionarContactoExtra(Long idPessoa, String contacto) {
        ValidadorObjeto.validarNaoNulo(idPessoa, "idPessoa");
        ValidadorTexto.validarContacto(contacto, "Contacto");

        Contacto c = new Contacto();
        c.setContacto(contacto);
        contactoBO.salvarComPessoa(c, idPessoa);
        return c;
    }

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

    public List<Contacto> listarContactos(Long idPessoa) {
        return contactoBO.buscarPorPessoa(idPessoa);
    }

    public List<Cidade> listarCidades() {
        return cidadeBO.listarTodos();
    }

    public Cidade salvarCidade(String nome) {
        Cidade cidade = new Cidade();
        cidade.setNome(nome);
        cidadeBO.salvar(cidade);
        return cidade;
    }

    public void salvarCidade(Cidade cidade) {
        cidadeBO.salvar(cidade);
    }

    public List<Bairro> listarBairros() {
        return bairroBO.listarTodos();
    }

    public Bairro salvarBairro(String nome, Cidade cidade) {
        Bairro bairro = new Bairro();
        bairro.setNome(nome);
        bairro.setCidade(cidade);
        bairroBO.salvar(bairro);
        return bairro;
    }

    public void salvarBairro(Bairro bairro) {
        bairroBO.salvar(bairro);
    }

    public List<Bairro> listarBairrosPorCidade(Long idCidade) {
        return bairroBO.listarPorCidade(idCidade);
    }
}
