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
import model.dao.ReembolsoDAO;
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
import model.vo.Reembolso;
import model.vo.Venda;

public class TesteReembolsoDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste ReembolsoDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        VendaDAO vendaDAO = new VendaDAO();
        ItemVendidoDAO itemVendidoDAO = new ItemVendidoDAO();
        ReembolsoDAO reembolsoDAO = new ReembolsoDAO();

        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Reembolso " + System.currentTimeMillis());
        cargo.setSalarioBase(21000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Reembolso " + System.currentTimeMillis());
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1990, 2, 14));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Reembolso " + System.currentTimeMillis());

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Reembolso " + System.currentTimeMillis());

        Produto produto = new Produto();
        produto.setNome("Produto Reembolso " + System.currentTimeMillis());
        produto.setPreco(300.0);
        produto.setDescricao("Produto para teste de reembolso");
        produto.setUnidade("Unidade");
        produto.setEstado(EstadoProduto.ATIVO);
        produto.setQuantidadeMinima(2);

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setTotal(600.0);
        venda.setEstado(EstadoVenda.CONCLUIDA);

        ItemVendido item = new ItemVendido();
        item.setQuantidade(2);
        item.setPreco(300.0);
        item.setDesconto(0.0);

        Reembolso reembolso = new Reembolso();
        reembolso.setQuantidade(1);
        reembolso.setValor(300.0);
        reembolso.setMotivo("Produto com defeito de fabrica");
        reembolso.setData(LocalDateTime.now());

        try {
            System.out.println("-> Criando entidades de apoio (Cargo, Funcionario, Cliente, Categoria, Produto, Venda, ItemVendido)");
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
            itemVendidoDAO.salvar(item);

            reembolso.setItem(item);
            reembolso.setFuncionario(funcionario);

            System.out.println("-> Salvando reembolso: Qtd " + reembolso.getQuantidade() + " | Valor: " + reembolso.getValor());
            reembolsoDAO.salvar(reembolso);
            if (reembolso.getIdReembolso() != null) {
                System.out.println("  [OK] Reembolso salvo com ID: " + reembolso.getIdReembolso());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do reembolso");
                return;
            }

            System.out.println("-> Buscando reembolso por ID: " + reembolso.getIdReembolso());
            Reembolso buscado = reembolsoDAO.buscarPorId(reembolso.getIdReembolso());
            if (buscado != null && buscado.getMotivo().equals(reembolso.getMotivo())) {
                System.out.println("  [OK] Reembolso encontrado | Motivo: " + buscado.getMotivo() + " | Valor: " + buscado.getValor());
                if (buscado.getItem() != null) {
                    System.out.println("  [OK] Item associado ID: " + buscado.getItem().getIdItem());
                }
                if (buscado.getFuncionario() != null) {
                    System.out.println("  [OK] Funcionario que aprovou: " + buscado.getFuncionario().getNome());
                }
            } else {
                System.out.println("  [ERRO] Reembolso nao encontrado");
            }

            System.out.println("-> Buscando reembolsos pelo item ID: " + item.getIdItem());
            List<Reembolso> porItem = reembolsoDAO.buscarPorItem(item.getIdItem());
            if (porItem != null && !porItem.isEmpty()) {
                System.out.println("  [OK] Reembolsos encontrados para o item: " + porItem.size());
            } else {
                System.out.println("  [ERRO] Nenhum reembolso encontrado para o item");
            }

            System.out.println("-> Buscando reembolsos pelo funcionario ID: " + funcionario.getIdFuncionario());
            List<Reembolso> porFunc = reembolsoDAO.buscarPorFuncionario(funcionario.getIdFuncionario());
            if (porFunc != null && !porFunc.isEmpty()) {
                System.out.println("  [OK] Reembolsos encontrados para o funcionario: " + porFunc.size());
            } else {
                System.out.println("  [ERRO] Nenhum reembolso encontrado para o funcionario");
            }

            System.out.println("-> Atualizando motivo e valor do reembolso");
            reembolso.setMotivo("Produto com defeito - Aprovado com desconto");
            reembolso.setValor(280.0);
            reembolsoDAO.atualizar(reembolso);
            Reembolso atualizado = reembolsoDAO.buscarPorId(reembolso.getIdReembolso());
            if (atualizado != null && atualizado.getValor().equals(280.0)) {
                System.out.println("  [OK] Reembolso atualizado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao atualizar reembolso");
            }

            System.out.println("-> Listando todos os reembolsos");
            List<Reembolso> lista = reembolsoDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de reembolsos listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum reembolso encontrado na listagem");
            }

            System.out.println("-> Eliminando reembolso ID: " + reembolso.getIdReembolso());
            reembolsoDAO.eliminar(reembolso.getIdReembolso());
            Reembolso apagado = reembolsoDAO.buscarPorId(reembolso.getIdReembolso());
            if (apagado == null) {
                System.out.println("  [OK] Reembolso eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar reembolso");
            }

            System.out.println("-> Limpando entidades de apoio (ItemVendido, Venda, Produto, Categoria, Cliente, Funcionario, Cargo)");
            itemVendidoDAO.eliminar(item.getIdItem());
            vendaDAO.eliminar(venda.getIdVenda());
            produtoDAO.eliminar(produto.getIdProduto());
            categoriaDAO.eliminar(categoria.getIdCategoria());
            clienteDAO.eliminar(cliente.getIdCliente());
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste ReembolsoDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

