package com.example.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Route("logout")
@AnonymousAllowed
public class LogoutView extends VerticalLayout {

    private static final long serialVersionUID = 1L;

    public LogoutView(AuthenticationContext authenticationContext) {
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        add(new H1("Spring Boot Tutorial"));
        add(new H2("Logout!"));
        Button button = new Button("Logout", _ -> authenticationContext.logout());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(button);
    }
}
