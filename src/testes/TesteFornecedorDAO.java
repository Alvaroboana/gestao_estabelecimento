package testes;

import java.util.ArrayList;
import java.util.List;
import infra.DBException;
import model.dao.FornecedorDAO;
import model.vo.Contacto;
import model.vo.Fornecedor;

public class TesteFornecedorDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste FornecedorDAO ==========");
        FornecedorDAO dao = new FornecedorDAO();

        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome("Fornecedor Teste " + System.currentTimeMillis());

        List<Contacto> contactos = new ArrayList<>();
        Contacto c1 = new Contacto();
        c1.setContacto("841234567");
        contactos.add(c1);

        Contacto c2 = new Contacto();
        c2.setContacto("829876543");
        contactos.add(c2);

        fornecedor.setContacto(contactos);

        try {
            // 1. Salvar
            System.out.println("-> Salvando fornecedor: " + fornecedor.getNome());
            dao.salvar(fornecedor);
            if (fornecedor.getIdFornecedor() != null && fornecedor.getIdPessoa() != null) {
                System.out.println("  [OK] Fornecedor salvo | ID Fornecedor: " + fornecedor.getIdFornecedor() + " | ID Pessoa: " + fornecedor.getIdPessoa());
            } else {
                System.out.println("  [ERRO] Falha ao obter IDs gerados");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando fornecedor por ID: " + fornecedor.getIdFornecedor());
            Fornecedor buscado = dao.buscarPorId(fornecedor.getIdFornecedor());
            if (buscado != null && buscado.getNome().equals(fornecedor.getNome())) {
                System.out.println("  [OK] Fornecedor encontrado: " + buscado.getNome());
                if (buscado.getContacto() != null) {
                    System.out.println("  [OK] Contactos carregados: " + buscado.getContacto().size());
                }
            } else {
                System.out.println("  [ERRO] Fornecedor nao encontrado");
            }

            // 3. Atualizar
            System.out.println("-> Atualizando nome do fornecedor");
            fornecedor.setNome(fornecedor.getNome() + " Atualizado");
            dao.atualizar(fornecedor);
            Fornecedor atualizado = dao.buscarPorId(fornecedor.getIdFornecedor());
            if (atualizado != null && atualizado.getNome().equals(fornecedor.getNome())) {
                System.out.println("  [OK] Fornecedor atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar fornecedor");
            }

            // 4. Listar
            System.out.println("-> Listando fornecedores");
            List<Fornecedor> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de fornecedores listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum fornecedor encontrado na listagem");
            }

            // 5. Eliminar
            System.out.println("-> Eliminando fornecedor ID: " + fornecedor.getIdFornecedor());
            dao.eliminar(fornecedor.getIdFornecedor());
            Fornecedor apagado = dao.buscarPorId(fornecedor.getIdFornecedor());
            if (apagado == null) {
                System.out.println("  [OK] Fornecedor eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar fornecedor");
            }

            System.out.println("=== Teste FornecedorDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

