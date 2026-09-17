package com.example.config;

import com.example.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AllAuthoritiesAuthorizationManager;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.PASSWORD_AUTHORITY,
        TotpAuthenticationProvider.TOTP_AUTHORITY
})
public class SecurityConfig {

    @Bean
    TotpService totpService(JdbcClient jdbc) {
        return new TotpService(jdbc);
    }

    @Bean
    TotpMfaAuthenticationSuccessHandler totpMfaAuthenticationSuccessHandler() {
        return new TotpMfaAuthenticationSuccessHandler();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        http
                .authenticationManager(authenticationManager)
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(totpMfaAuthenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .defaultDeniedHandlerForMissingAuthority(
                                (request, response, exception) ->
                                        response.sendRedirect(request.getContextPath() + "/totp/verify"),
                                TotpAuthenticationProvider.TOTP_AUTHORITY))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/styles.css").permitAll()
                        .requestMatchers("/totp/verify")
                        .access(AllAuthoritiesAuthorizationManager
                                .hasAllAuthorities(FactorGrantedAuthority.PASSWORD_AUTHORITY))
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    TotpAuthenticationProvider totpAuthenticationProvider(TotpService totpService) {
        return new TotpAuthenticationProvider(totpService);
    }

    @Bean
    AuthenticationManager authenticationManager(TotpAuthenticationProvider totpAuthenticationProvider,
            UserService users, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider passwordProvider = new DaoAuthenticationProvider(users);
        passwordProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(passwordProvider, totpAuthenticationProvider);
    }

}
