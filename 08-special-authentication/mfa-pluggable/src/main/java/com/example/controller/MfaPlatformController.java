package com.example.controller;

import com.example.mfa.MfaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mfa/platform")
public class MfaPlatformController {

    private final MfaService mfaService;

    public MfaPlatformController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping
    public String platform(Model model) {
        model.addAttribute("providers", mfaService.allProviders());
        model.addAttribute("enabled", mfaService.platformSnapshot());
        return "mfa-platform";
    }

    @PostMapping
    public String update(@RequestParam String providerId, @RequestParam boolean enabled) {
        mfaService.setProviderEnabled(providerId, enabled);
        return "redirect:/mfa/platform?saved";
    }

}
