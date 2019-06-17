package demo.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author Mostafa
 */

@SpringBootApplication
@ComponentScan({"demo.ecommerce"})
public class Application
{

    @Bean
    String[] publicEndpoints() {
        return new String[]{};
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
