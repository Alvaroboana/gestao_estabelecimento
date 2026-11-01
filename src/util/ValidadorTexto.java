package util;

import model.exception.DadoInvalidoException;

public class ValidadorTexto {

    private ValidadorTexto() {
    }

    public static void validarObrigatorio(String valor, String nomeCampo) {
        if (valor == null) {
            throw new DadoInvalidoException(nomeCampo + " é obrigatório(a).");
        }
        if (valor.trim().length() == 0) {
            throw new DadoInvalidoException(nomeCampo + " não pode estar em branco.");
        }
    }

    public static void validarTamanhoMax(String valor, int tamanhoMax, String nomeCampo) {
        if (valor != null) {
            if (valor.trim().length() > tamanhoMax) {
                throw new DadoInvalidoException(nomeCampo + " não pode ter mais de " + tamanhoMax + " caracteres.");
            }
        }
    }

    public static void validarTamanhoMin(String valor, int tamanhoMin, String nomeCampo) {
        if (valor != null) {
            if (valor.trim().length() < tamanhoMin) {
                throw new DadoInvalidoException(nomeCampo + " não pode ter menos de " + tamanhoMin + " caracteres.");
            }
        }
    }

    public static void validarTamanhoExato(String valor, int tamanhoExato, String nomeCampo) {
        if (valor != null) {
            if (valor.trim().length() != tamanhoExato) {
                throw new DadoInvalidoException(nomeCampo + " deve ter exatamente " + tamanhoExato + " caracteres.");
            }
        }
    }

    public static void validarObrigatorioComTamanho(String valor, int tamanhoMax, String nomeCampo) {
        validarObrigatorio(valor, nomeCampo);
        validarTamanhoMax(valor, tamanhoMax, nomeCampo);
    }

    public static void validarApenasDigitos(String valor, String nomeCampo) {
        if (valor != null && valor.trim().length() > 0) {
            String limpo = valor.trim();
            for (int i = 0; i < limpo.length(); i++) {
                char c = limpo.charAt(i);
                if (!Character.isDigit(c)) {
                    throw new DadoInvalidoException(nomeCampo + " deve conter apenas dígitos numéricos.");
                }
            }
        }
    }

    public static void validarContacto(String valor, String nomeCampo) {
        validarObrigatorio(valor, nomeCampo);
        validarTamanhoMax(valor, 30, nomeCampo);
    }

    public static void validarBI(String valor, String nomeCampo) {
        validarObrigatorio(valor, nomeCampo);
        validarTamanhoMax(valor, 20, nomeCampo);
    }

}
