package demo.ecommerce.product.controller;

import demo.ecommerce.model.product.Product;
import demo.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class ProductController {

    @Autowired
    ProductService productService;


    @GetMapping("/list")
    public Mono<Page<Product>> getProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findAllProductsPaged(PageRequest.of(page, pageSize));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_merchant', 'SCOPE_admin')")
    @GetMapping("/list/merchant")
    public Mono<Page<Product>> getMerchantProducts(JwtAuthenticationToken auth, @RequestParam Integer page, @RequestParam Integer pageSize) {
        String email = auth.getTokenAttributes().get("client_id").toString();
        return productService.findUserProductsPaged(email, PageRequest.of(page, pageSize));
    }

    // Not available for clients
    @PreAuthorize("hasAnyAuthority('SCOPE_merchant', 'SCOPE_admin')")
    @PostMapping("/save")
    public Mono<Product> saveProduct(JwtAuthenticationToken auth, @RequestBody Product product) {
        String email = auth.getTokenAttributes().get("client_id").toString();
        return productService.saveProduct(product, email);
    }

    // Not available for clients
    @PreAuthorize("hasAnyAuthority('SCOPE_merchant', 'SCOPE_admin')")
    @PutMapping("/save")
    public Mono<Product> updateProduct(Authentication auth, @RequestBody Product product) {
        if (product.getId() == null)
            throw new IllegalArgumentException("Product id is required to update existing product");
        return productService.saveProduct(product, product.getUserId());
    }

    @GetMapping("/list/available")
    public Mono<Page<Product>> getNonZeroInventoryProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findAvailableProductsPaged(PageRequest.of(page, pageSize));
    }

    @GetMapping("/list/not-available")
    public Mono<Page<Product>> getZeroInventoryProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findNotAvailableProductsPaged(PageRequest.of(page, pageSize));
    }

}
