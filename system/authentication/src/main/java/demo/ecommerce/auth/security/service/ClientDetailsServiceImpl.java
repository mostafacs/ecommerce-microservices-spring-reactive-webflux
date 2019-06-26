package demo.ecommerce.auth.security.service;

import demo.ecommerce.auth.model.User;
import demo.ecommerce.auth.repository.UserRepository;
import demo.ecommerce.auth.security.model.ClientDetailsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

/**
 * @author Mostafa Albana
 */

@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encode;

    @Override

    public ClientDetails loadClientByClientId(String id) throws ClientRegistrationException {
        User user = userRepository.findByEmail(id.trim());
        if (user == null) return null;
        return new ClientDetailsInfo(user);
    }


}
