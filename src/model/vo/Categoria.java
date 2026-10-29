package model.vo;

import java.io.Serializable;
import java.util.List;

public class Categoria implements Serializable {
    private Long idCategoria;
    private String nome;
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

    public void setProduto(List<Produto> produto) {
        this.produto = produto;
    }

    public void adicionarProduto(Produto produto) {

    }

    public void removerProduto(Produto produto) {

    }

    public int contarProduto() {
        return 0;
    }

    public boolean podeSerRemovida() {
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
        Categoria outra = (Categoria) obj;
        if (this.idCategoria == null) {
            if (outra.idCategoria != null) {
                return false;
            }
        } else {
            if (!this.idCategoria.equals(outra.idCategoria)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idCategoria != null) {
            resultado = primo * resultado + this.idCategoria.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Categoria [idCategoria=" + idCategoria + ", nome=" + nome + "]";
    }
}
