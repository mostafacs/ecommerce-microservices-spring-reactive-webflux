package demo.ecommerce.security;

import net.minidev.json.JSONArray;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Mostafa Albana
 */
public class EcommerceJwtAuthenticationConverter implements Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> {
    private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";

    private static final Collection<String> WELL_KNOWN_SCOPE_ATTRIBUTE_NAMES =
            Arrays.asList("read", "writer"); // added authorities


    public EcommerceJwtAuthenticationConverter() {

    }

    @Override
    public Mono<? extends AbstractAuthenticationToken> convert(Jwt jwt) {
        //BearerTokenAuthenticationToken a;

        List<GrantedAuthority> authoritiesList = new ArrayList<>();
        JSONArray authorities = (JSONArray) jwt.getClaims().get("authorities");
        authorities.forEach(itm -> authoritiesList.add(new SimpleGrantedAuthority(itm.toString())));

        AbstractAuthenticationToken token = new AbstractAuthenticationToken(authoritiesList) {

            @Override
            public Object getCredentials() {
                return jwt.getTokenValue();
            }

            @Override
            public Object getPrincipal() {
                return jwt.getClaimAsString("client_id");
            }
        };
        token.setAuthenticated(true);

        return Mono.just(token);
    }

//    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
//        return this.getScopes(jwt)
//                .stream()
//                .map(authority -> SCOPE_AUTHORITY_PREFIX + authority)
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//    }
//
//    private Collection<String> getScopes(Jwt jwt) {
//        Collection<String> authorities = new ArrayList<>();
//        // add to collection instead of returning early
//        for (String attributeName : WELL_KNOWN_SCOPE_ATTRIBUTE_NAMES) {
//            Object scopes = jwt.getClaims().get(attributeName);
//            if (scopes instanceof String) {
//                if (StringUtils.hasText((String) scopes)) {
//                    authorities.addAll(Arrays.asList(((String) scopes).split(" ")));
//                }
//            } else if (scopes instanceof Collection) {
//                authorities.addAll((Collection<String>) scopes);
//            }
//        }
//
//        return authorities;
//    }
}