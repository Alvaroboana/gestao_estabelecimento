package model.vo;

public class Utilizador {
    private Long idUtilizador;
    private String username;
    private String password;
    private Perfil perfil;
    private Funcionario funcionario;
    
    public Utilizador() {
    }

    public Utilizador(String username, String password, Perfil perfil, Funcionario funcionario) {
        this.username = username;
        this.password = password;
        this.perfil = perfil;
        this.funcionario = funcionario;
    }

    public Utilizador(Long idUtilizador, String username, String password, Perfil perfil, Funcionario funcionario) {
        this.idUtilizador = idUtilizador;
        this.username = username;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public boolean autenticar(String senha){
        return false;
    }

    public String alterarSenha(String senhaAtual, String senhaNova){
        return null;
    }

    public void alterarPerfil(Perfil novoPerfil){

    }

    public boolean podeEntrar(){
        return false;
    }

    public boolean isAdmin(){
        return false;
    }

    public boolean podeGerirProdutos(){
        return false;
    }

    public boolean podeAnularVendas(){
        return false;
    }

    public boolean podeVerRelatorios(){
        return false;
    }
    public boolean podeGerirFuncionarios(){
        return false;
    }

    @Override
    public String toString() {
        return "Utilizador [username=" + username + ", password=" + password + ", perfil=" + perfil + ", funcionario="
                + funcionario + "]";
    }

    
}
