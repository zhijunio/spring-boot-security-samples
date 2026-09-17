package com.example.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;

class MagicLinkOneTimeTokenGenerationSuccessHandlerTest {

    @Test
    void redirectsToDefaultOttSubmitPage() throws Exception {
        MagicLinkOneTimeTokenGenerationSuccessHandler handler =
                new MagicLinkOneTimeTokenGenerationSuccessHandler();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRequestURI()).thenReturn("/ott/generate");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(response.encodeRedirectURL("/login/ott")).thenReturn("/login/ott");

        handler.handle(request, response,
                new DefaultOneTimeToken("token-value", "user", Instant.parse("2026-09-15T13:00:00Z")));

        verify(response).sendRedirect("/login/ott");
    }

}
