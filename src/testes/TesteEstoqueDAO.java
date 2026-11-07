package testes;

import java.time.LocalDate;
import java.util.List;
import infra.DBException;
import model.dao.CargoDAO;
import model.dao.CategoriaDAO;
import model.dao.EstoqueDAO;
import model.dao.FuncionarioDAO;
import model.dao.ProdutoDAO;
import model.vo.Cargo;
import model.vo.Categoria;
import model.vo.EstadoFuncionario;
import model.vo.EstadoProduto;
import model.vo.Estoque;
import model.vo.Funcionario;
import model.vo.Produto;

public class TesteEstoqueDAO {

    public static void main(String[] args) {
        System.out.println("========== Teste EstoqueDAO ==========");
        CargoDAO cargoDAO = new CargoDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();
        EstoqueDAO estoqueDAO = new EstoqueDAO();

        Cargo cargo = new Cargo();
        cargo.setNome("Cargo Teste Estoque " + System.currentTimeMillis());
        cargo.setSalarioBase(15000.0);

        Funcionario funcionario = new Funcionario();
        funcionario.setNome("Funcionario Para Estoque " + System.currentTimeMillis());
        String bi = ("BI" + System.currentTimeMillis());
        if (bi.length() > 20) {
            bi = bi.substring(0, 20);
        }
        funcionario.setBi(bi);
        funcionario.setDataNascimento(LocalDate.of(1995, 8, 20));
        funcionario.setDataInicio(LocalDate.now());
        funcionario.setEstado(EstadoFuncionario.ATIVO);

        Categoria categoria = new Categoria();
        categoria.setNome("Categoria Para Estoque " + System.currentTimeMillis());

        Produto produto = new Produto();
        produto.setNome("Produto Para Estoque " + System.currentTimeMillis());
        produto.setPreco(80.0);
        produto.setDescricao("Item para teste de estoque");
        produto.setUnidade("Caixa");
        produto.setEstado(EstadoProduto.ATIVO);
        produto.setQuantidadeMinima(2);

        Estoque estoque = new Estoque();
        estoque.setQuantidade(50);
        estoque.setDataEntrada(LocalDate.now());
        estoque.setDataValidade(LocalDate.now().plusMonths(12));

        try {
            System.out.println("-> Criando dados de apoio (Cargo, Funcionario, Categoria, Produto)");
            cargoDAO.salvar(cargo);
            funcionario.setCargo(cargo);
            funcionarioDAO.salvar(funcionario);

            categoriaDAO.salvar(categoria);
            produto.setCategoria(categoria);
            produtoDAO.salvar(produto);

            estoque.setFuncionario(funcionario);
            estoque.setProduto(produto);

            System.out.println("-> Salvando registro de estoque: Qtd " + estoque.getQuantidade());
            estoqueDAO.salvar(estoque);
            if (estoque.getIdEstoque() != null) {
                System.out.println("  [OK] Estoque salvo com ID: " + estoque.getIdEstoque());
            } else {
                System.out.println("  [ERRO] Falha ao obter ID do estoque");
                return;
            }

            System.out.println("-> Buscando estoque por ID: " + estoque.getIdEstoque());
            Estoque buscado = estoqueDAO.buscarPorId(estoque.getIdEstoque());
            if (buscado != null && buscado.getQuantidade().equals(estoque.getQuantidade())) {
                System.out.println("  [OK] Estoque encontrado: Qtd " + buscado.getQuantidade());
                if (buscado.getProduto() != null) {
                    System.out.println("  [OK] Produto associado: " + buscado.getProduto().getNome());
                }
                if (buscado.getFuncionario() != null) {
                    System.out.println("  [OK] Funcionario responsavel: " + buscado.getFuncionario().getNome());
                }
            } else {
                System.out.println("  [ERRO] Estoque nao encontrado");
            }

            System.out.println("-> Buscando estoques pelo produto ID: " + produto.getIdProduto());
            List<Estoque> estoquesProduto = estoqueDAO.buscarPorProduto(produto.getIdProduto());
            if (estoquesProduto != null && !estoquesProduto.isEmpty()) {
                System.out.println("  [OK] Lotes de estoque encontrados para o produto: " + estoquesProduto.size());
            } else {
                System.out.println("  [ERRO] Nenhum estoque encontrado para o produto");
            }

            System.out.println("-> Atualizando quantidade e validade do estoque");
            estoque.setQuantidade(40);
            estoque.setDataValidade(LocalDate.now().plusMonths(6));
            estoqueDAO.atualizar(estoque);
            Estoque atualizado = estoqueDAO.buscarPorId(estoque.getIdEstoque());
            if (atualizado != null && atualizado.getQuantidade().equals(40)) {
                System.out.println("  [OK] Estoque atualizado com sucesso: Qtd " + atualizado.getQuantidade());
            } else {
                System.out.println("  [ERRO] Falha ao atualizar estoque");
            }

            System.out.println("-> Listando todos os registros de estoque");
            List<Estoque> lista = estoqueDAO.listarTodos();
            if (lista != null && !lista.isEmpty()) {
                System.out.println("  [OK] Total de estoques listados: " + lista.size());
            } else {
                System.out.println("  [ERRO] Nenhum estoque encontrado na listagem");
            }

            System.out.println("-> Eliminando estoque ID: " + estoque.getIdEstoque());
            estoqueDAO.eliminar(estoque.getIdEstoque());
            Estoque apagado = estoqueDAO.buscarPorId(estoque.getIdEstoque());
            if (apagado == null) {
                System.out.println("  [OK] Estoque eliminado com sucesso");
            } else {
                System.out.println("  [ERRO] Falha ao eliminar estoque");
            }

            System.out.println("-> Limpando entidades de apoio");
            produtoDAO.eliminar(produto.getIdProduto());
            categoriaDAO.eliminar(categoria.getIdCategoria());
            funcionarioDAO.eliminar(funcionario.getIdFuncionario());
            cargoDAO.eliminar(cargo.getIdCargo());

            System.out.println("=== Teste EstoqueDAO concluido com sucesso ===");

        } catch (DBException e) {
            System.out.println("  [ERRO DE BANCO] " + e.getMessage());
            System.out.println("  Certifique-se de que o container do MySQL esta em execucao.");
        }
    }
}

