package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ott.GenerateOneTimeTokenFilter;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class SecurityConfig {

    private final OneTimeTokenService oneTimeTokenService;
    private final OneTimeTokenGenerationSuccessHandler tokenGenerationSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;
    //private final HttpServletRequest request;


    public SecurityConfig(OneTimeTokenService oneTimeTokenService, CustomUserDetailsService customUserDetailsService,
                          HttpServletRequest request,
                          OneTimeTokenGenerationSuccessHandler tokenGenerationSuccessHandler) {
        this.oneTimeTokenService = oneTimeTokenService;
        this.customUserDetailsService = customUserDetailsService;
        //this.request = request;
        this.tokenGenerationSuccessHandler = tokenGenerationSuccessHandler;
    }

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
                .addFilterAt(new MyGenerateOneTimeTokenFilter(oneTimeTokenService, tokenGenerationSuccessHandler, customUserDetailsService), GenerateOneTimeTokenFilter.class)
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