package com.example.springbootsecuritypart06.controller;

import com.example.springbootsecuritypart06.config.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private String userName = "Anonymous";

    @GetMapping("/")
    public String home(Authentication authentication) {
        String login = "<a href='/login'>Login</a><br/>";
        String logout = "<a href='/logout'>Logout</a><br/>";
        String user = "<a href='/user'>Private for User</a><br/>";
        String admin = "<a href='/admin'>Private for Admin</a>";
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        } else {
            userName = "Anonymous";
        }

        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Home Page!</h2>
                <p>Username: %s</p>
                <a href='/public'>Public</a><br/>
                <p></p>
                %s
                %s
                <p></p>
                %s
                %s
                <p></p>
                </center>
                """).formatted(userName,
                authentication == null ? login : "",
                authentication == null ? "" : logout,
                userName.equals("Anonymous") ? "" : userName.equals("USER") || userName.equals("ADMIN") ? user : "",
                userName.equals("Anonymous") ? "" : userName.equals("ADMIN") ? admin : "");
    }

    @GetMapping("/public")
    public String public_page(Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        } else {
            userName = "Anonymous";
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
    public String private_page_user(@AuthenticationPrincipal CustomUser customUser) {
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for User!</h2>
                <p>Username: %s</p>
                <p>E-mail: %s</p>
                <p>Role: %s</p>
                <a href='/'>Home</a>
                </center>
                """).formatted(customUser.username().toUpperCase(), customUser.email(), customUser.getAuthorities());
    }

    @GetMapping("/admin")
    public String private_page_admin(@AuthenticationPrincipal CustomUser customUser) {
        return ("""
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for Admin!</h2>
                <p>Username: %s</p>
                <p>E-mail: %s</p>
                <p>Roles: %s</p>
                <a href='/'>Home</a>
                </center>
                """).formatted(customUser.username().toUpperCase(), customUser.email(), customUser.getAuthorities());
    }
}
