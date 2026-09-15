package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.config.EmailCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MfaEmailApplicationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailCodeService emailCodeService;

    @Test
    void passwordLoginRedirectsToEmailVerification() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email/verify"));
    }

    @Test
    void emailVerificationCompletesMfaAndAllowsProtectedPage() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String code = emailCodeService.issue("user");

        mockMvc.perform(post("/email/verify")
                        .session((org.springframework.mock.web.MockHttpSession) login.getRequest().getSession())
                        .param("username", "user")
                        .param("code", code)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/user")
                        .session((org.springframework.mock.web.MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().isOk());
    }
}
