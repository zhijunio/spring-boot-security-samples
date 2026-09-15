package com.example.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.PASSWORD_AUTHORITY,
        EmailAuthenticationProvider.EMAIL_AUTHORITY
})
public class SecurityConfig {

    @Bean
    EmailMfaAuthenticationSuccessHandler emailMfaAuthenticationSuccessHandler(EmailCodeService emailCodeService) {
        return new EmailMfaAuthenticationSuccessHandler(emailCodeService);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .authenticationManager(authenticationManager)
                .formLogin(login -> login
                        .successHandler(emailMfaAuthenticationSuccessHandler(emailCodeService()))
                        .failureHandler((request, response, exception) -> response.sendRedirect("/login?error"))
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/public", "/login").permitAll()
                        .requestMatchers("/email/request", "/email/verify").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
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
    EmailCodeService emailCodeService() {
        return new EmailCodeService();
    }

    @Bean
    EmailAuthenticationProvider emailAuthenticationProvider(EmailCodeService emailCodeService) {
        return new EmailAuthenticationProvider(emailCodeService);
    }

    @Bean
    AuthenticationManager authenticationManager(EmailAuthenticationProvider provider,
                                                 UserDetailsService users,
                                                 PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider passwordProvider = new DaoAuthenticationProvider(users);
        passwordProvider.setPasswordEncoder(passwordEncoder);
        var manager = new org.springframework.security.authentication.ProviderManager(passwordProvider, provider);
        return manager;
    }

}
