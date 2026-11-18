package model.vo;

import java.io.Serializable;
import org.mindrot.jbcrypt.BCrypt;
import model.exception.DadoInvalidoException;

public class Utilizador implements Serializable {
    private Long idUtilizador;
    private boolean trocaSenhaObrigatoria;
    public boolean isTrocaSenhaObrigatoria() { return trocaSenhaObrigatoria; }
    public void setTrocaSenhaObrigatoria(boolean valor) { trocaSenhaObrigatoria = valor; }
    public static void validarNovaSenha(String senha) {
        if (senha == null || senha.length() < 8 || senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new DadoInvalidoException("A palavra-passe deve ter pelo menos 8 caracteres e no máximo 72 bytes.");
    }
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
        validarNovaSenha(password);
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
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
        if (senha == null || this.passwordHash == null) {
            return false;
        }
        try {
            if (this.passwordHash.startsWith("$2a$") || this.passwordHash.startsWith("$2b$") || this.passwordHash.startsWith("$2y$")) {
                return BCrypt.checkpw(senha, this.passwordHash);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String alterarSenha(String senhaAtual, String senhaNova) {
        validarNovaSenha(senhaNova);
        if (senhaNova.equals(senhaAtual)) throw new DadoInvalidoException("Escolha uma palavra-passe diferente da actual.");
        if (autenticar(senhaAtual)) {
            if (senhaNova != null && !senhaNova.trim().isEmpty()) {
                String novoHash;
                try {
                    novoHash = BCrypt.hashpw(senhaNova, BCrypt.gensalt());
                } catch (IllegalArgumentException e) {
                    throw new DadoInvalidoException("Nao foi possivel gerar o hash da nova senha.");
                }
                this.passwordHash = novoHash;
                return novoHash;
            }
        }
        return null;
    }

    public void alterarPerfil(Perfil novoPerfil) {
        if (novoPerfil != null) {
            this.perfil = novoPerfil;
        }
    }

    public boolean podeEntrar() {
        if (this.funcionario != null) {
            if (this.funcionario.getEstado() == EstadoFuncionario.ATIVO) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        if (this.perfil == Perfil.ADMIN) {
            return true;
        }
        return false;
    }

    public boolean podeGerirProdutos() {
        if (this.perfil == Perfil.ADMIN || this.perfil == Perfil.GERENTE) {
            return true;
        }
        return false;
    }

    public boolean podeAnularVendas() {
        if (this.perfil == Perfil.ADMIN || this.perfil == Perfil.GERENTE) {
            return true;
        }
        return false;
    }

    public boolean podeVerRelatorios() {
        if (this.perfil == Perfil.ADMIN || this.perfil == Perfil.GERENTE) {
            return true;
        }
        return false;
    }

    public boolean podeGerirFuncionarios() {
        if (this.perfil == Perfil.ADMIN) {
            return true;
        }
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
