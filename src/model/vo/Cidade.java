package model.vo;

import java.util.List;

public class Cidade {
    private Long idCidade;
    private  String nome;
    private List<Bairro> bairro;

    public Cidade() {
    }

    public Cidade(String nome, List<Bairro> bairro) {
        this.nome = nome;
        this.bairro = bairro;
    }

    public Cidade(Long idCidade, String nome, List<Bairro> bairro) {
        this.idCidade = idCidade;
        this.nome = nome;
        this.bairro = bairro;
    }

    public Long getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(Long idCidade) {
        this.idCidade = idCidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Bairro> getBairro() {
        return bairro;
    }

    public void adicionarBairro(Bairro bairro){

    }

    public void removerBairro(Bairro bairro){

    }

    public Bairro  buscarBairro(String nome){
        return null;
    }

    @Override
    public String toString() {
        return "Cidade [nome=" + nome + ", bairro=" + bairro + "]";
    }

    
    
    
}
