package demo.ecommerce.product.controller;

import demo.ecommerce.product.model.Product;
import demo.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;


    @GetMapping("/list")
    public Flux<Product> getProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findAllProductsPaged(PageRequest.of(page, pageSize));
    }


    @GetMapping("/list/user/{userId}")
    public Flux<Product> getUserProducts(@PathVariable Long userId, @RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findUserProductsPaged(userId, PageRequest.of(page, pageSize));
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
    public Flux<Product> getNonZeroInventoryProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findAvailableProductsPaged(PageRequest.of(page, pageSize));
    }

    @GetMapping("/list/not-available")
    public Flux<Product> getZeroInventoryProducts(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return productService.findNotAvailableProductsPaged(PageRequest.of(page, pageSize));
    }

}
