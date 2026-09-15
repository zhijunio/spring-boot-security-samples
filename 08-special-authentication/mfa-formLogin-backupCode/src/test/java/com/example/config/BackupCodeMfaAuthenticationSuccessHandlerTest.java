package com.example.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

class BackupCodeMfaAuthenticationSuccessHandlerTest {

    @Test
    void redirectsToBackupPhraseVerificationPage() throws Exception {
        BackupCodeMfaAuthenticationSuccessHandler handler = new BackupCodeMfaAuthenticationSuccessHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("");

        handler.onAuthenticationSuccess(request, response, new TestingAuthenticationToken("user", "password"));

        verify(response).sendRedirect("/backup/verify");
    }

}
