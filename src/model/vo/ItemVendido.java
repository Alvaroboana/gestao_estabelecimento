package model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemVendido implements Serializable {
    private Long idItem;
    private Integer quantidade;
    private Double preco;
    private Double desconto;
    private Venda venda;
    private Produto produto;
    private List<Reembolso> reembolso;

    public ItemVendido() {
    }

    public ItemVendido(Integer quantidade, Double preco, Double desconto, Venda venda, Produto produto,
            List<Reembolso> reembolso) {
        this.quantidade = quantidade;
        this.preco = preco;
        this.desconto = desconto;
        this.venda = venda;
        this.produto = produto;
        this.reembolso = reembolso;
    }

    public ItemVendido(Long idItem, Integer quantidade, Double preco, Double desconto, Venda venda, Produto produto,
            List<Reembolso> reembolso) {
        this.idItem = idItem;
        this.quantidade = quantidade;
        this.preco = preco;
        this.desconto = desconto;
        this.venda = venda;
        this.produto = produto;
        this.reembolso = reembolso;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Reembolso> getReembolso() {
        return reembolso;
    }

    public void setReembolso(List<Reembolso> reembolso) {
        this.reembolso = reembolso;
    }

    public void alterarQuantidade(int quantidade) {
        if (quantidade > 0) {
            this.quantidade = quantidade;
        }
    }

    public void aplicarDesconto(Double desconto) {
        if (desconto != null && desconto >= 0) {
            this.desconto = desconto;
        }
    }

    public void trocarProduto(Produto produto) {
        if (produto != null) {
            this.produto = produto;
            if (produto.getPreco() != null) {
                this.preco = produto.getPreco();
            }
        }
    }

    public Reembolso reembolsar(int quantidade, String motivo, Funcionario funcionario) {
        if (quantidade > 0 && this.quantidade != null && quantidade <= this.quantidade) {
            double valorReembolso = 0.0;
            if (this.preco != null) {
                valorReembolso = quantidade * this.preco;
                if (this.desconto != null && this.desconto > 0 && this.quantidade > 0) {
                    valorReembolso = valorReembolso - ((this.desconto / this.quantidade) * quantidade);
                }
            }
            if (valorReembolso < 0) {
                valorReembolso = 0.0;
            }
            Reembolso r = new Reembolso(quantidade, valorReembolso, motivo, LocalDateTime.now(), this, funcionario);
            if (this.reembolso == null) {
                this.reembolso = new ArrayList<Reembolso>();
            }
            this.reembolso.add(r);
            return r;
        }
        return null;
    }

    public Double calcularSubtotal() {
        double total = 0.0;
        if (this.quantidade != null && this.preco != null) {
            total = this.quantidade * this.preco;
        }
        double desc = calcularValorDesconto();
        total = total - desc;
        if (total < 0) {
            total = 0.0;
        }
        return total;
    }

    public Double calcularValorDesconto() {
        if (this.desconto != null && this.desconto > 0) {
            return this.desconto;
        }
        return 0.0;
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
        ItemVendido outro = (ItemVendido) obj;
        if (this.idItem == null) {
            if (outro.idItem != null) {
                return false;
            }
        } else {
            if (!this.idItem.equals(outro.idItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idItem != null) {
            resultado = primo * resultado + this.idItem.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "ItemVendido [idItem=" + idItem + ", quantidade=" + quantidade + ", preco=" + preco + "]";
    }
}
