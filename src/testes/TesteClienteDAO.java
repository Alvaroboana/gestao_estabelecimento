package testes;

import java.util.ArrayList;
import java.util.List;
import infra.DBException;
import model.dao.ClienteDAO;
import model.vo.Cliente;
import model.vo.Contacto;

public class TesteClienteDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste ClienteDAO ==========");
        ClienteDAO dao = new ClienteDAO();

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste " + System.currentTimeMillis());

        List<Contacto> contactos = new ArrayList<Contacto>();
        Contacto c1 = new Contacto();
        c1.setContacto("840000001");
        contactos.add(c1);

        Contacto c2 = new Contacto();
        c2.setContacto("820000002");
        contactos.add(c2);

        cliente.setContacto(contactos);

        try {
            // 1. Salvar (Pessoa + Cliente + Contactos em transacao)
            System.out.println("-> Salvando cliente: " + cliente.getNome());
            dao.salvar(cliente);
            if (cliente.getIdCliente() != null && cliente.getIdPessoa() != null) {
                System.out.println("  [OK] Cliente salvo | ID Cliente: " + cliente.getIdCliente() + " | ID Pessoa: " + cliente.getIdPessoa());
            } else {
                System.out.println("  [ERRO] Falha ao obter IDs gerados");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando cliente por ID: " + cliente.getIdCliente());
            Cliente buscado = dao.buscarPorId(cliente.getIdCliente());
            if (buscado != null && buscado.getNome().equals(cliente.getNome())) {
                System.out.println("  [OK] Cliente encontrado: " + buscado.getNome());
                if (buscado.getContacto() != null) {
                    System.out.println("  [OK] Contactos carregados: " + buscado.getContacto().size());
                }
            } else {
                System.out.println("  [ERRO] Cliente nao encontrado");
            }

            // 3. Atualizar
            System.out.println("-> Atualizando nome do cliente");
            cliente.setNome(cliente.getNome() + " Atualizado");
            dao.atualizar(cliente);
            Cliente atualizado = dao.buscarPorId(cliente.getIdCliente());
            if (atualizado != null && atualizado.getNome().equals(cliente.getNome())) {
                System.out.println("  [OK] Cliente atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar cliente");
            }

            // 4. Listar
            System.out.println("-> Listando clientes");
            List<Cliente> lista = dao.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de clientes listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum cliente encontrado");
            }

            // 5. Eliminar
            System.out.println("-> Eliminando cliente ID: " + cliente.getIdCliente());
            dao.eliminar(cliente.getIdCliente());
            Cliente apagado = dao.buscarPorId(cliente.getIdCliente());
            if (apagado == null) {
                System.out.println("  [OK] Cliente eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar cliente");
            }

            System.out.println("=== Teste ClienteDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }

}

