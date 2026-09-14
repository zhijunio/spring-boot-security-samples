package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.config.CustomUser;

@Controller
public class HomeController {

    private String userName = "Anonymous";

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
            model.addAttribute("roles", authentication.getAuthorities().toString());
        } else {
            userName = "Anonymous";
            model.addAttribute("roles", "");
        }
        model.addAttribute("userName", userName);
        return "index";
    }

    @GetMapping("/public")
    public String public_page(Model model, Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        } else {
            userName = "Anonymous";
        }
        model.addAttribute("userName", userName);
        return "public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String private_page_user(Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String private_page_admin(Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("user", user);
        return "admin";
    }
}