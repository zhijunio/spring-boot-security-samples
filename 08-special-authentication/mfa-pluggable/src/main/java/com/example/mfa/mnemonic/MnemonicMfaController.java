package com.example.mfa.mnemonic;

import com.example.mfa.MfaFactors;
import com.example.user.User;
import com.example.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
public class MnemonicMfaController {

	static final String SESSION_PHRASE = "mnemonic.pending";

	static final String SESSION_BANK = "mnemonic.bank";

	private final UserService userService;

	private final MnemonicMfaProvider mnemonicMfa;

	private final SecureRandom random = new SecureRandom();

	public MnemonicMfaController(UserService userService, MnemonicMfaProvider mnemonicMfa) {
		this.userService = userService;
		this.mnemonicMfa = mnemonicMfa;
	}

	@GetMapping(path = "/enable-mnemonic")
	public String reveal(@AuthenticationPrincipal User user, HttpSession session, Model model) {
		if (user.mnemonicMfaEnabled()) {
			return "redirect:/";
		}
		String phrase = (String) session.getAttribute(SESSION_PHRASE);
		if (!StringUtils.hasText(phrase)) {
			phrase = this.mnemonicMfa.generate();
			session.setAttribute(SESSION_PHRASE, phrase);
		}
		session.removeAttribute(SESSION_BANK);
		model.addAttribute("words", words(phrase));
		return "mfa/enable-mnemonic";
	}

	@PostMapping(path = "/enable-mnemonic")
	public String continueToConfirm(@AuthenticationPrincipal User user, HttpSession session) {
		if (user.mnemonicMfaEnabled()) {
			return "redirect:/";
		}
		String phrase = (String) session.getAttribute(SESSION_PHRASE);
		if (!StringUtils.hasText(phrase)) {
			return "redirect:/enable-mnemonic";
		}
		session.setAttribute(SESSION_BANK, shuffled(phrase));
		return "redirect:/enable-mnemonic/confirm";
	}

	@GetMapping(path = "/enable-mnemonic/confirm")
	public String confirm(@AuthenticationPrincipal User user, HttpSession session, Model model) {
		if (user.mnemonicMfaEnabled()) {
			return "redirect:/";
		}
		String phrase = (String) session.getAttribute(SESSION_PHRASE);
		@SuppressWarnings("unchecked")
		List<String> bank = (List<String>) session.getAttribute(SESSION_BANK);
		if (!StringUtils.hasText(phrase) || bank == null) {
			return "redirect:/enable-mnemonic";
		}
		return confirmView(model, bank, null);
	}

	@PostMapping(path = "/enable-mnemonic/confirm")
	public String processConfirm(@RequestParam String code, @AuthenticationPrincipal User user,
			Authentication authentication, HttpSession session, Model model) {
		if (user.mnemonicMfaEnabled()) {
			return "redirect:/";
		}
		String phrase = (String) session.getAttribute(SESSION_PHRASE);
		@SuppressWarnings("unchecked")
		List<String> bank = (List<String>) session.getAttribute(SESSION_BANK);
		if (!StringUtils.hasText(phrase) || bank == null) {
			return "redirect:/enable-mnemonic";
		}
		if (!this.mnemonicMfa.matches(phrase, code)) {
			return confirmView(model, bank, "Incorrect order. Tap the words in the sequence you wrote down.");
		}
		session.removeAttribute(SESSION_PHRASE);
		session.removeAttribute(SESSION_BANK);
		MfaFactors.grant(authentication, this.userService.enableMnemonic(user, this.mnemonicMfa.hash(phrase)),
				this.mnemonicMfa);
		return "redirect:/";
	}

	private List<String> shuffled(String phrase) {
		List<String> bank = new ArrayList<>(words(phrase));
		Collections.shuffle(bank, this.random);
		return List.copyOf(bank);
	}

	private static String confirmView(Model model, List<String> bank, String message) {
		model.addAttribute("bank", bank);
		model.addAttribute("message", message);
		return "mfa/enable-mnemonic-confirm";
	}

	private static List<String> words(String phrase) {
		return Arrays.asList(phrase.trim().split("\\s+"));
	}

}
