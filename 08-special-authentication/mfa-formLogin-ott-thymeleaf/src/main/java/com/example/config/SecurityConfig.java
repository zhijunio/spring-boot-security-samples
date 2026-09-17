package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.ott.JdbcOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenService;
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
        FactorGrantedAuthority.OTT_AUTHORITY
})
public class SecurityConfig {

    @Bean
    OneTimeTokenService oneTimeTokenService(JdbcTemplate jdbcTemplate) {
        return new JdbcOneTimeTokenService(jdbcTemplate);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect(request.getContextPath() + "/login?factor.type=ott"))
                        .permitAll())
                .oneTimeTokenLogin(ott -> ott
                        .loginPage("/login/ott")
                        .showDefaultSubmitPage(false))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/login/ott", "/ott/sent", "/styles.css").permitAll()
                        .requestMatchers("/ott/generate")
                        .access(AllAuthoritiesAuthorizationManager
                                .hasAllAuthorities(FactorGrantedAuthority.PASSWORD_AUTHORITY))
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
