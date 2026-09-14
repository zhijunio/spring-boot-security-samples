package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "User location demo. Submit location=office when logging in.";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "Authenticated from the office location";
    }
}
