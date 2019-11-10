package demo.ecommerce.order.service;

import demo.ecommerce.model.order.ShoppingCart;
import demo.ecommerce.model.order.ShoppingCartItem;
import demo.ecommerce.model.product.Product;
import demo.ecommerce.model.user.User;
import demo.ecommerce.repository.order.ShoppingCartItemRepository;
import demo.ecommerce.repository.order.ShoppingCartRepository;
import demo.ecommerce.repository.product.ProductRepository;
import demo.ecommerce.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @Author Mostafa Albana
 */

@Service
public class OrderService {

    private static String PRODUCT_NOT_AVAILABLE_MESSAGE = "Product %s is not Available";
    private static Logger logger = LoggerFactory.getLogger(OrderService.class);

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


    Mono<ShoppingCart> updateCartData(ShoppingCart cart, Mono<ShoppingCart> existingCart, Flux<Product> productFlux) {
        if (existingCart != null) {
            // delete items removed by user
            existingCart.flatMap(ecart -> {
                HashSet<Long> newItemsId = new HashSet();
                cart.getCartItemList().forEach(i -> {
                    if (i.getId() != null) newItemsId.add(i.getId());
                });
                //existingItems.stream().filter(i -> )
                List<ShoppingCartItem> deletedItems = ecart.getCartItemList().stream().filter(i -> !newItemsId.contains(i.getId())).collect(Collectors.toList());
                if (deletedItems.size() > 0) deleteCartItems(deletedItems).subscribe();
                return Mono.just(ecart);
            }).subscribe();
        }
        return productFlux.collectList().flatMap(products -> {
            BigDecimal totalCost = BigDecimal.ZERO;
            BigDecimal totalShipping = BigDecimal.ZERO;
            int totalQuantity = 0;

            for (int i = 0; i < cart.getCartItemList().size(); i++) {
                ShoppingCartItem item = cart.getCartItemList().get(i);

                // set price data from product
                item.setUnitPrice(products.get(i).getSellPrice());
                item.setUnitCostPrice(products.get(i).getCostPrice());

                BigDecimal itemTotal = BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getUnitPrice()));
                item.setTotalPrice(itemTotal.doubleValue());

                totalCost = totalCost.add(itemTotal);
                totalQuantity += item.getQuantity();
                totalShipping = totalShipping.add(BigDecimal.valueOf(item.getShippingCost()));
            }

            cart.setSubTotalPrice(totalCost.doubleValue());
            cart.setTotalShippingCost(totalShipping.doubleValue());
            // total + shipping
            cart.setTotalCost(totalCost.add(totalShipping).doubleValue());
            cart.setTotalQuantity(totalQuantity);

            return Mono.just(cart);
        });
    }

    private Mono<ShoppingCart> validateQuantities(ShoppingCart cart, Mono<ShoppingCart> existingCart, BiFunction<ShoppingCart, Mono<ShoppingCart>, Mono<ShoppingCart>> onSuccess, Function<String, Mono<ShoppingCart>> onFail) {

        // get all products ids from  cart items
        List<Long> productIds = cart.getCartItemList().stream().map(itm -> itm.getProduct().getId()).collect(Collectors.toList());
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
        Flux<Product> validProducts = products.filter(product -> {
            int cartIndex = index.getAndIncrement();
            if (product.getInventoryCounts() >= cart.getCartItemList().get(cartIndex).getQuantity()) {
                return true;
            } else {
                if (errorMessageBuilder.length() > 0) errorMessageBuilder.append(", ");
                errorMessageBuilder.append(String.format(PRODUCT_NOT_AVAILABLE_MESSAGE, product.getSku()));
                return false;
            }
        });

        return validProducts.count().flatMap(validCount ->
                products.count().flatMap(allproductsCount -> {
                    if (validCount.equals(allproductsCount)) {
                        return onSuccess.apply(cart, existingCart);
                    } else {

                        return onFail.apply(errorMessageBuilder.toString());
                    }
                })
        );
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

    private Flux<Product> updateCartProductQuantities(ShoppingCart cart, Mono<ShoppingCart> existingCart) {
        final Mono<Map<Long, Product>> oldProductsRestored;

        // if edit CartOrder increase counts of products from old cart
        if (existingCart != null) {
            oldProductsRestored = existingCart.flatMap(this::restoreProductsQuantities);
        } else {
            oldProductsRestored = Mono.just(new HashMap<>());
        }

        List<Long> productIds = cart.getCartItemList().stream().map(item -> item.getProduct().getId()).collect(Collectors.toList());
        Flux<Product> newCartProducts = productRepository.findAllById(productIds);


        return newCartProducts.collectList().flatMapMany(newProducts -> {
            Map<Long, Product> newProductMap = new HashMap<>();
            newProducts.forEach(np -> newProductMap.put(np.getId(), np));

            return oldProductsRestored.flatMapMany(oldProductsMap -> {

                List<Product> productsToSave = new ArrayList<>();

                for (int i = 0; i < newProducts.size(); i++) {
                    if (oldProductsMap.containsKey(newProducts.get(i).getId())) {
                        Product product = oldProductsMap.get(newProducts.get(i).getId());
                        product.decreaseInventory(cart.getCartItemList().get(i).getQuantity());
                        productsToSave.add(product);
                    } else {
                        newProducts.get(i).decreaseInventory(cart.getCartItemList().get(i).getQuantity());
                        productsToSave.add(newProducts.get(i));
                    }
                }


                // find delete items products to save them
                List<Product> deletedProducts = oldProductsMap.keySet().stream()
                        .filter(opk -> !newProductMap.containsKey(opk))
                        .flatMap(deletedProductIds -> Stream.of(oldProductsMap.get(deletedProductIds))).collect(Collectors.toList());
                productsToSave.addAll(deletedProducts);

                return Flux.fromArray(productsToSave.toArray(new Product[0]));

            });

        });

    }


    public Mono<ShoppingCart> saveShoppingCart(ShoppingCart cart, String email) {
        return userRepository.getUserByEmail(email).flatMap(user -> {
            Mono<ShoppingCart> existingCartMono = null;
            cart.setUserId(user.getId());
            if (cart.getId() == null) {
                cart.setCreatedOn(new Date());
            } else {
                existingCartMono = shoppingCartRepository.findById(cart.getId()).flatMap(this::fillCartWithCartItems);
            }
            cart.setUpdatedOn(new Date());

            // validate client not save another cart
            final Mono<ShoppingCart> tempExistingCart = existingCartMono;
            return validateUserId(cart, existingCartMono, user).flatMap(vc ->
                    validateQuantities(cart, tempExistingCart, this::internalSaveShoppingCart, errorMessage -> {
                        throw new IllegalArgumentException(errorMessage);
                    })
            );


        });

    }

    Mono<ShoppingCart> validateUserId(ShoppingCart cart, Mono<ShoppingCart> cartMono, User user) {

        if (cartMono != null) {
            return validateUserId(cartMono, user);
        }
        return Mono.just(cart);
    }

    Mono<ShoppingCart> validateUserId(Mono<ShoppingCart> cart, User user) {
        return cart.flatMap(dbCart -> {
            if (!dbCart.getUserId().equals(user.getId())) {
                throw new IllegalArgumentException("Not authorized to save this order");
            }
            return cart;
        });
    }

    // You should never call a blocking method within a method that returns a reactive type
    private Mono<ShoppingCart> internalSaveShoppingCart(ShoppingCart shoppingCart, Mono<ShoppingCart> existingCartMono) {

        // update product Quantities and save into database
        Flux<Product> savedProducts = productRepository.saveAll(updateCartProductQuantities(shoppingCart, existingCartMono));
        savedProducts.subscribe(products -> logger.info("Update product quantities"));

        Mono<ShoppingCart> savedShoppingCart = updateCartData(shoppingCart, existingCartMono, savedProducts).flatMap(cartWithData -> shoppingCartRepository.save(cartWithData));


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

    public Mono<Void> deleteCartItems(List<ShoppingCartItem> cartItems) {
        return shoppingCartItemRepository.deleteAll(cartItems);
    }

    public Mono<ShoppingCart> getShoppingCart(Long cartId, String email) {

        final Mono<ShoppingCart> shoppingCartMono = shoppingCartRepository.findById(cartId);

        return userRepository.getUserByEmail(email).flatMap(user ->

                validateUserId(shoppingCartMono, user).flatMap(valid ->
                        shoppingCartMono.flatMap(this::fillCartWithCartItems)
                )
        );

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
                // hide cost price details from end-user client (available only for merchants
                product.setCostPrice(null);
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
