package testes;

import java.util.List;
import infra.DBException;
import model.dao.BairroDAO;
import model.dao.CidadeDAO;
import model.dao.EnderecoDAO;
import model.dao.PessoaDAO;
import model.vo.Bairro;
import model.vo.Cidade;
import model.vo.Cliente;
import model.vo.Endereco;
import model.vo.Pessoa;

public class TesteEnderecoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste EnderecoDAO ==========");
        CidadeDAO cidadeDAO = new CidadeDAO();
        BairroDAO bairroDAO = new BairroDAO();
        PessoaDAO pessoaDAO = new PessoaDAO();
        EnderecoDAO enderecoDAO = new EnderecoDAO();

        Cidade cidade = new Cidade();
        cidade.setNome("Cidade Para Endereco " + System.currentTimeMillis());

        Bairro bairro = new Bairro();
        bairro.setNome("Bairro Para Endereco " + System.currentTimeMillis());

        Pessoa pessoa = new Cliente();
        pessoa.setNome("Pessoa Para Endereco " + System.currentTimeMillis());

        Endereco endereco = new Endereco();
        endereco.setQuarteirao("Q12");
        endereco.setRua("Av. de Teste");
        endereco.setNumeroCasa("100");

        try {
            // Cria entidades de suporte
            System.out.println("-> Criando dados de apoio (Cidade, Bairro, Pessoa)");
            cidadeDAO.salvar(cidade);
            bairro.setCidade(cidade);
            bairroDAO.salvar(bairro);
            pessoaDAO.salvar(pessoa);
            endereco.setBairro(bairro);

            // 1. Salvar Endereco
            System.out.println("-> Salvando endereco");
            enderecoDAO.salvarComPessoa(endereco, pessoa.getIdPessoa());
            if (endereco.getIdEndereco() != null) {
                System.out.println("  [OK] Endereco salvo com ID: " + endereco.getIdEndereco());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do endereco");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando endereco por ID: " + endereco.getIdEndereco());
            Endereco buscado = enderecoDAO.buscarPorId(endereco.getIdEndereco());
            if (buscado != null && buscado.getRua().equals(endereco.getRua())) {
                System.out.println("  [OK] Endereco encontrado: Rua " + buscado.getRua() + ", Casa " + buscado.getNumeroCasa());
                if (buscado.getBairro() != null && buscado.getBairro().getCidade() != null) {
                    System.out.println("  [OK] Bairro: " + buscado.getBairro().getNome() + " | Cidade: " + buscado.getBairro().getCidade().getNome());
                }
            } else {
                System.out.println("  [ERRO] Endereco nao encontrado");
            }

            // 3. Buscar por Pessoa
            System.out.println("-> Buscando endereco pela pessoa ID: " + pessoa.getIdPessoa());
            Endereco endPessoa = enderecoDAO.buscarPorPessoa(pessoa.getIdPessoa());
            if (endPessoa != null && endPessoa.getIdEndereco().equals(endereco.getIdEndereco())) {
                System.out.println("  [OK] Endereco associado a pessoa encontrado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao buscar endereco por pessoa");
            }

            // 4. Atualizar
            System.out.println("-> Atualizando endereco");
            endereco.setRua("Av. Modificada");
            endereco.setNumeroCasa("200B");
            enderecoDAO.atualizar(endereco);
            Endereco atualizado = enderecoDAO.buscarPorId(endereco.getIdEndereco());
            if (atualizado != null && "Av. Modificada".equals(atualizado.getRua()) && "200B".equals(atualizado.getNumeroCasa())) {
                System.out.println("  [OK] Endereco atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar endereco");
            }

            // 5. Listar
            System.out.println("-> Listando enderecos");
            List<Endereco> lista = enderecoDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de enderecos listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum endereco encontrado na listagem");
            }

            // 6. Eliminar endereco
            System.out.println("-> Eliminando endereco ID: " + endereco.getIdEndereco());
            enderecoDAO.eliminar(endereco.getIdEndereco());
            Endereco apagado = enderecoDAO.buscarPorId(endereco.getIdEndereco());
            if (apagado == null) {
                System.out.println("  [OK] Endereco eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar endereco");
            }

            // Limpeza de entidades de apoio
            System.out.println("-> Limpando entidades de apoio (Pessoa, Bairro, Cidade)");
            pessoaDAO.eliminar(pessoa.getIdPessoa());
            bairroDAO.eliminar(bairro.getIdBairro());
            cidadeDAO.eliminar(cidade.getIdCidade());

            System.out.println("=== Teste EnderecoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

