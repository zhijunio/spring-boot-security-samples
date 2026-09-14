package com.example.config;

import java.time.Duration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthorizationManagerFactory<Object> globelMFA) throws Exception {
        DefaultAuthorizationManagerFactory<Object> userMFA = new DefaultAuthorizationManagerFactory<Object>();
        http
                .formLogin(login -> login
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .oneTimeTokenLogin(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/public").permitAll()
                        .requestMatchers("/ott/sent", "/login/ott").permitAll()
                        .requestMatchers("/user/**").access(userMFA.authenticated())
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").access(globelMFA.authenticated())
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    UserDetailsService users(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthorizationManagerFactory<Object> authz() {
        return AuthorizationManagerFactories.multiFactor()
                .requireFactor(b -> b.passwordAuthority().validDuration(Duration.ofMinutes(5)))
                .requireFactor(b -> b.ottAuthority().validDuration(Duration.ofMinutes(5)))
                .build();
    }

}