package demo.ecommerce.repository.user;

import demo.ecommerce.model.user.User;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("Select * from users where email=$1")
    Mono<User> getUserByEmail(String email);

}
