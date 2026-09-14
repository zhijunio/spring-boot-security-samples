package com.example.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {
    @PreAuthorize("hasPermission(#document, 'read')")
    public String read(String document) {
        return "reading " + document;
    }
}
