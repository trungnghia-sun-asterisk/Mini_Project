package com.example.employeemanagement.dto.response;

import java.util.List;

public record EmployeeReportResponse(
        long totalEmployees,
        List<DepartmentEmployeeCountResponse> byDepartment) {
}
