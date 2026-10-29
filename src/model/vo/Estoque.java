package model.vo;

import java.io.Serializable;
import java.time.LocalDate;

public class Estoque implements Serializable {
    private Long idEstoque;
    private Integer quantidade;
    private LocalDate dataEntrada;
    private LocalDate dataValidade;
    private Produto produto;
    private Funcionario funcionario;

    public Estoque() {
    }

    public Estoque(Integer quantidade, LocalDate dataEntrada, LocalDate dataValidade, Produto produto,
            Funcionario funcionario) {
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
        this.produto = produto;
        this.funcionario = funcionario;
    }

    public Estoque(Long idEstoque, Integer quantidade, LocalDate dataEntrada, LocalDate dataValidade, Produto produto,
            Funcionario funcionario) {
        this.idEstoque = idEstoque;
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
        this.produto = produto;
        this.funcionario = funcionario;
    }

    public Long getIdEstoque() {
        return idEstoque;
    }

    public void setIdEstoque(Long idEstoque) {
        this.idEstoque = idEstoque;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public void repor(Integer quantidade) {

    }

    public void retirar(Integer quantidade) {

    }

    public boolean estaVazio() {
        return false;
    }

    public boolean isExpirado() {
        return false;
    }

    public boolean estaExpirado() {
        return isExpirado();
    }

    public int calcularDiasParaExpirar() {
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
        Estoque outro = (Estoque) obj;
        if (this.idEstoque == null) {
            if (outro.idEstoque != null) {
                return false;
            }
        } else {
            if (!this.idEstoque.equals(outro.idEstoque)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idEstoque != null) {
            resultado = primo * resultado + this.idEstoque.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Estoque [idEstoque=" + idEstoque + ", quantidade=" + quantidade + ", dataEntrada=" + dataEntrada
                + ", dataValidade=" + dataValidade + ", produto=" + produto + ", funcionario=" + funcionario + "]";
    }
}
