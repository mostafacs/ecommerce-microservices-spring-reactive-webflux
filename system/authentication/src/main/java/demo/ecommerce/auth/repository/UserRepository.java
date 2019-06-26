package demo.ecommerce.auth.repository;

import demo.ecommerce.auth.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Mostafa Albana
 */

public interface UserRepository extends CrudRepository<User, Long> {

    @Query("select * from users where email = :email")
    User findByEmail(@Param("email") String email);
}
