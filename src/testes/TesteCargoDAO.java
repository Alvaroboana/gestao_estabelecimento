package testes;

import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.vo.Cargo;

public class TesteCargoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste CargoDAO ==========");
        CargoDAO dao = new CargoDAO();

        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Teste " + System.currentTimeMillis());
        cargo.setSalarioBase(18500.0);

        try {
            // 1. Salvar
            System.out.println("-> Salvando cargo: " + cargo.getNome());
            dao.salvar(cargo);
            if (cargo.getIdCargo() != null) {
                System.out.println("  [OK] Cargo salvo com ID: " + cargo.getIdCargo());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID gerado");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando cargo por ID: " + cargo.getIdCargo());
            Cargo buscado = dao.buscarPorId(cargo.getIdCargo());
            if (buscado != null && buscado.getNome().equals(cargo.getNome())) {
                System.out.println("  [OK] Cargo encontrado: " + buscado.getNome() + " | Salario: " + buscado.getSalarioBase());
            } else {
                System.out.println("  [ERRO] Cargo nao encontrado");
            }

            // 3. Atualizar
            System.out.println("-> Atualizando cargo");
            cargo.setNome(cargo.getNome() + " Senior");
            cargo.setSalarioBase(25000.0);
            dao.atualizar(cargo);
            Cargo atualizado = dao.buscarPorId(cargo.getIdCargo());
            if (atualizado != null && atualizado.getSalarioBase().equals(25000.0)) {
                System.out.println("  [OK] Cargo atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar cargo");
            }

            // 4. Listar
            System.out.println("-> Listando todos os cargos");
            List<Cargo> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de cargos listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum cargo encontrado");
            }

            // 5. Eliminar
            System.out.println("-> Eliminando cargo ID: " + cargo.getIdCargo());
            dao.eliminar(cargo.getIdCargo());
            Cargo apagado = dao.buscarPorId(cargo.getIdCargo());
            if (apagado == null) {
                System.out.println("  [OK] Cargo eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar cargo");
            }

            System.out.println("=== Teste CargoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }

}

