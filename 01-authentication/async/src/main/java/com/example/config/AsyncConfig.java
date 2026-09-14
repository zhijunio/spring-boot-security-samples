package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.ExecutorService;

@Configuration
public class AsyncConfig {

    @Bean
    ThreadPoolTaskExecutor delegateTaskExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(2);
        delegate.setMaxPoolSize(2);
        delegate.setQueueCapacity(10);
        delegate.setThreadNamePrefix("security-task-");
        delegate.initialize();
        return delegate;
    }

    @Bean
    AsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegateTaskExecutor) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegateTaskExecutor);
    }

    @Bean
    ExecutorService securityExecutorService(ThreadPoolTaskExecutor delegateTaskExecutor) {
        return new DelegatingSecurityContextExecutorService(delegateTaskExecutor.getThreadPoolExecutor());
    }

    @Bean
    ThreadPoolTaskExecutor decoratedTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("decorated-security-task-");
        executor.setTaskDecorator(delegate -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(SecurityContextHolder.getContext().getAuthentication());
            return () -> {
                try {
                    SecurityContextHolder.setContext(context);
                    delegate.run();
                }
                finally {
                    SecurityContextHolder.clearContext();
                }
            };
        });
        executor.initialize();
        return executor;
    }
}
