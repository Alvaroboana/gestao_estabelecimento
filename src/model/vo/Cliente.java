package model.vo;

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

    public void adicionarVenda(Venda venda) {

    }

    public void removerVenda() {

    }

    public Double calcularTotalCompras() {
        return 0.0;
    }

    public Venda obterUltimaCompra() {
        return null;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public List<Venda> getVenda() {
        return venda;
    }

    @Override
    public String toString() {
        return "Cliente [venda=" + venda + "]";
    }

}
