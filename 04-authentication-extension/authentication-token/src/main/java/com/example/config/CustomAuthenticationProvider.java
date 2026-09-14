package com.example.config;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IO.println("CustomAuthenticationProvider: " + authentication);
        CustomAuthenticationToken usernamePasswordAuthenticationToken = new CustomAuthenticationToken();
        IO.println("CustomAuthenticationProvider: " + usernamePasswordAuthenticationToken);
        return usernamePasswordAuthenticationToken;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
