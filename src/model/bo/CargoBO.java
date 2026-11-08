package model.bo;

import java.util.List;
import model.dao.CargoDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Cargo;
import util.ValidadorNumero;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class CargoBO {

    private static final int TAMANHO_MAXIMO_NOME = 50;

    private final CargoDAO dao;

    public CargoBO() {
        this(new CargoDAO());
    }

    public CargoBO(CargoDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de cargo");
        this.dao = dao;
    }

    public void salvar(Cargo cargo) {
        validar(cargo, false);
        dao.salvar(cargo);
    }

    public void atualizar(Cargo cargo) {
        validar(cargo, true);
        dao.atualizar(cargo);
    }

    public void eliminar(Long id) {
        validarId(id, "idCargo");
        dao.eliminar(id);
    }

    public List<Cargo> listarTodos() {
        return dao.listarTodos();
    }

    public Cargo buscarPorId(Long id) {
        validarId(id, "idCargo");
        Cargo cargo = dao.buscarPorId(id);
        if (cargo == null) {
            throw new EntidadeNaoEncontradaException("Cargo nao encontrado.");
        }
        return cargo;
    }

    private void validar(Cargo cargo, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(cargo, "Cargo");
        if (exigirId) {
            validarId(cargo.getIdCargo(), "idCargo");
        }
        ValidadorTexto.validarObrigatorioComTamanho(cargo.getNome(), TAMANHO_MAXIMO_NOME, "Nome do cargo");
        if (cargo.getSalarioBase() == null) {
            cargo.setSalarioBase(0.0);
        }
        ValidadorNumero.validarNaoNegativo(cargo.getSalarioBase(), "Salario base");
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
