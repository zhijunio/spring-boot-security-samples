package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor delegate = new ThreadPoolTaskExecutor();
        delegate.setCorePoolSize(2);
        delegate.setMaxPoolSize(2);
        delegate.setQueueCapacity(10);
        delegate.setThreadNamePrefix("security-task-");
        delegate.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
