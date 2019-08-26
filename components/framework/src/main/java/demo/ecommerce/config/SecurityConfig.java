package demo.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.Assert;
import org.springframework.web.server.adapter.HttpWebHandlerAdapter;


/**
 * @Author Mostafa Albana
 */

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    // Defined in each Service use this config file.
    @Autowired
    String[] publicEndpoints;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {

        Assert.notNull(publicEndpoints, "No bean with name publicEndPoints defined");

        if (publicEndpoints.length > 0) {
            HttpWebHandlerAdapter a;
            http.authorizeExchange().pathMatchers(publicEndpoints).permitAll();
        }

        http.csrf().disable().cors().disable().authorizeExchange()
                .pathMatchers("/product/save").hasAuthority("SCOPE_merchant")
                .pathMatchers("/**").authenticated()
                .and()
                .oauth2ResourceServer().jwt(); //.jwtAuthenticationConverter(grantedAuthoritiesExtractor());

        return http.build();
    }


/*    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        GrantedAuthoritiesExtractor extractor = new GrantedAuthoritiesExtractor();
        return new ReactiveJwtAuthenticationConverterAdapter(extractor);
    }


    static class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {
        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
            Collection<String> authorities = (Collection<String>)
                    jwt.getClaims().get("authorities");

            return authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }*/

//    static class GrantedAuthoritiesExtractor implements Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> {
//
//        @Override
//        public final Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
//            Collection<GrantedAuthority> authorities = this.extractAuthorities(jwt);
//            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
//        }
//
//
//        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
//            Collection<String> authorities = ((Collection<String>)
//                    jwt.getClaims().get("authorities")).stream().map(itm -> "SCOPE_" + itm).collect(Collectors.toList());
//
//            return authorities.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//        }
//    }


}
