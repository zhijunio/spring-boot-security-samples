package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AsyncSecurityTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void securityContextIsAvailableInAsyncTask() throws Exception {
        var result = mvc.perform(get("/async-user").with(user("user")))
                .andExpect(request().asyncStarted())
                .andReturn();

        mvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }

    @Test
    void asyncEndpointRequiresAuthentication() throws Exception {
        mvc.perform(get("/async-user"))
                .andExpect(status().is3xxRedirection());
    }
}
