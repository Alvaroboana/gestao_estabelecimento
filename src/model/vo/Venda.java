package model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
        return null;
    }

    public void removerItem(ItemVendido item) {

    }

    public void concluir() {

    }

    public void anular() {

    }

    public Reembolso reembolsarItem(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        return null;
    }

    public boolean podeSerAnulado() {
        return false;
    }

    public boolean estaConcluida() {
        return false;
    }

    public Double calcularTotal() {
        return 0.0;
    }

    public Double calcularTotalDesconto() {
        return 0.0;
    }

    public Double calcularTotalReembolsado() {
        return 0.0;
    }

    public int contarItens() {
        return 0;
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
