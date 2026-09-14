package com.example.config;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record CustomUser(String username, String name, String email, String password, boolean enabled,
                         List<String> roles) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(auth -> new SimpleGrantedAuthority(auth)).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return password();
    }

    @Override
    public String getUsername() {
        return username();
    }

    @Override
    public boolean isEnabled() {
        return enabled();
    }

}