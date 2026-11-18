package controller;

import model.bo.LoginBO;
import model.vo.Utilizador;
import util.ValidadorObjeto;

public class LoginController {

    private final LoginBO loginBO;

    public LoginController() {
        this(new LoginBO());
    }

    public LoginController(LoginBO loginBO) {
        ValidadorObjeto.validarNaoNulo(loginBO, "LoginBO");
        this.loginBO = loginBO;
    }

    public Utilizador alterarSenhaObrigatoria(String username, String actual, String nova) {
        return new model.bo.UtilizadorBO().alterarSenhaAutenticada(username, actual, nova);
    }

    public Utilizador autenticar(String username, String senha) {
        return loginBO.autenticar(username, senha);
    }
}
