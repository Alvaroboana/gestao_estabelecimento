package model.vo;

import java.io.Serializable;

public class Utilizador implements Serializable {
    private Long idUtilizador;
    private String username;
    private String passwordHash;
    private Perfil perfil;
    private Funcionario funcionario;

    public Utilizador() {
    }

    public Utilizador(String username, String passwordHash, Perfil perfil, Funcionario funcionario) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.funcionario = funcionario;
    }

    public Utilizador(Long idUtilizador, String username, String passwordHash, Perfil perfil,
            Funcionario funcionario) {
        this.idUtilizador = idUtilizador;
        this.username = username;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.funcionario = funcionario;
    }

    public Long getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(Long idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public boolean autenticar(String senha) {
        return false;
    }

    public String alterarSenha(String senhaAtual, String senhaNova) {
        return null;
    }

    public void alterarPerfil(Perfil novoPerfil) {

    }

    public boolean podeEntrar() {
        return false;
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean podeGerirProdutos() {
        return false;
    }

    public boolean podeAnularVendas() {
        return false;
    }

    public boolean podeVerRelatorios() {
        return false;
    }

    public boolean podeGerirFuncionarios() {
        return false;
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
        Utilizador outro = (Utilizador) obj;
        if (this.idUtilizador == null) {
            if (outro.idUtilizador != null) {
                return false;
            }
        } else {
            if (!this.idUtilizador.equals(outro.idUtilizador)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idUtilizador != null) {
            resultado = primo * resultado + this.idUtilizador.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Utilizador [idUtilizador=" + idUtilizador + ", username=" + username + ", perfil=" + perfil
                + ", funcionario=" + funcionario + "]";
    }
}
