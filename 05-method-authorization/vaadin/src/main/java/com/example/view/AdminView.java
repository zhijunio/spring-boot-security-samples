package com.example.view;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;

import jakarta.annotation.security.RolesAllowed;
import com.example.config.CustomUser;

@Route("admin")
@PageTitle("Spring Boot Tutorials")
@RolesAllowed("ROLE_ADMIN")
public class AdminView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private String userName = "Anonymous";
    private String roles = "Role: ";

    public AdminView(AuthenticationContext authenticationContext) {

        if (authenticationContext.isAuthenticated()) {
            userName = authenticationContext.getAuthenticatedUser(CustomUser.class).get().getUsername().toUpperCase();
            if (authenticationContext.getAuthenticatedUser(CustomUser.class).get().roles().size() > 1) {
                roles = "Roles: ";
            } else {
                roles = "Role: ";
            }
        }

        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(new H1("Spring Boot Tutorial"));
        add(new H2("Private Page for Admin!"));
        add(new Paragraph("Username: " + userName));
        add(new Paragraph("Naam: " + authenticationContext.getAuthenticatedUser(CustomUser.class).get().name()));
        add(new Paragraph("E-Mail: " + authenticationContext.getAuthenticatedUser(CustomUser.class).get().email()));
        add(new Paragraph(roles + authenticationContext.getAuthenticatedUser(CustomUser.class).get().roles()));
        add(new Paragraph(" "));
        add(new Anchor("/", "Home"));
    }


}
