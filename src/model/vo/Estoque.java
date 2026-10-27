package model.vo;

import java.time.LocalDate;

public class Estoque {
    private Long idEstoque;
    private Integer quantidade;
    private LocalDate dataEntrada;
    private LocalDate dataValidade;
    private Produto produto;


    public Estoque() {
    }


    public Estoque(Integer quantidade, LocalDate dataEntrada, LocalDate dataValidade, Produto produto) {
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
        this.produto = produto;
    }


    public Estoque(Long idEstoque, Integer quantidade, LocalDate dataEntrada, LocalDate dataValidade, Produto produto) {
        this.idEstoque = idEstoque;
        this.quantidade = quantidade;
        this.dataEntrada = dataEntrada;
        this.dataValidade = dataValidade;
        this.produto = produto;
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

    public void repor(Integer quantidade){

    }
    public void retirar(Integer quantidade){

    }

    public boolean estaVazio(){
        return false;
    }

    public boolean isExpiredo(){
        return false;
    }

    public int calcularDiasParaExpirar(){
        return 0;
    }


    @Override
    public String toString() {
        return "Estoque [quantidade=" + quantidade + ", dataEntrada=" + dataEntrada + ", dataValidade=" + dataValidade
                + ", produto=" + produto + "]";
    }

    
}
