package model.vo;

import java.util.List;

public class Cargo {
private Long idCargo;
private String nome;
private Double salarioBase;
private List<Funcionario> funcionario;

public Cargo() {
}

public Cargo(String nome, Double salarioBase, List<Funcionario> funcionario) {
    this.nome = nome;
    this.salarioBase = salarioBase;
    this.funcionario = funcionario;
}

public Cargo(Long idCargo, String nome, Double salarioBase, List<Funcionario> funcionario) {
    this.idCargo = idCargo;
    this.nome = nome;
    this.salarioBase = salarioBase;
    this.funcionario = funcionario;
}

public Long getIdCargo() {
    return idCargo;
}

public void setIdCargo(Long idCargo) {
    this.idCargo = idCargo;
}

public String getNome() {
    return nome;
}

public void setNome(String nome) {
    this.nome = nome;
}

public Double getSalarioBase() {
    return salarioBase;
}

public void setSalarioBase(Double salarioBase) {
    this.salarioBase = salarioBase;
}

public List<Funcionario> getFuncionario() {
    return funcionario;
}

public void setFuncionario(List<Funcionario> funcionario) {
    this.funcionario = funcionario;
}

public void adicionarFuncionario(Funcionario funcionario){

}
public void removerFuncionario(){

}
private void alterarSalarioBase(){

}

public int contarFuncionarios(){
    return 0;
}

public boolean podeSerRemovido(){
    return false;
}

}
