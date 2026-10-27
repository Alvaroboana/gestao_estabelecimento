package model.vo;

import java.util.List;

public class Cliente {
private Long idCliente;
private List<Venda> venda;

public Cliente() {
}

public Cliente(List<Venda> venda) {
    this.venda = venda;
}

public Cliente(Long idCliente, List<Venda> venda) {
    this.idCliente = idCliente;
    this.venda = venda;
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

public void adicionarVenda(Venda venda){

}

public void removerVenda(){

}

public Double calcularTotalCompras(){
    return 0.0;
}

public Venda obterUltimaCompra(){
    return null;
}

@Override
public String toString() {
    return "Cliente [venda=" + venda + "]";
}



}
