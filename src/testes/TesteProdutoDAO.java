package testes;

import java.util.List;
import infra.DBException;
import model.dao.CategoriaDAO;
import model.dao.ProdutoDAO;
import model.vo.Categoria;
import model.vo.EstadoProduto;
import model.vo.Produto;

public class TesteProdutoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste ProdutoDAO ==========");
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Para Produto " + System.currentTimeMillis());

        Produto produto = new Produto();
        produto.setNome("Produto Teste " + System.currentTimeMillis());
        produto.setPreco(150.0);
        produto.setDescricao("Descricao do produto teste");
        produto.setUnidade("Unidade");
        produto.setEstado(EstadoProduto.ATIVO);
        produto.setQuantidadeMinima(5);

        try {
            System.out.println("-> Criando categoria de apoio");
            categoriaDAO.salvar(categoria);
            produto.setCategoria(categoria);

            System.out.println("-> Salvando produto: " + produto.getNome());
            produtoDAO.salvar(produto);
            if (produto.getIdProduto() != null) {
                System.out.println("  [OK] Produto salvo com ID: " + produto.getIdProduto());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do produto");
                return;
            }

            System.out.println("-> Buscando produto por ID: " + produto.getIdProduto());
            Produto buscado = produtoDAO.buscarPorId(produto.getIdProduto());
            if (buscado != null && buscado.getNome().equals(produto.getNome())) {
                System.out.println("  [OK] Produto encontrado: " + buscado.getNome() + " | Preco: " + buscado.getPreco());
                if (buscado.getCategoria() != null) {
                    System.out.println("  [OK] Categoria associada: " + buscado.getCategoria().getNome());
                }
            } else {
                System.out.println("  [ERRO] Produto nao encontrado");
            }

            System.out.println("-> Atualizando produto");
            produto.setNome(produto.getNome() + " Atualizado");
            produto.setPreco(175.50);
            produto.setQuantidadeMinima(10);
            produtoDAO.atualizar(produto);
            Produto atualizado = produtoDAO.buscarPorId(produto.getIdProduto());
            if (atualizado != null && atualizado.getNome().equals(produto.getNome()) && atualizado.getPreco().equals(175.50)) {
                System.out.println("  [OK] Produto atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar produto");
            }

            System.out.println("-> Listando produtos");
            List<Produto> lista = produtoDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de produtos listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum produto encontrado na listagem");
            }

            System.out.println("-> Eliminando produto ID: " + produto.getIdProduto());
            produtoDAO.eliminar(produto.getIdProduto());
            Produto apagado = produtoDAO.buscarPorId(produto.getIdProduto());
            if (apagado == null) {
                System.out.println("  [OK] Produto eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar produto");
            }

            System.out.println("-> Eliminando categoria de apoio ID: " + categoria.getIdCategoria());
            categoriaDAO.eliminar(categoria.getIdCategoria());

            System.out.println("=== Teste ProdutoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

