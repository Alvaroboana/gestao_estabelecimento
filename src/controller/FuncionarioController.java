package controller;

import java.util.List;
import model.bo.BairroBO;
import model.bo.CargoBO;
import model.bo.CidadeBO;
import model.bo.FuncionarioBO;
import model.bo.RelatorioBO;
import model.bo.UtilizadorBO;
import model.vo.Bairro;
import model.vo.Cargo;
import model.vo.Cidade;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Utilizador;
import util.ValidadorObjeto;

public class FuncionarioController {

    private final FuncionarioBO funcionarioBO;
    private final CargoBO cargoBO;
    private final UtilizadorBO utilizadorBO;
    private final CidadeBO cidadeBO;
    private final BairroBO bairroBO;
    private final RelatorioBO relatorioBO;

    public FuncionarioController() {
        this(new FuncionarioBO(), new CargoBO(), new UtilizadorBO(), new CidadeBO(), new BairroBO(), new RelatorioBO());
    }

    public FuncionarioController(FuncionarioBO funcionarioBO, CargoBO cargoBO, UtilizadorBO utilizadorBO) {
        this(funcionarioBO, cargoBO, utilizadorBO, new CidadeBO(), new BairroBO(), new RelatorioBO());
    }

    public FuncionarioController(
            FuncionarioBO funcionarioBO,
            CargoBO cargoBO,
            UtilizadorBO utilizadorBO,
            CidadeBO cidadeBO,
            BairroBO bairroBO,
            RelatorioBO relatorioBO) {
        ValidadorObjeto.validarNaoNulo(funcionarioBO, "FuncionarioBO");
        ValidadorObjeto.validarNaoNulo(cargoBO, "CargoBO");
        ValidadorObjeto.validarNaoNulo(utilizadorBO, "UtilizadorBO");
        ValidadorObjeto.validarNaoNulo(cidadeBO, "CidadeBO");
        ValidadorObjeto.validarNaoNulo(bairroBO, "BairroBO");
        ValidadorObjeto.validarNaoNulo(relatorioBO, "RelatorioBO");
        this.funcionarioBO = funcionarioBO;
        this.cargoBO = cargoBO;
        this.utilizadorBO = utilizadorBO;
        this.cidadeBO = cidadeBO;
        this.bairroBO = bairroBO;
        this.relatorioBO = relatorioBO;
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarioBO.listarTodos();
    }

    public List<Funcionario> listarTodos() {
        return listarFuncionarios();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioBO.buscarPorId(id);
    }

    public void salvar(Funcionario funcionario) {
        funcionarioBO.salvar(funcionario);
    }

    public void salvar(Funcionario funcionario, Utilizador operador) {
        if (operador != null) {
            funcionarioBO.salvar(funcionario, operador);
        } else {
            funcionarioBO.salvar(funcionario);
        }
    }

    public void salvarNovo(Funcionario funcionario, Utilizador novoUtilizador, Utilizador operador) {
        salvar(funcionario, operador);
        if (novoUtilizador != null) {
            novoUtilizador.setFuncionario(funcionario);
            utilizadorBO.salvar(novoUtilizador);
        }
    }

    public void atualizar(Funcionario funcionario) {
        funcionarioBO.atualizar(funcionario);
    }

    public void atualizar(Funcionario funcionario, Utilizador operador) {
        if (operador != null) {
            funcionarioBO.atualizar(funcionario, operador);
        } else {
            funcionarioBO.atualizar(funcionario);
        }
    }

    public void alterarEstado(Long idFuncionario, EstadoFuncionario novoEstado) {
        alterarEstado(idFuncionario, novoEstado, null);
    }

    public void alterarEstado(Long idFuncionario, EstadoFuncionario novoEstado, Utilizador operador) {
        Funcionario f = funcionarioBO.buscarPorId(idFuncionario);
        f.setEstado(novoEstado);
        atualizar(f, operador);
    }

    public void transferirCargo(Long idFuncionario, Cargo novoCargo) {
        transferirCargo(idFuncionario, novoCargo, null);
    }

    public void transferirCargo(Long idFuncionario, Cargo novoCargo, Utilizador operador) {
        Funcionario f = funcionarioBO.buscarPorId(idFuncionario);
        f.setCargo(novoCargo);
        atualizar(f, operador);
    }

    public void transferirCargo(Long idFuncionario, Long idNovoCargo) {
        Cargo cargo = cargoBO.buscarPorId(idNovoCargo);
        transferirCargo(idFuncionario, cargo, null);
    }

    public boolean eliminar(Long id) {
        try {
            funcionarioBO.eliminar(id);
            return true;
        } catch (Exception ex) {
            Funcionario f = funcionarioBO.buscarPorId(id);
            if (f != null) {
                f.setEstado(EstadoFuncionario.INATIVO);
                funcionarioBO.atualizar(f);
                return false;
            }
            throw ex;
        }
    }

    public List<Cargo> listarCargos() {
        return cargoBO.listarTodos();
    }

    public Cargo salvarCargo(String nome, double salarioBase) {
        Cargo cargo = new Cargo();
        cargo.setNome(nome);
        cargo.setSalarioBase(salarioBase);
        cargoBO.salvar(cargo);
        return cargo;
    }

    public void salvarCargo(Cargo cargo) {
        cargoBO.salvar(cargo);
    }

    public List<Utilizador> listarUtilizadores() {
        return utilizadorBO.listarTodos();
    }

    public void salvarUtilizador(Utilizador utilizador) {
        utilizadorBO.salvar(utilizador);
    }

    public void atualizarUtilizador(Utilizador utilizador) {
        utilizadorBO.atualizar(utilizador);
    }

    public void eliminarUtilizador(Long idUtilizador) {
        utilizadorBO.eliminar(idUtilizador);
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

    public double obterTotalVendasFuncionarioMes(Long idFuncionario) {
        return relatorioBO.obterTotalVendasFuncionarioMes(idFuncionario);
    }
}
