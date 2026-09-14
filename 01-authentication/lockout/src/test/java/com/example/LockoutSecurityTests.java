package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LockoutSecurityTests {
    @Autowired
    MockMvc mvc;

    @Test
    void lockedUserLoginFails() throws Exception {
        mvc.perform(formLogin().user("locked").password("password")).andExpect(status().is3xxRedirection());
    }
}
