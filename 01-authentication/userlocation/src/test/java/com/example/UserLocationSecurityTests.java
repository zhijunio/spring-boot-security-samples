package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserLocationSecurityTests {

    @Autowired
    MockMvc mvc;

    @Test
    void loginFromOfficeSucceeds() throws Exception {
        mvc.perform(post("/login").param("username", "user").param("password", "password")
                        .param("location", "office").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void loginFromOutsideOfficeFails() throws Exception {
        mvc.perform(post("/login").param("username", "user").param("password", "password")
                        .param("location", "home").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
