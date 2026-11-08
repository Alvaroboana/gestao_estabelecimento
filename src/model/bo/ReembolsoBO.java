package model.bo;

import java.time.LocalDateTime;
import java.util.List;

import model.dao.EstoqueDAO;
import model.dao.ReembolsoDAO;
import model.exception.DadoInvalidoException;
import model.exception.EntidadeNaoEncontradaException;
import model.exception.ReembolsoInvalidoException;
import model.vo.EstadoVenda;
import model.vo.Funcionario;
import model.vo.ItemVendido;
import model.vo.Reembolso;
import util.ValidadorNumero;
import util.ValidadorObjeto;
import util.ValidadorTexto;

public class ReembolsoBO {
    private final ReembolsoDAO dao;
    private final EstoqueDAO estoqueDAO;

    public ReembolsoBO() {
        this(new ReembolsoDAO(), new EstoqueDAO());
    }

    public ReembolsoBO(ReembolsoDAO dao) {
        this(dao, new EstoqueDAO());
    }

    public ReembolsoBO(ReembolsoDAO dao, EstoqueDAO estoqueDAO) {
        ValidadorObjeto.validarNaoNulo(dao, "DAO de reembolso");
        ValidadorObjeto.validarNaoNulo(estoqueDAO, "DAO de estoque");
        this.dao = dao;
        this.estoqueDAO = estoqueDAO;
    }

    public void salvar(Reembolso reembolso) {
        validar(reembolso);
        dao.salvar(reembolso);
    }

    public Reembolso criarEGuardar(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        validarContexto(item, quantidade, motivo, funcionario);
        double valor = calcularValor(item, quantidade);
        Reembolso reembolso = new Reembolso(
                quantidade, valor, motivo, LocalDateTime.now(), item, funcionario);
        dao.salvarReembolsoEReporEstoque(reembolso, estoqueDAO);
        anexarDepoisDePersistir(item, reembolso);
        return reembolso;
    }

    public void atualizar(Reembolso reembolso) {
        validar(reembolso);
        validarId(reembolso.getIdReembolso(), "idReembolso");
        dao.atualizar(reembolso);
    }

    public void eliminar(Long id) {
        validarId(id, "idReembolso");
        dao.eliminar(id);
    }

    public List<Reembolso> listarTodos() {
        return dao.listarTodos();
    }

    public Reembolso buscarPorId(Long id) {
        validarId(id, "idReembolso");
        Reembolso reembolso = dao.buscarPorId(id);
        if (reembolso == null) {
            throw new EntidadeNaoEncontradaException("Reembolso nao encontrado.");
        }
        return reembolso;
    }

    public List<Reembolso> buscarPorItem(Long idItem) {
        validarId(idItem, "idItem");
        return dao.buscarPorItem(idItem);
    }

    public List<Reembolso> buscarPorFuncionario(Long idFuncionario) {
        validarId(idFuncionario, "idFuncionario");
        return dao.buscarPorFuncionario(idFuncionario);
    }

    private void validar(Reembolso reembolso) {
        ValidadorObjeto.validarNaoNulo(reembolso, "Reembolso");
        ValidadorNumero.validarNaoNulo(reembolso.getQuantidade(), "Quantidade");
        ValidadorNumero.validarPositivo(reembolso.getQuantidade(), "Quantidade");
        ValidadorNumero.validarNaoNulo(reembolso.getValor(), "Valor");
        ValidadorNumero.validarNaoNegativo(reembolso.getValor(), "Valor");
        ValidadorTexto.validarObrigatorio(reembolso.getMotivo(), "Motivo");
        validarContexto(reembolso.getItem(), reembolso.getQuantidade(),
                reembolso.getMotivo(), reembolso.getFuncionario());
    }

    private void validarContexto(ItemVendido item, int quantidade, String motivo, Funcionario funcionario) {
        ValidadorObjeto.validarNaoNulo(item, "Item vendido");
        validarId(item.getIdItem(), "item.idItem");
        ValidadorObjeto.validarNaoNulo(item.getVenda(), "Venda do item");
        if (item.getVenda().getEstado() != EstadoVenda.CONCLUIDA
                && item.getVenda().getEstado() != EstadoVenda.REEMBOLSADA) {
            throw new ReembolsoInvalidoException("Apenas vendas concluidas podem ser reembolsadas.");
        }
        ValidadorNumero.validarPositivo(quantidade, "Quantidade");
        ValidadorTexto.validarObrigatorio(motivo, "Motivo");
        ValidadorObjeto.validarNaoNulo(funcionario, "Funcionario");
        validarId(funcionario.getIdFuncionario(), "funcionario.idFuncionario");
        if (funcionario.getEstado() != model.vo.EstadoFuncionario.ATIVO) {
            throw new ReembolsoInvalidoException("O funcionario responsavel deve estar ativo.");
        }
        int disponivel = quantidadeRestante(item);
        if (quantidade > disponivel) {
            throw new ReembolsoInvalidoException("A quantidade do reembolso excede o saldo do item.");
        }
    }

    private int quantidadeRestante(ItemVendido item) {
        int total = item.getQuantidade() == null ? 0 : item.getQuantidade();
        if (item.getReembolso() != null) {
            for (Reembolso reembolso : item.getReembolso()) {
                if (reembolso != null && reembolso.getQuantidade() != null) {
                    total -= reembolso.getQuantidade();
                }
            }
        }
        return Math.max(total, 0);
    }

    private double calcularValor(ItemVendido item, int quantidade) {
        double preco = item.getPreco() == null ? 0.0 : item.getPreco();
        double desconto = item.getDesconto() == null ? 0.0 : item.getDesconto();
        double valor = quantidade * preco;
        if (item.getQuantidade() != null && item.getQuantidade() > 0) {
            valor -= (desconto / item.getQuantidade()) * quantidade;
        }
        return Math.max(valor, 0.0);
    }

    private void anexarDepoisDePersistir(ItemVendido item, Reembolso reembolso) {
        if (item.getReembolso() == null) {
            item.setReembolso(new java.util.ArrayList<Reembolso>());
        }
        item.getReembolso().add(reembolso);
    }

    private void validarId(Long id, String nome) {
        if (id == null || id <= 0) {
            throw new DadoInvalidoException(nome + " deve ser um identificador positivo.");
        }
    }
}
