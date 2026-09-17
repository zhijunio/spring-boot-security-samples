package com.example.user;

import com.example.mfa.MfaFactors;
import com.example.mfa.totp.TotpMfaProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final TotpMfaProvider totp;

	public SignupController(UserService userService, PasswordEncoder passwordEncoder, TotpMfaProvider totp) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.totp = totp;
	}

	@GetMapping(path = "/signup")
	public String signup() {
		return "user/signup";
	}

	@PostMapping(path = "/signup")
	public String signup(SignupForm form, Model model) {
		if (!form.password().equals(form.passwordConfirm())) {
			model.addAttribute("error", "Passwords do not match");
			return "user/signup";
		}
		User user = User.registered(form.username(), this.passwordEncoder.encode(form.password()), form.email(),
				this.totp.generateSecret());
		this.userService.insert(user);
		MfaFactors.grant(UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()), user,
				FactorGrantedAuthority.PASSWORD_AUTHORITY);
		return "redirect:/";
	}

	record SignupForm(String username, String email, String password, String passwordConfirm) {
	}

}
