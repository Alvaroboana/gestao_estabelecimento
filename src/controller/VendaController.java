package controller;

import java.util.Collections;
import java.util.List;
import model.bo.ClienteBO;
import model.bo.EstoqueBO;
import model.bo.ProdutoBO;
import model.bo.ReembolsoBO;
import model.bo.VendaBO;
import model.vo.Cliente;
import model.vo.Contacto;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Reembolso;
import model.vo.Utilizador;
import model.vo.Venda;
import util.ValidadorObjeto;

public class VendaController {

    private final VendaBO vendaBO;
    private final EstoqueBO estoqueBO;
    private final ReembolsoBO reembolsoBO;
    private final ClienteBO clienteBO;
    private final ProdutoBO produtoBO;

    public VendaController() {
        this(new VendaBO(), new EstoqueBO(), new ReembolsoBO(), new ClienteBO(), new ProdutoBO());
    }

    public VendaController(VendaBO vendaBO, EstoqueBO estoqueBO, ReembolsoBO reembolsoBO) {
        this(vendaBO, estoqueBO, reembolsoBO, new ClienteBO(), new ProdutoBO());
    }

    public VendaController(
            VendaBO vendaBO,
            EstoqueBO estoqueBO,
            ReembolsoBO reembolsoBO,
            ClienteBO clienteBO,
            ProdutoBO produtoBO) {
        ValidadorObjeto.validarNaoNulo(vendaBO, "VendaBO");
        ValidadorObjeto.validarNaoNulo(estoqueBO, "EstoqueBO");
        ValidadorObjeto.validarNaoNulo(reembolsoBO, "ReembolsoBO");
        ValidadorObjeto.validarNaoNulo(clienteBO, "ClienteBO");
        ValidadorObjeto.validarNaoNulo(produtoBO, "ProdutoBO");
        this.vendaBO = vendaBO;
        this.estoqueBO = estoqueBO;
        this.reembolsoBO = reembolsoBO;
        this.clienteBO = clienteBO;
        this.produtoBO = produtoBO;
    }

    public List<Venda> listarVendas() {
        return vendaBO.listarTodos();
    }

    public List<Venda> listarTodos() {
        return listarVendas();
    }

    public Venda buscarPorId(Long id) {
        return vendaBO.buscarPorId(id);
    }

    public void salvarRascunho(Venda venda) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (venda.getEstado() == null) {
            venda.setEstado(EstadoVenda.PENDENTE);
        }
        vendaBO.salvar(venda);
    }

    public void salvarRascunho(Venda venda, Utilizador operador) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (venda.getEstado() == null) {
            venda.setEstado(EstadoVenda.PENDENTE);
        }
        vendaBO.salvar(venda, operador);
    }

    public void concluirVenda(Venda venda) {
        concluirVenda(venda, null);
    }

    public void concluirVenda(Venda venda, Utilizador operador) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        if (operador != null) {
            vendaBO.concluirEGuardar(venda, operador);
        } else {
            vendaBO.concluirEGuardar(venda);
        }
    }

    public void anularVenda(Venda venda, Utilizador operador) {
        vendaBO.anular(venda, operador);
    }

    public void anular(Venda venda, Utilizador operador) {
        anularVenda(venda, operador);
    }

    public void anularVenda(Long idVenda, Utilizador operador) {
        Venda venda = vendaBO.buscarPorId(idVenda);
        anularVenda(venda, operador);
    }

    public Reembolso processarReembolso(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        return reembolsoBO.criarEGuardar(item, quantidade, motivo, funcionario);
    }

    public ItemVendido adicionarItem(Venda venda, Produto produto, int quantidade, Double desconto) {
        return vendaBO.adicionarItem(venda, produto, quantidade, desconto);
    }

    public List<Reembolso> listarReembolsos() {
        return reembolsoBO.listarTodos();
    }

    public List<Reembolso> buscarReembolsosPorItem(Long idItem) {
        return reembolsoBO.buscarPorItem(idItem);
    }

    public List<Cliente> listarClientes() {
        return clienteBO.listarTodos();
    }

    public Cliente obterOuCriarConsumidorFinal() {
        List<Cliente> todos = clienteBO.listarTodos();
        if (todos != null) {
            for (Cliente c : todos) {
                if (c != null && c.getNome() != null
                        && ("Consumidor Final".equalsIgnoreCase(c.getNome().trim())
                                || "Consumidor".equalsIgnoreCase(c.getNome().trim()))) {
                    return c;
                }
            }
        }
        Cliente consumidor = new Cliente();
        consumidor.setNome("Consumidor Final");
        clienteBO.salvar(consumidor);
        return consumidor;
    }

    public Cliente salvarClienteRapido(String nome, String contacto) {
        Cliente novo = new Cliente();
        novo.setNome(nome);
        if (contacto != null && !contacto.trim().isEmpty()) {
            Contacto c = new Contacto();
            c.setContacto(contacto.trim());
            novo.setContacto(Collections.singletonList(c));
        }
        clienteBO.salvar(novo);
        return novo;
    }

    public List<Produto> listarProdutos() {
        return produtoBO.listarTodos();
    }

    public Produto buscarProdutoPorCodigoBarras(String codigoBarras) {
        return produtoBO.buscarPorCodigoBarras(codigoBarras);
    }

    public void atualizarRascunho(Venda venda, Utilizador utilizador) {
        if (utilizador != null && utilizador.getFuncionario() != null && venda != null && venda.getFuncionario() == null) {
            venda.setFuncionario(utilizador.getFuncionario());
        }
        vendaBO.atualizar(venda);
    }
}
