package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AllAuthoritiesAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.WEBAUTHN_AUTHORITY,
        FactorGrantedAuthority.X509_AUTHORITY
})
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .x509(Customizer.withDefaults())
                .webAuthn(web -> web
                        .allowedOrigins("https://localhost:8443")
                        .rpId("localhost")
                        .rpName("WebAuthn + X.509 MFA"))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/login/**", "/styles.css").permitAll()
                        .requestMatchers("/webauthn/**")
                        .access(AllAuthoritiesAuthorizationManager
                                .hasAllAuthorities(FactorGrantedAuthority.X509_AUTHORITY))
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authorities("USER")
                .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
