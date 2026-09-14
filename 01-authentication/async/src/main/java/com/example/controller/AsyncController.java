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

    @GetMapping("/async-user/runnable")
    public CompletableFuture<String> asyncUserRunnable() {
        return asyncUserService.currentUserWithRunnable();
    }

    @GetMapping("/async-user/callable")
    public CompletableFuture<String> asyncUserCallable() {
        return asyncUserService.currentUserWithCallable();
    }

    @GetMapping("/async-user/executor-service")
    public CompletableFuture<String> asyncUserExecutorService() {
        return asyncUserService.currentUserWithExecutorService();
    }

    @GetMapping("/async-user/task-decorator")
    public CompletableFuture<String> asyncUserTaskDecorator() {
        return asyncUserService.currentUserWithTaskDecorator();
    }

    @GetMapping("/async-user/explicit-context")
    public CompletableFuture<String> asyncUserExplicitContext() {
        return asyncUserService.currentUserWithExplicitContext();
    }
}
