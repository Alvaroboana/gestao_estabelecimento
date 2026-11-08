package testes;

import java.util.List;
import infra.DBException;
import model.dao.BairroDAO;
import model.dao.CidadeDAO;
import model.vo.Bairro;
import model.vo.Cidade;

public class TesteBairroDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste BairroDAO ==========");
        CidadeDAO cidadeDAO = new CidadeDAO();
        BairroDAO bairroDAO = new BairroDAO();

        Cidade cidade = new Cidade();
        cidade.setNome("Cidade Para Bairro " + System.currentTimeMillis());

        Bairro bairro = new Bairro();
        bairro.setNome("Bairro Teste " + System.currentTimeMillis());

        try {
            // Cria cidade
            System.out.println("Criando cidade para teste");
            cidadeDAO.salvar(cidade);
            bairro.setCidade(cidade);

            // 1. Salvar Bairro
            System.out.println("Salvando bairro: " + bairro.getNome());
            bairroDAO.salvar(bairro);
            if (bairro.getIdBairro() != null) {
                System.out.println("Bairro salvo com ID: " + bairro.getIdBairro());
            } else {
                System.out.println("ERRO Falha ao obter ID do bairro");
                return;
            }

            // 2. Buscar por ID 
            System.out.println("Buscar bairro por ID: " + bairro.getIdBairro());
            Bairro buscado = bairroDAO.buscarPorId(bairro.getIdBairro());
            if (buscado != null && buscado.getCidade() != null && buscado.getCidade().getNome() != null) {
                System.out.println("Bairro encontrado: " + buscado.getNome() + " | Cidade associada: " + buscado.getCidade().getNome());
            } else {
                System.out.println("ERRO Bairro ou cidade associada nao carregada");
            }

            // 3. Atualizar Bairro
            System.out.println("Atualizando nome do bairro");
            bairro.setNome(bairro.getNome() + " Modificado");
            bairroDAO.atualizar(bairro);
            Bairro atualizado = bairroDAO.buscarPorId(bairro.getIdBairro());
            if (atualizado != null && atualizado.getNome().equals(bairro.getNome())) {
                System.out.println("Bairro atualizado com sucesso");
            } else {
                System.out.println("ERRO Falha ao atualizar bairro");
            }

            // 4. Listar
            System.out.println("Listar bairros");
            List<Bairro> lista = bairroDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("Total de bairros listados: " + lista.size());
            } else {
                System.out.println("ERRO Nenhum bairro encontrado");
            }

            // 5. Eliminar Bairro e Cidade
            System.out.println("Eliminar bairro");
            bairroDAO.eliminar(bairro.getIdBairro());
            Bairro apagado = bairroDAO.buscarPorId(bairro.getIdBairro());
            if (apagado == null) {
                System.out.println("Bairro eliminado com sucesso");
            } else {
                System.out.println("ERRO Falha ao eliminar bairro");
            }

            System.out.println("Eliminar cidade de teste");
            cidadeDAO.eliminar(cidade.getIdCidade());

            System.out.println("=== Teste BairroDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("O container do MySQL esta em execucao");
        }
    }

}
