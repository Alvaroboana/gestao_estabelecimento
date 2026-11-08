package infra;

import java.util.Map;


public final class DatabaseConfig {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/gestao?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "gestao";
    private static final String DEFAULT_PASSWORD = "gestao";

    private DatabaseConfig() {
    }

    public static String url() {
        return value("gestao.db.url", "GESTAO_DB_URL", DEFAULT_URL);
    }

    public static String username() {
        return value("gestao.db.username", "GESTAO_DB_USERNAME", DEFAULT_USER);
    }

    public static String password() {
        return value("gestao.db.password", "GESTAO_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String value(String propertyName, String environmentName, String fallback) {
        String property = System.getProperty(propertyName);
        if (property != null && !property.isBlank()) {
            return property;
        }

        Map<String, String> environment = System.getenv();
        String variable = environment.get(environmentName);
        if (variable != null && !variable.isBlank()) {
            return variable;
        }

        return fallback;
    }
}
