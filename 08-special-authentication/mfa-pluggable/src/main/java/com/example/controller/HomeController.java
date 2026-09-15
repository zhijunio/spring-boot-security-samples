package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "public";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

}
