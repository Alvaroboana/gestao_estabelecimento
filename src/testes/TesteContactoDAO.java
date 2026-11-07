package testes;

import java.util.List;
import infra.DBException;
import model.dao.ContactoDAO;
import model.dao.PessoaDAO;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.Pessoa;

public class TesteContactoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste ContactoDAO ==========");
        PessoaDAO pessoaDAO = new PessoaDAO();
        ContactoDAO contactoDAO = new ContactoDAO();

        Pessoa pessoa = new Cliente();
        pessoa.setNome("Pessoa Para Contacto " + System.currentTimeMillis());

        Contacto contacto = new Contacto();
        contacto.setContacto("84" + (System.currentTimeMillis() % 10000000));

        try {
            System.out.println("-> Criando pessoa para associar contacto");
            pessoaDAO.salvar(pessoa);
            if (pessoa.getIdPessoa() == null) {
                System.out.println("  [ERRO] Falha ao criar pessoa de suporte");
                return;
            }

            System.out.println("-> Salvando contacto: " + contacto.getContacto());
            contactoDAO.salvarComPessoa(contacto, pessoa.getIdPessoa());
            if (contacto.getIdContacto() != null) {
                System.out.println("  [OK] Contacto salvo com ID: " + contacto.getIdContacto());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do contacto");
                return;
            }

            System.out.println("-> Buscando contacto por ID: " + contacto.getIdContacto());
            Contacto buscado = contactoDAO.buscarPorId(contacto.getIdContacto());
            if (buscado != null && buscado.getContacto().equals(contacto.getContacto())) {
                System.out.println("  [OK] Contacto encontrado: " + buscado.getContacto());
            } else {
                System.out.println("  [ERRO] Contacto nao encontrado");
            }

            System.out.println("-> Buscando contactos pela pessoa ID: " + pessoa.getIdPessoa());
            List<Contacto> listaPessoa = contactoDAO.buscarPorPessoa(pessoa.getIdPessoa());
            if (listaPessoa != null && !listaPessoa.isEmpty()) {
                System.out.println("  [OK] Total de contactos da pessoa: " + listaPessoa.size());
            } else {
                System.out.println("  [ERRO] Nenhum contacto encontrado para a pessoa");
            }

            System.out.println("-> Atualizando valor do contacto");
            contacto.setContacto("85" + (System.currentTimeMillis() % 10000000));
            contactoDAO.atualizar(contacto);
            Contacto atualizado = contactoDAO.buscarPorId(contacto.getIdContacto());
            if (atualizado != null && atualizado.getContacto().equals(contacto.getContacto())) {
                System.out.println("  [OK] Contacto atualizado com sucesso: " + atualizado.getContacto());
            } else {
                System.out.println("  [ERRO] Falha ao atualizar contacto");
            }

            System.out.println("-> Listando contactos");
            List<Contacto> lista = contactoDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de contactos listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum contacto encontrado na listagem");
            }

            System.out.println("-> Eliminando contacto ID: " + contacto.getIdContacto());
            contactoDAO.eliminar(contacto.getIdContacto());
            Contacto apagado = contactoDAO.buscarPorId(contacto.getIdContacto());
            if (apagado == null) {
                System.out.println("  [OK] Contacto eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar contacto");
            }

            System.out.println("-> Eliminando pessoa de teste ID: " + pessoa.getIdPessoa());
            pessoaDAO.eliminar(pessoa.getIdPessoa());

            System.out.println("=== Teste ContactoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

