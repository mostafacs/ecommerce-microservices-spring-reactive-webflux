package demo.ecommerce.product.config;

import com.ecommerce.common.model.product.Product;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.function.DatabaseClient;

import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = {"demo.ecommerce.product.repository"})
public class DatabaseConfig extends AbstractR2dbcConfiguration {

    @Value("${db.hostname}")
    String hostName;

    @Value("${db.database}")
    String database;

    @Value("${db.username}")
    String username;

    @Value("${db.password}")
    String password;

   @Bean
   @Override
   public PostgresqlConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
            .host(hostName)
            .database(database)
            .username(username)
            .password(password).build());
    }



    @Bean("databaseClient")
    public DatabaseClient dataBaseClient() {
      return  DatabaseClient.create(connectionFactory());
    }
}
