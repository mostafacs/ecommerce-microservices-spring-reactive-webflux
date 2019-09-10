package demo.ecommerce.order.service;

import demo.ecommerce.model.order.ShoppingCart;
import demo.ecommerce.model.order.ShoppingCartItem;
import demo.ecommerce.model.product.Product;
import demo.ecommerce.repository.order.ShoppingCartItemRepository;
import demo.ecommerce.repository.order.ShoppingCartRepository;
import demo.ecommerce.repository.product.ProductRepository;
import demo.ecommerce.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @Author Mostafa Albana
 */

@Service
public class OrderService {

    private static String PRODUCT_NOT_AVAILABLE_MESSAGE = "Product %s is not Available";

    @Autowired
    ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    ShoppingCartRepository shoppingCartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatabaseClient db;


    private Mono<ShoppingCart> validateQuantities(ShoppingCart cart, Function<ShoppingCart, Mono<ShoppingCart>> onSuccess, Function<String, Mono<ShoppingCart>> onFail) {
        Mono<ShoppingCart> existingCart = null;
        if (cart.getId() != null) {
            existingCart = shoppingCartRepository.findById(cart.getId()).flatMap(this::fillCartWithCartItems);
        }
        List<Long> productIds = cart.getCartItemList().stream().map(ShoppingCartItem::getProductId).collect(Collectors.toList());
        final Flux<Product> newCartProducts = productRepository.findAllById(productIds);
        Flux<Product> products;
        if (existingCart != null) {

            products = existingCart.flatMapMany(oldCart -> {
                Map<Long, Integer> oldCartQuantities = oldCart.getCartItemList().stream().collect(Collectors.toMap(ShoppingCartItem::getProductId, ShoppingCartItem::getQuantity));
                return newCartProducts.flatMap(product -> {
                    if (oldCartQuantities.containsKey(product.getId())) {
                        product.increaseInventory(oldCartQuantities.get(product.getId()));
                    }
                    return Mono.just(product);
                });
            });
        } else {
            products = newCartProducts;
        }
        AtomicInteger index = new AtomicInteger(0);
        StringBuilder errorMessageBuilder = new StringBuilder();
        Flux<Product> validProducts = newCartProducts.filter(product -> {
            if (product.getInventoryCounts() >= cart.getCartItemList().get(index.getAndIncrement()).getQuantity()) {
                return true;
            } else {
                if (errorMessageBuilder.length() > 0) errorMessageBuilder.append(", ");
                errorMessageBuilder.append(String.format(PRODUCT_NOT_AVAILABLE_MESSAGE, product.getSku()));
                return false;
            }
        });

        if (validProducts.count() == products.count()) {
            return onSuccess.apply(cart);
        } else {

            return onFail.apply(errorMessageBuilder.toString());
        }
    }

    private Mono<Map<Long, Product>> restoreProductsQuantities(ShoppingCart oldCart) {

        List<Long> productIds = oldCart.getCartItemList().stream().map(ShoppingCartItem::getProductId).collect(Collectors.toList());
        Flux<Product> products = productRepository.findAllById(productIds);
        AtomicInteger indexer = new AtomicInteger(0);
        final Map<Long, Product> productMap = new HashMap<>();
        return products.collectList().flatMap(productList -> {
            for (Product product : productList) {
                product.increaseInventory(oldCart.getCartItemList().get(indexer.getAndIncrement()).getQuantity());
                productMap.put(product.getId(), product);
            }
            return Mono.just(productMap);
        });

    }

    private Flux<Product> updateCartProductQuantities(ShoppingCart cart) {
        final Mono<Map<Long, Product>> oldProductsRestored;
        if (cart.getId() != null) {
            Mono<ShoppingCart> existingCart = shoppingCartRepository.findById(cart.getId()).flatMap(this::fillCartWithCartItems);
            oldProductsRestored = existingCart.flatMap(this::restoreProductsQuantities);
        } else {
            oldProductsRestored = Mono.just(new HashMap<>());
        }

        AtomicInteger indexer = new AtomicInteger(0);
        List<Long> productIds = cart.getCartItemList().stream().map(ShoppingCartItem::getProductId).collect(Collectors.toList());
        Flux<Product> newCartProducts = productRepository.findAllById(productIds);

        return newCartProducts.flatMap(newProduct -> {

            return oldProductsRestored.flatMap(oldProductsMap -> {
                if (oldProductsMap.containsKey(newProduct.getId())) {
                    Product product = oldProductsMap.get(newProduct.getId());
                    product.decreaseInventory(cart.getCartItemList().get(indexer.getAndIncrement()).getQuantity());
                    return Mono.just(product);
                } else {
                    newProduct.decreaseInventory(cart.getCartItemList().get(indexer.getAndIncrement()).getQuantity());
                    return Mono.just(newProduct);
                }
            });

        });

    }


    public Mono<ShoppingCart> saveShoppingCart(ShoppingCart cart, String email) {
        return userRepository.getUserByEmail(email).flatMap(user -> {
            cart.setUserId(user.getId());
            if (cart.getId() == null) {
                cart.setCreatedOn(new Date());
            }
            cart.setUpdatedOn(new Date());
            return validateQuantities(cart, this::internalSaveShoppingCart, errorMessage -> {
                throw new IllegalArgumentException(errorMessage);
            });
        });

    }

    // You should never call a blocking method within a method that returns a reactive type
    private Mono<ShoppingCart> internalSaveShoppingCart(ShoppingCart shoppingCart) {

        // update product Quantities and save into database
        productRepository.saveAll(updateCartProductQuantities(shoppingCart)).subscribe();

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


    public Mono<Page<ShoppingCart>> getUserShoppingCarts(String email, Pageable pageable) {
        return userRepository.getUserByEmail(email).flatMap(user -> {
            Flux<ShoppingCart> shoppingCarts = shoppingCartRepository.getUserShoppingCartsPageable(user.getId(), pageable.getPageSize(), pageable.getOffset());
            shoppingCarts = shoppingCarts.flatMap(this::fillCartWithCartItems);
            Mono<Long> totalCartsCount = countUserShoppingCarts(user.getId());
            return shoppingCarts.collectList().flatMap(carts ->
                    totalCartsCount.flatMap(count -> Mono.just(new PageImpl<ShoppingCart>(carts, pageable, count))));
        });
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

    Mono<Long> countUserShoppingCarts(Long userId) {
        return db.execute().sql("Select count(id) as total from shopping_cart where user_id = $1").bind(0, userId).map((row, rowMetadata) -> row.get("total", Long.class)).one();
    }

}
