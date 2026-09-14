package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.DocumentService;

@RestController
public class PermissionController {
    private final DocumentService documentService;

    public PermissionController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String home() {
        return "PermissionEvaluator demo";
    }

    @GetMapping("/document")
    public String document(@RequestParam String owner) {
        return documentService.read(owner + ":document");
    }
}
