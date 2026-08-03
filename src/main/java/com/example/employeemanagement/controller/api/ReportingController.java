package com.example.employeemanagement.controller.api;

import java.util.List;

import com.example.employeemanagement.dto.response.DepartmentEmployeeCountResponse;
import com.example.employeemanagement.dto.response.EmployeeTotalResponse;
import com.example.employeemanagement.service.ReportingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/employees")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/total")
    public ResponseEntity<EmployeeTotalResponse> totalEmployees() {
        return ResponseEntity.ok(new EmployeeTotalResponse(reportingService.totalEmployees()));
    }

    @GetMapping("/by-department")
    public ResponseEntity<List<DepartmentEmployeeCountResponse>> byDepartment() {
        return ResponseEntity.ok(reportingService.employeesByDepartment());
    }
}
