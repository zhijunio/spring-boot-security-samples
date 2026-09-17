package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.config.EmailCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MfaFormLoginEmailSecurityTests {

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
    void passwordOnlyCannotAccessHome() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get("/")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email/verify"));
    }

    @Test
    void emailVerificationCompletesMfaAndAllowsHome() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String code = this.emailCodeService.issue("user");

        mockMvc.perform(post("/email/verify")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("username", "user")
                        .param("code", code)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().isOk());
    }

    @Test
    void wrongEmailCodeRedirectsWithError() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/email/verify")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("username", "user")
                        .param("code", "000000")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/email/verify?error"));
    }

}
