package com.example.employeemanagement.dto.response;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        Long departmentId,
        String departmentName) {
}
