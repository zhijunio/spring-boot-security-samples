package com.example.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Secured("ROLE_ADMIN")
    public String adminOperation() {
        return "admin operation";
    }
}
