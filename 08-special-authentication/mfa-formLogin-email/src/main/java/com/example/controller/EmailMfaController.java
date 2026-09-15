package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.config.EmailAuthenticationProvider;
import com.example.config.EmailAuthenticationToken;
import com.example.config.EmailCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/email")
public class EmailMfaController {

    private final EmailCodeService emailCodeService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public EmailMfaController(EmailCodeService emailCodeService, AuthenticationManager authenticationManager) {
        this.emailCodeService = emailCodeService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/request")
    @ResponseBody
    public String request(@RequestParam String username) {
        Authentication current = requirePasswordAuthentication(username);
        IO.println("Email code for " + current.getName() + ": " + emailCodeService.issue(current.getName()));
        return "redirect:/email/verify";
    }

    @GetMapping("/verify")
    public String verifyPage(Model model) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (current == null || !current.isAuthenticated() || "anonymousUser".equals(current.getPrincipal())) {
            return "redirect:/login";
        }
        model.addAttribute("username", current.getName());
        return "email-verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String username, @RequestParam String code,
                       HttpServletRequest request, HttpServletResponse response) {
        Authentication current = requirePasswordAuthentication(username);
        Authentication email = authenticationManager.authenticate(new EmailAuthenticationToken(username, code));
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>(current.getAuthorities());
        authorities.addAll(email.getAuthorities());
        Authentication complete = UsernamePasswordAuthenticationToken.authenticated(
                current.getPrincipal(), current.getCredentials(), authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(complete);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
        return "redirect:/";
    }

    private Authentication requirePasswordAuthentication(String username) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        boolean passwordFactor = current != null && current.getAuthorities().stream()
                .anyMatch(authority -> FactorGrantedAuthority.PASSWORD_AUTHORITY.equals(authority.getAuthority()));
        if (!passwordFactor || !current.getName().equals(username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password authentication is required");
        }
        return current;
    }
}
