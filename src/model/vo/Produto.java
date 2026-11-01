package model.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
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
        if (estoque != null) {
            if (this.estoque == null) {
                this.estoque = new ArrayList<Estoque>();
            }
            this.estoque.add(estoque);
            estoque.setProduto(this);
        }
    }

    public void removerEstoque(Estoque estoque) {
        if (this.estoque != null && estoque != null) {
            this.estoque.remove(estoque);
        }
    }

    public void adicionarFornecedor(Fornecedor fornecedor) {
        if (fornecedor != null) {
            if (this.fornecedor == null) {
                this.fornecedor = new HashSet<Fornecedor>();
            }
            this.fornecedor.add(fornecedor);
        }
    }

    public void removerFornecedor(Fornecedor fornecedor) {
        if (this.fornecedor != null && fornecedor != null) {
            this.fornecedor.remove(fornecedor);
        }
    }

    public void atualizarPreco(Double novoPreco) {
        if (novoPreco != null && novoPreco >= 0) {
            this.preco = novoPreco;
        }
    }

    public void mudarCategoria(Categoria categoria) {
        if (categoria != null) {
            if (this.categoria != null) {
                this.categoria.removerProduto(this);
            }
            this.categoria = categoria;
            categoria.adicionarProduto(this);
        }
    }

    public void descontinuar() {
        this.estado = EstadoProduto.DESCONTINUADO;
    }

    public void reativar() {
        this.estado = EstadoProduto.ATIVO;
    }

    public Estoque darEntrada(int quantidade, LocalDate dataValidade) {
        if (quantidade > 0) {
            if (this.estoque == null) {
                this.estoque = new ArrayList<Estoque>();
            }
            Estoque novoEstoque = new Estoque(quantidade, LocalDate.now(), dataValidade, this, null);
            this.estoque.add(novoEstoque);
            return novoEstoque;
        }
        return null;
    }

    public void darBaixa(int quantidade) {
        if (quantidade > 0 && this.estoque != null) {
            int restante = quantidade;
            for (int i = 0; i < this.estoque.size(); i++) {
                Estoque lote = this.estoque.get(i);
                if (lote != null && lote.getQuantidade() != null && lote.getQuantidade() > 0) {
                    if (lote.getQuantidade() <= restante) {
                        restante = restante - lote.getQuantidade();
                        lote.setQuantidade(0);
                    } else {
                        lote.setQuantidade(lote.getQuantidade() - restante);
                        restante = 0;
                    }
                    if (restante == 0) {
                        break;
                    }
                }
            }
        }
    }

    public int calcularQuantidadeEmEstoque() {
        int total = 0;
        if (this.estoque != null) {
            for (int i = 0; i < this.estoque.size(); i++) {
                Estoque lote = this.estoque.get(i);
                if (lote != null && lote.getQuantidade() != null && lote.getQuantidade() > 0) {
                    total = total + lote.getQuantidade();
                }
            }
        }
        return total;
    }

    public boolean itemStockSuficiente(int quantidade) {
        if (quantidade > 0 && calcularQuantidadeEmEstoque() >= quantidade) {
            return true;
        }
        return false;
    }

    public boolean estaAbaixoDoMinimo() {
        if (this.quantidadeMinima != null) {
            if (calcularQuantidadeEmEstoque() < this.quantidadeMinima) {
                return true;
            }
        }
        return false;
    }

    public boolean podeSerVendido() {
        if (this.estado == EstadoProduto.ATIVO && calcularQuantidadeEmEstoque() > 0) {
            return true;
        }
        return false;
    }

    public List<Estoque> obterLotesDisponiveis() {
        List<Estoque> disponiveis = new ArrayList<Estoque>();
        if (this.estoque != null) {
            for (int i = 0; i < this.estoque.size(); i++) {
                Estoque lote = this.estoque.get(i);
                if (lote != null && lote.getQuantidade() != null && lote.getQuantidade() > 0) {
                    if (!lote.isExpirado()) {
                        disponiveis.add(lote);
                    }
                }
            }
        }
        return disponiveis;
    }

    public LocalDate obterProximaValidade() {
        LocalDate proxima = null;
        if (this.estoque != null) {
            for (int i = 0; i < this.estoque.size(); i++) {
                Estoque lote = this.estoque.get(i);
                if (lote != null && lote.getDataValidade() != null && lote.getQuantidade() != null && lote.getQuantidade() > 0) {
                    if (proxima == null) {
                        proxima = lote.getDataValidade();
                    } else {
                        if (lote.getDataValidade().isBefore(proxima)) {
                            proxima = lote.getDataValidade();
                        }
                    }
                }
            }
        }
        return proxima;
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
