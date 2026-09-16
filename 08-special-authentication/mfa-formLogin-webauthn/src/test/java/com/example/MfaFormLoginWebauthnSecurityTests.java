package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MfaFormLoginWebauthnSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicHomeIsAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void passwordOnlyCannotAccessUserPage() throws Exception {
        mockMvc.perform(get("/user").with(authentication(token("user", "ROLE_USER",
                        FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=webauthn&factor.reason=missing"));
    }

    @Test
    void passwordAndWebAuthnAllowUserPage() throws Exception {
        mockMvc.perform(get("/user").with(authentication(token("user", "ROLE_USER",
                        FactorGrantedAuthority.PASSWORD_AUTHORITY,
                        FactorGrantedAuthority.WEBAUTHN_AUTHORITY))))
                .andExpect(status().isOk());
    }

    private static UsernamePasswordAuthenticationToken token(String username, String role, String... factors) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        for (String factor : factors) {
            authorities.add(FactorGrantedAuthority.fromAuthority(factor));
        }
        User principal = new User(username, "n/a", authorities);
        return UsernamePasswordAuthenticationToken.authenticated(principal, "n/a", authorities);
    }
}
