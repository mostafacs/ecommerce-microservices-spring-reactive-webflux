package demo.ecommerce.order.service;

import demo.ecommerce.order.ShoppingCart;
import demo.ecommerce.order.ShoppingCartItem;
import demo.ecommerce.order.repository.ShoppingCartItemRepository;
import demo.ecommerce.order.repository.ShoppingCartRepository;
import demo.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @Author Mostafa Albana
 */

@Service
public class OrderService {

    @Autowired
    ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    ProductRepository productRepository;

    // You should never call a blocking method within a method that returns a reactive type
    public Mono<ShoppingCart> saveShoppingCart(ShoppingCart shoppingCart) {

        Mono<ShoppingCart> savedShoppingCart = shoppingCartRepository.save(shoppingCart);

        // assign saveShoppingCart is required to fill with data enhanced from calling flatMap
        savedShoppingCart = savedShoppingCart.flatMap(savedCart -> {

            shoppingCart.getCartItemList().forEach(cartItem -> {

                cartItem.setShoppingCartId(savedCart.getId());
                cartItem.setProductId(cartItem.getProduct().getId());
            });

            // calling subscribe/flatMap/... is a must to actually save in database otherwise data will not saved to database.
            Flux<ShoppingCartItem> cartItemFlux = shoppingCartItemRepository.saveAll(shoppingCart.getCartItemList()).flatMap(this::fillCartItemWithProduct);

            return cartItemFlux.collectList().flatMap(a -> {
                savedCart.setCartItemList(a);
                return Mono.just(savedCart);
            });

        });


        return savedShoppingCart;
    }


    public Mono<ShoppingCart> getShoppingCart(Long cartId) {

        Mono<ShoppingCart> shoppingCartMono = shoppingCartRepository.findById(cartId);
        shoppingCartMono = shoppingCartMono.flatMap(this::fillCartWithCartItems);
        return shoppingCartMono;
    }


    public Flux<ShoppingCart> getAllShoppingCarts() {
        Flux<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        shoppingCarts = shoppingCarts.flatMap(this::fillCartWithCartItems);
        return shoppingCarts;
    }


    Mono<ShoppingCart> fillCartWithCartItems(ShoppingCart cart) {
        Flux<ShoppingCartItem> shoppingCartItemFlux = shoppingCartItemRepository.getShoppingOrderCartItems(cart.getId());
        Flux<ShoppingCartItem> result = shoppingCartItemFlux.flatMap(this::fillCartItemWithProduct);

        return result.collectList().flatMap(a -> {
            cart.setCartItemList(a);
            return Mono.just(cart);
        });
    }

    Mono<ShoppingCartItem> fillCartItemWithProduct(ShoppingCartItem cartItem) {

        if (cartItem.getProductId() != null) {
            return productRepository.findById(cartItem.getProductId()).flatMap(product -> {
                cartItem.setProduct(product);
                return Mono.just(cartItem);
            });
        } else {
            return Mono.just(cartItem);
        }
    }

}
