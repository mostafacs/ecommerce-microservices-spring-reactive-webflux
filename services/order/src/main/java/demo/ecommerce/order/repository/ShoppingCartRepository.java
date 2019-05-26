package demo.ecommerce.order.repository;

import demo.ecommerce.order.model.ShoppingCart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ShoppingCartRepository extends ReactiveCrudRepository<ShoppingCart, Long> {
}
