package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordCheckerSecurityTests {

    @Autowired
    MockMvc mvc;

    @Test
    void safePasswordCanAuthenticate() throws Exception {
        mvc.perform(formLogin().user("user").password("safe-password"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void compromisedPasswordIsRejected() throws Exception {
        mvc.perform(formLogin().user("compromised").password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }
}
