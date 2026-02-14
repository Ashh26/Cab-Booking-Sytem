package com.yasif.project.uber.Uber.backend.system.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/")
    public ResponseEntity<String> healthCheckRunner(){
        return ResponseEntity.ok("I am healthy");
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

}
