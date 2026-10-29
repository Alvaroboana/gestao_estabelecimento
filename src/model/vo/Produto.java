package model.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Produto implements Serializable {
    private Long idProduto;
    private String nome;
    private Double preco;
    private String descricao;
    private String unidade;
    private EstadoProduto estado;
    private Integer quantidadeMinima;
    private Categoria categoria;
    private List<Estoque> estoque;
    private Set<Fornecedor> fornecedor;

    public Produto() {
    }

    public Produto(String nome, Double preco, String descricao, String unidade, Integer quantidadeMinima,
            Categoria categoria, List<Estoque> estoque, Set<Fornecedor> fornecedor) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.unidade = unidade;
        this.quantidadeMinima = quantidadeMinima;
        this.categoria = categoria;
        this.estoque = estoque;
        this.fornecedor = fornecedor;
        this.estado = EstadoProduto.ATIVO;
    }

    public Produto(Long idProduto, String nome, Double preco, String descricao, String unidade,
            Integer quantidadeMinima, Categoria categoria, List<Estoque> estoque, Set<Fornecedor> fornecedor) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.unidade = unidade;
        this.quantidadeMinima = quantidadeMinima;
        this.categoria = categoria;
        this.estoque = estoque;
        this.fornecedor = fornecedor;
        this.estado = EstadoProduto.ATIVO;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public EstadoProduto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProduto estado) {
        this.estado = estado;
    }

    public Integer getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(Integer quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Estoque> getEstoque() {
        return estoque;
    }

    public void setEstoque(List<Estoque> estoque) {
        this.estoque = estoque;
    }

    public Set<Fornecedor> getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Set<Fornecedor> fornecedor) {
        this.fornecedor = fornecedor;
    }

    public void adicionarEstoque(Estoque estoque) {

    }

    public void removerEstoque(Estoque estoque) {

    }

    public void adicionarFornecedor(Fornecedor fornecedor) {

    }

    public void removerFornecedor(Fornecedor fornecedor) {

    }

    public void atualizarPreco(Double novoPreco) {

    }

    public void mudarCategoria(Categoria categoria) {

    }

    public void descontinuar() {

    }

    public void reativar() {

    }

    public Estoque darEntrada(int quantidade, LocalDate dataValidade) {
        return null;
    }

    public void darBaixa(int quantidade) {

    }

    public int calcularQuantidadeEmEstoque() {
        return 0;
    }

    public boolean itemStockSuficiente(int quantidade) {
        return false;
    }

    public boolean estaAbaixoDoMinimo() {
        return false;
    }

    public boolean podeSerVendido() {
        return false;
    }

    public List<Estoque> obterLotesDisponiveis() {
        return null;
    }

    public LocalDate obterProximaValidade() {
        return null;
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
        Produto outro = (Produto) obj;
        if (this.idProduto == null) {
            if (outro.idProduto != null) {
                return false;
            }
        } else {
            if (!this.idProduto.equals(outro.idProduto)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idProduto != null) {
            resultado = primo * resultado + this.idProduto.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Produto [idProduto=" + idProduto + ", nome=" + nome + ", preco=" + preco + ", estado=" + estado
                + ", unidade=" + unidade + ", quantidadeMinima=" + quantidadeMinima + "]";
    }
}
