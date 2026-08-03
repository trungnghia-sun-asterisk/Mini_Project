package com.example.employeemanagement.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

@Service
public class UtilityService {

    private final AtomicLong employeeCodeSequence = new AtomicLong();

    public String formatString(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    public String generateEmployeeCode() {
        return "EMP-%05d".formatted(employeeCodeSequence.incrementAndGet());
    }
}
