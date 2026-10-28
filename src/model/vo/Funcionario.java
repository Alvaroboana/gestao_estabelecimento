package model.vo;

import java.time.LocalDate;
import java.util.List;

public class Funcionario extends Pessoa {
    private Long idFuncionario;
    private String bi;
    private LocalDate dataInicio;
    private EstadoFuncionario estado;
    private Cargo cargo;
    private Endereco endereco;
    private List<Venda> venda;

    public Funcionario() {
    }

    public Funcionario(String nome, List<Contacto> contacto, String bi, LocalDate dataInicio, EstadoFuncionario estado,
            Cargo cargo, Endereco endereco, List<Venda> venda) {
        super(nome, contacto);
        this.bi = bi;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }

    public Funcionario(String nome, List<Contacto> contacto, Long idFuncionario, String bi, LocalDate dataInicio,
            EstadoFuncionario estado, Cargo cargo, Endereco endereco, List<Venda> venda) {
        super(nome, contacto);
        this.idFuncionario = idFuncionario;
        this.bi = bi;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }

    public void adicionarVenda(Venda venda) {

    }

    public void removerVenda(Venda venda) {

    }

    public void transferirCargo(Cargo novCargo) {

    }

    public void alterarEndereco(Endereco endereco) {

    }

    public void ativar() {

    }

    public void inativar() {

    }

    public void suspender() {

    }

    public boolean estadoAtivo() {
        return false;
    }

    public int calcularAntiguidadeAnos() {
        return 0;
    }

    public int calcularTotalVendasMes(int mes, int ano) {
        return 0;

    }

    public Long getIdFuncionario() {
        return idFuncionario;
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

    @Override
    public String toString() {
        return "Funcionario [bi=" + bi + ", dataInicio=" + dataInicio + ", estado=" + estado + ", cargo=" + cargo
                + ", endereco=" + endereco + ", venda=" + venda + "]";
    }

}
