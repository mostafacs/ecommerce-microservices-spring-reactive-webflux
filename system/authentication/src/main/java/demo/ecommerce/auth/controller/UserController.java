package demo.ecommerce.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;

@RestController
public class UserController {

    @GetMapping("/user")
    public Principal getLoginUser(Principal principal) {
        return principal;
    }
}
