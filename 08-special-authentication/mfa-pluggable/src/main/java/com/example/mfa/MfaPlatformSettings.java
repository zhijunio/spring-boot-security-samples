package com.example.mfa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class MfaPlatformSettings {

    private final Map<String, Boolean> enabled = new ConcurrentHashMap<>();

    public MfaPlatformSettings(List<MfaProvider> providers) {
        for (MfaProvider provider : providers) {
            enabled.put(provider.id(), true);
        }
    }

    public boolean isEnabled(String providerId) {
        return Boolean.TRUE.equals(enabled.get(providerId));
    }

    public void setEnabled(String providerId, boolean value) {
        if (!enabled.containsKey(providerId)) {
            throw new IllegalArgumentException("Unknown provider: " + providerId);
        }
        enabled.put(providerId, value);
    }

    public Map<String, Boolean> snapshot() {
        return new LinkedHashMap<>(enabled);
    }

}
