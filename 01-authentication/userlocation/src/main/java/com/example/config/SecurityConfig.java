package com.example.config;

import com.example.security.UserLocationAuthenticationDetailsSource;
import com.example.security.UserLocationAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider provider) throws Exception {
        return http.authenticationProvider(provider)
                .formLogin(l -> l.authenticationDetailsSource(new UserLocationAuthenticationDetailsSource()).permitAll())
                .authorizeHttpRequests(a -> a.requestMatchers("/").permitAll().anyRequest().authenticated())
                .build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        return new UserLocationAuthenticationProvider();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
