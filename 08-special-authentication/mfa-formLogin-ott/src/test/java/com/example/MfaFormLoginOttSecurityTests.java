package com.example;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
class MfaFormLoginOttSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousHomeRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void defaultLoginPageIsGenerated() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("Request a One-Time Token")));
    }

    @Test
    void passwordOnlyCannotAccessHome() throws Exception {
        mockMvc.perform(get("/").with(authentication(token(FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=ott&factor.reason=missing"));
    }

    @Test
    void passwordOnlyCanOpenDefaultOttSubmitPage() throws Exception {
        mockMvc.perform(get("/login/ott").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Please input the token")));
    }

    @Test
    void passwordAndOttAllowHome() throws Exception {
        mockMvc.perform(get("/").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY,
                        FactorGrantedAuthority.OTT_AUTHORITY))))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, user"));
    }

    private static UsernamePasswordAuthenticationToken token(String... factors) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        for (String factor : factors) {
            authorities.add(FactorGrantedAuthority.fromAuthority(factor));
        }
        User principal = new User("user", "n/a", authorities);
        return UsernamePasswordAuthenticationToken.authenticated(principal, "n/a", authorities);
    }

}
