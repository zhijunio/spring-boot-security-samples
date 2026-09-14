package com.example.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private String userName = "Anonymous";

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        }
        return ("HOME-Page! Username: %s").formatted(userName);
    }

    @GetMapping("/public")
    public String public_page(Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        }
        return ("Public-Page! Username: %s").formatted(userName);
    }

    @GetMapping("/user")
    public String private_page_user(Principal principal) {
        return ("Private-Page-for-User! Username: %s").formatted(principal.getName().toUpperCase());
    }

    @GetMapping("/admin")
    public String private_page_admin(Authentication authentication) {
        return ("Private-Page-for-Admin! Username: %s Roles: %s").formatted(authentication.getName().toUpperCase(), authentication.getAuthorities());
    }
}
