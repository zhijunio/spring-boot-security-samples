package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.exceptionHandling(e -> e.authenticationEntryPoint(entryPoint()).accessDeniedHandler(deniedHandler())).formLogin(l -> l.permitAll()).authorizeHttpRequests(a -> a.requestMatchers("/").permitAll().requestMatchers("/admin").hasRole("ADMIN").anyRequest().authenticated()).build();
    }

    @Bean
    AuthenticationEntryPoint entryPoint() {
        return (req, res, ex) -> res.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication required");
    }

    @Bean
    AccessDeniedHandler deniedHandler() {
        return (req, res, ex) -> res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied");
    }

    @Bean
    UserDetailsService users(PasswordEncoder p) {
        return new InMemoryUserDetailsManager(User.withUsername("user").password(p.encode("password")).roles("USER").build(), User.withUsername("admin").password(p.encode("password")).roles("ADMIN").build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
