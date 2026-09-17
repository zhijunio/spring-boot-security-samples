package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/login/ott")
    public String ottLogin() {
        return "login-ott";
    }

    @GetMapping("/ott/sent")
    public String ottSent() {
        return "ott-sent";
    }

}
