package testes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.dao.FuncionarioDAO;
import model.vo.Cargo;
import model.vo.Contacto;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;

public class TesteFuncionarioDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste FuncionarioDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Teste Func " + System.currentTimeMillis());
        cargo.setSalarioBase(22000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Teste " + System.currentTimeMillis());
        // BI unico com max 20 caracteres
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1992, 4, 10));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        List<Contacto> contactos = new ArrayList<>();
        Contacto c1 = new Contacto();
        c1.setContacto("845551234");
        contactos.add(c1);
        funcionario.setContacto(contactos);

        try {
            // 0. Criar Cargo
            System.out.println("-> Criando cargo para associar ao funcionario");
            cargoDAO.salvar(cargo);
            funcionario.setCargo(cargo);

            // 1. Salvar
            System.out.println("-> Salvando funcionario: " + funcionario.getNome() + " | BI: " + funcionario.getBi());
            funcionarioDAO.salvar(funcionario);
            if (funcionario.getIdFuncionario() != null && funcionario.getIdPessoa() != null) {
                System.out.println("  [OK] Funcionario salvo | ID Funcionario: " + funcionario.getIdFuncionario() + " | ID Pessoa: " + funcionario.getIdPessoa());
            } else {
                System.out.println("  [ERRO] Falha ao obter IDs gerados");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando funcionario por ID: " + funcionario.getIdFuncionario());
            Funcionario buscado = funcionarioDAO.buscarPorId(funcionario.getIdFuncionario());
            if (buscado != null && buscado.getNome().equals(funcionario.getNome())) {
                System.out.println("  [OK] Funcionario encontrado: " + buscado.getNome() + " | BI: " + buscado.getBi());
                if (buscado.getCargo() != null) {
                    System.out.println("  [OK] Cargo associado: " + buscado.getCargo().getNome());
                }
                if (buscado.getContacto() != null) {
                    System.out.println("  [OK] Contactos associados: " + buscado.getContacto().size());
                }
            } else {
                System.out.println("  [ERRO] Funcionario nao encontrado");
            }

            // 3. Atualizar
            System.out.println("-> Atualizando funcionario");
            funcionario.setNome(funcionario.getNome() + " Atualizado");
            funcionario.setEstado(EstadoFuncionario.INATIVO);
            funcionarioDAO.atualizar(funcionario);
            Funcionario atualizado = funcionarioDAO.buscarPorId(funcionario.getIdFuncionario());
            if (atualizado != null && atualizado.getNome().equals(funcionario.getNome()) && atualizado.getEstado() == EstadoFuncionario.INATIVO) {
                System.out.println("  [OK] Funcionario atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar funcionario");
            }

            // 4. Listar
            System.out.println("-> Listando funcionarios");
            List<Funcionario> lista = funcionarioDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de funcionarios listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum funcionario encontrado na listagem");
            }

            // 5. Eliminar
            System.out.println("-> Eliminando funcionario ID: " + funcionario.getIdFuncionario());
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            Funcionario apagado = funcionarioDAO.buscarPorId(funcionario.getIdFuncionario());
            if (apagado == null) {
                System.out.println("  [OK] Funcionario eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar funcionario");
            }

            // Limpeza de cargo de apoio
            System.out.println("-> Eliminando cargo de apoio ID: " + cargo.getIdCargo());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste FuncionarioDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

