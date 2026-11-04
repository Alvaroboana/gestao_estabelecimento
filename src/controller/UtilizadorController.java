package controller;

import java.util.List;
import model.bo.UtilizadorBO;
import model.dao.BairroDAO;
import model.dao.CargoDAO;
import model.dao.CidadeDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.vo.Bairro;
import model.vo.Cargo;
import model.vo.Cidade;
import model.vo.Perfil;
import model.vo.Utilizador;
import org.mindrot.jbcrypt.BCrypt;
import util.ValidadorObjeto;
import util.ValidadorTexto;

/**
 * Controller responsável pela gestão de utilizadores do sistema,
 * alteração de credenciais, além de cadastros auxiliares de cargos, cidades e bairros.
 */
public class UtilizadorController {

    private final UtilizadorBO utilizadorBO;
    private final CargoDAO cargoDAO;
    private final CidadeDAO cidadeDAO;
    private final BairroDAO bairroDAO;

    public UtilizadorController() {
        this(new UtilizadorBO(), new CargoDAO(), new CidadeDAO(), new BairroDAO());
    }

    public UtilizadorController(UtilizadorBO utilizadorBO, CargoDAO cargoDAO, CidadeDAO cidadeDAO, BairroDAO bairroDAO) {
        ValidadorObjeto.validarNaoNulo(utilizadorBO, "UtilizadorBO");
        ValidadorObjeto.validarNaoNulo(cargoDAO, "CargoDAO");
        ValidadorObjeto.validarNaoNulo(cidadeDAO, "CidadeDAO");
        ValidadorObjeto.validarNaoNulo(bairroDAO, "BairroDAO");
        this.utilizadorBO = utilizadorBO;
        this.cargoDAO = cargoDAO;
        this.cidadeDAO = cidadeDAO;
        this.bairroDAO = bairroDAO;
    }

    // ==========================================
    // MÉTODOS DE UTILIZADOR
    // ==========================================

    /**
     * Retorna a lista de todos os utilizadores cadastrados no sistema.
     *
     * @return Lista de utilizadores
     */
    public List<Utilizador> listarUtilizadores() {
        return utilizadorBO.listarTodos();
    }

    /**
     * Alias para listarUtilizadores.
     *
     * @return Lista de utilizadores
     */
    public List<Utilizador> listarTodos() {
        return listarUtilizadores();
    }

    /**
     * Localiza um utilizador pelo seu identificador primário.
     *
     * @param id Identificador do utilizador
     * @return Utilizador correspondente
     */
    public Utilizador buscarPorId(Long id) {
        return utilizadorBO.buscarPorId(id);
    }

    /**
     * Salva um novo utilizador no sistema.
     *
     * @param utilizador Objeto Utilizador a persistir
     */
    public void salvar(Utilizador utilizador) {
        utilizadorBO.salvar(utilizador);
    }

    /**
     * Atualiza os dados de um utilizador existente.
     *
     * @param utilizador Objeto Utilizador atualizado
     */
    public void atualizar(Utilizador utilizador) {
        utilizadorBO.atualizar(utilizador);
    }

    /**
     * Elimina um utilizador pelo seu identificador primário.
     *
     * @param id Identificador do utilizador
     */
    public void eliminar(Long id) {
        utilizadorBO.eliminar(id);
    }

    /**
     * Altera a senha de um utilizador mediante confirmação da sua senha atual.
     *
     * @param idUtilizador Identificador do utilizador
     * @param senhaAtual   Senha atual em texto simples
     * @param novaSenha    Nova senha pretendida
     * @return true se a senha foi alterada com sucesso, false caso contrário
     */
    public boolean alterarSenha(Long idUtilizador, String senhaAtual, String novaSenha) {
        Utilizador u = utilizadorBO.buscarPorId(idUtilizador);
        String novoHash = u.alterarSenha(senhaAtual, novaSenha);
        if (novoHash != null) {
            utilizadorBO.atualizar(u);
            return true;
        }
        return false;
    }

    /**
     * Redefine a senha de um utilizador administrativamente (exigindo perfil ADMIN do operador).
     *
     * @param idUtilizador Identificador do utilizador alvo
     * @param novaSenha    Nova senha pretendida
     * @param operador     Utilizador administrador realizando a redefinição
     */
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

    // ==========================================
    // MÉTODOS DE CARGO
    // ==========================================

    /**
     * Lista todos os cargos cadastrados.
     *
     * @return Lista de cargos
     */
    public List<Cargo> listarCargos() {
        return cargoDAO.listarTodos();
    }

    /**
     * Localiza um cargo pelo identificador.
     *
     * @param id Identificador do cargo
     * @return Cargo encontrado
     */
    public Cargo buscarCargoPorId(Long id) {
        return cargoDAO.buscarPorId(id);
    }

