package demo.ecommerce.auth.service;

import demo.ecommerce.auth.exception.EmailAlreadyExists;
import demo.ecommerce.auth.model.User;
import demo.ecommerce.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Mostafa Albana
 */

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    public User createUser(User user) throws EmailAlreadyExists {
        User exist = userRepository.findByEmail(user.getEmail());
        if (exist != null) {
            throw new EmailAlreadyExists();
        }
        Date now = new Date();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreateDate(now);
        user.setUpdateDate(now);
        return userRepository.save(user);
    }
}
