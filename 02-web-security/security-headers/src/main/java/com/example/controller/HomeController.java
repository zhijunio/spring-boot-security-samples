package com.example.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> getHome(@RequestHeader("User-Agent") String userAgent, @RequestHeader(value = "x-test", required = false, defaultValue = "header") String test) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-report-id", UUID.randomUUID().toString());
        headers.add("x-timestamp", Instant.now().toString());
        return new ResponseEntity<>("Home - " + userAgent + " - " + test, headers, HttpStatus.OK);
    }

    @GetMapping("/public")
    public ResponseEntity<String> getPublic() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-report-id", UUID.randomUUID().toString());
        headers.add("x-timestamp", Instant.now().toString());
        return new ResponseEntity<>("Public", headers, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-report-id", UUID.randomUUID().toString());
        headers.add("x-timestamp", Instant.now().toString());
        return new ResponseEntity<>("User", headers, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<String> getAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-report-id", UUID.randomUUID().toString());
        headers.add("x-timestamp", Instant.now().toString());
        return new ResponseEntity<>("Admin", headers, HttpStatus.OK);
    }
}
