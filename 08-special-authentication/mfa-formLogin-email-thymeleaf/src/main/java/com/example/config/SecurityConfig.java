package com.example.config;

import com.example.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        EmailAuthenticationProvider.EMAIL_AUTHORITY
})
public class SecurityConfig {

    @Bean
    EmailCodeService emailCodeService() {
        return new EmailCodeService();
    }

    @Bean
    EmailMfaAuthenticationSuccessHandler emailMfaAuthenticationSuccessHandler(EmailCodeService emailCodeService) {
        return new EmailMfaAuthenticationSuccessHandler(emailCodeService);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
            EmailMfaAuthenticationSuccessHandler emailMfaAuthenticationSuccessHandler) throws Exception {
        http
                .authenticationManager(authenticationManager)
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(emailMfaAuthenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .defaultDeniedHandlerForMissingAuthority(
                                (request, response, exception) ->
                                        response.sendRedirect(request.getContextPath() + "/email/verify"),
                                EmailAuthenticationProvider.EMAIL_AUTHORITY))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/styles.css").permitAll()
                        .requestMatchers("/email/verify", "/email/request")
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
    EmailAuthenticationProvider emailAuthenticationProvider(EmailCodeService emailCodeService) {
        return new EmailAuthenticationProvider(emailCodeService);
    }

    @Bean
    AuthenticationManager authenticationManager(EmailAuthenticationProvider emailAuthenticationProvider,
            UserService users, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider passwordProvider = new DaoAuthenticationProvider(users);
        passwordProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(passwordProvider, emailAuthenticationProvider);
    }

}
