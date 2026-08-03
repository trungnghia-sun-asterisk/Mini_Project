package com.example.employeemanagement.service;

import java.util.List;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.dto.response.DepartmentResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.BusinessRuleException;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final UtilityService utilityService;

    public DepartmentService(DepartmentRepository departmentRepository,
                             EmployeeRepository employeeRepository,
                             UtilityService utilityService) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.utilityService = utilityService;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        String name = utilityService.formatString(request.name());
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Department with name '" + name + "' already exists");
        }
        Department department = departmentRepository.save(new Department(name,
                utilityService.formatString(request.description())));
        log.info("Created department id={} name={}", department.getId(), department.getName());
        return toResponse(department);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = getEntity(id);
        String name = utilityService.formatString(request.name());
        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException("Department with name '" + name + "' already exists");
        }
        department.update(name, utilityService.formatString(request.description()));
        log.info("Updated department id={} name={}", id, name);
        return toResponse(department);
    }

    @Transactional
    public void delete(Long id) {
        Department department = getEntity(id);
        long employeeCount = employeeRepository.countByDepartmentId(id);
        if (employeeCount > 0) {
            throw new BusinessRuleException("Department '" + department.getName()
                    + "' cannot be deleted while it has assigned employees");
        }
        departmentRepository.delete(department);
        log.info("Deleted department id={} name={}", id, department.getName());
    }

    @Transactional(readOnly = true)
    public Department getEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id " + id + " not found"));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getName(), department.getDescription());
    }
}
