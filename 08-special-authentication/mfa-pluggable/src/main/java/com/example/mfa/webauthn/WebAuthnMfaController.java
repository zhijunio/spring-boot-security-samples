package com.example.mfa.webauthn;

import com.example.mfa.MfaFactors;
import com.example.user.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebAuthnMfaController {

	private static final String ENROLLING_SESSION_KEY = "webauthn.enrolling";

	private final WebAuthnMfaProvider webAuthnMfa;

	public WebAuthnMfaController(WebAuthnMfaProvider webAuthnMfa) {
		this.webAuthnMfa = webAuthnMfa;
	}

	@GetMapping(path = "/enable-webauthn")
	public String enable(@AuthenticationPrincipal User user, CsrfToken csrf, HttpSession session, Model model) {
		if (this.webAuthnMfa.enabled(user)) {
			return "redirect:/";
		}
		session.setAttribute(ENROLLING_SESSION_KEY, Boolean.TRUE);
		model.addAttribute("csrfHeader", csrf.getHeaderName());
		model.addAttribute("csrfToken", csrf.getToken());
		return "mfa/enable-webauthn";
	}

	@GetMapping(path = "/webauthn/register")
	public String afterRegister(@RequestParam(required = false) String success, @AuthenticationPrincipal User user,
			Authentication authentication, HttpSession session) {
		if (success != null && this.webAuthnMfa.enabled(user)
				&& Boolean.TRUE.equals(session.getAttribute(ENROLLING_SESSION_KEY))) {
			session.removeAttribute(ENROLLING_SESSION_KEY);
			MfaFactors.grant(authentication, user, this.webAuthnMfa);
			return "redirect:/";
		}
		return "redirect:/enable-webauthn";
	}

}
