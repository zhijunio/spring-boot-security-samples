package com.example.controller;

import com.example.config.CustomUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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

    @GetMapping("/ott/sent")
    String ottSent() {
        return "ott-template";
    }

}