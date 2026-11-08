package infra;

public final class JdbcValidation {
    private JdbcValidation() {
    }

    public static long requireId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " deve ser um identificador positivo.");
        }
        return id;
    }
}
