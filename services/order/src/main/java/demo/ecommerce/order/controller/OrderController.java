package demo.ecommerce.order.controller;

import demo.ecommerce.order.model.ShoppingCart;
import demo.ecommerce.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping // save
    @PutMapping  // update
    public Mono<ShoppingCart> saveOrder(@RequestBody ShoppingCart shoppingCart) {
        return orderService.saveShoppingCart(shoppingCart);
    }

    @GetMapping("/{orderId}")
    Mono<ShoppingCart> getOrder(@PathVariable("orderId") Long orderId) {
        return orderService.getShoppingCart(orderId);
    }

    @GetMapping
    Flux<ShoppingCart> getAllShoppingCart() {
        return orderService.getAllShoppingCarts();
    }



}
