package com.example.controller;

import com.example.service.AsyncUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class AsyncController {

    private final AsyncUserService asyncUserService;

    public AsyncController(AsyncUserService asyncUserService) {
        this.asyncUserService = asyncUserService;
    }

    @GetMapping("/")
    public String home() {
        return "Spring Security async SecurityContext demo";
    }

    @GetMapping("/async-user")
    public CompletableFuture<String> asyncUser() {
        return asyncUserService.currentUser();
    }
}
