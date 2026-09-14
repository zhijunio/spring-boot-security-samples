package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .oneTimeTokenLogin(ott -> ott
                        .loginPage("/ott/login")
                        .defaultSuccessUrl("/", true)
                        .showDefaultSubmitPage(false)
                        .loginProcessingUrl("/login/ott")
                        .permitAll()
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/public", "/main.css", "/favicon.ico").permitAll()
                        .requestMatchers("/login/ott", "/ott/sent", "/my-ott-submit").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}