package demo.ecommerce.order.service;

import demo.ecommerce.order.model.ShoppingCart;
import demo.ecommerce.order.model.ShoppingCartItem;
import demo.ecommerce.order.repository.ShoppingCartItemRepository;
import demo.ecommerce.order.repository.ShoppingCartRepository;
import demo.ecommerce.product.model.Product;
import demo.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;


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

        savedShoppingCart = savedShoppingCart.flatMap(savedCart -> {

            shoppingCart.getCartItemList().forEach( cartItem -> {

                        cartItem.setShoppingCartId(savedCart.getId());
                        cartItem.setProductId(cartItem.getProduct().getId());
            });

            // calling subscribe is a must to actually save in database otherwise data will not saved to database.
           shoppingCartItemRepository.saveAll(shoppingCart.getCartItemList()).collectList().flatMap(savedCartItem ->  {
                savedCart.setCartItemList(savedCartItem);
                return Mono.just(savedCartItem);
            });

            return Mono.just(savedCart);
        });


        return savedShoppingCart;
    }


    public Mono<ShoppingCart> getShoppingCart(Long cartId) {

      Mono<ShoppingCart> shoppingCartMono = shoppingCartRepository.findById(cartId);
      Flux<ShoppingCartItem> shoppingCartItemFlux = shoppingCartItemRepository.getShoppingOrderCartItems(cartId);


      shoppingCartMono = shoppingCartMono.flatMap( icart ->  {

          final ShoppingCart cart = icart;

          cart.setCartItemList(new ArrayList<>());

        Flux<ShoppingCartItem> result = shoppingCartItemFlux.flatMap(cartItem -> {

              if(cartItem.getProductId() != null) {
                  return productRepository.findById(cartItem.getProductId()).flatMap(product -> {
                      cartItem.setProduct(product);
                      return Mono.just(cartItem);
                  });
              } else  {
                  return Mono.just(cartItem);
              }
          });

        return result.collectList().flatMap(a -> {
            cart.setCartItemList(a);
            return Mono.just(cart);
        });

      });


      return shoppingCartMono;
    }

    public Flux<ShoppingCart> getAllShoppingCarts() {
        Flux<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();
        return shoppingCarts;
    }

}
