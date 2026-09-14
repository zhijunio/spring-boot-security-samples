package com.example.config;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    public CustomAuthenticationToken() {
        super(AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN", "ROLE_CUSTOMROLE"));

    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return "Custom User";
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        throw new RuntimeException("Don't do this!!");
    }


}
