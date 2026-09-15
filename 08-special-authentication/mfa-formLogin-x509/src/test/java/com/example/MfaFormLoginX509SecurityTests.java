package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.x509;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MfaFormLoginX509SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicHomeIsAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
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
                .andExpect(status().isForbidden());
    }

    @Test
    void x509OnlyRedirectsToPasswordLogin() throws Exception {
        mockMvc.perform(get("/user").with(x509("certs/user.crt")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=password&factor.reason=missing"));
    }

    @Test
    void passwordAndX509AllowUserPage() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf())
                        .with(x509("certs/user.crt")))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get("/user")
                        .session((MockHttpSession) login.getRequest().getSession())
                        .with(x509("certs/user.crt")))
                .andExpect(status().isOk());
    }

}
