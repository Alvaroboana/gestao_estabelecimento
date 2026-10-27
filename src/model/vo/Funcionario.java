package model.vo;


import java.time.LocalDate;
import java.util.List;

public class Funcionario {
    private Long idFuncionario;
    private String bi;
    private LocalDate dataInicio;
    private EstadoFuncionario estado;
    private Cargo cargo;
    private Endereco endereco;
    private List<Venda> venda;

    
    public Funcionario() {
    }


    public Funcionario(String bi, LocalDate dataInicio, EstadoFuncionario estado, Cargo cargo, Endereco endereco,
            List<Venda> venda) {
        this.bi = bi;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }


    public Funcionario(Long idFuncionario, String bi, LocalDate dataInicio, EstadoFuncionario estado, Cargo cargo,
            Endereco endereco, List<Venda> venda) {
        this.idFuncionario = idFuncionario;
        this.bi = bi;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }


    public Long getIdFuncionario() {
        return idFuncionario;
    }


    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }


    public String getBi() {
        return bi;
    }


    public void setBi(String bi) {
        this.bi = bi;
    }


    public LocalDate getDataInicio() {
        return dataInicio;
    }


    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }


    public EstadoFuncionario getEstado() {
        return estado;
    }


    public void setEstado(EstadoFuncionario estado) {
        this.estado = estado;
    }


    public Cargo getCargo() {
        return cargo;
    }


    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }


    public Endereco getEndereco() {
        return endereco;
    }


    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }


    public List<Venda> getVenda() {
        return venda;
    }


   public void adicionarVenda(Venda venda){

   }

   public void removerVenda(Venda venda){

   }

   public void transferirCargo(Cargo novCargo){

   }

   public void alterarEndereco(Endereco endereco){

   }

   public void ativar(){

   }
   public void inativar(){

   }

   public void suspender(){

   }

   public boolean estadoAtivo(){
    return false;
   }

   public int calcularAntiguidadeAnos(){
    return 0;
   }

   public int calcularTotalVendasMes(int mes, int ano){
    return 0;

   }


   @Override
   public String toString() {
    return "Funcionario [bi=" + bi + ", dataInicio=" + dataInicio + ", estado=" + estado + ", cargo=" + cargo
            + ", endereco=" + endereco + ", venda=" + venda + "]";
   }
    

   
}
