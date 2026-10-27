package model.vo;

import java.time.LocalDate;

public abstract class Pessoa {
    protected Long id;
    protected  String nome;
    protected LocalDate dataNascimento;
    protected String nuit;
    protected Contacto contacto;
    
    public Pessoa() {
    }

    public Pessoa(String nome, LocalDate dataNascimento, String nuit, Contacto contacto) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nuit = nuit;
        this.contacto = contacto;
    }

    public Pessoa(Long id, String nome, Contacto contacto) {
        this.id = id;
        this.nome = nome;
        this.contacto = contacto;
    }

    public Pessoa(Long id, String nome, LocalDate dataNascimento, String nuit, Contacto contacto) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.nuit = nuit;
        this.contacto = contacto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getNuit() {
        return nuit;
    }

    public void setNuit(String nuit) {
        this.nuit = nuit;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    @Override
    public String toString() {
        return "Pessoa [nome=" + nome + ", dataNascimento=" + dataNascimento + ", nuit=" + nuit + ", contacto="
                + contacto + "]";
    }
    
    
    
    
}
