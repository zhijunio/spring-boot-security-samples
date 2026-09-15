package com.example.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.example.mfa.MfaAuthorities;
import com.example.mfa.MfaService;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.FactorAuthorizationDecision;
import org.springframework.security.authorization.RequiredFactor;
import org.springframework.security.authorization.RequiredFactorError;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Always requires {@link FactorGrantedAuthority#PASSWORD_AUTHORITY}.
 * Requires {@link MfaAuthorities#MFA_AUTHORITY} when the account still has any binding.
 */
public class PluggableMfaAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final MfaService mfaService;

    public PluggableMfaAuthorizationManager(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @Override
    public AuthorizationResult authorize(Supplier<? extends Authentication> authentication,
                                         RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        List<RequiredFactorError> errors = new ArrayList<>();
        if (auth == null || !auth.isAuthenticated()
                || !hasAuthority(auth, FactorGrantedAuthority.PASSWORD_AUTHORITY)) {
            errors.add(RequiredFactorError.createMissing(
                    RequiredFactor.withAuthority(FactorGrantedAuthority.PASSWORD_AUTHORITY).build()));
            return new FactorAuthorizationDecision(errors);
        }
        if (mfaService.requiresSecondFactor(auth.getName())
                && !hasAuthority(auth, MfaAuthorities.MFA_AUTHORITY)) {
            errors.add(RequiredFactorError.createMissing(
                    RequiredFactor.withAuthority(MfaAuthorities.MFA_AUTHORITY).build()));
        }
        return new FactorAuthorizationDecision(errors);
    }

    public static AuthorizationManager<RequestAuthorizationContext> withRole(String role, MfaService mfaService) {
        return AuthorizationManagers.allOf(
                AuthorityAuthorizationManager.hasRole(role),
                new PluggableMfaAuthorizationManager(mfaService));
    }

    private static boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }

}
