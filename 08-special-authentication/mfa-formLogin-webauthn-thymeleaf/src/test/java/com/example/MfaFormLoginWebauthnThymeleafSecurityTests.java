package com.example;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.webauthn.api.Bytes;
import org.springframework.security.web.webauthn.api.CredentialRecord;
import org.springframework.security.web.webauthn.api.ImmutableCredentialRecord;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCose;
import org.springframework.security.web.webauthn.api.ImmutablePublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.web.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.security.web.webauthn.management.UserCredentialRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class MfaFormLoginWebauthnThymeleafSecurityTests {

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
                .andExpect(status().isOk());
    }

    @Test
    void passwordLoginFromDatabaseRedirectsToMissingWebAuthn() throws Exception {
        MvcResult login = mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        mockMvc.perform(get("/")
                        .session((MockHttpSession) login.getRequest().getSession()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=webauthn&factor.reason=missing"));
    }

    @Test
    void unknownPasswordIsRejected() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "wrong")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void webauthnScriptIsAccessible() throws Exception {
        mockMvc.perform(get("/login/webauthn.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("setupRegistration")));
    }

    @Test
    void passwordOnlyCannotAccessHome() throws Exception {
        mockMvc.perform(get("/").with(authentication(token(FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?factor.type=webauthn&factor.reason=missing"));
    }

    @Test
    void passwordAndWebAuthnAllowHome() throws Exception {
        mockMvc.perform(get("/").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY,
                        FactorGrantedAuthority.WEBAUTHN_AUTHORITY))))
                .andExpect(status().isOk());
    }

    @Test
    void registerPasskeyRequiresPasswordOnly() throws Exception {
        mockMvc.perform(get("/webauthn/register").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No passkeys yet.")))
                .andExpect(content().string(containsString("id=\"register\"")));
    }

    @Test
    void registeredPasskeyIsListed() throws Exception {
        seedPasskey("Office key");
        mockMvc.perform(get("/webauthn/register").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Office key")))
                .andExpect(content().string(not(containsString("id=\"register\""))));
        mockMvc.perform(get("/").with(authentication(token(
                        FactorGrantedAuthority.PASSWORD_AUTHORITY,
                        FactorGrantedAuthority.WEBAUTHN_AUTHORITY))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Office key")))
                .andExpect(content().string(containsString("Manage passkey")));
    }

    @Test
    void secondPasskeyForSameUserIsRejected() {
        Bytes userId = seedPasskey("Office key");
        assertThatThrownBy(() -> this.userCredentials.save(ImmutableCredentialRecord.builder()
                .credentialId(Bytes.random())
                .userEntityUserId(userId)
                .publicKey(new ImmutablePublicKeyCose(new byte[] { 2 }))
                .transports(Set.of())
                .label("Phone")
                .build()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("This account already has a passkey");
    }

    private Bytes seedPasskey(String label) {
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
        return userId;
    }

    private static UsernamePasswordAuthenticationToken token(String... factors) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        for (String factor : factors) {
            authorities.add(FactorGrantedAuthority.fromAuthority(factor));
        }
        User principal = new User("user", "n/a", authorities);
        return UsernamePasswordAuthenticationToken.authenticated(principal, "n/a", authorities);
    }
}
