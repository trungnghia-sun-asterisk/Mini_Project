package com.example.employeemanagement.service;

import java.util.List;

import com.example.employeemanagement.dto.response.DepartmentEmployeeCountResponse;
import com.example.employeemanagement.dto.response.EmployeeReportResponse;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportingService {

    private static final Logger log = LoggerFactory.getLogger(ReportingService.class);

    private final EmployeeRepository employeeRepository;

    public ReportingService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "employeeTotal")
    public long totalEmployees() {
        long total = employeeRepository.countTotalEmployees();
        log.debug("Calculated total employee report: {}", total);
        return total;
    }

    @Transactional(readOnly = true)
    public List<DepartmentEmployeeCountResponse> employeesByDepartment() {
        return employeeRepository.countEmployeesByDepartment().stream()
                .map(item -> new DepartmentEmployeeCountResponse(
                        item.getDepartmentId(), item.getDepartmentName(), item.getEmployeeCount()))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeReportResponse summary() {
        return new EmployeeReportResponse(totalEmployees(), employeesByDepartment());
    }
}
