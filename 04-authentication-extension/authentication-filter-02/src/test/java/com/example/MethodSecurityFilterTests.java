package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MethodSecurityFilterTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void postFilterKeepsOnlyMessagesOwnedByCurrentUser() throws Exception {
        mvc.perform(get("/messages").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"user: private message\"]"));
    }

    @Test
    void preFilterRemovesMessagesBeforeControllerRuns() throws Exception {
        mvc.perform(post("/messages/sent")
                        .with(httpBasic("user", "password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"user: first\",\"admin: second\"]"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"user: first\"]"));
    }
}
