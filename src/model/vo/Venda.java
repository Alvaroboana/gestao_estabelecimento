package model.vo;

import java.time.LocalDateTime;
import java.util.List;

public class Venda {
private Long idVenda;
private LocalDateTime data;
private EstadoVenda estado;
private Cliente Cliente;
private Funcionario funcionario;
private List<ItemVendido> itemVendido;


public Venda() {
}


public Venda(LocalDateTime data, EstadoVenda estado, model.vo.Cliente cliente, Funcionario funcionario,
        List<ItemVendido> itemVendido) {
    this.data = data;
    this.estado = estado;
    Cliente = cliente;
    this.funcionario = funcionario;
    this.itemVendido = itemVendido;
}


public Venda(Long idVenda, LocalDateTime data, EstadoVenda estado, model.vo.Cliente cliente, Funcionario funcionario,
        List<ItemVendido> itemVendido) {
    this.idVenda = idVenda;
    this.data = data;
    this.estado = estado;
    Cliente = cliente;
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


public EstadoVenda getEstado() {
    return estado;
}


public void setEstado(EstadoVenda estado) {
    this.estado = estado;
}


public Cliente getCliente() {
    return Cliente;
}


public void setCliente(Cliente cliente) {
    Cliente = cliente;
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

public ItemVendido adicionarItem(Produto produto, int quantidade, Double desconto){
    return null;
}

public void removerItem(ItemVendido item){

}

public void comcluir(){

}

public void anular(){

}

public Reembolso ReembolsarItem(ItemVendido item, int quantidade,String motivo, Funcionario funcionario){
    return null;
}

public boolean podeSerAnulado(){
    return false;
}

public boolean estaComcluida(){
    return false;
}

public Double calcularTotal(){
    return 0.0;
}

public Double calcularTotalDesconto(){
    return 0.0;
}

public Double calclarTotalReembolsado(){
    return 0.0;
}

public int contarItens(){
    return 0;
}


}
