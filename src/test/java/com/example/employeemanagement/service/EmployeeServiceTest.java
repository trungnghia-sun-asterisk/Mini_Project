package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UtilityService utilityService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void createRejectsDuplicateEmail() {
        EmployeeRequest request = new EmployeeRequest("Ada", "ada@example.com", 1L);
        when(utilityService.formatString("ada@example.com")).thenReturn("ada@example.com");
        when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ada@example.com");
        verify(departmentRepository, never()).findById(any());
    }

    @Test
    void createRejectsMissingDepartment() {
        EmployeeRequest request = new EmployeeRequest("Ada", "ada@example.com", 99L);
        when(utilityService.formatString("ada@example.com")).thenReturn("ada@example.com");
        when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
        when(departmentRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department with id 99");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createUsesExistingDepartment() {
        EmployeeRequest request = new EmployeeRequest("  Ada  ", "ADA@example.com", 1L);
        Department department = new Department("Engineering", "");
        when(utilityService.formatString("ADA@example.com")).thenReturn("ADA@example.com");
        when(utilityService.formatString("  Ada  ")).thenReturn("Ada");
        when(employeeRepository.existsByEmailIgnoreCase("ada@example.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(java.util.Optional.of(department));
        when(employeeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.create(request);

        verify(employeeRepository).save(any());
    }
}
