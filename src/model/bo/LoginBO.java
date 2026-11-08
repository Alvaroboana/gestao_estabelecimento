package model.bo;

import model.vo.Utilizador;
import util.ValidadorObjeto;

public class LoginBO {
    private final UtilizadorBO utilizadorBO;

    public LoginBO() {
        this(new UtilizadorBO());
    }

    public LoginBO(UtilizadorBO utilizadorBO) {
        ValidadorObjeto.validarNaoNulo(utilizadorBO, "UtilizadorBO");
        this.utilizadorBO = utilizadorBO;
    }

    public Utilizador autenticar(String username, String senha) {
        return utilizadorBO.autenticar(username, senha);
    }
}
