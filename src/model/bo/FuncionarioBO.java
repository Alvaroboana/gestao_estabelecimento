package model.bo;

import java.util.List;

import model.dao.FuncionarioDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.Cargo;
import model.vo.Contacto;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.ValidadorData;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class FuncionarioBO {
    private final FuncionarioDAO dao;

    public FuncionarioBO() {
        this(new FuncionarioDAO());
    }

    public FuncionarioBO(FuncionarioDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de funcionario");
        this.dao = dao;
    }

    public void salvar(Funcionario funcionario) {
        validar(funcionario, false);
        dao.salvar(funcionario);
    }

    public void salvar(Funcionario funcionario, Utilizador utilizador) {
        exigirGestao(utilizador);
        salvar(funcionario);
    }

    public void atualizar(Funcionario funcionario) {
        validar(funcionario, true);
        dao.atualizar(funcionario);
    }

    public void atualizar(Funcionario funcionario, Utilizador utilizador) {
        exigirGestao(utilizador);
        atualizar(funcionario);
    }

    public void eliminar(Long id) {
        validarId(id, "idFuncionario");
        dao.eliminar(id);
    }

    public List<Funcionario> listarTodos() {
        return dao.listarTodos();
    }

    public Funcionario buscarPorId(Long id) {
        validarId(id, "idFuncionario");
        Funcionario funcionario = dao.buscarPorId(id);
        if (funcionario == null) {
            throw new EntidadeNaoEncontradaException("Funcionario nao encontrado.");
        }
        return funcionario;
    }

    private void validar(Funcionario funcionario, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(funcionario, "Funcionario");
        if (exigirId) {
            validarId(funcionario.getIdFuncionario(), "idFuncionario");
        }
        ValidadorTexto.validarObrigatorioComTamanho(funcionario.getNome(), 100, "Nome");
        ValidadorTexto.validarBI(funcionario.getBi(), "BI");
        ValidadorData.validarIdadeMinima(funcionario.getDataNascimento(), 18, "Data de nascimento");
        ValidadorData.validarNaoFutura(funcionario.getDataInicio(), "Data de inicio");
        ValidadorData.validarOrdem(funcionario.getDataNascimento(), funcionario.getDataInicio(),
                "Data de nascimento", "Data de inicio");
        ValidadorObjeto.validarNaoNulo(funcionario.getCargo(), "Cargo");
        validarId(funcionario.getCargo().getIdCargo(), "cargo.idCargo");
        if (funcionario.getEstado() == null) {
            funcionario.setEstado(EstadoFuncionario.ATIVO);
        }
        if (funcionario.getContacto() != null) {
            for (Contacto contacto : funcionario.getContacto()) {
                if (contacto != null) {
                    ValidadorTexto.validarContacto(contacto.getContacto(), "Contacto");
                }
            }
        }
        if (funcionario.getEndereco() != null) {
            ValidadorObjeto.validarNaoNulo(funcionario.getEndereco().getBairro(), "Bairro");
            validarId(funcionario.getEndereco().getBairro().getIdBairro(), "bairro.idBairro");
            ValidadorTexto.validarObrigatorioComTamanho(funcionario.getEndereco().getQuarteirao(), 30, "Quarteirao");
            ValidadorTexto.validarObrigatorioComTamanho(funcionario.getEndereco().getRua(), 100, "Rua");
            ValidadorTexto.validarObrigatorioComTamanho(funcionario.getEndereco().getNumeroCasa(), 20, "Numero da casa");
        }
    }

    private void exigirGestao(Utilizador utilizador) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (utilizador.getPerfil() != Perfil.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores podem gerir funcionarios.");
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
