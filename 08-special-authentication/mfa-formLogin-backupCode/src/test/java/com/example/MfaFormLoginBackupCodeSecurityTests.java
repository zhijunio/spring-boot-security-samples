package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.config.BackupCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MfaFormLoginBackupCodeSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void passwordLoginRedirectsToBackupPhraseVerification() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backup/verify"));
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
                .andExpect(redirectedUrl("/backup/verify"));
    }

    @Test
    void backupPhraseCompletesMfaAndCanBeReused() throws Exception {
        MvcResult firstLogin = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/backup/verify")
                        .session((MockHttpSession) firstLogin.getRequest().getSession())
                        .param("username", "user")
                        .param("mnemonic", BackupCodeService.USER_MNEMONIC)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/user")
                        .session((MockHttpSession) firstLogin.getRequest().getSession()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/logout")
                        .session((MockHttpSession) firstLogin.getRequest().getSession())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        MvcResult secondLogin = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/backup/verify")
                        .session((MockHttpSession) secondLogin.getRequest().getSession())
                        .param("username", "user")
                        .param("mnemonic", "  PAYMENT noodle  vivid slogan gather metal pilot enact fragile hip physical CANVAS ")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void validBip39PhraseForAnotherUserRedirectsWithError() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(post("/backup/verify")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .param("username", "user")
                        .param("mnemonic", BackupCodeService.ADMIN_MNEMONIC)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backup/verify?error"));
    }

}
