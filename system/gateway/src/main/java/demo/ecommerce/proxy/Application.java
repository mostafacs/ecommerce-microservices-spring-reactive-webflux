package demo.ecommerce.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author Mostafa Albana
 */

@EnableDiscoveryClient
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/uaa/**")
                        .filters(f -> f.rewritePath("/uaa/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://authentication-service")
                        .id("authentication-service"))
                .route(r -> r.path("/product/**")
                        .filters(f -> f.rewritePath("/product/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://product-service")
                        .id("product-service"))
                .route(r -> r.path("/order/**")
                        .filters(f -> f.rewritePath("/order/(?<path>.*)", "/$\\{path}"))
                        .uri("lb://order-service")
                        .id("order-service"))
                .build();
    }
}
