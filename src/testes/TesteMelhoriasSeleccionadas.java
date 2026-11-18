package testes;

import infra.MigracoesDB;
import model.vo.ItemVendido;
import util.AsyncTask;
import util.ValidadorNumero;
import javax.swing.SwingUtilities;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public final class TesteMelhoriasSeleccionadas {
    private static int verificacoes;
    private static void conferir(boolean ok, String caso) {
        if (!ok) throw new AssertionError(caso);
        verificacoes++;
    }
    private static void rejeita(Runnable acao, String caso) {
        boolean falhou = false;
        try { acao.run(); } catch (IllegalArgumentException | model.exception.DadoInvalidoException e) { falhou = true; }
        conferir(falhou, caso);
    }
    public static void main(String[] args) throws Exception {
        for (double valor : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            rejeita(() -> ValidadorNumero.validarNaoNegativo(valor,"Valor"), "não finito");
            rejeita(() -> ValidadorNumero.validarPositivo(valor,"Valor"), "positivo não finito");
            rejeita(() -> ValidadorNumero.validarIntervalo(valor,0.0,100.0,"Valor"), "intervalo não finito");
        }
        conferir(new ItemVendido(2, 10.0, 5.0, null, null).calcularSubtotal() == 15.0, "desconto válido");
        conferir(new ItemVendido(2, 10.0, 20.0, null, null).calcularSubtotal() == 0.0, "desconto total");
        rejeita(() -> new ItemVendido(2,10.0,20.01,null,null).calcularSubtotal(), "desconto excessivo");
        rejeita(() -> new ItemVendido(2,Double.MAX_VALUE,0.0,null,null).calcularSubtotal(), "overflow");
        rejeita(() -> new ItemVendido(0,10.0,0.0,null,null).calcularSubtotal(), "quantidade zero");
        rejeita(() -> new ItemVendido(1,10.0,Double.NaN,null,null).calcularSubtotal(), "desconto NaN");
        var sql = MigracoesDB.separarComandos("-- início\nSET @x='a;b'; /* ; */ SELECT 'd''e;f'; SELECT 1;");
        conferir(sql.size()==3 && sql.get(0).contains("a;b") && sql.get(1).contains("d''e;f"), "separador SQL");
        rejeita(() -> MigracoesDB.separarComandos("USE outra;"), "migração não troca base");
        rejeita(() -> MigracoesDB.separarComandos("SELECT 'ab"), "SQL incompleto");
        try (var ficheiros = java.nio.file.Files.list(java.nio.file.Path.of("docker/migrations"))) {
            for (var f : ficheiros.toList()) conferir(!MigracoesDB.separarComandos(java.nio.file.Files.readString(f)).isEmpty(), "migração legível");
        }
        Object painel = new Object();
        CountDownLatch antigaIniciada = new CountDownLatch(1), libertarAntiga = new CountDownLatch(1), novaTerminada = new CountDownLatch(1);
        AtomicInteger valor = new AtomicInteger();
        AtomicReference<Throwable> falha = new AtomicReference<>();
        AsyncTask.executarUltima(painel,"lista", () -> { antigaIniciada.countDown(); libertarAntiga.await(); return 1; },
                v -> valor.set(v), falha::set);
        conferir(antigaIniciada.await(5,TimeUnit.SECONDS), "leitura antiga iniciada");
        AsyncTask.executarUltima(painel,"lista", () -> 2, v -> {
            if (!SwingUtilities.isEventDispatchThread()) falha.set(new AssertionError("callback fora da EDT"));
            valor.set(v); novaTerminada.countDown();
        }, falha::set);
        conferir(novaTerminada.await(5,TimeUnit.SECONDS), "leitura nova concluída");
        libertarAntiga.countDown();
        // A barreira ocupa os quatro workers e garante que a leitura antiga já publicou o callback.
        var executorField = AsyncTask.class.getDeclaredField("EXECUTOR"); executorField.setAccessible(true);
        ThreadPoolExecutor executor = (ThreadPoolExecutor)executorField.get(null);
        long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (executor.getActiveCount()!=0 && System.nanoTime()<limite) Thread.sleep(10);
        SwingUtilities.invokeAndWait(() -> {});
        conferir(valor.get()==2 && falha.get()==null, "resposta antiga ignorada e callback EDT");
        CountDownLatch bloquear = new CountDownLatch(1), ocupados = new CountDownLatch(4), rejeitada = new CountDownLatch(1);
        AtomicInteger escritas = new AtomicInteger();
        for (int i=0;i<4;i++) executor.execute(() -> { ocupados.countDown(); try { bloquear.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
        conferir(ocupados.await(5,TimeUnit.SECONDS), "quatro workers limitados");
        for (int i=0;i<128;i++) AsyncTask.executar(() -> { escritas.incrementAndGet(); }, () -> {}, falha::set);
        AsyncTask.executar(() -> { throw new AssertionError("não deve executar"); }, () -> {}, e -> rejeitada.countDown());
        conferir(rejeitada.await(5,TimeUnit.SECONDS), "fila cheia informa rejeição");
        bloquear.countDown();
        limite=System.nanoTime()+TimeUnit.SECONDS.toNanos(5);
        while (escritas.get()<128 && System.nanoTime()<limite) Thread.sleep(10);
        conferir(escritas.get()==128, "operações aceites não descartadas");
        conferir(executor.getLargestPoolSize()==4, "limite de threads");
        System.out.println(verificacoes + " verificações passaram.");
    }
}
