package model.vo;

import java.time.LocalDate;
import java.util.List;

public abstract class Pessoa {
   protected String nome;
   protected List<Contacto> contacto;

   
   public Pessoa() {
   }


   public Pessoa(String nome, List<Contacto> contacto) {
    this.nome = nome;
    this.contacto = contacto;
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


   public void adicionarContacto(Contacto contacto){

   }

   public void removerContacto(Contacto contacto){
    
   }


   @Override
   public String toString() {
    return "Pessoa [nome=" + nome + ", contacto=" + contacto + "]";
   }

   
    
}
