package com.example.config;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private List<AuthenticationProvider> providers = Collections.emptyList();

    public CustomAuthenticationManager(List<AuthenticationProvider> providers) {
        this.providers = providers;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication result = null;
        IO.println("CustomAuthenticationManager: " + authentication);
        IO.println("List AuthenticationProvider: " + this.providers.size() + " - " + this.providers);

        for (AuthenticationProvider provider : providers) {
            result = provider.authenticate(authentication);
            if (result != null) {
                break;
            }
        }
        return result;
    }

}
