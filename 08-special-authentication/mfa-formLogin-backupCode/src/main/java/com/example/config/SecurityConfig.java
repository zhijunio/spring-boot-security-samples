package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {
        FactorGrantedAuthority.PASSWORD_AUTHORITY,
        BackupCodeAuthenticationProvider.BACKUP_CODE_AUTHORITY
})
public class SecurityConfig {

    @Bean
    Bip39 bip39() {
        return new Bip39();
    }

    @Bean
    BackupCodeService backupCodeService(Bip39 bip39) {
        return new BackupCodeService(bip39);
    }

    @Bean
    BackupCodeMfaAuthenticationSuccessHandler backupCodeMfaAuthenticationSuccessHandler() {
        return new BackupCodeMfaAuthenticationSuccessHandler();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        http
                .authenticationManager(authenticationManager)
                .formLogin(login -> login
                        .successHandler(backupCodeMfaAuthenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .defaultDeniedHandlerForMissingAuthority(
                                (request, response, exception) ->
                                        response.sendRedirect(request.getContextPath() + "/backup/verify"),
                                BackupCodeAuthenticationProvider.BACKUP_CODE_AUTHORITY))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/public").permitAll()
                        .requestMatchers("/backup/verify").permitAll()
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    BackupCodeAuthenticationProvider backupCodeAuthenticationProvider(BackupCodeService backupCodeService) {
        return new BackupCodeAuthenticationProvider(backupCodeService);
    }

    @Bean
    AuthenticationManager authenticationManager(BackupCodeAuthenticationProvider backupCodeAuthenticationProvider,
                                                 UserDetailsService users,
                                                 PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider passwordProvider = new DaoAuthenticationProvider(users);
        passwordProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(passwordProvider, backupCodeAuthenticationProvider);
    }

}
