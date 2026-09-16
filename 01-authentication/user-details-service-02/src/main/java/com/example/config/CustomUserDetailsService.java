package com.example.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.concurrent.ConcurrentHashMap;

public class CustomUserDetailsService implements UserDetailsService {

    private final ConcurrentHashMap<String, CustomUser> users = new ConcurrentHashMap<>();

    public CustomUserDetailsService(CustomUser... users) {
        for (CustomUser user : users) {
            this.users.put(user.getUsername(), user);
        }
    }

    @Override
    @NullMarked
    public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = users.get(username);
        if (user == null || !user.getUsername().equals(username)) {
            throw UsernameNotFoundException.fromUsername(username);
        }
        return new CustomUser(user.getUsername(), user.getPassword(), user.email(), user.getAuthorities(), user.isEnabled());
    }
}
