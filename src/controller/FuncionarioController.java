package controller;

import java.util.List;
import model.bo.FuncionarioBO;
import model.bo.UtilizadorBO;
import model.dao.CargoDAO;
import model.vo.Cargo;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Utilizador;
import util.ValidadorObjeto;

/**
 * Controller responsável pela gestão do quadro de funcionários,
 * atribuição e transferência de cargos, estados operacionais e criação vinculada de utilizador.
 */
public class FuncionarioController {

    private final FuncionarioBO funcionarioBO;
    private final CargoDAO cargoDAO;
    private final UtilizadorBO utilizadorBO;

    public FuncionarioController() {
        this(new FuncionarioBO(), new CargoDAO(), new UtilizadorBO());
    }

    public FuncionarioController(FuncionarioBO funcionarioBO, CargoDAO cargoDAO, UtilizadorBO utilizadorBO) {
        ValidadorObjeto.validarNaoNulo(funcionarioBO, "FuncionarioBO");
        ValidadorObjeto.validarNaoNulo(cargoDAO, "CargoDAO");
        ValidadorObjeto.validarNaoNulo(utilizadorBO, "UtilizadorBO");
        this.funcionarioBO = funcionarioBO;
        this.cargoDAO = cargoDAO;
        this.utilizadorBO = utilizadorBO;
    }

    /**
     * Lista todos os funcionários da instituição.
     *
     * @return Lista de funcionários
     */
    public List<Funcionario> listarFuncionarios() {
        return funcionarioBO.listarTodos();
    }

    /**
     * Alias para listarFuncionarios.
     *
     * @return Lista de funcionários
     */
    public List<Funcionario> listarTodos() {
        return listarFuncionarios();
    }

    /**
     * Localiza um funcionário pelo seu identificador primário.
     *
     * @param id Identificador do funcionário
     * @return Funcionário encontrado
     */
    public Funcionario buscarPorId(Long id) {
        return funcionarioBO.buscarPorId(id);
    }

    /**
     * Salva um novo funcionário.
     *
     * @param funcionario Dados do funcionário
     */
    public void salvar(Funcionario funcionario) {
        funcionarioBO.salvar(funcionario);
    }

    /**
     * Salva um funcionário validando a permissão do utilizador operador.
     *
     * @param funcionario Dados do funcionário
     * @param operador    Utilizador operador (ex: Administrador)
     */
    public void salvar(Funcionario funcionario, Utilizador operador) {
        if (operador != null) {
            funcionarioBO.salvar(funcionario, operador);
        } else {
            funcionarioBO.salvar(funcionario);
        }
    }

    /**
     * Salva um novo funcionário e, opcionalmente, cria a sua conta de utilizador associada.
     *
     * @param funcionario    Dados do funcionário
     * @param novoUtilizador Objeto Utilizador correspondente (ou null se não tiver acesso ao sistema)
     * @param operador       Utilizador administrador operando a criação
     */
    public void salvarNovo(Funcionario funcionario, Utilizador novoUtilizador, Utilizador operador) {
        salvar(funcionario, operador);
        if (novoUtilizador != null) {
            novoUtilizador.setFuncionario(funcionario);
            utilizadorBO.salvar(novoUtilizador);
        }
    }

    /**
     * Atualiza os dados de um funcionário.
     *
     * @param funcionario Objeto funcionário atualizado
     */
    public void atualizar(Funcionario funcionario) {
        funcionarioBO.atualizar(funcionario);
    }

    /**
     * Atualiza os dados de um funcionário validando permissão do operador.
     *
     * @param funcionario Objeto funcionário atualizado
     * @param operador    Utilizador administrador
     */
    public void atualizar(Funcionario funcionario, Utilizador operador) {
        if (operador != null) {
            funcionarioBO.atualizar(funcionario, operador);
        } else {
            funcionarioBO.atualizar(funcionario);
        }
    }

    /**
     * Altera o estado de atividade de um funcionário (ATIVO, INATIVO, FERIAS, etc.).
     *
     * @param idFuncionario Identificador do funcionário
     * @param novoEstado    Novo estado operacional
     */
    public void alterarEstado(Long idFuncionario, EstadoFuncionario novoEstado) {
        alterarEstado(idFuncionario, novoEstado, null);
    }

    /**
     * Altera o estado de atividade de um funcionário validando permissão do operador.
     *
     * @param idFuncionario Identificador do funcionário
     * @param novoEstado    Novo estado operacional
     * @param operador      Utilizador administrador
     */
    public void alterarEstado(Long idFuncionario, EstadoFuncionario novoEstado, Utilizador operador) {
        Funcionario f = funcionarioBO.buscarPorId(idFuncionario);
        f.setEstado(novoEstado);
        atualizar(f, operador);
    }

    /**
     * Transfere um funcionário para um novo cargo.
     *
     * @param idFuncionario Identificador do funcionário
     * @param novoCargo     Novo cargo a ser assumido
     */
    public void transferirCargo(Long idFuncionario, Cargo novoCargo) {
        transferirCargo(idFuncionario, novoCargo, null);
    }

    /**
     * Transfere um funcionário para um novo cargo validando operador.
     *
     * @param idFuncionario Identificador do funcionário
     * @param novoCargo     Novo cargo
     * @param operador      Utilizador administrador
     */
    public void transferirCargo(Long idFuncionario, Cargo novoCargo, Utilizador operador) {
        Funcionario f = funcionarioBO.buscarPorId(idFuncionario);
        f.setCargo(novoCargo);
        atualizar(f, operador);
    }

    /**
     * Transfere um funcionário para um cargo a partir do identificador do cargo.
     *
     * @param idFuncionario Identificador do funcionário
     * @param idNovoCargo   Identificador do novo cargo
     */
    public void transferirCargo(Long idFuncionario, Long idNovoCargo) {
        Cargo cargo = cargoDAO.buscarPorId(idNovoCargo);
        transferirCargo(idFuncionario, cargo, null);
    }

    /**
     * Elimina um funcionário de forma segura: tenta a remoção física; caso existam
     * dependências referenciais históricas (ex: vendas ou lotes cadastrados), inativa
     * o funcionário para preservar a integridade dos dados.
     *
     * @param id Identificador do funcionário
     * @return true se foi fisicamente eliminado, false se foi inativado com segurança
     */
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

    /**
     * Lista todos os cargos disponíveis na empresa.
     *
     * @return Lista de cargos
     */
    public List<Cargo> listarCargos() {
        return cargoDAO.listarTodos();
    }
}
