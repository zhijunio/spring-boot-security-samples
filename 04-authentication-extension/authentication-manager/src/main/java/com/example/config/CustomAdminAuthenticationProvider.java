package com.example.config;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomAdminAuthenticationProvider implements AuthenticationProvider {

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        IO.println("CustomAdminAuthenticationProvider: " + authentication);
        if (authentication.getPrincipal().equals("admin")) {
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_TEST"));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("admin", "", authorities);
            IO.println("CustomAdminAuthenticationProvider: " + usernamePasswordAuthenticationToken);
            return usernamePasswordAuthenticationToken;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
