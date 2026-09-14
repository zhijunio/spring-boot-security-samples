package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Password checker demo. Login with user / safe-password.";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "Authenticated with a non-compromised password";
    }
}
