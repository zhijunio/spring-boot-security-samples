package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfSecurityTests {
    @Autowired
    MockMvc mvc;

    @Test
    void postWithoutCsrfTokenIsRejected() throws Exception {
        mvc.perform(post("/change").with(user("user"))).andExpect(status().isForbidden());
    }

    @Test
    void postWithCsrfTokenIsAccepted() throws Exception {
        mvc.perform(post("/change").with(user("user")).with(csrf())).andExpect(status().isOk());
    }
}
