package com.example.mfa;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    private final Map<String, MfaProvider> providers;
    private final MfaBindingStore bindingStore;
    private final MfaPlatformSettings platformSettings;

    public MfaService(List<MfaProvider> providers, MfaBindingStore bindingStore,
                      MfaPlatformSettings platformSettings) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(MfaProvider::id, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        this.bindingStore = bindingStore;
        this.platformSettings = platformSettings;
    }

    public List<MfaProvider> allProviders() {
        return List.copyOf(providers.values());
    }

    public MfaProvider requireProvider(String providerId) {
        MfaProvider provider = providers.get(providerId);
        if (provider == null) {
            throw new BadCredentialsException("Unknown MFA provider");
        }
        return provider;
    }

    public boolean requiresSecondFactor(String username) {
        return !bindingStore.boundProviderIds(username).isEmpty();
    }

    public List<MfaProvider> availableForChallenge(String username) {
        return bindingStore.boundProviderIds(username).stream()
                .filter(platformSettings::isEnabled)
                .map(providers::get)
                .filter(provider -> provider != null)
                .toList();
    }

    public void prepare(String username, String providerId) {
        MfaProvider provider = requireAvailable(username, providerId);
        String payload = bindingStore.payload(username, providerId).orElseThrow();
        provider.prepare(username, payload);
    }

    public void prepareIfNeeded(String username) {
        for (MfaProvider provider : availableForChallenge(username)) {
            if (provider.needsPrepare()) {
                prepare(username, provider.id());
            }
        }
    }

    public void verify(String username, String providerId, String credential) {
        MfaProvider provider = requireAvailable(username, providerId);
        String payload = bindingStore.payload(username, providerId).orElseThrow();
        provider.verify(username, payload, credential);
    }

    public BindingMaterial beginBinding(String username, String providerId) {
        if (!platformSettings.isEnabled(providerId)) {
            throw new IllegalStateException("Provider is disabled by the platform");
        }
        return requireProvider(providerId).beginBinding(username);
    }

    public void completeBinding(String username, BindingMaterial material, String confirmation) {
        if (!platformSettings.isEnabled(material.providerId())) {
            throw new IllegalStateException("Provider is disabled by the platform");
        }
        MfaProvider provider = requireProvider(material.providerId());
        String payload = provider.completeBinding(username, material, confirmation);
        bindingStore.bind(username, material.providerId(), payload);
    }

    public void unbind(String username, String providerId) {
        bindingStore.unbind(username, providerId);
    }

    public Map<String, Boolean> platformSnapshot() {
        return platformSettings.snapshot();
    }

    public Map<String, String> bindingSnapshot(String username) {
        return bindingStore.snapshot(username);
    }

    public void setProviderEnabled(String providerId, boolean enabled) {
        platformSettings.setEnabled(providerId, enabled);
    }

    private MfaProvider requireAvailable(String username, String providerId) {
        if (!platformSettings.isEnabled(providerId)) {
            throw new BadCredentialsException("Provider is disabled");
        }
        if (!bindingStore.isBound(username, providerId)) {
            throw new BadCredentialsException("Provider is not bound");
        }
        return requireProvider(providerId);
    }

}
