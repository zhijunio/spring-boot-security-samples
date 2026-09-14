package com.example.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {
    @GetMapping("/")
    public String home(CsrfToken token) {
        return "CSRF token attribute: " + token.getParameterName();
    }

    @PostMapping("/change")
    public String change() {
        return "changed";
    }
}
