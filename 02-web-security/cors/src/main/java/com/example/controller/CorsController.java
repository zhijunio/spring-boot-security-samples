package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {
    @GetMapping("/")
    public String home() {
        return "CORS demo";
    }

    @GetMapping("/api/public")
    public String api() {
        return "public api";
    }
}
