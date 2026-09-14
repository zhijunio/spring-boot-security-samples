package com.example.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

import com.example.config.CustomUser;

@Route("public")
@PageTitle("Spring Boot Tutorials")
@AnonymousAllowed
public class PublicView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private String userName = "Anonymous";

    public PublicView(AuthenticationContext authenticationContext) {

        Boolean isAuthenticated = authenticationContext.isAuthenticated();
        if (isAuthenticated) {
            userName = authenticationContext.getAuthenticatedUser(CustomUser.class).get().getUsername().toUpperCase();
        }
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(new H1("Spring Boot Tutorial"));
        add(new H2("Public Page!"));
        add(new Paragraph("Username: " + userName));
        add(new Anchor("/", "Home"));
    }


}
