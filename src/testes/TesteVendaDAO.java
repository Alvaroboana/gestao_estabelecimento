package testes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.dao.CategoriaDAO;
import model.dao.ClienteDAO;
import model.dao.FuncionarioDAO;
import model.dao.ProdutoDAO;
import model.dao.VendaDAO;
import model.vo.Cargo;
import model.vo.Categoria;
import model.vo.Cliente;
import model.vo.EstadoFuncionario;
import model.vo.EstadoProduto;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Venda;

public class TesteVendaDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste VendaDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        VendaDAO vendaDAO = new VendaDAO();

        // Dados de apoio
        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Para Venda " + System.currentTimeMillis());
        cargo.setSalarioBase(18000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Vendedor " + System.currentTimeMillis());
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1991, 7, 10));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Venda " + System.currentTimeMillis());

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Para Venda " + System.currentTimeMillis());

        Produto produto = new Produto();
        produto.setNome("Produto Venda " + System.currentTimeMillis());
        produto.setPreco(250.0);
        produto.setDescricao("Produto para teste de venda");
        produto.setUnidade("Unidade");
        produto.setEstado(EstadoProduto.ATIVO);
        produto.setQuantidadeMinima(1);

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setTotal(500.0);
        venda.setEstado(EstadoVenda.PENDENTE);

        try {
            // Cria entidades de apoio
            System.out.println("-> Criando entidades de apoio (Cargo, Funcionario, Cliente, Categoria, Produto)");
            cargoDAO.salvar(cargo);
            funcionario.setCargo(cargo);
            funcionarioDAO.salvar(funcionario);

            clienteDAO.salvar(cliente);

            categoriaDAO.salvar(categoria);
            produto.setCategoria(categoria);
            produtoDAO.salvar(produto);

            venda.setCliente(cliente);
            venda.setFuncionario(funcionario);

            // Item vendido dentro da venda
            List<ItemVendido> itens = new ArrayList<>();
            ItemVendido item = new ItemVendido();
            item.setProduto(produto);
            item.setQuantidade(2);
            item.setPreco(250.0);
            item.setDesconto(0.0);
            itens.add(item);
            venda.setItemVendido(itens);

            // 1. Salvar
            System.out.println("-> Salvando venda e itens associados");
            vendaDAO.salvar(venda);
            if (venda.getIdVenda() != null) {
                System.out.println("  [OK] Venda salva com ID: " + venda.getIdVenda());
                if (item.getIdItem() != null) {
                    System.out.println("  [OK] Item vendido salvo em cascata com ID: " + item.getIdItem());
                }
            } else {
                System.out.println("  [ERRO] Falha ao obter ID da venda");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando venda por ID: " + venda.getIdVenda());
            Venda buscada = vendaDAO.buscarPorId(venda.getIdVenda());
            if (buscada != null && buscada.getIdVenda().equals(venda.getIdVenda())) {
                System.out.println("  [OK] Venda encontrada | Total: " + buscada.getTotal() + " | Estado: " + buscada.getEstado());
                if (buscada.getCliente() != null) {
                    System.out.println("  [OK] Cliente associado: " + buscada.getCliente().getNome());
                }
                if (buscada.getFuncionario() != null) {
                    System.out.println("  [OK] Funcionario associado: " + buscada.getFuncionario().getNome());
                }
                if (buscada.getItemVendido() != null) {
                    System.out.println("  [OK] Itens carregados: " + buscada.getItemVendido().size());
                }
            } else {
                System.out.println("  [ERRO] Venda nao encontrada");
            }

            // 3. Atualizar
            System.out.println("-> Atualizando status da venda para CONCLUIDA");
            venda.setEstado(EstadoVenda.CONCLUIDA);
            venda.setTotal(450.0);
            vendaDAO.atualizar(venda);
            Venda atualizada = vendaDAO.buscarPorId(venda.getIdVenda());
            if (atualizada != null && atualizada.getEstado() == EstadoVenda.CONCLUIDA) {
                System.out.println("  [OK] Venda atualizada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar venda");
            }

            // 4. Listar
            System.out.println("-> Listando vendas");
            List<Venda> lista = vendaDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de vendas listadas: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhuma venda encontrada na listagem");
            }

            // 5. Eliminar venda
            System.out.println("-> Eliminando venda ID: " + venda.getIdVenda());
            vendaDAO.eliminar(venda.getIdVenda());
            Venda apagada = vendaDAO.buscarPorId(venda.getIdVenda());
            if (apagada == null) {
                System.out.println("  [OK] Venda eliminada com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar venda");
            }

            // Limpeza de dados de apoio
            System.out.println("-> Limpando dados de apoio (Produto, Categoria, Cliente, Funcionario, Cargo)");
            produtoDAO.eliminar(produto.getIdProduto());
            categoriaDAO.eliminar(categoria.getIdCategoria());
            clienteDAO.eliminar(cliente.getIdCliente());
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste VendaDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

