package model.bo;

import java.util.List;

import model.dao.UtilizadorDAO;
import model.exception.AcessoNegadoException;
import model.exception.AutenticacaoException;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.vo.EstadoFuncionario;
import model.vo.Perfil;
import model.vo.Utilizador;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class UtilizadorBO {
    private final UtilizadorDAO dao;

    public UtilizadorBO() {
        this(new UtilizadorDAO());
    }

    public UtilizadorBO(UtilizadorDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de utilizador");
        this.dao = dao;
    }

    public void salvar(Utilizador utilizador) {
        infra.Autorizacao.exigir(model.vo.Perfil.ADMIN);
        validar(utilizador, false);
        utilizador.setTrocaSenhaObrigatoria(true);
        dao.salvar(utilizador);
    }

    public void atualizar(Utilizador utilizador) {
        infra.Autorizacao.exigir(model.vo.Perfil.ADMIN);
        validar(utilizador, true);
        dao.atualizar(utilizador);
    }

    public void eliminar(Long id) {
        infra.Autorizacao.exigir(model.vo.Perfil.ADMIN);
        validarId(id, "idUtilizador");
        dao.eliminar(id);
    }

    public List<Utilizador> listarTodos() {
        infra.Autorizacao.exigir(model.vo.Perfil.ADMIN);
        return dao.listarTodos();
    }

    public Utilizador buscarPorId(Long id) {
        infra.Autorizacao.exigir(model.vo.Perfil.ADMIN);
        validarId(id, "idUtilizador");
        Utilizador utilizador = dao.buscarPorId(id);
        if (utilizador == null) {
            throw new EntidadeNaoEncontradaException("Utilizador nao encontrado.");
        }
        return utilizador;
    }

    public Utilizador autenticar(String username, String senha) {
        ValidadorTexto.validarObrigatorioComTamanho(username, 50, "Username");
        ValidadorTexto.validarObrigatorio(senha, "Senha");

        Utilizador utilizador = dao.buscarPorUsername(username);
        if (utilizador == null || !utilizador.podeEntrar() || !utilizador.autenticar(senha)) {
            throw new AutenticacaoException("Credenciais invalidas ou funcionario inativo.");
        }
        return utilizador;
    }

    public Utilizador alterarSenhaAutenticada(String username, String actual, String nova) {
        Utilizador u = autenticar(username, actual);
        String hash = u.alterarSenha(actual, nova);
        if (hash == null) throw new AutenticacaoException("Palavra-passe actual incorrecta.");
        u.setTrocaSenhaObrigatoria(false);
        dao.atualizar(u);
        return u;
    }

    public void exigirPermissao(Utilizador utilizador, Perfil perfilMinimo) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        ValidadorObjeto.validarNaoNulo(perfilMinimo, "Perfil minimo");
        infra.Autorizacao.conferirOperador(utilizador, perfilMinimo);
        if (utilizador.getPerfil() == null || !temPermissao(utilizador.getPerfil(), perfilMinimo)) {
            throw new AcessoNegadoException("O utilizador nao possui a permissao necessaria.");
        }
    }

    private boolean temPermissao(Perfil perfil, Perfil perfilMinimo) {
        if (perfil == Perfil.ADMIN) {
            return true;
        }
        return perfil == perfilMinimo || (perfil == Perfil.GERENTE && perfilMinimo == Perfil.VENDEDOR);
    }

    private void validar(Utilizador utilizador, boolean exigirId) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (exigirId) {
            validarId(utilizador.getIdUtilizador(), "idUtilizador");
        }
        ValidadorTexto.validarObrigatorioComTamanho(utilizador.getUsername(), 50, "Username");
        ValidadorTexto.validarObrigatorio(utilizador.getPasswordHash(), "Password");
        if (!utilizador.getPasswordHash().matches("\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}"))
            throw new DadoInvalidoException("A palavra-passe deve ser guardada com BCrypt.");
        ValidadorObjeto.validarNaoNulo(utilizador.getPerfil(), "Perfil");
        ValidadorObjeto.validarNaoNulo(utilizador.getFuncionario(), "Funcionario");
        validarId(utilizador.getFuncionario().getIdFuncionario(), "funcionario.idFuncionario");
        if (utilizador.getFuncionario().getEstado() != EstadoFuncionario.ATIVO) {
            throw new DadoInvalidoException("O funcionario do utilizador deve estar ativo.");
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
