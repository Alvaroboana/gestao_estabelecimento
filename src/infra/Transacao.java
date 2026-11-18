package infra;

import java.sql.Connection;
import java.sql.SQLException;

public final class Transacao {
    private Transacao() {}

    @FunctionalInterface
    public interface Operacao<T> {
        T executar(Connection connection) throws SQLException;
    }

    public static <T> T executar(String mensagem, Operacao<T> operacao) {
        try (Connection connection = ConexaoDB.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T resultado = operacao.executar(connection);
                connection.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollback) {
                    e.addSuppressed(rollback);
                }
                if (e instanceof RuntimeException runtime) throw runtime;
                throw new DBException(mensagem, e);
            }
        } catch (SQLException e) {
            throw new DBException(mensagem, e);
        }
    }
}
