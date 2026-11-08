package controller;


import infra.DBException;
import java.util.List;
import java.util.Map;
import model.bo.RelatorioBO;
import util.ValidadorObjeto;

public class DashboardController {
    private final RelatorioBO relatorioBO;

    public DashboardController() {
        this(new RelatorioBO());
    }

    public DashboardController(RelatorioBO relatorioBO) {
        ValidadorObjeto.validarNaoNulo(relatorioBO, "RelatorioBO");
        this.relatorioBO = relatorioBO;
    }

    public Map<String, Integer> obterIndicadoresDashboard() throws DBException {
        return relatorioBO.obterIndicadoresDashboard();
    }

    public List<Map<String, Object>> obterProdutosMaisVendidos() throws DBException {
        return relatorioBO.obterProdutosMaisVendidosDashboard();
    }

    public List<Map<String, Object>> obterEstoqueBaixo() throws DBException {
        return relatorioBO.obterEstoqueBaixoDashboard();
    }
}
