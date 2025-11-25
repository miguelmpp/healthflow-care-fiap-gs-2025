package br.com.fiap.healthflow_care.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health-check")
    public String healthCheck() {
        return "HealthFlow Care API está online e saudável!";
    }
}
