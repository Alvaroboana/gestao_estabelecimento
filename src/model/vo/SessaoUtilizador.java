package model.vo;

public final class SessaoUtilizador {

    private static volatile Utilizador utilizadorAtual;

    private SessaoUtilizador() {
    }

    public static synchronized void iniciarSessao(Utilizador utilizador) {
        utilizadorAtual = utilizador;
    }

    public static synchronized void encerrarSessao() {
        utilizadorAtual = null;
    }

    public static Utilizador getUtilizador() {
        return utilizadorAtual;
    }

    public static Long getIdUtilizador() {
        Utilizador u = utilizadorAtual;
        return (u != null) ? u.getIdUtilizador() : null;
    }

    public static boolean isAutenticado() {
        return utilizadorAtual != null;
    }

    public static Perfil getPerfil() {
        Utilizador u = utilizadorAtual;
        return (u != null) ? u.getPerfil() : null;
    }

    public static boolean isAdmin() {
        Utilizador u = utilizadorAtual;
        return u != null && u.isAdmin();
    }

    public static boolean podeGerirProdutos() {
        Utilizador u = utilizadorAtual;
        return u != null && u.podeGerirProdutos();
    }

    public static boolean podeAnularVendas() {
        Utilizador u = utilizadorAtual;
        return u != null && u.podeAnularVendas();
    }

    public static boolean podeVerRelatorios() {
        Utilizador u = utilizadorAtual;
        return u != null && u.podeVerRelatorios();
    }

    public static boolean podeGerirFuncionarios() {
        Utilizador u = utilizadorAtual;
        return u != null && u.podeGerirFuncionarios();
    }
}
