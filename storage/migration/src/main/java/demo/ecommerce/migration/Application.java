package demo.ecommerce.migration;

import org.flywaydb.core.Flyway;

/**
 * @author Mostafa Albana
 */
public class Application {

    private static final String hostEnvName = "DATABASE_HOST";
    private static final String portEnvName = "DATABASE_PORT";
    private static final String schemaEnvName = "DATABASE_SCHEMA";
    private static final String userEnvName = "DATABASE_USER";
    private static final String passwordEnvName = "DATABASE_PASSWORD";

    public static void main(String[] args) throws Exception {


        String host = System.getenv(hostEnvName);
        String port = System.getenv(portEnvName);
        String schema = System.getenv(schemaEnvName);
        String user = System.getenv(userEnvName);
        String password = System.getenv(passwordEnvName);
        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, schema);

        System.out.println(url);
        System.out.println(schema);
        System.out.println(user);
        System.out.println(password);

        String path = Application.class.getClassLoader().getResource("flyway/migrations").getFile().replace("file:", "filesystem:");
        System.out.println(path);
        Flyway flyway = Flyway.configure().
                locations("flyway/migrations").
                dataSource(url, user, password).
                sqlMigrationPrefix("m").
                sqlMigrationSeparator("__")
                .schemas(schema)
                .load();

        flyway.migrate();
        System.out.println(".................... Migration Completed successfully .................");
    }
}
