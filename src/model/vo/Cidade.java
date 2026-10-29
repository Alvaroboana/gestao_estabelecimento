package model.vo;

import java.io.Serializable;
import java.util.List;

public class Cidade implements Serializable {
    private Long idCidade;
    private String nome;
    private List<Bairro> bairro;

    public Cidade() {
    }

    public Cidade(String nome, List<Bairro> bairro) {
        this.nome = nome;
        this.bairro = bairro;
    }

    public Cidade(Long idCidade, String nome, List<Bairro> bairro) {
        this.idCidade = idCidade;
        this.nome = nome;
        this.bairro = bairro;
    }

    public Long getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(Long idCidade) {
        this.idCidade = idCidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Bairro> getBairro() {
        return bairro;
    }

    public void setBairro(List<Bairro> bairro) {
        this.bairro = bairro;
    }

    public void adicionarBairro(Bairro bairro) {

    }

    public void removerBairro(Bairro bairro) {

    }

    public Bairro buscarBairro(String nome) {
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
        Cidade outra = (Cidade) obj;
        if (this.idCidade == null) {
            if (outra.idCidade != null) {
                return false;
            }
        } else {
            if (!this.idCidade.equals(outra.idCidade)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idCidade != null) {
            resultado = primo * resultado + this.idCidade.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Cidade [idCidade=" + idCidade + ", nome=" + nome + ", bairro=" + bairro + "]";
    }
}
