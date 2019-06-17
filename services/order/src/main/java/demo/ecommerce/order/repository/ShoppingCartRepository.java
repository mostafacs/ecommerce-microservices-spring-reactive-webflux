package demo.ecommerce.order.repository;

import demo.ecommerce.order.ShoppingCart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @Author Mostafa Albana
 */

public interface ShoppingCartRepository extends ReactiveCrudRepository<ShoppingCart, Long> {
}
