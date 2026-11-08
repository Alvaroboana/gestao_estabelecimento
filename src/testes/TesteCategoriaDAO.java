package testes;

import java.util.List;
import infra.DBException;
import model.dao.CategoriaDAO;
import model.vo.Categoria;

public class TesteCategoriaDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste CategoriaDAO ==========");
        CategoriaDAO dao = new CategoriaDAO();

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Teste " + System.currentTimeMillis());

        try {
            System.out.println("-> Salvando categoria: " + categoria.getNome());
            dao.salvar(categoria);
            if (categoria.getIdCategoria() != null) {
                System.out.println("  [OK] Categoria salva com ID: " + categoria.getIdCategoria());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID gerado");
                return;
            }

            System.out.println("-> Buscando categoria por ID: " + categoria.getIdCategoria());
            Categoria buscada = dao.buscarPorId(categoria.getIdCategoria());
            if (buscada != null && buscada.getNome().equals(categoria.getNome())) {
                System.out.println("  [OK] Categoria encontrada: " + buscada.getNome());
            } else {
                System.out.println("  [ERRO] Categoria nao encontrada");
            }

            System.out.println("-> Atualizando categoria");
            categoria.setNome(categoria.getNome() + " Atualizada");
            dao.atualizar(categoria);
            Categoria atualizada = dao.buscarPorId(categoria.getIdCategoria());
            if (atualizada != null && atualizada.getNome().equals(categoria.getNome())) {
                System.out.println("  [OK] Categoria atualizada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar categoria");
            }

            System.out.println("-> Listando categorias");
            List<Categoria> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de categorias listadas: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhuma categoria encontrada");
            }

            System.out.println("-> Eliminando categoria ID: " + categoria.getIdCategoria());
            dao.eliminar(categoria.getIdCategoria());
            Categoria apagada = dao.buscarPorId(categoria.getIdCategoria());
            if (apagada == null) {
                System.out.println("  [OK] Categoria eliminada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar categoria");
            }

            System.out.println("=== Teste CategoriaDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

