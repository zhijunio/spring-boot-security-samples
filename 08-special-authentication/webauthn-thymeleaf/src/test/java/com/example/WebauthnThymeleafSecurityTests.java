package com.example;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.ImmutableCredentialRecord;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCose;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class WebauthnThymeleafSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicKeyCredentialUserEntityRepository userEntities;

    @Autowired
    private UserCredentialRepository userCredentials;

    @BeforeEach
    void clearPasskeys() {
        PublicKeyCredentialUserEntity entity = this.userEntities.findByUsername("user");
        if (entity == null) {
            return;
        }
        for (CredentialRecord credential : this.userCredentials.findByUserId(entity.getId())) {
            this.userCredentials.delete(credential.getCredentialId());
        }
        this.userEntities.delete(entity.getId());
    }

    @Test
    void anonymousHomeRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginPageIsAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign in with passkey")));
    }

    @Test
    void webauthnScriptIsAccessible() throws Exception {
        mockMvc.perform(get("/login/webauthn.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("setupRegistration")));
    }

    @Test
    void authenticatedUserCanAccessHome() throws Exception {
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No passkeys yet.")));
    }

    @Test
    void authenticatedUserCanOpenRegisterPage() throws Exception {
        mockMvc.perform(get("/webauthn/register").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No passkeys yet.")))
                .andExpect(content().string(containsString("id=\"register\"")));
    }

    @Test
    void registeredPasskeyIsListed() throws Exception {
        seedPasskey("Office key");
        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Office key")));
        mockMvc.perform(get("/webauthn/register").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Office key")))
                .andExpect(content().string(containsString("id=\"register\"")));
    }

    private void seedPasskey(String label) {
        Bytes userId = Bytes.random();
        this.userEntities.save(ImmutablePublicKeyCredentialUserEntity.builder()
                .name("user")
                .id(userId)
                .displayName("user")
                .build());
        this.userCredentials.save(ImmutableCredentialRecord.builder()
                .credentialId(Bytes.random())
                .userEntityUserId(userId)
                .publicKey(new ImmutablePublicKeyCose(new byte[] { 1 }))
                .transports(Set.of())
                .label(label)
                .build());
    }

}
