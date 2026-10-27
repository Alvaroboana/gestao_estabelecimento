package model.vo;

import java.time.LocalDate;
import java.util.Set;

public class Fornecedor {
private Long idFornecedor;
private Endereco endereco;
private Set<Produto> produto;

public Fornecedor() {
}

public Fornecedor(Endereco endereco, Set<Produto> produto) {
    this.endereco = endereco;
    this.produto = produto;
}

public Fornecedor(Long idFornecedor, Endereco endereco, Set<Produto> produto) {
    this.idFornecedor = idFornecedor;
    this.endereco = endereco;
    this.produto = produto;
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


public void adicionarProduto(Produto produto){

}

public void removerProduto(Produto produto){

}

public boolean fornece(Produto produto){
    return false;
}

public void alterarEndereco(Endereco endereco){

}

}
