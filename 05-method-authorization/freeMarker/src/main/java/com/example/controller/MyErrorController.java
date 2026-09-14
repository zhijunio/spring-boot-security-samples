package com.example.controller;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyErrorController implements ErrorController {

    private final static String PATH = "/error";

    @GetMapping(PATH)
    public String getErrorPath() {
        return "error_page";
    }
}
