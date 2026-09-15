package com.example.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.mfa.MfaAuthorities;
import com.example.mfa.MfaProvider;
import com.example.mfa.MfaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/mfa/challenge")
public class MfaChallengeController {

    private final MfaService mfaService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public MfaChallengeController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping
    public String challenge(Model model) {
        Authentication current = requirePasswordFactor();
        List<MfaProvider> providers = mfaService.availableForChallenge(current.getName());
        model.addAttribute("username", current.getName());
        model.addAttribute("providers", providers);
        model.addAttribute("blocked", providers.isEmpty() && mfaService.requiresSecondFactor(current.getName()));
        return "mfa-challenge";
    }

    @PostMapping("/prepare")
    public String prepare(@RequestParam String providerId) {
        Authentication current = requirePasswordFactor();
        try {
            mfaService.prepare(current.getName(), providerId);
            return "redirect:/mfa/challenge?provider=" + providerId;
        } catch (BadCredentialsException | IllegalStateException ex) {
            return "redirect:/mfa/challenge?error";
        }
    }

    @PostMapping
    public String verify(@RequestParam String providerId, @RequestParam String credential,
                         HttpServletRequest request, HttpServletResponse response) {
        Authentication current = requirePasswordFactor();
        try {
            mfaService.verify(current.getName(), providerId, credential);
            List<GrantedAuthority> authorities = new ArrayList<>(current.getAuthorities());
            authorities.add(FactorGrantedAuthority.withAuthority(MfaAuthorities.MFA_AUTHORITY)
                    .issuedAt(Instant.now())
                    .build());
            Authentication complete = UsernamePasswordAuthenticationToken.authenticated(
                    current.getPrincipal(), current.getCredentials(), authorities);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(complete);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/";
        } catch (BadCredentialsException ex) {
            return "redirect:/mfa/challenge?error&provider=" + providerId;
        }
    }

    private Authentication requirePasswordFactor() {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (current == null || !current.isAuthenticated() || !hasPasswordFactor(current)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return current;
    }

    private static boolean hasPasswordFactor(Authentication current) {
        return current.getAuthorities().stream()
                .anyMatch(authority -> FactorGrantedAuthority.PASSWORD_AUTHORITY.equals(authority.getAuthority()));
    }

}
