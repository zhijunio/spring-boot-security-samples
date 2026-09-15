package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.config.BackupCodeAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
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
@RequestMapping("/backup")
public class BackupCodeMfaController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public BackupCodeMfaController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/verify")
    public String verifyPage(Model model) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (!hasPasswordFactor(current)) {
            return "redirect:/login";
        }
        model.addAttribute("username", current.getName());
        return "backup-verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String username, @RequestParam String mnemonic,
                         HttpServletRequest request, HttpServletResponse response) {
        Authentication current = requirePasswordAuthentication(username);
        try {
            Authentication backup = authenticationManager.authenticate(
                    new BackupCodeAuthenticationToken(username, mnemonic));
            List<GrantedAuthority> authorities = new ArrayList<>(current.getAuthorities());
            authorities.addAll(backup.getAuthorities());
            Authentication complete = UsernamePasswordAuthenticationToken.authenticated(
                    current.getPrincipal(), current.getCredentials(), authorities);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(complete);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/";
        } catch (BadCredentialsException ex) {
            return "redirect:/backup/verify?error";
        }
    }

    private Authentication requirePasswordAuthentication(String username) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (!hasPasswordFactor(current) || !current.getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password authentication is required");
        }
        return current;
    }

    private boolean hasPasswordFactor(Authentication current) {
        return current != null && current.getAuthorities().stream()
                .anyMatch(authority -> FactorGrantedAuthority.PASSWORD_AUTHORITY.equals(authority.getAuthority()));
    }

}
