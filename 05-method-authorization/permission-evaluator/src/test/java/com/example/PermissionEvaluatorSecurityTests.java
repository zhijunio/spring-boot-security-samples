package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PermissionEvaluatorSecurityTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void ownerCanReadDocument() throws Exception {
        mvc.perform(get("/document?owner=user").with(httpBasic("user", "password"))).andExpect(status().isOk());
    }

    @Test
    void otherUserCannotReadDocument() throws Exception {
        mvc.perform(get("/document?owner=user").with(httpBasic("admin", "password"))).andExpect(status().isForbidden());
    }
}
