package testes;

import java.util.ArrayList;
import java.util.Arrays;
import model.vo.Fornecedor;
import model.vo.Produto;

public class TesteFornecedorProdutos {
    public static void main(String[] args) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setProduto(new ArrayList<Produto>());
        verificar(fornecedor.getProduto().isEmpty(), "Fornecedor sem produtos");

        Produto primeiro = new Produto();
        primeiro.setIdProduto(1L);
        Produto segundo = new Produto();
        segundo.setIdProduto(2L);

        fornecedor.setProduto(new ArrayList<>(Arrays.asList(primeiro)));
        verificar(fornecedor.getProduto().size() == 1 && fornecedor.fornece(primeiro),
                "Fornecedor com um produto");

        fornecedor.setProduto(new ArrayList<>(Arrays.asList(primeiro, segundo, primeiro)));
        verificar(fornecedor.getProduto().size() == 2
                && fornecedor.fornece(primeiro) && fornecedor.fornece(segundo),
                "Fornecedor com varios produtos, sem duplicados");

        fornecedor.setProduto(null);
        verificar(fornecedor.getProduto() != null && fornecedor.getProduto().isEmpty(),
                "Lista nula tratada como conjunto vazio");
        System.out.println("Todos os testes de produtos do fornecedor passaram.");
    }

    private static void verificar(boolean condicao, String caso) {
        if (!condicao) {
            throw new AssertionError(caso);
        }
        System.out.println("[OK] " + caso);
    }
}
