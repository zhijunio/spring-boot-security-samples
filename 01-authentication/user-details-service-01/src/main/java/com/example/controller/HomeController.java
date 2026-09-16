package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    private String userName = "Anonymous";

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        }
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Home Page!</h2>
                <p>Username: %s</p>
                <a href='/public'>Public</a><br/>
                <a href='/user'>Private for User</a><br/>
                <a href='/admin'>Private for Admin</a>
                </center>
                """).formatted(userName);
    }

    @GetMapping("/public")
    public String public_page(Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        }
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Public Page!</h2>
                <p>Username: %s</p>
                <a href='/'>Home</a>
                </center>
                """).formatted(userName);
    }

    @GetMapping("/user")
    public String private_page_user(Principal principal) {
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for User!</h2>
                <p>Username: %s</p>
                <a href='/'>Home</a>
                </center>
                """).formatted(principal.getName().toUpperCase());
    }

    @GetMapping("/admin")
    public String private_page_admin(Authentication authentication) {
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for Admin!</h2>
                <p>Username: %s</p>
                <p>Roles: %s</p>
                <a href='/'>Home</a>
                </center>
                """).formatted(authentication.getName().toUpperCase(), authentication.getAuthorities());
    }
}
