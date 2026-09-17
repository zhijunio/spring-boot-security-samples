package com.example.mfa;

import com.example.mfa.mnemonic.MnemonicMfaProvider;
import com.example.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MfaChallengeController {

	private final PrimaryMfa primaries;

	private final MnemonicMfaProvider mnemonicMfa;

	public MfaChallengeController(PrimaryMfa primaries, MnemonicMfaProvider mnemonicMfa) {
		this.primaries = primaries;
		this.mnemonicMfa = mnemonicMfa;
	}

	@GetMapping(path = "/challenge")
	public String challenge(Authentication authentication, @AuthenticationPrincipal User user, CsrfToken csrf,
			Model model) {
		List<MfaProvider> available = available(authentication, user);
		if (available.isEmpty()) {
			return "redirect:/";
		}
		for (MfaProvider provider : available) {
			provider.sendChallengeIfAbsent(user);
		}
		return view(model, user, csrf, available, null, null);
	}

	@PostMapping(path = "/challenge")
	public String challenge(@RequestParam String method, @RequestParam(required = false) String resend,
			@RequestParam(required = false) String code, Authentication authentication,
			@AuthenticationPrincipal User user, CsrfToken csrf, Model model) {
		List<MfaProvider> available = available(authentication, user);
		if (available.isEmpty()) {
			return "redirect:/";
		}
		MfaProvider selected = available.stream()
			.filter(provider -> provider.method().equals(method))
			.findFirst()
			.orElse(null);
		if (selected == null) {
			return "redirect:/challenge";
		}
		if (resend != null) {
			if (!selected.sendChallengeIfReady(user)) {
				return view(model, user, csrf, available, selected.method(), "Wait before requesting another code.");
			}
			return "redirect:/challenge";
		}
		if (selected.verify(user, code)) {
			MfaFactors.grant(authentication, user, selected);
			if (selected == this.mnemonicMfa || available(authentication, user).isEmpty()) {
				return "redirect:/";
			}
			return "redirect:/challenge";
		}
		return view(model, user, csrf, available, selected.method(), "Invalid code");
	}

	private List<MfaProvider> available(Authentication authentication, User user) {
		List<MfaProvider> methods = new ArrayList<>();
		for (MfaProvider provider : this.primaries.factors()) {
			if (provider.pending(authentication, user)) {
				methods.add(provider);
			}
		}
		if (this.mnemonicMfa.pending(authentication, user)
				&& (!methods.isEmpty() || !this.primaries.anyEnabled(user))) {
			methods.add(this.mnemonicMfa);
		}
		return methods;
	}

	private static String view(Model model, User user, CsrfToken csrf, List<MfaProvider> available, String errorMethod,
			String message) {
		model.addAttribute("methods", available.stream().map(MfaProvider::method).toList());
		model.addAttribute("email", user.email());
		model.addAttribute("errorMethod", errorMethod);
		model.addAttribute("message", message);
		model.addAttribute("csrfHeader", csrf.getHeaderName());
		model.addAttribute("csrfToken", csrf.getToken());
		int cooldown = 0;
		for (MfaProvider provider : available) {
			cooldown = Math.max(cooldown, provider.cooldownRemaining(user));
		}
		model.addAttribute("cooldown", cooldown);
		return "mfa/challenge";
	}

}
