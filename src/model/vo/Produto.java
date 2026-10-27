package model.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Produto {
    private Long idProduto;
    private String nome;
    private Double preco;
    private String descricao;
    private String unidade;
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


    public Set<Fornecedor> getFornecedor() {
        return fornecedor;
    }

    
    public void adicionarEstoque(Estoque estoque){

    }

    public void removerEstoque(Estoque estoque){

    }

    public void adicionarFornecedor(Fornecedor fornecedor){

    }

    public void removerFuncionario(Fornecedor fornecedor){

    }

    public void atualizarPreco(Double novoPreco){

    }

    public void mudarCategoria(Categoria categoria){

    }
    public void descontinuar(){

    }
    public void reativar(){

    }
    public Estoque darEntrada(int quantidade, LocalDate dataValidade){
        return null;
    }

    public void darBaixa(int quantidade){

    }

    public int calcularQuantidadeEmEstoque(){
        return 0;
    }

    public boolean itemStockSuficiente(int quantidade){
        return false;
    }

    public boolean estaAbaixoDoMinimo(){
        return false;
    }

    public boolean podeSerVendido(){
        return false;
    }

    public List<Estoque> obterLotesDisponiveis(){
        return  null;
    }

    public LocalDate obterProximaValidade(){
        return null;
    }

    @Override
    public String toString() {
        return "Produto [nome=" + nome + ", preco=" + preco + ", descricao=" + descricao + ", unidade=" + unidade
                + ", quantidadeMinima=" + quantidadeMinima + ", categoria=" + categoria + ", estoque=" + estoque
                + ", fornecedor=" + fornecedor + "]";
    }

    
}
