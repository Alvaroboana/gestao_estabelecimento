package controller;


import infra.DBException;
import java.util.List;
import java.util.Map;
import model.dao.RelatorioDAO;

public class DashboardController {
    private final RelatorioDAO relatorioDAO;

    public DashboardController() {
        this.relatorioDAO = new RelatorioDAO();
    }

    public List<Map<String, Object>> obterProdutosMaisVendidos() throws DBException {
        return relatorioDAO.produtosMaisVendidos();
    }

    public List<Map<String, Object>> obterEstoqueBaixo() throws DBException {
        return relatorioDAO.estoqueBaixo();
    }
}
