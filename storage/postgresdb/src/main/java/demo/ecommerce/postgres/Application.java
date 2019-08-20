package demo.ecommerce.postgres;

import org.flywaydb.core.Flyway;

/**
 * @author Mostafa Albana
 */
public class Application {

    private static final String urlEnvName = "database_url";
    private static final String schemaEnvName = "schema_name";
    private static final String userEnvName = "user_name";
    private static final String passwordEnvName = "user_password";

    public static void main(String[] args) throws Exception {

        String url = System.getenv(urlEnvName);
        String schema = System.getenv(schemaEnvName);
        String user = System.getenv(userEnvName);
        String password = System.getenv(passwordEnvName);


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
