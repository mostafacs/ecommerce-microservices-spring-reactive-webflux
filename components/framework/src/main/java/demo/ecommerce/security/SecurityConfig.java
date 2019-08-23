package demo.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.Assert;


/**
 * @Author Mostafa Albana
 */

@EnableWebFluxSecurity
public class SecurityConfig {

    // Defined in each Service use this config file.
    @Autowired
    String[] publicEndpoints;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {

        Assert.notNull(publicEndpoints, "No bean with name publicEndPoints defined");

        if (publicEndpoints.length > 0) {
            http.authorizeExchange().pathMatchers(publicEndpoints).permitAll();
        }

        http.csrf().disable().cors().disable().authorizeExchange()
                .pathMatchers("/**").authenticated()
                .and()
                .oauth2ResourceServer().jwt();

        return http.build();
    }


}
