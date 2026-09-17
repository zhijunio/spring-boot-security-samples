package com.example.controller;

import java.security.Principal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final DateTimeFormatter CREATED = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneOffset.UTC);

    private final PublicKeyCredentialUserEntityRepository userEntities;

    private final UserCredentialRepository userCredentials;

    public HomeController(PublicKeyCredentialUserEntityRepository userEntities,
            UserCredentialRepository userCredentials) {
        this.userEntities = userEntities;
        this.userCredentials = userCredentials;
    }

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        addPasskeys(principal.getName(), model);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/webauthn/register")
    public String registerPasskey(Principal principal, CsrfToken csrf, Model model) {
        model.addAttribute("csrfHeader", csrf.getHeaderName());
        model.addAttribute("csrfToken", csrf.getToken());
        addPasskeys(principal.getName(), model);
        return "register-webauthn";
    }

    private void addPasskeys(String username, Model model) {
        model.addAttribute("passkeys", passkeys(username));
    }

    private List<PasskeyView> passkeys(String username) {
        PublicKeyCredentialUserEntity entity = this.userEntities.findByUsername(username);
        if (entity == null) {
            return List.of();
        }
        return this.userCredentials.findByUserId(entity.getId()).stream().map(HomeController::view).toList();
    }

    private static PasskeyView view(CredentialRecord passkey) {
        return new PasskeyView(passkey.getLabel(),
                CREATED.format(passkey.getCreated().truncatedTo(ChronoUnit.MINUTES)) + " UTC",
                passkey.getCredentialId().toBase64UrlString());
    }

    public record PasskeyView(String label, String created, String credentialId) {
    }

}
