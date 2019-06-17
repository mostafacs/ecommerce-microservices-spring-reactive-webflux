package demo.ecommerce.auth.controller;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Mostafa Albana
 */

@FrameworkEndpoint
public class JWTKeyEndPoint {


    @Value("${keystore.password}")
    String keyStorePassword;

    @Value("${keystore.keyPairAlias}")
    String keyPairAlias;


    @GetMapping(value = "/.well-known/jwks")
    @ResponseBody
    public ResponseEntity key() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), keyStorePassword.toCharArray());
        KeyPair keypair = keyStoreKeyFactory.getKeyPair(keyPairAlias);

        JWK jwk = new RSAKey.Builder((RSAPublicKey) keypair.getPublic())
                .privateKey((RSAPrivateKey) keypair.getPrivate())
                .keyUse(KeyUse.SIGNATURE)
                .keyID("resource")
                .build();

        return ResponseEntity.ok(new Jwts(Arrays.asList(jwk.toJSONObject())));
    }


    class Jwts {
        public List<JSONObject> keys;

        public Jwts(List<JSONObject> keys) {
            this.keys = keys;
        }

    }
}