package util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * Utilitário para execução de operações pesadas (I/O, JDBC) fora da EDT (Event Dispatch Thread).
 * Garante que a interface nunca congele e que as atualizações visuais ocorram sempre na EDT.
 */
public final class AsyncTask {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "AsyncTask-Worker");
        t.setDaemon(true);
        return t;
    });

    private AsyncTask() {
    }

    /**
     * Executa uma tarefa em background e entrega o resultado na EDT.
     *
     * @param <T> tipo do retorno
     * @param tarefa operação a ser executada fora da EDT (ex: chamada de BO ou DAO)
     * @param onSuccess callback invocado na EDT em caso de sucesso
     * @param onError callback invocado na EDT em caso de falha
     */
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

    /**
     * Executa uma ação sem retorno em background.
     *
     * @param acao operação a ser executada fora da EDT
     * @param onComplete callback invocado na EDT após término
     * @param onError callback invocado na EDT em caso de falha
     */
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
