package model.vo;

import java.time.LocalDateTime;

public class Reembolso {
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

public boolean isParcial(){
    return false;
}

@Override
public String toString() {
    return "Reembolso [quantidade=" + quantidade + ", valor=" + valor + ", motivo=" + motivo + ", data=" + data
            + ", item=" + item + ", funcionario=" + funcionario + "]";
}


}
