package com.example.user;

import com.example.mfa.PrimaryMfa;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

	private final PrimaryMfa primaries;

	public WelcomeController(PrimaryMfa primaries) {
		this.primaries = primaries;
	}

	@GetMapping(path = "/")
	public String index(@AuthenticationPrincipal User user, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("webauthnEnabled", this.primaries.byMethod("webauthn").enabled(user));
		return "user/index";
	}

}
