package com.example.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {
    @GetMapping("/")
    public String home() {
        return "Session management demo";
    }

    @GetMapping("/session")
    public String session(HttpSession s) {
        return "session=" + s.getId();
    }
}
