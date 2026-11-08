package model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Pessoa implements Serializable {
    protected Long idPessoa;
    protected String nome;
    protected List<Contacto> contacto;

    public Pessoa() {
    }

    public Pessoa(String nome, List<Contacto> contacto) {
        this.nome = nome;
        this.contacto = contacto;
    }

    public Pessoa(Long idPessoa, String nome, List<Contacto> contacto) {
        this.idPessoa = idPessoa;
        this.nome = nome;
        this.contacto = contacto;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Contacto> getContacto() {
        return contacto;
    }

    public void setContacto(List<Contacto> contacto) {
        if (contacto != null) {
            this.contacto = contacto;
        } else {
            this.contacto = new ArrayList<>();
        }
    }

    public void adicionarContacto(Contacto novoContacto) {
        if (this.contacto == null) {
            this.contacto = new ArrayList<>();
        }
        this.contacto.add(novoContacto);
    }

    public void removerContacto(Contacto velhoContacto) {
        if (this.contacto != null) {
            if (velhoContacto != null) {
                this.contacto.remove(velhoContacto);
            }
        }
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
        Pessoa outra = (Pessoa) obj;
        if (this.idPessoa == null) {
            if (outra.idPessoa != null) {
                return false;
            }
        } else {
            if (!this.idPessoa.equals(outra.idPessoa)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idPessoa != null) {
            resultado = primo * resultado + this.idPessoa.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Pessoa [idPessoa=" + idPessoa + ", nome=" + nome + ", contacto=" + contacto + "]";
    }
}
