package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.x509;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MfaWebauthnX509SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void anonymousHomeRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/").with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void x509OnlyRedirectsToWebAuthnLogin() throws Exception {
        mockMvc.perform(get("/").with(x509("certs/user.crt")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=webauthn&factor.reason=missing"));
    }

    @Test
    void webAuthnAndX509AllowHome() throws Exception {
        User user = new User("user", "n/a", List.of(
                new SimpleGrantedAuthority("USER"),
                FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.WEBAUTHN_AUTHORITY),
                FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.X509_AUTHORITY)));
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(user, "n/a", user.getAuthorities());

        mockMvc.perform(get("/").with(authentication(authentication)))
                .andExpect(status().isOk());
    }

}
