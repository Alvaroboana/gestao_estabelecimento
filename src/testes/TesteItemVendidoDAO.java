package testes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.dao.CategoriaDAO;
import model.dao.ClienteDAO;
import model.dao.FuncionarioDAO;
import model.dao.ItemVendidoDAO;
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

public class TesteItemVendidoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste ItemVendidoDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        VendaDAO vendaDAO = new VendaDAO();
        ItemVendidoDAO itemVendidoDAO = new ItemVendidoDAO();

        // Entidades de apoio
        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Para ItemVendido " + System.currentTimeMillis());
        cargo.setSalarioBase(19000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Para Item " + System.currentTimeMillis());
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1989, 11, 23));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Para Item " + System.currentTimeMillis());

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Para Item " + System.currentTimeMillis());

        Produto produto = new Produto();
        produto.setNome("Produto Para Item " + System.currentTimeMillis());
        produto.setPreco(120.0);
        produto.setDescricao("Descricao do produto para item");
        produto.setUnidade("Unidade");
        produto.setEstado(EstadoProduto.ATIVO);
        produto.setQuantidadeMinima(5);

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setTotal(360.0);
        venda.setEstado(EstadoVenda.PENDENTE);

        ItemVendido item = new ItemVendido();
        item.setQuantidade(3);
        item.setPreco(120.0);
        item.setDesconto(10.0);

        try {
            // Cria entidades de apoio
            System.out.println("-> Criando entidades de apoio (Cargo, Funcionario, Cliente, Categoria, Produto, Venda)");
            cargoDAO.salvar(cargo);
            funcionario.setCargo(cargo);
            funcionarioDAO.salvar(funcionario);

            clienteDAO.salvar(cliente);

            categoriaDAO.salvar(categoria);
            produto.setCategoria(categoria);
            produtoDAO.salvar(produto);

            venda.setCliente(cliente);
            venda.setFuncionario(funcionario);
            vendaDAO.salvar(venda);

            item.setVenda(venda);
            item.setProduto(produto);

            // 1. Salvar
            System.out.println("-> Salvando item vendido");
            itemVendidoDAO.salvar(item);
            if (item.getIdItem() != null) {
                System.out.println("  [OK] Item vendido salvo com ID: " + item.getIdItem());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do item vendido");
                return;
            }

            // 2. Buscar por ID
            System.out.println("-> Buscando item vendido por ID: " + item.getIdItem());
            ItemVendido buscado = itemVendidoDAO.buscarPorId(item.getIdItem());
            if (buscado != null && buscado.getQuantidade().equals(item.getQuantidade())) {
                System.out.println("  [OK] Item encontrado | Qtd: " + buscado.getQuantidade() + " | Preco: " + buscado.getPreco());
                if (buscado.getProduto() != null) {
                    System.out.println("  [OK] Produto associado: " + buscado.getProduto().getNome());
                }
            } else {
                System.out.println("  [ERRO] Item vendido nao encontrado");
            }

            // 3. Buscar por Venda
            System.out.println("-> Buscando itens da venda ID: " + venda.getIdVenda());
            List<ItemVendido> itensVenda = itemVendidoDAO.buscarPorVenda(venda.getIdVenda());
            if (itensVenda != null && !itensVenda.isEmpty()) {
                System.out.println("  [OK] Itens encontrados para a venda: " + itensVenda.size());
            } else {
                System.out.println("  [ERRO] Nenhum item encontrado para a venda");
            }

            // 4. Atualizar
            System.out.println("-> Atualizando quantidade e preco do item vendido");
            item.setQuantidade(5);
            item.setPreco(110.0);
            item.setDesconto(15.0);
            itemVendidoDAO.atualizar(item);
            ItemVendido atualizado = itemVendidoDAO.buscarPorId(item.getIdItem());
            if (atualizado != null && atualizado.getQuantidade().equals(5)) {
                System.out.println("  [OK] Item vendido atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar item vendido");
            }

            // 5. Listar
            System.out.println("-> Listando todos os itens vendidos");
            List<ItemVendido> lista = itemVendidoDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de itens listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum item vendido encontrado na listagem");
            }

            // 6. Eliminar item vendido
            System.out.println("-> Eliminando item vendido ID: " + item.getIdItem());
            itemVendidoDAO.eliminar(item.getIdItem());
            ItemVendido apagado = itemVendidoDAO.buscarPorId(item.getIdItem());
            if (apagado == null) {
                System.out.println("  [OK] Item vendido eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar item vendido");
            }

            // Limpeza de entidades de apoio
            System.out.println("-> Limpando dados de apoio (Venda, Produto, Categoria, Cliente, Funcionario, Cargo)");
            vendaDAO.eliminar(venda.getIdVenda());
            produtoDAO.eliminar(produto.getIdProduto());
            categoriaDAO.eliminar(categoria.getIdCategoria());
            clienteDAO.eliminar(cliente.getIdCliente());
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste ItemVendidoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

