package view;

import model.vo.Utilizador;

public interface PainelRecarregavel {

    void recarregarDados();

    void aplicarPermissoes(Utilizador utilizador);

    void aplicarTema();
}
