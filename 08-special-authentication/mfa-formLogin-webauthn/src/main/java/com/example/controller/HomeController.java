package com.example.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.config.CustomUser;
import com.example.config.CustomUserRepository;

@Controller
public class HomeController {

    private final CustomUserRepository customUserRepository;

    public HomeController(CustomUserRepository customUserRepository) {
        this.customUserRepository = customUserRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/public")
    public String public_page() {
        return "public";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String private_page_user(Model model, Principal principal) {
        CustomUser customUser = customUserRepository.findByUsername(principal.getName());
        model.addAttribute("user", customUser);
        return "user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String private_page_admin(Model model, Principal principal) {
        CustomUser customUser = customUserRepository.findByUsername(principal.getName());
        model.addAttribute("user", customUser);
        return "admin";
    }
}
