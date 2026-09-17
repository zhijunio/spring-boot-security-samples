package com.example;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class WebauthnSecurityTests {

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
                .andExpect(content().string(containsString("webauthn")));
    }

    @Test
    void webauthnScriptIsAccessible() throws Exception {
        mockMvc.perform(get("/login/webauthn.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("setupRegistration")));
    }

    @Test
    void authenticatedUserCanOpenDefaultRegisterPage() throws Exception {
        mockMvc.perform(get("/webauthn/register").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No Passkeys")));
    }

    @Test
    void authenticatedUserCanAccessHome() throws Exception {
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, user"));
    }

}
