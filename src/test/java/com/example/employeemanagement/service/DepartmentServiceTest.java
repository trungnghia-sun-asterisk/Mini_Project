package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.employeemanagement.dto.request.DepartmentRequest;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.BusinessRuleException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UtilityService utilityService;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void deleteRejectsDepartmentWithEmployees() {
        Department department = new Department("Engineering", "");
        when(departmentRepository.findById(1L)).thenReturn(java.util.Optional.of(department));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(2L);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("cannot be deleted");
    }

    @Test
    void deleteRemovesEmptyDepartment() {
        Department department = new Department("Engineering", "");
        when(departmentRepository.findById(1L)).thenReturn(java.util.Optional.of(department));
        when(employeeRepository.countByDepartmentId(1L)).thenReturn(0L);

        departmentService.delete(1L);

        verify(departmentRepository).delete(department);
    }

    @Test
    void createRejectsDuplicateName() {
        DepartmentRequest request = new DepartmentRequest("Engineering", "");
        when(utilityService.formatString("Engineering")).thenReturn("Engineering");
        when(departmentRepository.existsByNameIgnoreCase("Engineering")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(com.example.employeemanagement.exception.DuplicateResourceException.class);
    }
}
