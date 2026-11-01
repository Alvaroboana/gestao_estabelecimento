package model.vo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Funcionario extends Pessoa {
    private Long idFuncionario;
    private String bi;
    private LocalDate dataNascimento;
    private LocalDate dataInicio;
    private EstadoFuncionario estado;
    private Cargo cargo;
    private Endereco endereco;
    private List<Venda> venda;

    public Funcionario() {
    }

    public Funcionario(String nome, List<Contacto> contacto, String bi, LocalDate dataNascimento, LocalDate dataInicio,
            EstadoFuncionario estado, Cargo cargo, Endereco endereco, List<Venda> venda) {
        super(nome, contacto);
        this.bi = bi;
        this.dataNascimento = dataNascimento;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }

    public Funcionario(String nome, List<Contacto> contacto, Long idFuncionario, String bi, LocalDate dataNascimento,
            LocalDate dataInicio, EstadoFuncionario estado, Cargo cargo, Endereco endereco, List<Venda> venda) {
        super(nome, contacto);
        this.idFuncionario = idFuncionario;
        this.bi = bi;
        this.dataNascimento = dataNascimento;
        this.dataInicio = dataInicio;
        this.estado = estado;
        this.cargo = cargo;
        this.endereco = endereco;
        this.venda = venda;
    }

    public void adicionarVenda(Venda venda) {
        if (venda != null) {
            if (this.venda == null) {
                this.venda = new ArrayList<Venda>();
            }
            this.venda.add(venda);
            venda.setFuncionario(this);
        }
    }

    public void removerVenda(Venda venda) {
        if (this.venda != null && venda != null) {
            this.venda.remove(venda);
        }
    }

    public void transferirCargo(Cargo novoCargo) {
        if (novoCargo != null) {
            if (this.cargo != null) {
                this.cargo.removerFuncionario(this);
            }
            this.cargo = novoCargo;
            novoCargo.adicionarFuncionario(this);
        }
    }

    public void alterarEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public void ativar() {
        this.estado = EstadoFuncionario.ATIVO;
    }

    public void inativar() {
        this.estado = EstadoFuncionario.INATIVO;
    }

    public void suspender() {
        this.estado = EstadoFuncionario.SUSPENSO;
    }

    public boolean estadoAtivo() {
        if (this.estado == EstadoFuncionario.ATIVO) {
            return true;
        }
        return false;
    }

    public int calcularAntiguidadeAnos() {
        if (this.dataInicio != null) {
            long anos = ChronoUnit.YEARS.between(this.dataInicio, LocalDate.now());
            return (int) anos;
        }
        return 0;
    }

    public int calcularTotalVendasMes(int mes, int ano) {
        int total = 0;
        if (this.venda != null) {
            for (int i = 0; i < this.venda.size(); i++) {
                Venda v = this.venda.get(i);
                if (v != null && v.getData() != null) {
                    if (v.getData().getMonthValue() == mes && v.getData().getYear() == ano) {
                        total = total + 1;
                    }
                }
            }
        }
        return total;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public void setVenda(List<Venda> venda) {
        this.venda = venda;
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
        Funcionario outro = (Funcionario) obj;
        if (this.idFuncionario == null) {
            if (outro.idFuncionario != null) {
                return false;
            }
        } else {
            if (!this.idFuncionario.equals(outro.idFuncionario)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idFuncionario != null) {
            resultado = primo * resultado + this.idFuncionario.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Funcionario [idFuncionario=" + idFuncionario + ", nome=" + nome + ", bi=" + bi + ", dataNascimento="
                + dataNascimento + ", dataInicio=" + dataInicio + ", estado=" + estado + ", cargo=" + cargo + "]";
    }
}
