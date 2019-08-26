package demo.ecommerce.auth.security.model;

import demo.ecommerce.auth.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.*;

/**
 * @author Mostafa Albana
 */
public class ClientDetailsInfo implements ClientDetails {

    private User user;

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

    // Actually used by webflux security filter.
    @Override
    public Set<String> getScope() {
        final String[] roles = user.getRoles().split(",");
        return new HashSet<>(Arrays.asList(roles));
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

        if (StringUtils.isNotEmpty(user.getRoles())) {
            List<GrantedAuthority> gRoles = new ArrayList<>();
            final String[] roles = user.getRoles().split(",");
            for (String role : roles) {
                gRoles.add(new SimpleGrantedAuthority(role));
            }

            return gRoles;
        }
        return null;
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
        additionInformation.put("userId", user.getId());
        return additionInformation;
    }

}
