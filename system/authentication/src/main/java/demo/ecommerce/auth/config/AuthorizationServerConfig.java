package demo.ecommerce.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.*;


/**
 * @Author Mostafa Albana
 */

@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Override
    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
        clientDetailsServiceConfigurer.withClientDetails(userDetailsService());
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(encoder());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter());
    }


    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), "admin123".toCharArray());
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("ecommerce");
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair);
        return converter;
    }


    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ClientDetailsService userDetailsService() {

        // PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        return new ClientDetailsService() {
            @Override
            public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
                return new ClientDetails() {
                    @Override
                    public String getClientId() {
                        return "admin";
                    }

                    @Override
                    public Set<String> getResourceIds() {
                        return null;
                    }

                    @Override
                    public boolean isSecretRequired() {
                        return false;
                    }

                    @Override
                    public String getClientSecret() {
                        return encoder().encode("123");
                    }

                    @Override
                    public boolean isScoped() {
                        return false;
                    }

                    @Override
                    public Set<String> getScope() {
                        return new HashSet<>(Arrays.asList("test"));
                    }

                    @Override
                    public Set<String> getAuthorizedGrantTypes() {
                        return new HashSet<>(Arrays.asList("client_credentials"));
                    }

                    @Override
                    public Set<String> getRegisteredRedirectUri() {
                        return null;
                    }

                    @Override
                    public Collection<GrantedAuthority> getAuthorities() {
                        return Arrays.asList(new GrantedAuthority() {
                            @Override
                            public String getAuthority() {
                                return "admin";
                            }
                        });
                    }

                    @Override
                    public Integer getAccessTokenValiditySeconds() {
                        return 100000;
                    }

                    @Override
                    public Integer getRefreshTokenValiditySeconds() {
                        return null;
                    }

                    @Override
                    public boolean isAutoApprove(String s) {
                        return true;
                    }

                    @Override
                    public Map<String, Object> getAdditionalInformation() {
                        HashMap additionInformation = new HashMap<>();
                        additionInformation.put("name", "mostafa additional");
                        return additionInformation;
                    }
                };
            }
        };
    }


//    @Bean
//    public ReactiveJwtDecoder passwordDecoder() {
//        return ReactiveJwtDecoders.fromOidcIssuerLocation("aaa");
//    }


//    @Bean
//    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
//        http
//                .authorizeExchange()
//                // .pathMatchers("/message/**").hasAuthority("SCOPE_message:read")
//                .anyExchange().authenticated()
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
//        return http.build();
//    }
}
