package testes;

import infra.*;
import model.bo.*;
import model.dao.*;
import model.vo.*;
import model.exception.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/** Integração destrutiva exclusivamente na instância temporária de testes, porta 3307. */
public class TesteOperacoesSeguras {
    private static Funcionario funcionario;
    private static Cliente cliente;
    private static Categoria categoria;
    private static Utilizador admin;
    private static int passou;
    private static final VendaBO vendas = new VendaBO();
    private static final VendaDAO vendaDAO = new VendaDAO();
    private static final ProdutoDAO produtoDAO = new ProdutoDAO();
    private static final ReembolsoBO reembolsos = new ReembolsoBO();

    public static void main(String[] args) throws Exception {
        if (!Boolean.getBoolean("gestao.testes.integration") || !DatabaseConfig.url().contains(":3307/gestao"))
            throw new IllegalStateException("Este teste só pode usar a base temporária na porta 3307.");
        try (Connection c = ConexaoDB.getConnection(); Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT id_utilizador FROM utilizador WHERE perfil = 'ADMIN' LIMIT 1")) {
            if (!rs.next()) throw new AssertionError("Falta administrador de teste");
            admin = new UtilizadorDAO().buscarPorId(rs.getLong(1));
        }
        admin.setTrocaSenhaObrigatoria(false); SessaoUtilizador.iniciarSessao(admin);
        funcionario = admin.getFuncionario(); cliente = new ClienteDAO().listarTodos().get(0);
        categoria = new CategoriaDAO().listarTodos().get(0);
        testarRascunhoPagamento();
        testarRollback();
        testarConcorrenciaStock();
        testarConclusaoDuplicada();
        testarReembolsos();
        testarReembolsoConcorrente();
        testarCadastroAtomico();
        testarPermissoesSenhas();
        testarTrocaObrigatoria();
        testarResumoDiario();
        System.out.println("SUCESSO: " + passou + " verificações de integração.");
    }

