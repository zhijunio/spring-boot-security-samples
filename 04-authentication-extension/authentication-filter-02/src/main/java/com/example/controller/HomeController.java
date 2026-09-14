package com.example.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home_page() {

        return "HOME PAGE";
    }

    @GetMapping("/public")
    public String pubic_page() {

        return "PUBLIC PAGE";
    }

    @GetMapping("/private")
    public String private_page() {

        return "PRIVATE PAGE";
    }

    @PostFilter("filterObject.startsWith(authentication.name)")
    @GetMapping("/messages")
    public List<String> messages() {
        return List.of("user: private message", "admin: private message");
    }

    @PreFilter(value = "filterObject.startsWith(authentication.name)", filterTarget = "messages")
    @PostMapping("/messages/sent")
    public List<String> sentMessages(@RequestBody List<String> messages, Authentication authentication) {
        return messages;
    }
}
