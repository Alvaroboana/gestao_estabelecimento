package testes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import infra.DBException;
import model.dao.RelatorioDAO;

public class TesteRelatorioDAO {
    public static void main(String[] args) {
        System.out.println("========== Teste RelatorioDAO ==========");
        RelatorioDAO dao = new RelatorioDAO();
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fim = LocalDateTime.now().plusMinutes(1);

        try {
            testarResultado("auditoria", dao.consultarAuditoria(null, null, inicio, fim, null));
            testarResultado("resumo de auditoria", dao.resumoAuditoria(inicio, fim));
            testarResultado("vendas", dao.vendasPeriodo(inicio, fim));
            testarResultado("estoque baixo", dao.estoqueBaixo());
            testarResultado("produtos mais vendidos", dao.produtosMaisVendidos());
            testarResultado("reembolsos", dao.reembolsosPeriodo(inicio, fim));
            System.out.println("=== Teste RelatorioDAO concluido com sucesso ===");
        } catch (DBException e) {
            System.out.println("[ERRO DE BANCO] " + e.getMessage());
            System.out.println("Certifique-se de que o MySQL foi inicializado com os scripts SQL.");
        }
    }

    private static void testarResultado(String nome, List<Map<String, Object>> resultado) {
        if (resultado == null) {
            throw new AssertionError("O relatorio " + nome + " retornou null.");
        }
        System.out.println("[OK] " + nome + ": " + resultado.size() + " linha(s)");
    }
}
