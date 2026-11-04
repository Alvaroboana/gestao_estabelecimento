package view;

import model.vo.Utilizador;

/**
 * Contrato comum implementado por todas as telas do sistema para suportar:
 * 1. Recarregamento de dados do banco de dados (assíncrono via SwingWorker);
 * 2. Aplicação de regras de permissão por perfil (ADMIN, GERENTE, VENDEDOR);
 * 3. Transição dinâmica de tema (Modo Escuro / Modo Claro).
 */
public interface PainelRecarregavel {

    /**
     * Recarrega os dados reais a partir do banco de dados de maneira assíncrona.
     */
    void recarregarDados();

    /**
     * Aplica restrições de visibilidade e ação com base no utilizador logado.
     * @param utilizador utilizador autenticado
     */
    void aplicarPermissoes(Utilizador utilizador);

    /**
     * Atualiza as cores e fontes de acordo com o tema ativo em UiStyle.
     */
    void aplicarTema();
}
