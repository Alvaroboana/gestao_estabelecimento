package model.vo;

import java.io.Serializable;
import java.util.List;

public class Cargo implements Serializable {
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

    public void adicionarFuncionario(Funcionario funcionario) {

    }

    public void removerFuncionario(Funcionario funcionario) {

    }

    public void alterarSalarioBase(Double novoSalario) {

    }

    public int contarFuncionarios() {
        return 0;
    }

    public boolean podeSerRemovido() {
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
        Cargo outro = (Cargo) obj;
        if (this.idCargo == null) {
            if (outro.idCargo != null) {
                return false;
            }
        } else {
            if (!this.idCargo.equals(outro.idCargo)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idCargo != null) {
            resultado = primo * resultado + this.idCargo.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Cargo [idCargo=" + idCargo + ", nome=" + nome + ", salarioBase=" + salarioBase + "]";
    }
}
