package com.example.controller;

import com.example.mfa.BindingMaterial;
import com.example.mfa.MfaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mfa/settings")
public class MfaSettingsController {

    private static final String PENDING_BINDING = "pendingBinding";

    private final MfaService mfaService;

    public MfaSettingsController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @GetMapping
    public String settings(Model model, HttpSession session) {
        String username = currentUsername();
        BindingMaterial pending = (BindingMaterial) session.getAttribute(PENDING_BINDING);
        model.addAttribute("username", username);
        model.addAttribute("providers", mfaService.allProviders());
        model.addAttribute("platform", mfaService.platformSnapshot());
        model.addAttribute("bound", mfaService.bindingSnapshot(username));
        model.addAttribute("pending", pending);
        if (pending != null) {
            model.addAttribute("pendingProvider", mfaService.requireProvider(pending.providerId()));
        }
        return "mfa-settings";
    }

    @PostMapping("/bind/start")
    public String startBinding(@RequestParam String providerId, HttpSession session) {
        String username = currentUsername();
        try {
            BindingMaterial material = mfaService.beginBinding(username, providerId);
            session.setAttribute(PENDING_BINDING, material);
            return "redirect:/mfa/settings?binding=" + providerId;
        } catch (IllegalStateException ex) {
            return "redirect:/mfa/settings?error";
        }
    }

    @PostMapping("/bind/confirm")
    public String confirmBinding(@RequestParam(required = false) String confirmation, HttpSession session) {
        BindingMaterial material = (BindingMaterial) session.getAttribute(PENDING_BINDING);
        if (material == null) {
            return "redirect:/mfa/settings?error";
        }
        try {
            mfaService.completeBinding(currentUsername(), material, confirmation);
            session.removeAttribute(PENDING_BINDING);
            return "redirect:/mfa/settings?bound";
        } catch (RuntimeException ex) {
            return "redirect:/mfa/settings?error&binding=" + material.providerId();
        }
    }

    @PostMapping("/bind/cancel")
    public String cancelBinding(HttpSession session) {
        session.removeAttribute(PENDING_BINDING);
        return "redirect:/mfa/settings";
    }

    @PostMapping("/unbind")
    public String unbind(@RequestParam String providerId) {
        mfaService.unbind(currentUsername(), providerId);
        return "redirect:/mfa/settings?unbound";
    }

    private static String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
