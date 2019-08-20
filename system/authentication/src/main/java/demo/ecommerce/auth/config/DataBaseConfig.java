package demo.ecommerce.auth.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author Mostafa Albana
 */

@Configuration
@EnableJdbcRepositories("demo.ecommerce.auth.repository")
//@Import(AbstractJdbcConfiguration.class)
@Order(-558484)
public class DataBaseConfig extends JdbcConfiguration {

    @Value("${DATABASE_HOST}")
    String hostName;

    @Value("${DATABASE_NAME}")
    String database;

    @Value("${DATABASE_SCHEMA}")
    String schema;

    @Value("${DATABASE_USER}")
    String username;

    @Value("${DATABASE_PASSWORD}")
    String password;

    @Value("${DATABASE_PORT}")
    int port;

    @Bean
    NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }


    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%d/%s?currentSchema=%s", hostName, port, database, schema));
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(12);
        return dataSource;
    }

}
