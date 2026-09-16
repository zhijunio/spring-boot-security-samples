package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.ott.JdbcOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

    @Bean
    OneTimeTokenService oneTimeTokenService(JdbcTemplate jdbcTemplate) {
        return new JdbcOneTimeTokenService(jdbcTemplate);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    AuthorizationManagerFactory<Object> authorizationManagerFactory) throws Exception {
        http.formLogin(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/").deleteCookies("JSESSIONID").permitAll())
                .oneTimeTokenLogin(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public", "/ott/sent", "/login/ott").permitAll()
                        .requestMatchers("/user/**").access(authorizationManagerFactory.hasRole("USER"))
                        .requestMatchers("/admin/**").access(authorizationManagerFactory.hasRole("ADMIN"))
                        .anyRequest().authenticated());
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
                .requireFactor(builder -> builder.passwordAuthority().validDuration(Duration.ofMinutes(5)))
                .requireFactor(builder -> builder.ottAuthority().validDuration(Duration.ofMinutes(5)))
                .build();
    }
}
