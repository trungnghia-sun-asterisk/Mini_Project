package com.example.employeemanagement.dto.response;

public record DepartmentEmployeeCountResponse(
        Long departmentId,
        String departmentName,
        long employeeCount) {
}
