package com.example.mfa;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class MfaBindingStore {

    private final Map<String, Map<String, String>> bindings = new ConcurrentHashMap<>();

    public void bind(String username, String providerId, String payload) {
        bindings.computeIfAbsent(username, key -> new ConcurrentHashMap<>()).put(providerId, payload);
    }

    public void unbind(String username, String providerId) {
        Map<String, String> user = bindings.get(username);
        if (user != null) {
            user.remove(providerId);
            if (user.isEmpty()) {
                bindings.remove(username);
            }
        }
    }

    public Optional<String> payload(String username, String providerId) {
        Map<String, String> user = bindings.get(username);
        if (user == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.get(providerId));
    }

    public boolean isBound(String username, String providerId) {
        return payload(username, providerId).isPresent();
    }

    public Set<String> boundProviderIds(String username) {
        Map<String, String> user = bindings.get(username);
        if (user == null || user.isEmpty()) {
            return Set.of();
        }
        return Set.copyOf(user.keySet());
    }

    public Map<String, String> snapshot(String username) {
        Map<String, String> user = bindings.get(username);
        if (user == null) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(user));
    }

}
