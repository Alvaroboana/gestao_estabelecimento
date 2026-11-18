package util;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.*;
import javax.swing.SwingUtilities;

public final class AsyncTask {
    private static final Logger LOG = Logger.getLogger(AsyncTask.class.getName());
    private static final Map<Object, Map<String, Object>> LEITURAS = new WeakHashMap<>();
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(4, 4, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(128), r -> {
                Thread t = new Thread(r, "AsyncTask-Worker");
                t.setDaemon(true);
                return t;
            }, new ThreadPoolExecutor.AbortPolicy());

    private AsyncTask() {}

    public static <T> void executar(Callable<T> tarefa, Consumer<T> sucesso, Consumer<Throwable> erro) {
        try {
            EXECUTOR.execute(() -> {
                try {
                    T resultado = tarefa.call();
                    SwingUtilities.invokeLater(() -> { if (sucesso != null) sucesso.accept(resultado); });
                } catch (Exception e) { falhar(e, erro); }
            });
        } catch (RejectedExecutionException e) {
            falhar(new IllegalStateException("Há muitas operações em curso. Aguarde e tente novamente.", e), erro);
        }
    }

    // Apenas leituras: uma resposta antiga nunca substitui a consulta mais recente.
    public static <T> void executarUltima(Object painel, String consulta, Callable<T> tarefa,
            Consumer<T> sucesso, Consumer<Throwable> erro) {
        Object pedido = new Object();
        synchronized (LEITURAS) {
            LEITURAS.computeIfAbsent(painel, k -> new HashMap<>()).put(consulta, pedido);
        }
        executar(tarefa, valor -> {
            if (actual(painel, consulta, pedido)) {
                if (painel instanceof javax.swing.JComponent componente) componente.setToolTipText(null);
                if (sucesso != null) sucesso.accept(valor);
            }
        }, e -> {
            if (actual(painel, consulta, pedido) && erro != null) erro.accept(e);
        });
    }

    private static boolean actual(Object painel, String consulta, Object pedido) {
        synchronized (LEITURAS) {
            Map<String, Object> pedidos = LEITURAS.get(painel);
            return pedidos != null && pedidos.get(consulta) == pedido;
        }
    }

    private static void falhar(Exception e, Consumer<Throwable> erro) {
        LOG.log(Level.WARNING, "Operação não concluída", e);
        SwingUtilities.invokeLater(() -> { if (erro != null) erro.accept(e); });
    }

    public static void executar(Runnable tarefa, Runnable sucesso, Consumer<Throwable> erro) {
        executar(() -> { tarefa.run(); return null; }, valor -> {
            if (sucesso != null) sucesso.run();
        }, erro);
    }
}
