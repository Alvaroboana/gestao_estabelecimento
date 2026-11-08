package controller;

import java.util.List;
import model.bo.BairroBO;
import model.bo.CargoBO;
import model.bo.CategoriaBO;
import model.bo.CidadeBO;
import model.bo.ClienteBO;
import model.bo.ContactoBO;
import model.bo.FornecedorBO;
import model.bo.FuncionarioBO;
import model.bo.UtilizadorBO;
import model.exception.AcessoNegadoException;
import model.vo.Bairro;
import model.vo.Cargo;
import model.vo.Categoria;
import model.vo.Cidade;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.Fornecedor;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;
import org.mindrot.jbcrypt.BCrypt;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class UtilizadorController {

    private final UtilizadorBO utilizadorBO;
    private final CargoBO cargoBO;
    private final CidadeBO cidadeBO;
    private final BairroBO bairroBO;
    private final ContactoBO contactoBO;
    private final CategoriaBO categoriaBO;
    private final FuncionarioBO funcionarioBO;
    private final ClienteBO clienteBO;
    private final FornecedorBO fornecedorBO;

    public UtilizadorController() {
        this(
                new UtilizadorBO(),
                new CargoBO(),
                new CidadeBO(),
                new BairroBO(),
                new ContactoBO(),
                new CategoriaBO(),
                new FuncionarioBO(),
                new ClienteBO(),
                new FornecedorBO());
    }

    public UtilizadorController(UtilizadorBO utilizadorBO, CargoBO cargoBO, CidadeBO cidadeBO, BairroBO bairroBO) {
        this(
                utilizadorBO,
                cargoBO,
                cidadeBO,
                bairroBO,
                new ContactoBO(),
                new CategoriaBO(),
                new FuncionarioBO(),
                new ClienteBO(),
                new FornecedorBO());
    }

    public UtilizadorController(
            UtilizadorBO utilizadorBO,
            CargoBO cargoBO,
            CidadeBO cidadeBO,
            BairroBO bairroBO,
            ContactoBO contactoBO,
            CategoriaBO categoriaBO,
            FuncionarioBO funcionarioBO,
            ClienteBO clienteBO,
            FornecedorBO fornecedorBO) {
        ValidadorObjeto.validarNaoNulo(utilizadorBO, "UtilizadorBO");
        ValidadorObjeto.validarNaoNulo(cargoBO, "CargoBO");
        ValidadorObjeto.validarNaoNulo(cidadeBO, "CidadeBO");
        ValidadorObjeto.validarNaoNulo(bairroBO, "BairroBO");
        ValidadorObjeto.validarNaoNulo(contactoBO, "ContactoBO");
        ValidadorObjeto.validarNaoNulo(categoriaBO, "CategoriaBO");
        ValidadorObjeto.validarNaoNulo(funcionarioBO, "FuncionarioBO");
        ValidadorObjeto.validarNaoNulo(clienteBO, "ClienteBO");
        ValidadorObjeto.validarNaoNulo(fornecedorBO, "FornecedorBO");
        this.utilizadorBO = utilizadorBO;
        this.cargoBO = cargoBO;
        this.cidadeBO = cidadeBO;
        this.bairroBO = bairroBO;
        this.contactoBO = contactoBO;
        this.categoriaBO = categoriaBO;
        this.funcionarioBO = funcionarioBO;
        this.clienteBO = clienteBO;
        this.fornecedorBO = fornecedorBO;
    }


    public List<Utilizador> listarUtilizadores() {
        return utilizadorBO.listarTodos();
    }

    public List<Utilizador> listarTodos() {
        return listarUtilizadores();
    }

    public Utilizador buscarPorId(Long id) {
        return utilizadorBO.buscarPorId(id);
    }

    public void salvar(Utilizador utilizador) {
        utilizadorBO.salvar(utilizador);
    }

    public void atualizar(Utilizador utilizador) {
        utilizadorBO.atualizar(utilizador);
    }

    public void eliminar(Long id) {
        utilizadorBO.eliminar(id);
    }

    public boolean alterarSenha(Long idUtilizador, String senhaAtual, String novaSenha) {
        Utilizador u = utilizadorBO.buscarPorId(idUtilizador);
        String novoHash = u.alterarSenha(senhaAtual, novaSenha);
        if (novoHash != null) {
            utilizadorBO.atualizar(u);
            return true;
        }
        return false;
    }

    public void alterarSenhaAdmin(Long idUtilizador, String novaSenha, Utilizador operador) {
        ValidadorObjeto.validarNaoNulo(operador, "Operador");
        if (operador.getPerfil() != Perfil.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores podem redefinir senhas diretamente.");
        }
        ValidadorTexto.validarObrigatorio(novaSenha, "Nova senha");
        Utilizador u = utilizadorBO.buscarPorId(idUtilizador);
        u.setPasswordHash(BCrypt.hashpw(novaSenha, BCrypt.gensalt()));
        utilizadorBO.atualizar(u);
    }


    public List<Cargo> listarCargos() {
        return cargoBO.listarTodos();
    }

    public Cargo buscarCargoPorId(Long id) {
        return cargoBO.buscarPorId(id);
    }

    public void salvarCargo(Cargo cargo) {
        cargoBO.salvar(cargo);
    }

    public void atualizarCargo(Cargo cargo) {
        cargoBO.atualizar(cargo);
    }

    public void eliminarCargo(Long idCargo) {
        cargoBO.eliminar(idCargo);
    }


    public List<Cidade> listarCidades() {
        return cidadeBO.listarTodos();
    }

    public Cidade buscarCidadePorId(Long id) {
        return cidadeBO.buscarPorId(id);
    }

    public void salvarCidade(Cidade cidade) {
        cidadeBO.salvar(cidade);
    }

    public void atualizarCidade(Cidade cidade) {
        cidadeBO.atualizar(cidade);
    }

    public void eliminarCidade(Long idCidade) {
        cidadeBO.eliminar(idCidade);
    }


    public List<Bairro> listarBairros() {
        return bairroBO.listarTodos();
    }

    public List<Bairro> listarBairrosPorCidade(Long idCidade) {
        return bairroBO.listarPorCidade(idCidade);
    }

    public Bairro buscarBairroPorId(Long id) {
        return bairroBO.buscarPorId(id);
    }

    public void salvarBairro(Bairro bairro) {
        bairroBO.salvar(bairro);
    }

    public void atualizarBairro(Bairro bairro) {
        bairroBO.atualizar(bairro);
    }

    public void eliminarBairro(Long idBairro) {
        bairroBO.eliminar(idBairro);
    }


    public void salvarContactoComPessoa(Contacto contacto, Long idPessoa) {
        contactoBO.salvarComPessoa(contacto, idPessoa);
    }

    public void atualizarContacto(Contacto contacto) {
        contactoBO.atualizar(contacto);
    }

    public void eliminarContacto(Long idContacto) {
        contactoBO.eliminar(idContacto);
    }

    public List<Categoria> listarCategorias() {
        return categoriaBO.listarTodos();
    }

    public Categoria criarCategoria(String nomeBruto) {
        return categoriaBO.criar(nomeBruto);
    }

    public void atualizarCategoria(Categoria categoria) {
        categoriaBO.atualizar(categoria);
    }

    public void eliminarCategoria(Long idCategoria) {
        categoriaBO.eliminar(idCategoria);
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarioBO.listarTodos();
    }

    public List<Cliente> listarClientes() {
        return clienteBO.listarTodos();
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorBO.listarTodos();
    }
}