    private static Produto produto(int quantidade) {
        Produto p = new Produto(); p.setNome("TESTE " + UUID.randomUUID()); p.setPreco(100.0);
        p.setUnidade("un"); p.setEstado(EstadoProduto.ATIVO); p.setQuantidadeMinima(0); p.setCategoria(categoria);
        Estoque lote = new Estoque(); lote.setQuantidade(quantidade); lote.setFuncionario(funcionario);
        lote.setDataEntrada(LocalDate.now()); lote.setDataValidade(LocalDate.now().plusDays(30));
        new ProdutoBO().salvarCompleto(p, null, lote);
        return produtoDAO.buscarPorId(p.getIdProduto());
    }
    private static Venda venda(Produto p, int quantidade) {
        Venda v = new Venda(); v.setEstado(EstadoVenda.PENDENTE); v.setCliente(cliente);
        v.setFuncionario(funcionario); v.setData(LocalDateTime.now());
        v.setItemVendido(new ArrayList<>(List.of(new ItemVendido(quantidade, 100.0, 0.0, v, p))));
        v.setTotal(v.calcularTotal()); v.setValorRecebido(v.calcularTotal()); return v;
    }
    private static void testarRascunhoPagamento() throws Exception {
        Produto p = produto(12); Venda v = venda(p, 1); vendas.salvar(v);
        v.getItemVendido().get(0).setQuantidade(3); vendas.atualizar(v);
        Venda lida = vendaDAO.buscarPorId(v.getIdVenda());
        ok(lida.getItemVendido().get(0).getQuantidade() == 3 && lida.getTotal() == 300, "Rascunho guarda itens e total");
        v.setValorRecebido(100.0); falha(() -> vendas.concluirEGuardar(v), "Pagamento insuficiente rejeitado");
        ok(stock(p) == 12, "Pagamento rejeitado não altera stock");
        v.setValorRecebido(500.0); vendas.concluirEGuardar(v);
        lida = vendaDAO.buscarPorId(v.getIdVenda());
        ok(lida.getValorRecebido() == 500 && lida.getTroco() == 200 && stock(p) == 9, "Pagamento e troco persistidos; stock correcto");
        Venda finalLida = lida; falha(() -> vendas.atualizar(finalLida), "Venda concluída não pode ser editada");
        falha(() -> vendas.eliminar(v.getIdVenda()), "Venda concluída não pode ser eliminada");
        Venda invalida = venda(p, 1); invalida.setValorRecebido(Double.NaN);
        falha(() -> vendas.concluirEGuardar(invalida), "Pagamento NaN rejeitado");
    }
    private static void testarRollback() throws Exception {
        Produto p = produto(1); Venda v = venda(p, 2); v.setEstado(EstadoVenda.CONCLUIDA);
        long antes = numero("SELECT COUNT(*) FROM venda");
        falha(() -> vendaDAO.concluirVendaAtomicamente(v, new EstoqueDAO()), "Stock insuficiente aborta transacção");
        ok(v.getIdVenda() == null && numero("SELECT COUNT(*) FROM venda") == antes && stock(p) == 1, "Rollback remove venda, itens e IDs provisórios");
        Cliente c = new Cliente(); c.setNome("TESTE ROLLBACK"); Contacto contacto = new Contacto(); contacto.setContacto("x".repeat(100)); c.setContacto(List.of(contacto));
        long pessoas = numero("SELECT COUNT(*) FROM pessoa");
        falha(() -> new ClienteDAO().salvar(c), "Falha no contacto reverte cadastro");
        ok(numero("SELECT COUNT(*) FROM pessoa") == pessoas, "Rollback executado antes de fechar ligação");
    }
    private static void testarConcorrenciaStock() throws Exception {
        Produto p = produto(1); Venda a = venda(p,1), b = venda(p,1);
        ok(correrJuntos(() -> vendas.concluirEGuardar(a), () -> vendas.concluirEGuardar(b)) == 1, "Só uma venda simultânea consome a última unidade");
        ok(stock(p) == 0, "Concorrência preserva saldo de stock");
        Produto suficiente = produto(2);
        Venda primeira = venda(suficiente,1), segunda = venda(suficiente,1);
        ok(correrJuntos(() -> vendas.concluirEGuardar(primeira), () -> vendas.concluirEGuardar(segunda)) == 2,
                "Duas vendas simultâneas com stock suficiente terminam ambas");
        ok(stock(suficiente) == 0, "Ambas as baixas simultâneas são contabilizadas");
    }
    private static void testarConclusaoDuplicada() throws Exception {
        Produto p = produto(8); Venda v = venda(p,2); vendas.salvar(v);
        Venda a = vendaDAO.buscarPorId(v.getIdVenda()), b = vendaDAO.buscarPorId(v.getIdVenda());
        a.setValorRecebido(200.0); b.setValorRecebido(200.0);
        ok(correrJuntos(() -> vendas.concluirEGuardar(a), () -> vendas.concluirEGuardar(b)) == 1, "Venda pendente só é concluída uma vez");
        ok(stock(p) == 6, "Conclusão repetida não duplica baixa");
    }
    private static void testarReembolsos() throws Exception {
        Produto p = produto(10); Venda v = venda(p,4); vendas.concluirEGuardar(v);
        ItemVendido item = vendaDAO.buscarPorId(v.getIdVenda()).getItemVendido().get(0);
        Reembolso r = reembolsos.criarEGuardar(item,1,"Produto intacto",funcionario,true);
        ok(stock(p) == 7 && r.getValor() == 100, "Devolução repõe lote original");
        reembolsos.criarEGuardar(item,1,"Produto danificado",funcionario,false);
        ok(stock(p) == 7, "Devolução de produto danificado não repõe stock");
        falha(() -> new ReembolsoDAO().eliminar(r.getIdReembolso()), "Eliminação de reembolso bloqueada");
        falha(() -> new ReembolsoDAO().atualizar(r), "Edição de reembolso bloqueada");
        ItemVendido antigo = vendaDAO.buscarPorId(v.getIdVenda()).getItemVendido().get(0);
        reembolsos.criarEGuardar(item,2,"Restantes devolvidos",funcionario,true);
        falha(() -> reembolsos.criarEGuardar(antigo,1,"Cópia antiga",funcionario,true), "Reembolso com dados desactualizados rejeitado");
        ok(stock(p) == 9 && vendaDAO.buscarPorId(v.getIdVenda()).getEstado() == EstadoVenda.REEMBOLSADA, "Reembolso total actualiza estado");
    }
    private static void testarReembolsoConcorrente() throws Exception {
        Produto p = produto(2); Venda v = venda(p,1); vendas.concluirEGuardar(v);
        ItemVendido a = vendaDAO.buscarPorId(v.getIdVenda()).getItemVendido().get(0);
        ItemVendido b = vendaDAO.buscarPorId(v.getIdVenda()).getItemVendido().get(0);
        ok(correrJuntos(() -> reembolsos.criarEGuardar(a,1,"A",funcionario,true), () -> reembolsos.criarEGuardar(b,1,"B",funcionario,true)) == 1, "Reembolso simultâneo não ultrapassa quantidade vendida");
        ok(stock(p) == 2, "Reposição simultânea ocorre uma única vez");
    }
    private static void testarCadastroAtomico() throws Exception {
        Produto p = new Produto(); p.setNome("TESTE inválido"); p.setPreco(10.0); p.setUnidade("un"); p.setQuantidadeMinima(0); p.setCategoria(categoria);
        long antes = numero("SELECT COUNT(*) FROM produto");
        falha(() -> new ProdutoBO().salvarCompleto(p,Long.MAX_VALUE,null), "Fornecedor inexistente aborta cadastro completo");
        ok(p.getIdProduto() == null && numero("SELECT COUNT(*) FROM produto") == antes, "Produto não fica gravado parcialmente");
    }
    private static void testarPermissoesSenhas() throws Exception {
        SessaoUtilizador.encerrarSessao();
        falha(() -> new ProdutoBO().salvar(new Produto()), "Sem sessão não cadastra produto");
        Utilizador vendedor = new Utilizador(); vendedor.setIdUtilizador(admin.getIdUtilizador()); vendedor.setPerfil(Perfil.VENDEDOR); vendedor.setFuncionario(funcionario);
        SessaoUtilizador.iniciarSessao(vendedor);
        falha(() -> new UtilizadorBO().eliminar(admin.getIdUtilizador()), "Vendedor não administra contas");
        falha(() -> new EstoqueBO().eliminar(1L), "Vendedor não elimina stock");
        SessaoUtilizador.iniciarSessao(admin);
        Utilizador u = new Utilizador(); u.setPasswordHash("TextoSimples123"); ok(!u.autenticar("TextoSimples123"), "Autenticação recusa palavra-passe sem hash");
        u.setPassword("SenhaSegura123"); ok(u.autenticar("SenhaSegura123"), "Palavra-passe é convertida para BCrypt");
        falha(() -> Utilizador.validarNovaSenha("123"), "Palavra-passe curta rejeitada");
        admin.setTrocaSenhaObrigatoria(true);
        falha(() -> new ProdutoBO().eliminar(1L), "Troca obrigatória impede operações");
        admin.setTrocaSenhaObrigatoria(false);
    }
    private static void testarResumoDiario() throws Exception {
        Produto p = produto(10), outro = produto(10);
        Venda v = venda(p,1);
        v.getItemVendido().add(new ItemVendido(2,100.0,0.0,v,outro));
        v.setValorRecebido(300.0); vendas.concluirEGuardar(v);
        long divergencias = numero("SELECT COUNT(*) FROM vw_resumo_vendas_diario r JOIN (SELECT DATE(v.data) dia, SUM(v.total - COALESCE((SELECT SUM(r.valor) FROM reembolso r JOIN item_vendido i ON i.id_item=r.id_item WHERE i.id_venda=v.id_venda),0)) total FROM venda v WHERE v.estado IN ('CONCLUIDA','REEMBOLSADA') GROUP BY DATE(v.data)) v ON v.dia=r.dia WHERE r.total_vendido<>v.total");
        ok(divergencias == 0, "Resumo diário não multiplica total pelo número de itens");
    }
    private static void testarTrocaObrigatoria() throws Exception {
        Funcionario f = new FuncionarioDAO().listarTodos().stream().filter(x -> x.getEstado() == EstadoFuncionario.ATIVO
                && !x.getIdFuncionario().equals(funcionario.getIdFuncionario())).findFirst().orElseThrow();
        // Liberta apenas a conta de um funcionário dos dados descartáveis desta instância.
        try (Connection c = ConexaoDB.getConnection(); PreparedStatement st = c.prepareStatement("DELETE FROM utilizador WHERE id_funcionario = ?")) {
            st.setLong(1,f.getIdFuncionario()); st.executeUpdate();
        }
        Utilizador u = new Utilizador(); u.setUsername("teste_" + UUID.randomUUID().toString().substring(0,8));
        u.setFuncionario(f); u.setPerfil(Perfil.VENDEDOR); u.setPassword("SenhaInicial123");
        UtilizadorBO bo = new UtilizadorBO(); bo.salvar(u);
        ok(bo.autenticar(u.getUsername(),"SenhaInicial123").isTrocaSenhaObrigatoria(), "Nova conta exige troca de palavra-passe");
        Utilizador alterado = bo.alterarSenhaAutenticada(u.getUsername(),"SenhaInicial123","NovaSenhaSegura456");
        ok(!alterado.isTrocaSenhaObrigatoria() && bo.autenticar(u.getUsername(),"NovaSenhaSegura456") != null, "Troca autenticada remove obrigatoriedade");
        falha(() -> bo.autenticar(u.getUsername(),"SenhaInicial123"), "Palavra-passe antiga deixa de funcionar");
        new controller.UtilizadorController().alterarSenhaAdmin(u.getIdUtilizador(),"TemporariaSegura789",admin);
        ok(bo.autenticar(u.getUsername(),"TemporariaSegura789").isTrocaSenhaObrigatoria(), "Redefinição administrativa exige nova troca");
        try (Connection c = ConexaoDB.getConnection(); PreparedStatement st = c.prepareStatement("UPDATE utilizador SET password_hash = ?, troca_senha_obrigatoria=FALSE WHERE id_utilizador=?")) {
            st.setString(1,"LegadaSegura123"); st.setLong(2,u.getIdUtilizador()); st.executeUpdate();
        }
        MigrarPalavrasPasse.main(new String[]{"--converter-legadas"});
        ok(bo.autenticar(u.getUsername(),"LegadaSegura123").isTrocaSenhaObrigatoria(), "Migração preserva acesso e exige troca de credenciais antigas");
        try (Connection c = ConexaoDB.getConnection(); Statement st = c.createStatement()) {
            try { st.execute("CREATE TABLE permissao_indevida (id INT)"); throw new AssertionError("Conta da aplicação pode criar tabelas"); }
            catch (SQLException esperado) { ok(esperado.getErrorCode() == 1142, "Conta da aplicação sem privilégios de criação de tabelas"); }
        }
    }
    private static int correrJuntos(Runnable a, Runnable b) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2); CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Boolean> ca = () -> { start.await(); try { a.run(); return true; } catch (RuntimeException e) { return false; } };
            Callable<Boolean> cb = () -> { start.await(); try { b.run(); return true; } catch (RuntimeException e) { return false; } };
            Future<Boolean> fa = pool.submit(ca), fb = pool.submit(cb); start.countDown();
            return (fa.get(30,TimeUnit.SECONDS)?1:0) + (fb.get(30,TimeUnit.SECONDS)?1:0);
        } finally { pool.shutdownNow(); }
    }
    private static long stock(Produto p) throws Exception { return numero("SELECT COALESCE(SUM(quantidade),0) FROM estoque WHERE id_produto="+p.getIdProduto()); }
    private static long numero(String sql) throws Exception {
        try (Connection c = ConexaoDB.getConnection(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) { rs.next(); return rs.getLong(1); }
    }
    private static void ok(boolean condicao, String nome) {
        if (!condicao) throw new AssertionError(nome); passou++; System.out.println("[OK] " + nome);
    }
    private static void falha(Runnable acao, String nome) {
        try { acao.run(); } catch (RuntimeException e) { ok(true,nome); return; }
        throw new AssertionError("Não rejeitou: " + nome);
    }
}
