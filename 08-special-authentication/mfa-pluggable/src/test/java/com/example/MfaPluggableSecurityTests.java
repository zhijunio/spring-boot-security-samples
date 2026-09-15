package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.config.SecurityConfig;
import com.example.mfa.MfaPlatformSettings;
import com.example.mfa.TotpMfaProvider;
import com.example.mfa.TotpSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MfaPluggableSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TotpSupport totpSupport;

    @Autowired
    private MfaPlatformSettings platformSettings;

    @Test
    void plainPasswordLoginSkipsChallenge() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "plain")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void boundUserIsRedirectedToChallenge() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mfa/challenge"));
    }

    @Test
    void passwordOnlyCannotAccessUserPage() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get("/user")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mfa/challenge"));
    }

    @Test
    void totpChallengeCompletesMfa() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/mfa/challenge")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("providerId", TotpMfaProvider.ID)
                        .param("credential", totpSupport.currentCode(SecurityConfig.USER_TOTP_SECRET))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/user")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().isOk());
    }

    @Test
    void multiUserCanCompleteWithBip39() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "multi")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/mfa/challenge")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("providerId", "bip39")
                        .param("credential", SecurityConfig.MULTI_BIP39)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void disablingLastProviderDoesNotAllowPasswordOnlyAccess() throws Exception {
        platformSettings.setEnabled(TotpMfaProvider.ID, false);
        try {
            MvcResult login = mockMvc.perform(post("/login")
                            .param("username", "user")
                            .param("password", "password")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mfa/challenge"))
                    .andReturn();

            mockMvc.perform(get("/user")
                            .session((MockHttpSession) login.getRequest().getSession()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/mfa/challenge"));
        } finally {
            platformSettings.setEnabled(TotpMfaProvider.ID, true);
        }
    }

    @Test
    void reenablingProviderAllowsTotpAgain() throws Exception {
        platformSettings.setEnabled(TotpMfaProvider.ID, false);
        platformSettings.setEnabled(TotpMfaProvider.ID, true);

        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/mfa/challenge")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("providerId", TotpMfaProvider.ID)
                        .param("credential", totpSupport.currentCode(SecurityConfig.USER_TOTP_SECRET))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}
