package com.example.config;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ScopeAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ClientRegistration google;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
            new HttpSessionOAuth2AuthorizationRequestRepository();

    public OAuth2ScopeAuthenticationEntryPoint(ClientRegistrationRepository clients) {
        this.google = clients.findByRegistrationId("google");
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clients);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        OAuth2AuthorizationRequest oauth2 = this.authorizationRequestResolver.resolve(request,
                this.google.getRegistrationId());
        oauth2 = OAuth2AuthorizationRequest.from(oauth2).scopes(Set.of(SecurityConfig.SCOPE)).build();
        this.authorizationRequestRepository.saveAuthorizationRequest(oauth2, request, response);
        response.sendRedirect(oauth2.getAuthorizationRequestUri());
    }

}
