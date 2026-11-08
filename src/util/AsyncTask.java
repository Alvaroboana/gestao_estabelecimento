package util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public final class AsyncTask {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "AsyncTask-Worker");
        t.setDaemon(true);
        return t;
    });

    private AsyncTask() {
    }

    public static <T> void executar(Callable<T> tarefa, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> {
            try {
                final T resultado = tarefa.call();
                SwingUtilities.invokeLater(() -> {
                    if (onSuccess != null) {
                        onSuccess.accept(resultado);
                    }
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    if (onError != null) {
                        onError.accept(t);
                    }
                });
            }
        });
    }

    public static void executar(Runnable acao, Runnable onComplete, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> {
            try {
                acao.run();
                SwingUtilities.invokeLater(() -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
            } catch (Throwable t) {
                SwingUtilities.invokeLater(() -> {
                    if (onError != null) {
                        onError.accept(t);
                    }
                });
            }
        });
    }
}
