package com.example.mfa;

import java.util.Map;

/**
 * Opaque binding state kept in the HTTP session until the user confirms.
 */
public record BindingMaterial(String providerId, String secret, Map<String, String> attributes) {

    public BindingMaterial(String providerId, String secret) {
        this(providerId, secret, Map.of());
    }

}
