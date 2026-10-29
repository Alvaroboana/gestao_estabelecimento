package model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reembolso implements Serializable {
    private Long idReembolso;
    private Integer quantidade;
    private Double valor;
    private String motivo;
    private LocalDateTime data;
    private ItemVendido item;
    private Funcionario funcionario;

    public Reembolso() {
    }

    public Reembolso(Integer quantidade, Double valor, String motivo, LocalDateTime data, ItemVendido item,
            Funcionario funcionario) {
        this.quantidade = quantidade;
        this.valor = valor;
        this.motivo = motivo;
        this.data = data;
        this.item = item;
        this.funcionario = funcionario;
    }

    public Reembolso(Long idReembolso, Integer quantidade, Double valor, String motivo, LocalDateTime data,
            ItemVendido item, Funcionario funcionario) {
        this.idReembolso = idReembolso;
        this.quantidade = quantidade;
        this.valor = valor;
        this.motivo = motivo;
        this.data = data;
        this.item = item;
        this.funcionario = funcionario;
    }

    public Long getIdReembolso() {
        return idReembolso;
    }

    public void setIdReembolso(Long idReembolso) {
        this.idReembolso = idReembolso;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public ItemVendido getItem() {
        return item;
    }

    public void setItem(ItemVendido item) {
        this.item = item;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public boolean isParcial() {
        return false;
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
        Reembolso outro = (Reembolso) obj;
        if (this.idReembolso == null) {
            if (outro.idReembolso != null) {
                return false;
            }
        } else {
            if (!this.idReembolso.equals(outro.idReembolso)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idReembolso != null) {
            resultado = primo * resultado + this.idReembolso.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Reembolso [idReembolso=" + idReembolso + ", quantidade=" + quantidade + ", valor=" + valor
                + ", motivo=" + motivo + ", data=" + data + ", item=" + item + ", funcionario=" + funcionario + "]";
    }
}
