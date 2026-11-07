package model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.exception.DadoInvalidoException;
import model.exception.VendaInvalidaException;

public class Venda implements Serializable {
    private Long idVenda;
    private LocalDateTime data;
    private Double total;
    private EstadoVenda estado;
    private Cliente cliente;
    private Funcionario funcionario;
    private List<ItemVendido> itemVendido;

    public Venda() {
    }

    public Venda(LocalDateTime data, Double total, EstadoVenda estado, Cliente cliente, Funcionario funcionario,
            List<ItemVendido> itemVendido) {
        this.data = data;
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.itemVendido = itemVendido;
    }

    public Venda(Long idVenda, LocalDateTime data, Double total, EstadoVenda estado, Cliente cliente,
            Funcionario funcionario, List<ItemVendido> itemVendido) {
        this.idVenda = idVenda;
        this.data = data;
        this.total = total;
        this.estado = estado;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.itemVendido = itemVendido;
    }

    public Long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public EstadoVenda getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenda estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public List<ItemVendido> getItemVendido() {
        return itemVendido;
    }

    public void setItemVendido(List<ItemVendido> itemVendido) {
        this.itemVendido = itemVendido;
    }

    public ItemVendido adicionarItem(Produto produto, int quantidade, Double desconto) {
        exigirPendente();
        if (produto == null) {
            throw new DadoInvalidoException("O produto não pode ser nulo.");
        }

        if (quantidade <= 0) {
            throw new DadoInvalidoException("A quantidade não pode ser menor ou igual a 0.");
        }

        double valorDesconto = 0.0;
        if (desconto != null) {
            if (desconto < 0) {
                throw new DadoInvalidoException("O desconto não pode ser negativo.");
            }
            valorDesconto = desconto;
        }

        if (!produto.podeSerVendido()) {
            throw new VendaInvalidaException("O produto " + produto.getNome() + " não pode ser vendido.");
        }

        if (this.itemVendido == null) {
            this.itemVendido = new ArrayList<ItemVendido>();
        }

        ItemVendido repetido = null;
        for (int i = 0; i < this.itemVendido.size(); i++) {
            ItemVendido item = this.itemVendido.get(i);
            if (item != null && item.getProduto() != null) {
                boolean mesmoProduto = item.getProduto() == produto;
                if (!mesmoProduto && produto.getIdProduto() != null && item.getProduto().getIdProduto() != null) {
                    if (produto.getIdProduto().equals(item.getProduto().getIdProduto())) {
                        mesmoProduto = true;
                    }
                }
                if (mesmoProduto) {
                    repetido = item;
                    break;
                }
            }
        }

        if (repetido != null) {
            repetido.setQuantidade(repetido.getQuantidade() + quantidade);
            if (desconto != null) {
                repetido.setDesconto(repetido.getDesconto() + valorDesconto);
            }
            this.total = calcularTotal();
            return repetido;
        }

        Double preco = 0.0;
        if (produto.getPreco() != null) {
            preco = produto.getPreco();
        }
        ItemVendido novoItem = new ItemVendido(quantidade, preco, valorDesconto, this, produto, new ArrayList<Reembolso>());
        this.itemVendido.add(novoItem);
        this.total = calcularTotal();
        return novoItem;
    }

    private void exigirPendente() {
        if (this.estado != null && this.estado != EstadoVenda.PENDENTE) {
            throw new VendaInvalidaException("A venda não está no estado PENDENTE.");
        }
    }

    public void removerItem(ItemVendido item) {
        exigirPendente();
        if (this.itemVendido != null && item != null) {
            this.itemVendido.remove(item);
            this.total = calcularTotal();
        }
    }

    public void concluir() {
        exigirPendente();
        if (this.itemVendido == null || this.itemVendido.isEmpty()) {
            throw new VendaInvalidaException("Não é possível concluir uma venda sem itens.");
        }
        this.total = calcularTotal();
        this.estado = EstadoVenda.CONCLUIDA;
    }

