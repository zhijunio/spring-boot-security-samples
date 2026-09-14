package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
                <center>
                <h1>Spring Boot Tutorial</h1>
                <a href='/public'>Public</a><br/>
                <a href='/user'>Private for User</a><br/>
                <a href='/admin'>Private for Admin</a>
                </center>
                """;
    }

    @GetMapping("/public")
    public String public_page() {
        return """
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Public Page!</h2>
                <a href='/'>Home</a>
                </center>
                """;
    }

    @GetMapping("/user")
    public String private_page_user() {
        return """
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for User!</h2>
                <a href='/'>Home</a>
                </center>
                """;
    }

    @GetMapping("/admin")
    public String private_page_admin() {
        return """
                <center>
                <h1>Spring Boot Tutorial</h1>
                <h2>Private Page for Admin!</h2>
                <a href='/'>Home</a>
                </center>
                """;
    }
}
