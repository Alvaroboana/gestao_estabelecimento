package infra;

import model.exception.AcessoNegadoException;
import model.vo.Perfil;
import model.vo.SessaoUtilizador;
import model.vo.Utilizador;

public final class Autorizacao {
    private Autorizacao() {}

    public static Utilizador exigir(Perfil minimo) {
        Utilizador u = SessaoUtilizador.getUtilizador();
        if (u == null || u.getIdUtilizador() == null || !u.podeEntrar()
                || u.getPerfil() == null || u.isTrocaSenhaObrigatoria())
            throw new AcessoNegadoException("Inicie uma sessão válida e conclua a troca de palavra-passe.");
        if (u.getPerfil() != Perfil.ADMIN && (minimo == Perfil.ADMIN
                || (minimo == Perfil.GERENTE && u.getPerfil() != Perfil.GERENTE)))
            throw new AcessoNegadoException("O seu perfil não permite esta operação.");
        return u;
    }

    public static void conferirOperador(Utilizador operador, Perfil minimo) {
        Utilizador actual = exigir(minimo);
        if (operador == null || !actual.getIdUtilizador().equals(operador.getIdUtilizador()))
            throw new AcessoNegadoException("O operador não corresponde à sessão actual.");
    }
}
