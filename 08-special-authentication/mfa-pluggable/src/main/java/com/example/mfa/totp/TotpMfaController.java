package com.example.mfa.totp;

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
public class TotpMfaController {

	private final UserService userService;

	private final TotpMfaProvider totpMfa;

	private final QrCode qrCode;

	public TotpMfaController(UserService userService, TotpMfaProvider totpMfa, QrCode qrCode) {
		this.userService = userService;
		this.totpMfa = totpMfa;
		this.qrCode = qrCode;
	}

	@GetMapping(path = "/enable-mfa")
	public String requestEnableMfa(@AuthenticationPrincipal User user, Model model) {
		if (user.totpMfaEnabled()) {
			return "redirect:/";
		}
		String otpAuthUrl = "otpauth://totp/%s?secret=%s&digits=6".formatted("Demo: " + user.username(),
				user.totpSecret());
		model.addAttribute("qrCode", this.qrCode.dataUrl(otpAuthUrl));
		model.addAttribute("secret", user.totpSecret());
		return "mfa/enable-mfa";
	}

	@PostMapping(path = "/enable-mfa")
	public String processEnableMfa(@RequestParam String code, @AuthenticationPrincipal User user,
			Authentication authentication, Model model) {
		if (user.totpMfaEnabled()) {
			return "redirect:/";
		}
		if (!this.totpMfa.verify(user, code)) {
			model.addAttribute("message", "Invalid code");
			return this.requestEnableMfa(user, model);
		}
		MfaFactors.grant(authentication, this.userService.enableTotp(user), this.totpMfa);
		return "redirect:/";
	}

}
