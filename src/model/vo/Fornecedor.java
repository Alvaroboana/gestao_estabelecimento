package model.vo;

import java.time.LocalDate;
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

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Set<Produto> getProduto() {
        return produto;
    }

}
