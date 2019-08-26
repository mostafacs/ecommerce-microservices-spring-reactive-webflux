package demo.ecommerce.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {


    private static final String[] PUBLIC_RESOURCES = {

            "/.well-known**",
            "/user**",
            "/user/**",
            "/sign-key/public",
            "/oauth/token",
            "/oauth/check_token",

    };



    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous().and().authorizeRequests().antMatchers(PUBLIC_RESOURCES).permitAll()
                .antMatchers("/admin/**").hasRole("admin").anyRequest().authenticated();
    }

}
