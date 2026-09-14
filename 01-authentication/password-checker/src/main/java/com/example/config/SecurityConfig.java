package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.httpBasic(b -> {
                }).formLogin(l -> l.permitAll())
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .build();
    }

    @Bean
    UserDetailsService users() {
        return new InMemoryUserDetailsManager(User.withUsername("user")
                .password("safe-password")
                .roles("USER")
                .build(), User.withUsername("compromised")
                .password("password")
                .roles("USER")
                .build());
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService users,
                                                     PasswordEncoder passwordEncoder, CompromisedPasswordChecker passwordChecker) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setCompromisedPasswordChecker(passwordChecker);
        return provider;
    }

    @Bean
    CompromisedPasswordChecker passwordChecker() {
        return new ResourcePasswordChecker(new ClassPathResource("10-million-password-list-top-1000000.txt"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
