package demo.ecommerce.product.service;

import demo.ecommerce.model.product.Product;
import demo.ecommerce.repository.product.ProductRepository;
import demo.ecommerce.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;


/**
 * @Author Mostafa
 */

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DatabaseClient db;


    public Mono<Product> saveProduct(Product product, String email) {
        return userRepository.getUserByEmail(email).flatMap(user -> saveProduct(product, user.getId()));
    }

    public Mono<Product> saveProduct(Product product, Long userId) {
        if (product.getId() == null) {
            product.setCreatedOn(new Date());
        }
        product.setUpdatedOn(new Date());
        product.setUserId(userId);
        return productRepository.save(product);
    }

    public Mono<Page<Product>> findAllProductsPaged(Pageable pageable) {
        Mono<List<Product>> products = productRepository.findAllPageable(pageable.getPageSize(), pageable.getOffset()).collectList();
        Mono<Long> totalProductsCount = productRepository.count();
        return products.flatMap(productList ->
                totalProductsCount.flatMap(totalCount -> Mono.just(new PageImpl<Product>(productList, pageable, totalCount)))
        );
    }

    public Mono<Page<Product>> findNotAvailableProductsPaged(Pageable pageable) {
        return productRepository.notAvailableProductsPageable(pageable.getPageSize(), pageable.getOffset()).collectList().flatMap(
                products -> countNotAvailableProducts().flatMap(count -> Mono.just(new PageImpl<Product>(products, pageable, count)))
        );
    }

    public Mono<Page<Product>> findAvailableProductsPaged(Pageable pageable) {
        return productRepository.availableProductsPageable(pageable.getPageSize(), pageable.getOffset()).collectList().flatMap(products ->
                countAvailableProducts().flatMap(count -> Mono.just(new PageImpl<Product>(products, pageable, count))));
    }

    public Mono<Page<Product>> findUserProductsPaged(String email, Pageable pageable) {
        return userRepository.getUserByEmail(email).flatMap(user ->
                productRepository.userProductsPageable(user.getId(), pageable.getPageSize(), pageable.getOffset()).collectList().flatMap(products ->

                        countUserProducts(user.getId()).flatMap(count -> Mono.just(new PageImpl<Product>(products, pageable, count)))
                ));
    }

    // REF ->  https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference

    Mono<Long> countNotAvailableProducts() {
        return db.execute().sql("Select count(id) as total from product where inventory_count = 0").map((row, rowMetadata) -> row.get("total", Long.class)).one();
    }

    Mono<Long> countUserProducts(Long userId) {
        return db.execute().sql("Select count(id) as total from product where user_id = $1").bind(0, userId).map((row, rowMetadata) -> row.get("total", Long.class)).one();
    }

    Mono<Long> countAvailableProducts() {
        return db.execute().sql("Select count(id) as total from product where inventory_count > 0").map((row, rowMetadata) -> row.get("total", Long.class)).one();
    }


}
