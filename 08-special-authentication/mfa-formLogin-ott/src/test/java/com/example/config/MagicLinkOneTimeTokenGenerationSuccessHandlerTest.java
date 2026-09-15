package com.example.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;

class MagicLinkOneTimeTokenGenerationSuccessHandlerTest {

    @Test
    void redirectsToOttSentPage() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), any(ResultSetExtractor.class), eq("user")))
                .thenReturn("user@example.com");

        MagicLinkOneTimeTokenGenerationSuccessHandler handler =
                new MagicLinkOneTimeTokenGenerationSuccessHandler(jdbcTemplate);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRequestURI()).thenReturn("/ott/generate");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);
        when(response.encodeRedirectURL("/ott/sent")).thenReturn("/ott/sent");

        handler.handle(request, response,
                new DefaultOneTimeToken("token-value", "user", Instant.parse("2026-09-15T13:00:00Z")));

        verify(response).sendRedirect("/ott/sent");
    }

}
