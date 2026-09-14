package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.formLogin(l -> l.permitAll()).authorizeHttpRequests(a -> a.requestMatchers("/").permitAll().anyRequest().authenticated()).build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder p) {
        return new InMemoryUserDetailsManager(User.withUsername("locked").password(p.encode("password")).roles("USER").accountLocked(true).build(), User.withUsername("active").password(p.encode("password")).roles("USER").build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
