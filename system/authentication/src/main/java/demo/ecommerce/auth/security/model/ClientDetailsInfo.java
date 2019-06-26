package demo.ecommerce.auth.security.model;

import demo.ecommerce.auth.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

/**
 * @author Mostafa Albana
 */
public class ClientDetailsInfo implements ClientDetails {

    private final User user;

    public ClientDetailsInfo(User user) {
        this.user = user;
    }

    @Override
    public String getClientId() {
        return user.getEmail();
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
        return user.getPassword();
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

}
