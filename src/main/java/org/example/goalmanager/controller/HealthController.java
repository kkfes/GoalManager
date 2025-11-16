package org.example.goalmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String index() {
        return "Goal Manager backend is running";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

