package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ott")
public class OneTimeTokenRestController {

    private final AuthenticationManager authenticationManager;

    private final OneTimeTokenService oneTimeTokenService;

    public OneTimeTokenRestController(AuthenticationManager authenticationManager,
            OneTimeTokenService oneTimeTokenService) {
        this.authenticationManager = authenticationManager;
        this.oneTimeTokenService = oneTimeTokenService;
    }

    @PostMapping("/generate")
    public TokenResponse generate(@RequestBody GenerateTokenRequest request) {
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        }
        catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        OneTimeToken token = this.oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(authentication.getName()));
        return new TokenResponse(token.getTokenValue(), token.getUsername(), token.getExpiresAt());
    }

    public record GenerateTokenRequest(String username, String password) {
    }

    public record TokenResponse(String token, String username, java.time.Instant expiresAt) {
    }

}
