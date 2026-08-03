package com.example.employeemanagement.controller.api;

import java.util.Map;

import com.example.employeemanagement.service.UtilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final UtilityService utilityService;

    public HelloController(UtilityService utilityService) {
        this.utilityService = utilityService;
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        String message = utilityService.formatString("Employee Management System is running successfully");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