    /**
     * Salva um novo cargo.
     *
     * @param cargo Objeto Cargo
     */
    public void salvarCargo(Cargo cargo) {
        ValidadorObjeto.validarNaoNulo(cargo, "Cargo");
        ValidadorTexto.validarObrigatorioComTamanho(cargo.getNome(), 50, "Nome do cargo");
        cargoDAO.salvar(cargo);
    }

    /**
     * Atualiza os dados de um cargo existente.
     *
     * @param cargo Objeto Cargo atualizado
     */
    public void atualizarCargo(Cargo cargo) {
        ValidadorObjeto.validarNaoNulo(cargo, "Cargo");
        if (cargo.getIdCargo() == null || cargo.getIdCargo() <= 0) {
            throw new DadoInvalidoException("idCargo deve ser positivo.");
        }
        ValidadorTexto.validarObrigatorioComTamanho(cargo.getNome(), 50, "Nome do cargo");
        cargoDAO.atualizar(cargo);
    }

    /**
     * Elimina um cargo pelo identificador.
     *
     * @param idCargo Identificador do cargo
     */
    public void eliminarCargo(Long idCargo) {
        if (idCargo == null || idCargo <= 0) {
            throw new DadoInvalidoException("idCargo deve ser positivo.");
        }
        cargoDAO.eliminar(idCargo);
    }

    // ==========================================
    // MÉTODOS DE CIDADE
    // ==========================================

    /**
     * Lista todas as cidades cadastradas.
     *
     * @return Lista de cidades
     */
    public List<Cidade> listarCidades() {
        return cidadeDAO.listarTodos();
    }

    /**
     * Localiza uma cidade pelo identificador.
     *
     * @param id Identificador da cidade
     * @return Cidade encontrada
     */
    public Cidade buscarCidadePorId(Long id) {
        return cidadeDAO.buscarPorId(id);
    }

    /**
     * Salva uma nova cidade.
     *
     * @param cidade Objeto Cidade
     */
    public void salvarCidade(Cidade cidade) {
        ValidadorObjeto.validarNaoNulo(cidade, "Cidade");
        ValidadorTexto.validarObrigatorioComTamanho(cidade.getNome(), 50, "Nome da cidade");
        cidadeDAO.salvar(cidade);
    }

    /**
     * Atualiza os dados de uma cidade existente.
     *
     * @param cidade Objeto Cidade atualizado
     */
    public void atualizarCidade(Cidade cidade) {
        ValidadorObjeto.validarNaoNulo(cidade, "Cidade");
        if (cidade.getIdCidade() == null || cidade.getIdCidade() <= 0) {
            throw new DadoInvalidoException("idCidade deve ser positivo.");
        }
        ValidadorTexto.validarObrigatorioComTamanho(cidade.getNome(), 50, "Nome da cidade");
        cidadeDAO.atualizar(cidade);
    }

    /**
     * Elimina uma cidade pelo identificador.
     *
     * @param idCidade Identificador da cidade
     */
    public void eliminarCidade(Long idCidade) {
        if (idCidade == null || idCidade <= 0) {
            throw new DadoInvalidoException("idCidade deve ser positivo.");
        }
        cidadeDAO.eliminar(idCidade);
    }

    // ==========================================
    // MÉTODOS DE BAIRRO
    // ==========================================

    /**
     * Lista todos os bairros cadastrados.
     *
     * @return Lista de bairros
     */
    public List<Bairro> listarBairros() {
        return bairroDAO.listarTodos();
    }

    /**
     * Localiza um bairro pelo identificador.
     *
     * @param id Identificador do bairro
     * @return Bairro encontrado
     */
    public Bairro buscarBairroPorId(Long id) {
        return bairroDAO.buscarPorId(id);
    }

    /**
     * Salva um novo bairro no sistema.
     *
     * @param bairro Objeto Bairro
     */
    public void salvarBairro(Bairro bairro) {
        ValidadorObjeto.validarNaoNulo(bairro, "Bairro");
        ValidadorTexto.validarObrigatorioComTamanho(bairro.getNome(), 50, "Nome do bairro");
        bairroDAO.salvar(bairro);
    }

    /**
     * Atualiza os dados de um bairro existente.
     *
     * @param bairro Objeto Bairro atualizado
     */
    public void atualizarBairro(Bairro bairro) {
        ValidadorObjeto.validarNaoNulo(bairro, "Bairro");
        if (bairro.getIdBairro() == null || bairro.getIdBairro() <= 0) {
            throw new DadoInvalidoException("idBairro deve ser positivo.");
        }
        ValidadorTexto.validarObrigatorioComTamanho(bairro.getNome(), 50, "Nome do bairro");
        bairroDAO.atualizar(bairro);
    }

    /**
     * Elimina um bairro pelo identificador.
     *
     * @param idBairro Identificador do bairro
     */
    public void eliminarBairro(Long idBairro) {
        if (idBairro == null || idBairro <= 0) {
            throw new DadoInvalidoException("idBairro deve ser positivo.");
        }
        bairroDAO.eliminar(idBairro);
    }
}
