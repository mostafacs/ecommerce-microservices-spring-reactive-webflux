package demo.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


/**
 * @Author Mostafa Albana
 */

@SpringBootApplication
@ComponentScan({"demo.ecommerce"})
public class Application {

    // all endpoints is secure.
    @Bean
    String[] publicEndpoints() {
        return new String[]{};
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
