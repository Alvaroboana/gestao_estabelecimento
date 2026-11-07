package util;

import java.time.LocalDate;
import java.time.Period;
import model.exception.DadoInvalidoException;

public class ValidadorData {

    private ValidadorData() {
    }

    public static void validarNaoNula(LocalDate data, String nomeCampo) {
        if (data == null) {
            throw new DadoInvalidoException(nomeCampo + " é obrigatória.");
        }
    }

    public static void validarNaoFutura(LocalDate data, String nomeCampo) {
        validarNaoNula(data, nomeCampo);
        if (data.isAfter(LocalDate.now())) {
            throw new DadoInvalidoException(nomeCampo + " não pode ser uma data futura.");
        }
    }

    public static void validarFutura(LocalDate data, String nomeCampo) {
        validarNaoNula(data, nomeCampo);
        if (data.isBefore(LocalDate.now()) || data.isEqual(LocalDate.now())) {
            throw new DadoInvalidoException(nomeCampo + " deve ser uma data futura.");
        }
    }

    public static void validarOrdem(LocalDate dataAnterior, LocalDate dataPosterior,
                                     String nomeAnterior, String nomePosterior) {
        if (dataAnterior != null && dataPosterior != null) {
            if (dataPosterior.isBefore(dataAnterior)) {
                throw new DadoInvalidoException(
                    nomePosterior + " não pode ser anterior a " + nomeAnterior + "."
                );
            }
        }
    }

    public static void validarIdadeMinima(LocalDate dataNascimento, int idadeMinima, String nomeCampo) {
        validarNaoNula(dataNascimento, nomeCampo);
        validarNaoFutura(dataNascimento, nomeCampo);
        int anos = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (anos < idadeMinima) {
            throw new DadoInvalidoException("A idade mínima exigida para " + nomeCampo + " é de " + idadeMinima + " anos.");
        }
    }

}
