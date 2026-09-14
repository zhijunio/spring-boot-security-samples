package com.example.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class UserLocationAuthenticationDetails extends WebAuthenticationDetails {

    private final String location;

    public UserLocationAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.location = request.getParameter("location");
    }

    public String getLocation() {
        return location;
    }
}
