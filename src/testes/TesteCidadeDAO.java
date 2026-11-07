package testes;

import java.util.List;
import infra.DBException;
import model.dao.CidadeDAO;
import model.vo.Cidade;

public class TesteCidadeDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste CidadeDAO ==========");
        CidadeDAO dao = new CidadeDAO();

        Cidade cidade = new Cidade();
        cidade.setNome("Cidade Teste " + System.currentTimeMillis());

        try {
            System.out.println("-> Salvando cidade: " + cidade.getNome());
            dao.salvar(cidade);
            if (cidade.getIdCidade() != null) {
                System.out.println("  [OK] Cidade salva com ID: " + cidade.getIdCidade());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID gerado");
                return;
            }

            System.out.println("-> Buscando cidade por ID: " + cidade.getIdCidade());
            Cidade buscada = dao.buscarPorId(cidade.getIdCidade());
            if (buscada != null && buscada.getNome().equals(cidade.getNome())) {
                System.out.println("  [OK] Cidade encontrada: " + buscada.getNome());
            } else {
                System.out.println("  [ERRO] Cidade nao encontrada ou dados incorretos");
            }

            System.out.println("-> Atualizando nome da cidade");
            cidade.setNome(cidade.getNome() + " Atualizada");
            dao.atualizar(cidade);
            Cidade atualizada = dao.buscarPorId(cidade.getIdCidade());
            if (atualizada != null && atualizada.getNome().equals(cidade.getNome())) {
                System.out.println("  [OK] Cidade atualizada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar cidade");
            }

            System.out.println("-> Listando todas as cidades");
            List<Cidade> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de cidades listadas: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhuma cidade encontrada na listagem");
            }

            System.out.println("-> Eliminando cidade ID: " + cidade.getIdCidade());
            dao.eliminar(cidade.getIdCidade());
            Cidade apagada = dao.buscarPorId(cidade.getIdCidade());
            if (apagada == null) {
                System.out.println("  [OK] Cidade eliminada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar cidade");
            }

            System.out.println("=== Teste CidadeDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }

}

