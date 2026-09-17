package com.example.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping(path = "/login")
	public String login(@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "logout", required = false) String logout, Model model) {
		model.addAttribute("error", error != null);
		model.addAttribute("logoutSuccess", logout != null);
		return "user/login";
	}

}
