package demo.ecommerce.repository.order;

import demo.ecommerce.model.order.ShoppingCart;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @Author Mostafa Albana
 */

public interface ShoppingCartRepository extends ReactiveCrudRepository<ShoppingCart, Long> {

    @Query("Select * from shopping_cart where user_id = $1 order by updated_on desc, id desc LIMIT $2 OFFSET $3")
    Flux<ShoppingCart> getUserShoppingCartsPageable(Long userId, int limit, long offset);

}
