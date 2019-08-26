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

    @PostMapping("/user/merchant")
    public User merchantRegister(@RequestBody User user) throws Exception {
        user.setRoles("merchant");
        return userService.createUser(user);
    }

    @PostMapping("/user/client")
    public User clientRegister(@RequestBody User user) throws Exception {
        user.setRoles("client");
        return userService.createUser(user);
    }
}
