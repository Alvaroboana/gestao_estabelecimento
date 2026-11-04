package testes;

import model.bo.FornecedorBO;
import model.exception.DadoInvalidoException;
import model.vo.Fornecedor;

public class TesteFornecedorBO {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {
        System.out.println("========== Teste FornecedorBO (Validacoes) ==========");
        FornecedorBO bo = new FornecedorBO();

        // 1. Salvar nulo
        try {
            bo.salvar(null);
            erro("Salvar fornecedor nulo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Fornecedor nulo rejeitado: " + e.getMessage());
        }

        // 2. Salvar nome nulo
        try {
            Fornecedor f = new Fornecedor();
            f.setNome(null);
            bo.salvar(f);
            erro("Salvar nome nulo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Nome nulo rejeitado: " + e.getMessage());
        }

        // 3. Salvar nome em branco
        try {
            Fornecedor f = new Fornecedor();
            f.setNome("   ");
            bo.salvar(f);
            erro("Salvar nome em branco", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Nome em branco rejeitado: " + e.getMessage());
        }

        // 4. Salvar nome maior que 100 caracteres
        try {
            Fornecedor f = new Fornecedor();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 101; i++) {
                sb.append("A");
            }
            f.setNome(sb.toString());
            bo.salvar(f);
            erro("Salvar nome > 100 chars", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Nome > 100 caracteres rejeitado: " + e.getMessage());
        }

        // 5. Atualizar sem ID
        try {
            Fornecedor f = new Fornecedor();
            f.setNome("Fornecedor Valido");
            bo.atualizar(f);
            erro("Atualizar sem ID", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("Atualizacao sem ID rejeitada: " + e.getMessage());
        }

        // 6. Eliminar com ID invalido
        try {
            bo.eliminar(-1L);
            erro("Eliminar com ID negativo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("ID negativo rejeitado: " + e.getMessage());
        }

        // 7. Buscar por ID nulo
        try {
            bo.buscarPorId(null);
            erro("Buscar com ID nulo", "Deveria ter falhado");
        } catch (DadoInvalidoException e) {
            sucesso("ID nulo rejeitado: " + e.getMessage());
        }

        System.out.println("\n-------------------------------------------");
        System.out.println("Resultados: " + passou + " passou, " + falhou + " falhou.");
        System.out.println("===========================================");
    }

    private static void sucesso(String msg) {
        passou++;
        System.out.println("  [OK] " + msg);
    }

    private static void erro(String caso, String detalhe) {
        falhou++;
        System.err.println("  [FALHOU] " + caso + " -> " + detalhe);
    }
}
