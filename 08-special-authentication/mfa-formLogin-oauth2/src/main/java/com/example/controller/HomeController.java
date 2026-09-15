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
    public String public_page() {
        return "public";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

}
