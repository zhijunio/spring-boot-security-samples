package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class AuthenticationFilter01Application {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationFilter01Application.class, args);
    }

}
