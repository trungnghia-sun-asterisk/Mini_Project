package com.example.employeemanagement.service;

import java.util.List;
import java.util.Locale;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.dto.response.EmployeeResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UtilityService utilityService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           UtilityService utilityService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.utilityService = utilityService;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll(String name, Long departmentId) {
        String normalizedName = name == null ? null : utilityService.formatString(name);
        List<Employee> employees;
        if (normalizedName != null && !normalizedName.isBlank() && departmentId != null) {
            employees = employeeRepository.findByNameContainingIgnoreCaseAndDepartmentIdOrderByNameAsc(
                    normalizedName, departmentId);
        } else if (normalizedName != null && !normalizedName.isBlank()) {
            employees = employeeRepository.findByNameContainingIgnoreCaseOrderByNameAsc(normalizedName);
        } else if (departmentId != null) {
            employees = employeeRepository.findByDepartmentIdOrderByNameAsc(departmentId);
        } else {
            employees = employeeRepository.findAllByOrderByNameAsc();
        }
        return employees.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    @CacheEvict(cacheNames = "employeeTotal", allEntries = true)
    public EmployeeResponse create(EmployeeRequest request) {
        String email = normalizeEmail(request.email());
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Employee with email '" + email + "' already exists");
        }
        Department department = getDepartment(request.departmentId());
        Employee employee = employeeRepository.save(new Employee(
                utilityService.formatString(request.name()), email, department));
        log.info("Created employee id={} departmentId={}", employee.getId(), department.getId());
        return toResponse(employee);
    }

    @Transactional
    @CacheEvict(cacheNames = "employeeTotal", allEntries = true)
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEntity(id);
        String email = normalizeEmail(request.email());
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Employee with email '" + email + "' already exists");
        }
        Department department = getDepartment(request.departmentId());
        employee.update(utilityService.formatString(request.name()), email, department);
        log.info("Updated employee id={} departmentId={}", id, department.getId());
        return toResponse(employee);
    }

    @Transactional
    @CacheEvict(cacheNames = "employeeTotal", allEntries = true)
    public void delete(Long id) {
        Employee employee = getEntity(id);
        employeeRepository.delete(employee);
        log.info("Deleted employee id={}", id);
    }

    @Transactional(readOnly = true)
    public Employee getEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id " + id + " not found"));
    }

    private EmployeeResponse toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(employee.getId(), employee.getName(), employee.getEmail(),
                department.getId(), department.getName());
    }

    private String normalizeEmail(String email) {
        return utilityService.formatString(email).toLowerCase(Locale.ROOT);
    }
}
