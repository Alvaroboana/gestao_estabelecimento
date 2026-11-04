package controller;

import model.bo.LoginBO;
import model.vo.Utilizador;
import util.ValidadorObjeto;

/**
 * Controller responsável pela autenticação e controle de acesso inicial ao sistema.
 */
public class LoginController {

    private final LoginBO loginBO;

    public LoginController() {
        this(new LoginBO());
    }

    public LoginController(LoginBO loginBO) {
        ValidadorObjeto.validarNaoNulo(loginBO, "LoginBO");
        this.loginBO = loginBO;
    }

    /**
     * Autentica o utilizador no sistema utilizando o nome de utilizador e senha fornecidos.
     *
     * @param username Nome de utilizador
     * @param senha    Senha em texto simples
     * @return Utilizador autenticado
     */
    public Utilizador autenticar(String username, String senha) {
        return loginBO.autenticar(username, senha);
    }
}
