package demo.ecommerce.order.controller;

import demo.ecommerce.model.order.ShoppingCart;
import demo.ecommerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;


    /**
     * Mono<ServerResponse>  return serialization error
     * Because: ServerResponse is the HTTP response type used by Spring WebFlux.fn, the functional variant of the reactive web framework.
     * it'snot supposed to use it within an annotated controller.
     * Solution use ResponseEntity
     */
    @PostMapping // save
    @PutMapping  // update
    public Mono<ResponseEntity> saveOrder(@RequestBody ShoppingCart shoppingCart, JwtAuthenticationToken auth) {
        String email = auth.getTokenAttributes().get("client_id").toString();
        return orderService.saveShoppingCart(shoppingCart, email).
                flatMap(cart -> Mono.just(ResponseEntity.ok(cart)))
                .cast(ResponseEntity.class)
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage())));
    }

    @GetMapping("/{orderId}")
    Mono<ShoppingCart> getOrder(@PathVariable("orderId") Long orderId) {
        return orderService.getShoppingCart(orderId);
    }

    @GetMapping
    Mono<Page<ShoppingCart>> getAllShoppingCart(JwtAuthenticationToken auth, @RequestParam Integer page, @RequestParam Integer pageSize) {
        String email = auth.getTokenAttributes().get("client_id").toString();
        return orderService.getUserShoppingCarts(email, PageRequest.of(page, pageSize));
    }



}
