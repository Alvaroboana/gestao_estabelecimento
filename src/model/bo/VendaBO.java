package model.bo;

import java.util.List;

import model.dao.VendaDAO;
import model.exception.AcessoNegadoException;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.exception.EstoqueInsuficienteException;
import model.exception.VendaInvalidaException;
import model.vo.EstadoFuncionario;
import model.vo.EstadoVenda;
import model.vo.ItemVendido;
import model.vo.Produto;
import model.vo.Utilizador;
import model.vo.Venda;
import util.ValidadorNumero;
import util.ValidadorObjeto;

public class VendaBO {
    private final VendaDAO dao;

    public VendaBO() {
        this(new VendaDAO());
    }

    public VendaBO(VendaDAO dao) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de venda");
        this.dao = dao;
    }

    public void salvar(Venda venda) {
        validar(venda);
        dao.salvar(venda);
    }

    public void salvar(Venda venda, Utilizador utilizador) {
        validarOperador(venda, utilizador);
        salvar(venda);
    }

    public void concluirEGuardar(Venda venda) {
        validar(venda);
        EstadoVenda estadoAnterior = venda.getEstado();
        Double totalAnterior = venda.getTotal();
        venda.concluir();
        try {
            dao.salvar(venda);
        } catch (RuntimeException e) {
            venda.setEstado(estadoAnterior);
            venda.setTotal(totalAnterior);
            throw e;
        }
    }

    public void concluirEGuardar(Venda venda, Utilizador utilizador) {
        validarOperador(venda, utilizador);
        concluirEGuardar(venda);
    }

    public void atualizar(Venda venda) {
        validarId(venda == null ? null : venda.getIdVenda(), "idVenda");
        validarRelacionamentos(venda);
        if (venda.getEstado() == EstadoVenda.CONCLUIDA) {
            venda.setTotal(venda.calcularTotal());
        }
        dao.atualizar(venda);
    }

    public void eliminar(Long id) {
        validarId(id, "idVenda");
        dao.eliminar(id);
    }

    public List<Venda> listarTodos() {
        return dao.listarTodos();
    }

    public Venda buscarPorId(Long id) {
        validarId(id, "idVenda");
        Venda venda = dao.buscarPorId(id);
        if (venda == null) {
            throw new EntidadeNaoEncontradaException("Venda nao encontrada.");
        }
        return venda;
    }

    public ItemVendido adicionarItem(Venda venda, Produto produto, int quantidade, Double desconto) {
        validarRelacionamentos(venda);
        validarProdutoParaVenda(produto, quantidade);
        if (desconto != null) {
            ValidadorNumero.validarNaoNegativo(desconto, "Desconto");
        }
        return venda.adicionarItem(produto, quantidade, desconto);
    }

    public void anular(Venda venda, Utilizador utilizador) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (!utilizador.podeAnularVendas()) {
            throw new AcessoNegadoException("O utilizador nao pode anular vendas.");
        }
        validarId(venda == null ? null : venda.getIdVenda(), "idVenda");
        venda.anular();
        dao.atualizar(venda);
    }

    private void validar(Venda venda) {
        validarRelacionamentos(venda);
        if (venda.getEstado() == null) {
            venda.setEstado(EstadoVenda.PENDENTE);
        }
        if (venda.getItemVendido() != null) {
            for (ItemVendido item : venda.getItemVendido()) {
                validarItem(item);
            }
        }
        if (venda.getEstado() == EstadoVenda.CONCLUIDA) {
            if (venda.getItemVendido() == null || venda.getItemVendido().isEmpty()) {
                throw new VendaInvalidaException("Uma venda concluida deve possuir itens.");
            }
            venda.setTotal(venda.calcularTotal());
        }
    }

    private void validarRelacionamentos(Venda venda) {
        ValidadorObjeto.validarNaoNulo(venda, "Venda");
        ValidadorObjeto.validarNaoNulo(venda.getCliente(), "Cliente");
        ValidadorObjeto.validarNaoNulo(venda.getFuncionario(), "Funcionario");
        validarId(venda.getCliente().getIdCliente(), "cliente.idCliente");
        validarId(venda.getFuncionario().getIdFuncionario(), "funcionario.idFuncionario");
        if (venda.getFuncionario().getEstado() != EstadoFuncionario.ATIVO) {
            throw new VendaInvalidaException("O funcionario da venda deve estar ativo.");
        }
    }

    private void validarItem(ItemVendido item) {
        ValidadorObjeto.validarNaoNulo(item, "Item vendido");
        ValidadorNumero.validarNaoNulo(item.getQuantidade(), "Quantidade do item");
        ValidadorNumero.validarPositivo(item.getQuantidade(), "Quantidade do item");
        ValidadorNumero.validarNaoNulo(item.getPreco(), "Preco do item");
        ValidadorNumero.validarNaoNegativo(item.getPreco(), "Preco do item");
        if (item.getDesconto() != null) {
            ValidadorNumero.validarNaoNegativo(item.getDesconto(), "Desconto do item");
        }
        validarProdutoParaVenda(item.getProduto(), item.getQuantidade());
    }

    private void validarProdutoParaVenda(Produto produto, int quantidade) {
        ValidadorObjeto.validarNaoNulo(produto, "Produto");
        validarId(produto.getIdProduto(), "produto.idProduto");
        if (!produto.podeSerVendido() && produto.getEstoque() != null) {
            throw new VendaInvalidaException("O produto nao esta disponivel para venda.");
        }
        if (produto.getEstoque() != null && !produto.itemStockSuficiente(quantidade)) {
            throw new EstoqueInsuficienteException("Stock insuficiente para o produto " + produto.getNome() + ".");
        }
    }

    private void validarOperador(Venda venda, Utilizador utilizador) {
        ValidadorObjeto.validarNaoNulo(utilizador, "Utilizador");
        if (!utilizador.podeEntrar()) {
            throw new AcessoNegadoException("O utilizador nao esta autorizado a operar.");
        }
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