    public void anular() {
        if (!podeSerAnulado()) {
            throw new VendaInvalidaException("A venda não pode ser anulada no seu estado atual.");
        }
        this.estado = EstadoVenda.ANULADA;
    }

    public Reembolso reembolsarItem(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        if (this.estado != EstadoVenda.CONCLUIDA && this.estado != EstadoVenda.REEMBOLSADA) {
            throw new VendaInvalidaException("Apenas vendas concluídas podem ter itens reembolsados.");
        }
        if (item != null && this.itemVendido != null && this.itemVendido.contains(item)) {
            Reembolso r = item.reembolsar(quantidade, motivo, funcionario);
            if (r != null) {
                boolean todosReembolsados = true;
                for (int i = 0; i < this.itemVendido.size(); i++) {
                    ItemVendido iv = this.itemVendido.get(i);
                    int qtdReemb = 0;
                    if (iv.getReembolso() != null) {
                        for (int j = 0; j < iv.getReembolso().size(); j++) {
                            Reembolso reb = iv.getReembolso().get(j);
                            if (reb != null && reb.getQuantidade() != null) {
                                qtdReemb = qtdReemb + reb.getQuantidade();
                            }
                        }
                    }
                    if (iv.getQuantidade() != null && qtdReemb < iv.getQuantidade()) {
                        todosReembolsados = false;
                        break;
                    }
                }
                if (todosReembolsados) {
                    this.estado = EstadoVenda.REEMBOLSADA;
                }
                return r;
            }
        }
        return null;
    }

    public boolean podeSerAnulado() {
        if (this.estado == EstadoVenda.PENDENTE) {
            return true;
        }
        return false;
    }

    public boolean estaConcluida() {
        if (this.estado == EstadoVenda.CONCLUIDA) {
            return true;
        }
        return false;
    }

    public Double calcularTotal() {
        double soma = 0.0;
        if (this.itemVendido != null) {
            for (int i = 0; i < this.itemVendido.size(); i++) {
                ItemVendido item = this.itemVendido.get(i);
                if (item != null) {
                    soma = soma + item.calcularSubtotal();
                }
            }
        }
        return soma;
    }

    public Double calcularTotalDesconto() {
        double totalDesc = 0.0;
        if (this.itemVendido != null) {
            for (int i = 0; i < this.itemVendido.size(); i++) {
                ItemVendido item = this.itemVendido.get(i);
                if (item != null) {
                    totalDesc = totalDesc + item.calcularValorDesconto();
                }
            }
        }
        return totalDesc;
    }

    public Double calcularTotalReembolsado() {
        double totalReemb = 0.0;
        if (this.itemVendido != null) {
            for (int i = 0; i < this.itemVendido.size(); i++) {
                ItemVendido item = this.itemVendido.get(i);
                if (item != null && item.getReembolso() != null) {
                    for (int j = 0; j < item.getReembolso().size(); j++) {
                        Reembolso r = item.getReembolso().get(j);
                        if (r != null && r.getValor() != null) {
                            totalReemb = totalReemb + r.getValor();
                        }
                    }
                }
            }
        }
        return totalReemb;
    }

    public int contarItens() {
        int totalItens = 0;
        if (this.itemVendido != null) {
            for (int i = 0; i < this.itemVendido.size(); i++) {
                ItemVendido item = this.itemVendido.get(i);
                if (item != null && item.getQuantidade() != null) {
                    totalItens = totalItens + item.getQuantidade();
                }
            }
        }
        return totalItens;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Venda outra = (Venda) obj;
        if (this.idVenda == null) {
            if (outra.idVenda != null) {
                return false;
            }
        } else {
            if (!this.idVenda.equals(outra.idVenda)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idVenda != null) {
            resultado = primo * resultado + this.idVenda.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Venda [idVenda=" + idVenda + ", data=" + data + ", total=" + total + ", estado=" + estado
                + ", cliente=" + cliente + ", funcionario=" + funcionario + ", itemVendido=" + itemVendido + "]";
    }
}
