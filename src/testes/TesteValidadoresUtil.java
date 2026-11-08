package testes;

import java.time.LocalDate;
import model.exception.DadoInvalidoException;
import util.ValidadorData;
import util.ValidadorNumero;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class TesteValidadoresUtil {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {

        System.out.println("========== Teste ValidadorObjeto ==========");
        // Sucesso
        try {
            ValidadorObjeto.validarNaoNulo("qualquerCoisa", "Campo");
            sucesso("Objecto valido (nao nulo)");
        } catch (DadoInvalidoException e) {
            erro("Objecto valido (nao nulo)", e.getMessage());
        }

        // Falha
        try {
            ValidadorObjeto.validarNaoNulo(null, "Campo");
            erro("Objecto nulo", "Deveria ter lancado excecao");
        } catch (DadoInvalidoException e) {
            sucesso("Objecto nulo capturado: " + e.getMessage());
        }

        System.out.println("\n========== Teste ValidadorTexto ==========");
        // Obrigatorio valido
        try {
            ValidadorTexto.validarObrigatorio("Maputo", "Cidade");
            sucesso("Texto obrigatorio valido");
        } catch (DadoInvalidoException e) {
            erro("Texto obrigatorio valido", e.getMessage());
        }

        // Obrigatorio nulo
        try {
            ValidadorTexto.validarObrigatorio(null, "Cidade");
            erro("Texto nulo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Texto nulo capturado: " + e.getMessage());
        }

        // Obrigatorio vazio
        try {
            ValidadorTexto.validarObrigatorio("", "Cidade");
            erro("Texto vazio", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Texto vazio capturado: " + e.getMessage());
        }

        // Obrigatorio espacos
        try {
            ValidadorTexto.validarObrigatorio("     ", "Cidade");
            erro("Texto com espacos", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Texto espacos capturado: " + e.getMessage());
        }

        // Tamanho maximo valido
        try {
            ValidadorTexto.validarTamanhoMax("Ola", 10, "Nome");
            sucesso("Tamanho maximo respeitado");
        } catch (DadoInvalidoException e) {
            erro("Tamanho maximo respeitado", e.getMessage());
        }

        // Tamanho maximo excedido
        try {
            ValidadorTexto.validarTamanhoMax("12345678901", 10, "Nome");
            erro("Tamanho maximo excedido", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Tamanho excedido capturado: " + e.getMessage());
        }

        // Apenas digitos valido
        try {
            ValidadorTexto.validarApenasDigitos("841234567", "Telefone");
            sucesso("Apenas digitos valido");
        } catch (DadoInvalidoException e) {
            erro("Apenas digitos valido", e.getMessage());
        }

        // Apenas digitos com letras (falha)
        try {
            ValidadorTexto.validarApenasDigitos("84123A567", "Telefone");
            erro("Digitos com letras", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Digitos invalidos capturado: " + e.getMessage());
        }

        // Contacto valido
        try {
            ValidadorTexto.validarContacto("845000000", "Contacto");
            sucesso("Contacto valido");
        } catch (DadoInvalidoException e) {
            erro("Contacto valido", e.getMessage());
        }

        // BI valido
        try {
            ValidadorTexto.validarBI("110100234567M", "BI");
            sucesso("BI valido");
        } catch (DadoInvalidoException e) {
            erro("BI valido", e.getMessage());
        }

        System.out.println("\n========== Teste ValidadorNumero ==========");
        // Double nao negativo zero (valido)
        try {
            ValidadorNumero.validarNaoNegativo(0.0, "Preco");
            sucesso("Preco zero valido");
        } catch (DadoInvalidoException e) {
            erro("Preco zero valido", e.getMessage());
        }

        // Double negativo (falha)
        try {
            ValidadorNumero.validarNaoNegativo(-10.5, "Preco");
            erro("Preco negativo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Preco negativo capturado: " + e.getMessage());
        }

        // Integer positivo valido
        try {
            ValidadorNumero.validarPositivo(5, "Quantidade");
            sucesso("Quantidade positiva valida");
        } catch (DadoInvalidoException e) {
            erro("Quantidade positiva valida", e.getMessage());
        }

        // Integer positivo com zero (falha)
        try {
            ValidadorNumero.validarPositivo(0, "Quantidade");
            erro("Quantidade zero quando deve ser positiva", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Quantidade zero capturada: " + e.getMessage());
        }

        // Intervalo valido
        try {
            ValidadorNumero.validarIntervalo(50.0, 0.0, 100.0, "Percentagem");
            sucesso("Intervalo dentro dos limites valido");
        } catch (DadoInvalidoException e) {
            erro("Intervalo dentro dos limites valido", e.getMessage());
        }

        // Intervalo acima do maximo (falha)
        try {
            ValidadorNumero.validarIntervalo(150.0, 0.0, 100.0, "Percentagem");
            erro("Intervalo acima do maximo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Intervalo excedido capturado: " + e.getMessage());
        }

        System.out.println("\n========== Teste ValidadorData ==========");
        // Data nao nula valida
        try {
            ValidadorData.validarNaoNula(LocalDate.now(), "Data");
            sucesso("Data nao nula valida");
        } catch (DadoInvalidoException e) {
            erro("Data nao nula valida", e.getMessage());
        }

        // Data nao futura (passado valido)
        try {
            ValidadorData.validarNaoFutura(LocalDate.of(2000, 1, 1), "Data Nascimento");
            sucesso("Data no passado valida");
        } catch (DadoInvalidoException e) {
            erro("Data no passado valida", e.getMessage());
        }

        // Data nao futura com data futura (falha)
        try {
            ValidadorData.validarNaoFutura(LocalDate.now().plusDays(2), "Data Nascimento");
            erro("Data futura", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Data futura capturada: " + e.getMessage());
        }

        // Ordem cronologica valida
        LocalDate d1 = LocalDate.of(1995, 5, 10);
        LocalDate d2 = LocalDate.of(2020, 1, 1);
        try {
            ValidadorData.validarOrdem(d1, d2, "Nascimento", "Inicio");
            sucesso("Ordem cronologica valida");
        } catch (DadoInvalidoException e) {
            erro("Ordem cronologica valida", e.getMessage());
        }

        // Ordem cronologica invertida (falha)
        try {
            ValidadorData.validarOrdem(d2, d1, "Inicio", "Nascimento");
            erro("Ordem cronologica invertida", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Ordem invertida capturada: " + e.getMessage());
        }

        // Idade minima 18 anos valida
        try {
            ValidadorData.validarIdadeMinima(LocalDate.of(2000, 1, 1), 18, "Funcionario");
            sucesso("Idade minima atendida valida");
        } catch (DadoInvalidoException e) {
            erro("Idade minima atendida valida", e.getMessage());
        }

        // Idade minima nao atendida (falha)
        try {
            ValidadorData.validarIdadeMinima(LocalDate.now().minusYears(15), 18, "Funcionario");
            erro("Idade menor que o minimo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Idade insuficiente capturada: " + e.getMessage());
        }

        System.out.println("\n============================================");
        System.out.println(" RESULTADO: " + passou + " passou | " + falhou + " falhou");
        System.out.println("============================================");
    }

    private static void sucesso(String msg) {
        System.out.println("  [OK]    " + msg);
        passou = passou + 1;
    }

    private static void erro(String msg, String detalhe) {
        System.out.println("  [ERRO]  " + msg + " -> " + detalhe);
        falhou = falhou + 1;
    }

}
