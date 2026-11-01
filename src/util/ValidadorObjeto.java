package util;

import model.exception.DadoInvalidoException;

public class ValidadorObjeto {

    private ValidadorObjeto() {
    }

    public static void validarNaoNulo(Object objeto, String nomeCampo) {
        if (objeto == null) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser nulo(a).");
        }
    }

}
