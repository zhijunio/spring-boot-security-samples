package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.config.CustomUser;

@Controller
public class HomeController {

    private String userName = "Anonymous";
    private Boolean isAuthenticated = false;
    private Boolean isUser = false;
    private Boolean isAdmin = false;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        if (authentication != null) {
            isAuthenticated = true;
            userName = authentication.getName().toUpperCase();
            isUser = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));
            isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            userName = "Anonymous";
            isAuthenticated = false;
            isUser = false;
            isAdmin = false;
        }
        model.addAttribute("name", userName);
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isUser", isUser);
        model.addAttribute("isAdmin", isAdmin);
        return "views/index";
    }

    @GetMapping("/public")
    public String public_page(Model model, Authentication authentication) {
        if (authentication != null) {
            userName = authentication.getName().toUpperCase();
        } else {
            userName = "Anonymous";
        }
        model.addAttribute("name", userName);
        return "views/public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String private_page_user(Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("customUser", user);
        return "views/user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String private_page_admin(Model model, @AuthenticationPrincipal CustomUser user) {
        model.addAttribute("customUser", user);
        return "views/admin";
    }
}