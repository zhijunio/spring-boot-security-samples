package com.example.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;

class EmailMfaAuthenticationSuccessHandlerTest {

    @Test
    void redirectsToEmailVerificationPage() throws Exception {
        EmailMfaAuthenticationSuccessHandler handler = new EmailMfaAuthenticationSuccessHandler(new EmailCodeService());
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getContextPath()).thenReturn("");

        handler.onAuthenticationSuccess(request, response, new TestingAuthenticationToken("user", "password"));

        verify(response).sendRedirect("/email/verify");
    }
}
