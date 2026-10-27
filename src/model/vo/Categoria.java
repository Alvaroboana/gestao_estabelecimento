package model.vo;

import java.util.List;

public class Categoria {
    private Long idCategoria;
    private  String nome;
    private List<Produto> produto;


    public Categoria() {
    }


    public Categoria(String nome, List<Produto> produto) {
        this.nome = nome;
        this.produto = produto;
    }


    public Categoria(Long idCategoria, String nome, List<Produto> produto) {
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.produto = produto;
    }


    public Long getIdCategoria() {
        return idCategoria;
    }


    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }


    public String getNome() {
        return nome;
    }


    public void setNome(String nome) {
        this.nome = nome;
    }


    public List<Produto> getProduto() {
        return produto;
    }


   public void adicionarProduto(Produto produto){

   }

    public void removerProduto(Produto produto){

    }

    public int contarProduto(){
        return 0;
    }

    public boolean podeSerRemovida(){
        return false;
    }
    
}
