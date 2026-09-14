package com.example.config;

import me.gosimple.nbvcxz.Nbvcxz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    void configure(AuthenticationManagerBuilder builder) {
        // The success handler needs the raw credential to run Nbvcxz after login.
        builder.eraseCredentials(false);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CompromisedPasswordChecker passwordChecker = new NbvcxzPasswordChecker(new Nbvcxz());
        return http.httpBasic(b -> {
                })
                .formLogin(l -> l.successHandler(new CompromisedPasswordAwareAuthenticationSuccessHandler(passwordChecker)).permitAll())
                .authorizeHttpRequests(a -> a.anyRequest().authenticated())
                .build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password(encoder.encode("safe-password")).roles("USER").build(),
                User.withUsername("compromised").password(encoder.encode("password")).roles("USER").build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
