package model.vo;

import java.util.List;

public class Bairro {
    private Long idBairro;
    private String nome;
    private Cidade cidade;
    private List<Endereco> endereco;


    public Bairro() {
    }


    public Bairro(String nome, Cidade cidade, List<Endereco> endereco) {
        this.nome = nome;
        this.cidade = cidade;
        this.endereco = endereco;
    }


    public Bairro(Long idBairro, String nome, Cidade cidade, List<Endereco> endereco) {
        this.idBairro = idBairro;
        this.nome = nome;
        this.cidade = cidade;
        this.endereco = endereco;
    }


    public Long getIdBairro() {
        return idBairro;
    }


    public void setIdBairro(Long idBairro) {
        this.idBairro = idBairro;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public Cidade getCidade() {
        return cidade;
    }


    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }


    public List<Endereco> getEndereco() {
        return endereco;
    }


   public void adicionarEndereco(Endereco endereco){

   }

   public void removerEndereco(Endereco endereco){

   }
    public String obterNomeComple(){
        return null;
    }
    
}
