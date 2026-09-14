package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionController {
    @GetMapping("/")
    public String home() {
        return "Exception handling demo";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
