package model.vo;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Pessoa {
    private Long idCliente;
    private List<Venda> venda;

    public Cliente() {
    }

    public Cliente(String nome, List<Contacto> contacto, List<Venda> venda) {
        super(nome, contacto);
        this.venda = venda;
    }

    public Cliente(String nome, List<Contacto> contacto, Long idCliente, List<Venda> venda) {
        super(nome, contacto);
        this.idCliente = idCliente;
        this.venda = venda;
    }

    public void adicionarVenda(Venda novaVenda) {
        if (novaVenda != null) {
            if (this.venda == null) {
                this.venda = new ArrayList<>();
            }

            this.venda.add(novaVenda);
            novaVenda.setCliente(this);
        }
    }

    public void removerVenda(Venda velhaVenda) {
        if (this.venda != null) {
            if (velhaVenda != null) {
                this.venda.remove(velhaVenda);
            }
        }

    }

    public Double calcularTotalCompras() {
        if (this.venda == null) {
            return 0.0;
        }

        double totalG = 0.0;
        for (int i = 0; i < this.venda.size(); i++) {
            Venda venda = this.venda.get(i);
            if (venda.getEstado() == EstadoVenda.CONCLUIDA) {
                if (venda.getTotal() != null) {
                    totalG += venda.getTotal();
                } else {
                    totalG += venda.calcularTotal();
                }
            }
        }
        return totalG;
    }

    public Venda obterUltimaCompra() {
        if (this.venda == null) {
            return null;

        }
        if (this.venda.isEmpty()) {
            return null;
        }

        Venda recente = null;
        for (int i = 0; i < this.venda.size(); i++) {
            Venda venda = this.venda.get(i);
            if (venda.getData() != null) {
                if (recente == null) {
                    recente = venda;
                } else {
                    if (venda.getData().isAfter(recente.getData())) {
                        recente = venda;
                    }
                }
            }
        }

        return recente;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public List<Venda> getVenda() {
        return venda;
    }

    public void setVenda(List<Venda> venda) {
        this.venda = venda;
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
        Cliente outro = (Cliente) obj;
        if (this.idCliente == null) {
            if (outro.idCliente != null) {
                return false;
            }
        } else {
            if (!this.idCliente.equals(outro.idCliente)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idCliente != null) {
            resultado = primo * resultado + this.idCliente.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Cliente [idCliente=" + idCliente + ", nome=" + nome + ", venda=" + venda + "]";
    }
}
