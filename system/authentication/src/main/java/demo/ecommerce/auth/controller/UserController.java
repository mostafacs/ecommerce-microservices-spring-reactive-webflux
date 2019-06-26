package demo.ecommerce.auth.controller;

import demo.ecommerce.auth.model.User;
import demo.ecommerce.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user")
    public Principal getLoginUser(Principal principal) {
        return principal;
    }

    @PostMapping("/user")
    public User register(@RequestBody User user) throws Exception {
        return userService.createUser(user);
    }
}
