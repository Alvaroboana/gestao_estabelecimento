package util;

import model.exception.DadoInvalidoException;

public class ValidadorNumero {

    private ValidadorNumero() {
    }

    public static void validarNaoNulo(Double valor, String nomeCampo) {
        if (valor == null) {
            throw new DadoInvalidoException(nomeCampo + " é obrigatório(a).");
        }
    }

    public static void validarNaoNulo(Integer valor, String nomeCampo) {
        if (valor == null) {
            throw new DadoInvalidoException(nomeCampo + " é obrigatório(a).");
        }
    }

    public static void validarNaoNegativo(Double valor, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (valor < 0) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser negativo(a).");
        }
    }

    public static void validarNaoNegativo(Integer valor, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (valor < 0) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser negativo(a).");
        }
    }

    public static void validarPositivo(Double valor, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (valor <= 0) {
            throw new DadoInvalidoException(nomeCampo + " deve ser maior que zero.");
        }
    }

    public static void validarPositivo(Integer valor, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (valor <= 0) {
            throw new DadoInvalidoException(nomeCampo + " deve ser maior que zero.");
        }
    }

    public static void validarIntervalo(Double valor, Double minimo, Double maximo, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (minimo != null && valor < minimo) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser menor que " + minimo + ".");
        }
        if (maximo != null && valor > maximo) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser maior que " + maximo + ".");
        }
    }

    public static void validarIntervalo(Integer valor, Integer minimo, Integer maximo, String nomeCampo) {
        validarNaoNulo(valor, nomeCampo);
        if (minimo != null && valor < minimo) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser menor que " + minimo + ".");
        }
        if (maximo != null && valor > maximo) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser maior que " + maximo + ".");
        }
    }

}
