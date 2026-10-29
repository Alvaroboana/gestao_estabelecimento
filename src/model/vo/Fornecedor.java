package model.vo;


import java.util.List;
import java.util.Set;

public class Fornecedor extends Pessoa {
    private Long idFornecedor;
    private Endereco endereco;
    private Set<Produto> produto;

    public Fornecedor() {
    }

    public Fornecedor(String nome, List<Contacto> contacto, Endereco endereco, Set<Produto> produto) {
        super(nome, contacto);
        this.endereco = endereco;
        this.produto = produto;
    }

    public Fornecedor(String nome, List<Contacto> contacto, Long idFornecedor, Endereco endereco,
            Set<Produto> produto) {
        super(nome, contacto);
        this.idFornecedor = idFornecedor;
        this.endereco = endereco;
        this.produto = produto;
    }

    public void adicionarProduto(Produto produto) {

    }

    public void removerProduto(Produto produto) {

    }

    public boolean fornece(Produto produto) {
        return false;
    }

    public void alterarEndereco(Endereco endereco) {

    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Set<Produto> getProduto() {
        return produto;
    }

    public void setProduto(Set<Produto> produto) {
        this.produto = produto;
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
        Fornecedor outro = (Fornecedor) obj;
        if (this.idFornecedor == null) {
            if (outro.idFornecedor != null) {
                return false;
            }
        } else {
            if (!this.idFornecedor.equals(outro.idFornecedor)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idFornecedor != null) {
            resultado = primo * resultado + this.idFornecedor.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Fornecedor [idFornecedor=" + idFornecedor + ", nome=" + nome + ", endereco=" + endereco + "]";
    }
}
