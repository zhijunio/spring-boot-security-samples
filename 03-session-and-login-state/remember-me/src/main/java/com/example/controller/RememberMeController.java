package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RememberMeController {
    @GetMapping("/")
    public String home() {
        return "Remember-Me demo";
    }

    @GetMapping("/private")
    public String privatePage() {
        return "remembered user";
    }
}
