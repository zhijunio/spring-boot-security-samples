package com.example.service;

import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncUserService {

    private final AsyncTaskExecutor taskExecutor;

    public AsyncUserService(AsyncTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public CompletableFuture<String> currentUser() {
        return taskExecutor.submitCompletable(() ->
                SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
