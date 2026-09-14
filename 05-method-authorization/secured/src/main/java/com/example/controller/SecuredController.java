package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.AdminService;

@RestController
public class SecuredController {
    private final AdminService adminService;

    public SecuredController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String home() {
        return "Secured method authorization demo";
    }

    @GetMapping("/admin")
    public String admin() {
        return adminService.adminOperation();
    }
}
