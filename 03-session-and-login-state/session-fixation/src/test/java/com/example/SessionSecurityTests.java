package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SessionSecurityTests {
    @Autowired
    MockMvc mvc;

    @Test
    void protectedResourceRequiresAuthentication() throws Exception {
        mvc.perform(get("/session")).andExpect(status().is3xxRedirection());
    }

    @Test
    void authenticatedUserCanReadSession() throws Exception {
        mvc.perform(get("/session").with(user("user"))).andExpect(status().isOk());
    }
}
