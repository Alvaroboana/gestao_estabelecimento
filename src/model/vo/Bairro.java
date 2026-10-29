package model.vo;

import java.io.Serializable;
import java.util.List;

public class Bairro implements Serializable {
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

    public void setEndereco(List<Endereco> endereco) {
        this.endereco = endereco;
    }

    public void adicionarEndereco(Endereco endereco) {

    }

    public void removerEndereco(Endereco endereco) {

    }

    public String obterNomeCompleto() {
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
        Bairro outro = (Bairro) obj;
        if (this.idBairro == null) {
            if (outro.idBairro != null) {
                return false;
            }
        } else {
            if (!this.idBairro.equals(outro.idBairro)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idBairro != null) {
            resultado = primo * resultado + this.idBairro.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Bairro [idBairro=" + idBairro + ", nome=" + nome + ", cidade=" + cidade + "]";
    }

    public String getCep() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCep'");
    }
}
