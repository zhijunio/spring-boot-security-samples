package com.example.config;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authorization.EnableMultiFactorAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMultiFactorAuthentication(authorities = {})
public class SecurityConfig {

    public static final String SCOPE = "https://www.googleapis.com/auth/gmail.readonly";

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationEntryPoint oauth2) throws Exception {
        http
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .defaultDeniedHandlerForMissingAuthority(oauth2, "SCOPE_" + SCOPE))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public").permitAll()
                        .requestMatchers("/profile").hasAuthority("SCOPE_" + SCOPE)
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    ClientRegistrationRepository clients() {
        ClientRegistration google = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(System.getenv().getOrDefault("GOOGLE_CLIENT_ID", "id"))
                .clientSecret(System.getenv().getOrDefault("GOOGLE_CLIENT_SECRET", "secret"))
                .scope("openid", "profile", "email", SCOPE)
                .build();
        return new InMemoryClientRegistrationRepository(google);
    }

}
