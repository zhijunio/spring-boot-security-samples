package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class AsyncUserService {
    private static final Logger log = LoggerFactory.getLogger(AsyncUserService.class);

    private final AsyncTaskExecutor taskExecutor;
    private final ExecutorService securityExecutorService;
    private final ThreadPoolTaskExecutor decoratedTaskExecutor;

    public AsyncUserService(@Qualifier("taskExecutor") AsyncTaskExecutor taskExecutor,
                            ExecutorService securityExecutorService,
                            @Qualifier("decoratedTaskExecutor") ThreadPoolTaskExecutor decoratedTaskExecutor) {
        this.taskExecutor = taskExecutor;
        this.securityExecutorService = securityExecutorService;
        this.decoratedTaskExecutor = decoratedTaskExecutor;
    }

    public CompletableFuture<String> currentUser() {
        return taskExecutor.submitCompletable(() -> currentUserName());
    }

    public CompletableFuture<String> currentUserWithRunnable() {
        CompletableFuture<String> result = new CompletableFuture<>();
        Runnable task = () -> result.complete(currentUserName());
        taskExecutor.execute(DelegatingSecurityContextRunnable.create(task, currentContext()));
        return result;
    }

    public CompletableFuture<String> currentUserWithCallable() {
        CompletableFuture<String> result = new CompletableFuture<>();
        securityExecutorService.submit(DelegatingSecurityContextCallable.create(() -> {
            result.complete(currentUserName());
            return null;
        }, currentContext()));
        return result;
    }

    public CompletableFuture<String> currentUserWithExecutorService() {
        CompletableFuture<String> result = new CompletableFuture<>();
        securityExecutorService.submit(() -> result.complete(currentUserName()));
        return result;
    }

    public CompletableFuture<String> currentUserWithTaskDecorator() {
        CompletableFuture<String> result = new CompletableFuture<>();
        decoratedTaskExecutor.execute(() -> result.complete(currentUserName()));
        return result;
    }

    public CompletableFuture<String> currentUserWithExplicitContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        CompletableFuture<String> result = new CompletableFuture<>();
        taskExecutor.execute(() -> {
            try {
                SecurityContextHolder.setContext(context);
                result.complete(currentUserName());
            } finally {
                SecurityContextHolder.clearContext();
            }
        });
        return result;
    }

    private String currentUserName() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("currentUser: {}", user);
        return user;
    }

    private SecurityContext currentContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return context;
    }
}
