package demo.ecommerce.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableAuthorizationServer
@EnableResourceServer
public class Application  {
    public static void main( String[] args )
    {
        // curl admin:123@localhost:8087/oauth/token -d grant_type=client_credentials
        // curl admin%40admin.com:123@localhost:8087/oauth/token -d grant_type=client_credentials
        SpringApplication.run(Application.class, args);
    }


}
