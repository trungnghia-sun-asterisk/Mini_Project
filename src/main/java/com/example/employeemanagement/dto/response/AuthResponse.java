package com.example.employeemanagement.dto.response;

public record AuthResponse(
        String tokenType,
        String accessToken,
        String username,
        String role) {
}
