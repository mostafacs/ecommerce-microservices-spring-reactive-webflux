package demo.ecommerce.product.service;

import demo.ecommerce.product.model.Product;
import demo.ecommerce.repository.product.ProductRepository;
import demo.ecommerce.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;


/**
 * @Author Mostafa
 */

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;


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

    public Flux<Product> findAllProductsPaged(Pageable pageable) {
        Page a;
        return productRepository.findAllPageable(pageable.getPageSize(), pageable.getOffset());
    }

    public Flux<Product> findNotAvailableProductsPaged(Pageable pageable) {
        return productRepository.notAvailableProducts(pageable.getPageSize(), pageable.getOffset());
    }

    public Flux<Product> findAvailableProductsPaged(Pageable pageable) {
        return productRepository.availableProducts(pageable.getPageSize(), pageable.getOffset());
    }

    public Flux<Product> findUserProductsPaged(Long userId, Pageable pageable) {
        return productRepository.userProducts(userId, pageable.getPageSize(), pageable.getOffset());
    }


}
