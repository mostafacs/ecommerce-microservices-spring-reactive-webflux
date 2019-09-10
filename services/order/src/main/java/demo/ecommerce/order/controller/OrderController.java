package demo.ecommerce.order.controller;

import demo.ecommerce.model.order.ShoppingCart;
import demo.ecommerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping // save
    @PutMapping  // update
    public Mono<ServerResponse> saveOrder(@RequestBody ShoppingCart shoppingCart, JwtAuthenticationToken auth) {
        String email = auth.getTokenAttributes().get("client_id").toString();
        return orderService.saveShoppingCart(shoppingCart, email).
                flatMap(cart -> ServerResponse.ok().body(Mono.just(cart), ShoppingCart.class))
                .onErrorResume(ex -> ServerResponse.badRequest().body(Mono.just(ex.getMessage()), String.class));
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
