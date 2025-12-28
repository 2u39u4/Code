package com.sztu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class healthcheck {

    @GetMapping("/health")
    public String healthCheck() {
        return "Service is alive!";
    }
}