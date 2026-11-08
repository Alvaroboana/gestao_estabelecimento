package testes;

import java.time.LocalDate;
import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.dao.FuncionarioDAO;
import model.dao.UtilizadorDAO;
import model.vo.Cargo;
import model.vo.EstadoFuncionario;
import model.vo.Funcionario;
import model.vo.Perfil;
import model.vo.Utilizador;

public class TesteUtilizadorDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste UtilizadorDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        UtilizadorDAO utilizadorDAO = new UtilizadorDAO();

        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Para Utilizador " + System.currentTimeMillis());
        cargo.setSalarioBase(20000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Para Utilizador " + System.currentTimeMillis());
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1993, 3, 12));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        Utilizador utilizador = new Utilizador();
        String username = "user_" + (System.currentTimeMillis() % 1000000);
        utilizador.setUsername(username);
        utilizador.setPasswordHash("hash_senha_teste_123");
        utilizador.setPerfil(Perfil.VENDEDOR);

        try {
            System.out.println("-> Criando cargo e funcionario de apoio");
            cargoDAO.salvar(cargo);
            funcionario.setCargo(cargo);
            funcionarioDAO.salvar(funcionario);
            utilizador.setFuncionario(funcionario);

            System.out.println("-> Salvando utilizador: " + utilizador.getUsername());
            utilizadorDAO.salvar(utilizador);
            if (utilizador.getIdUtilizador() != null) {
                System.out.println("  [OK] Utilizador salvo com ID: " + utilizador.getIdUtilizador());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do utilizador");
                return;
            }

            System.out.println("-> Buscando utilizador por ID: " + utilizador.getIdUtilizador());
            Utilizador buscado = utilizadorDAO.buscarPorId(utilizador.getIdUtilizador());
            if (buscado != null && buscado.getUsername().equals(utilizador.getUsername())) {
                System.out.println("  [OK] Utilizador encontrado: " + buscado.getUsername() + " | Perfil: " + buscado.getPerfil());
                if (buscado.getFuncionario() != null) {
                    System.out.println("  [OK] Funcionario associado: " + buscado.getFuncionario().getNome());
                }
            } else {
                System.out.println("  [ERRO] Utilizador nao encontrado");
            }

            System.out.println("-> Buscando utilizador por username: " + utilizador.getUsername());
            Utilizador buscadoPorUser = utilizadorDAO.buscarPorUsername(utilizador.getUsername());
            if (buscadoPorUser != null && buscadoPorUser.getIdUtilizador().equals(utilizador.getIdUtilizador())) {
                System.out.println("  [OK] Utilizador encontrado com sucesso por username");
            } else {
                System.out.println("  [ERRO] Falha ao buscar utilizador por username");
            }

            System.out.println("-> Atualizando perfil e senha do utilizador");
            utilizador.setPerfil(Perfil.GERENTE);
            utilizador.setPasswordHash("novo_hash_senha_456");
            utilizadorDAO.atualizar(utilizador);
            Utilizador atualizado = utilizadorDAO.buscarPorId(utilizador.getIdUtilizador());
            if (atualizado != null && atualizado.getPerfil() == Perfil.GERENTE) {
                System.out.println("  [OK] Utilizador atualizado com sucesso: Perfil " + atualizado.getPerfil());
            } else {
                System.out.println("  [ERRO] Falha ao atualizar utilizador");
            }

            System.out.println("-> Listando utilizadores");
            List<Utilizador> lista = utilizadorDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de utilizadores listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum utilizador encontrado na listagem");
            }

            System.out.println("-> Eliminando utilizador ID: " + utilizador.getIdUtilizador());
            utilizadorDAO.eliminar(utilizador.getIdUtilizador());
            Utilizador apagado = utilizadorDAO.buscarPorId(utilizador.getIdUtilizador());
            if (apagado == null) {
                System.out.println("  [OK] Utilizador eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar utilizador");
            }

            System.out.println("-> Limpando funcionario e cargo de apoio");
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste UtilizadorDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

