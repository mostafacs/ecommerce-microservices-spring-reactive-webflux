package demo.ecommerce.auth.config;

import org.apache.commons.dbcp.BasicDataSource;
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
        dataSource.setUrl("jdbc:postgresql://localhost:5432/ecommerce");
        dataSource.setUsername("ecommerce");
        dataSource.setPassword("admin123");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setInitialSize(5);
        dataSource.setMaxActive(12);
        return dataSource;
    }

}
