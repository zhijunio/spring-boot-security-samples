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
class ExceptionSecurityTests {
    @Autowired
    MockMvc mvc;

    @Test
    void anonymousRequestGets401() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isUnauthorized());
    }

    @Test
    void insufficientRoleGets403() throws Exception {
        mvc.perform(get("/admin").with(user("user").roles("USER"))).andExpect(status().isForbidden());
    }
}
