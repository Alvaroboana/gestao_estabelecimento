package testes;

import java.util.List;
import infra.DBException;
import model.dao.PessoaDAO;
import model.vo.Cliente;
import model.vo.Pessoa;

public class TestePessoaDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste PessoaDAO ==========");
        PessoaDAO dao = new PessoaDAO();

        Pessoa pessoa = new Cliente();
        pessoa.setNome("Pessoa Teste " + System.currentTimeMillis());

        try {
            System.out.println("-> Salvando pessoa: " + pessoa.getNome());
            dao.salvar(pessoa);
            if (pessoa.getIdPessoa() != null) {
                System.out.println("  [OK] Pessoa salva com ID: " + pessoa.getIdPessoa());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID gerado");
                return;
            }

            System.out.println("-> Buscando pessoa por ID: " + pessoa.getIdPessoa());
            Pessoa buscada = dao.buscarPorId(pessoa.getIdPessoa());
            if (buscada != null && buscada.getNome().equals(pessoa.getNome())) {
                System.out.println("  [OK] Pessoa encontrada: " + buscada.getNome());
            } else {
                System.out.println("  [ERRO] Pessoa nao encontrada");
            }

            System.out.println("-> Atualizando nome da pessoa");
            pessoa.setNome(pessoa.getNome() + " Atualizada");
            dao.atualizar(pessoa);
            Pessoa atualizada = dao.buscarPorId(pessoa.getIdPessoa());
            if (atualizada != null && atualizada.getNome().equals(pessoa.getNome())) {
                System.out.println("  [OK] Pessoa atualizada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar pessoa");
            }

            System.out.println("-> Listando pessoas");
            List<Pessoa> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de pessoas listadas: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhuma pessoa encontrada");
            }

            System.out.println("-> Eliminando pessoa ID: " + pessoa.getIdPessoa());
            dao.eliminar(pessoa.getIdPessoa());
            Pessoa apagada = dao.buscarPorId(pessoa.getIdPessoa());
            if (apagada == null) {
                System.out.println("  [OK] Pessoa eliminada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar pessoa");
            }

            System.out.println("=== Teste PessoaDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

