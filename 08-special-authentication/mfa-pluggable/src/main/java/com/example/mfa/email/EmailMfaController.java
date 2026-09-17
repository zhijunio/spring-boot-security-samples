package com.example.mfa.email;

import com.example.mfa.MfaFactors;
import com.example.user.User;
import com.example.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailMfaController {

	private final UserService userService;

	private final EmailMfaProvider emailMfa;

	public EmailMfaController(UserService userService, EmailMfaProvider emailMfa) {
		this.userService = userService;
		this.emailMfa = emailMfa;
	}

	@GetMapping(path = "/enable-email")
	public String requestEnableEmail(@AuthenticationPrincipal User user, Model model) {
		if (user.emailMfaEnabled()) {
			return "redirect:/";
		}
		this.emailMfa.sendChallengeIfAbsent(user);
		return view(model, user, null, null);
	}

	@PostMapping(path = "/enable-email")
	public String processEnableEmail(@RequestParam(required = false) String resend,
			@RequestParam(required = false) String code, @AuthenticationPrincipal User user,
			Authentication authentication, Model model) {
		if (user.emailMfaEnabled()) {
			return "redirect:/";
		}
		if (resend != null) {
			if (this.emailMfa.sendChallengeIfReady(user)) {
				return view(model, user, "A new code was sent.", null);
			}
			return view(model, user, null, "Wait before requesting another code.");
		}
		if (!this.emailMfa.verify(user, code)) {
			return view(model, user, null, "Invalid code");
		}
		MfaFactors.grant(authentication, this.userService.enableEmailMfa(user), this.emailMfa);
		return "redirect:/";
	}

	private String view(Model model, User user, String notice, String message) {
		model.addAttribute("email", user.email());
		model.addAttribute("notice", notice);
		model.addAttribute("message", message);
		model.addAttribute("cooldown", this.emailMfa.cooldownRemaining(user));
		return "mfa/enable-email";
	}

}
