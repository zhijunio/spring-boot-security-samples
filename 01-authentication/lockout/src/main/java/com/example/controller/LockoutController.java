package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LockoutController {
    @GetMapping("/")
    public String home() {
        return "Account lockout demo";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "active user";
    }
}
