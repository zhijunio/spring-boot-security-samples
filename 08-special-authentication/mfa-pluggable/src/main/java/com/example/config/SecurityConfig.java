package com.example.config;

import com.example.mfa.Bip39MfaProvider;
import com.example.mfa.EmailMfaProvider;
import com.example.mfa.MfaAuthorities;
import com.example.mfa.MfaBindingStore;
import com.example.mfa.MfaService;
import com.example.mfa.TotpMfaProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

    public static final String USER_TOTP_SECRET = "JBSWY3DPEHPK3PXP";
    public static final String MULTI_TOTP_SECRET = "JBSWY3DPEHPK3PXP";
    public static final String ADMIN_TOTP_SECRET = "KVKFKRCPNZQUYMLX";
    /** Valid BIP-39 English mnemonic (demo only). */
    public static final String MULTI_BIP39 =
            "payment noodle vivid slogan gather metal pilot enact fragile hip physical canvas";

    @Bean
    PluggableMfaAuthenticationSuccessHandler pluggableMfaAuthenticationSuccessHandler(MfaService mfaService) {
        return new PluggableMfaAuthenticationSuccessHandler(mfaService);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager,
                                    MfaService mfaService,
                                    PluggableMfaAuthenticationSuccessHandler successHandler) throws Exception {
        http
                .authenticationManager(authenticationManager)
                .formLogin(login -> login
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .defaultDeniedHandlerForMissingAuthority(
                                (request, response, exception) ->
                                        response.sendRedirect(request.getContextPath() + "/mfa/challenge"),
                                MfaAuthorities.MFA_AUTHORITY))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public").permitAll()
                        .requestMatchers("/mfa/challenge", "/mfa/challenge/**").authenticated()
                        .requestMatchers("/mfa/settings", "/mfa/settings/**").access(
                                PluggableMfaAuthorizationManager.withRole("USER", mfaService))
                        .requestMatchers("/mfa/platform", "/mfa/platform/**").access(
                                PluggableMfaAuthorizationManager.withRole("ADMIN", mfaService))
                        .requestMatchers("/user/**").access(
                                PluggableMfaAuthorizationManager.withRole("USER", mfaService))
                        .requestMatchers("/admin/**").access(
                                PluggableMfaAuthorizationManager.withRole("ADMIN", mfaService))
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder passwordEncoder) {
        UserDetails plain = User.builder()
                .username("plain")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails mailer = User.builder()
                .username("mailer")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails multi = User.builder()
                .username("multi")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(plain, user, mailer, multi, admin);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsService users, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider passwordProvider = new DaoAuthenticationProvider(users);
        passwordProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(passwordProvider);
    }

    @Bean
    ApplicationRunner seedBindings(MfaBindingStore bindingStore, Bip39MfaProvider bip39MfaProvider) {
        return args -> {
            bindingStore.bind("user", TotpMfaProvider.ID, USER_TOTP_SECRET);
            bindingStore.bind("mailer", EmailMfaProvider.ID, "bound");
            bindingStore.bind("multi", TotpMfaProvider.ID, MULTI_TOTP_SECRET);
            bindingStore.bind("multi", Bip39MfaProvider.ID, bip39MfaProvider.hashOf(MULTI_BIP39));
            bindingStore.bind("admin", TotpMfaProvider.ID, ADMIN_TOTP_SECRET);
        };
    }

}
