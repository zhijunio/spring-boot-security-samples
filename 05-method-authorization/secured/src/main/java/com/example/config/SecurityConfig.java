package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.httpBasic(config -> {
                }).csrf(config -> config.disable())
                .authorizeHttpRequests(a -> a.requestMatchers("/").permitAll().anyRequest().authenticated()).build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder p) {
        return new InMemoryUserDetailsManager(User.withUsername("user").password(p.encode("password")).roles("USER").build(), User.withUsername("admin").password(p.encode("password")).roles("ADMIN").build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
