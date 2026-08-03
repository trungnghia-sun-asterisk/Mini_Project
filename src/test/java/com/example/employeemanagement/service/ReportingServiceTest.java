package com.example.employeemanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.employeemanagement.dto.response.DepartmentEmployeeCountProjection;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ReportingService reportingService;

    @Test
    void returnsTotalEmployeeCount() {
        when(employeeRepository.countTotalEmployees()).thenReturn(7L);

        assertThat(reportingService.totalEmployees()).isEqualTo(7L);
        verify(employeeRepository).countTotalEmployees();
    }

    @Test
    void mapsDepartmentReportProjection() {
        DepartmentEmployeeCountProjection projection = new DepartmentEmployeeCountProjection() {
            @Override
            public Long getDepartmentId() {
                return 3L;
            }

            @Override
            public String getDepartmentName() {
                return "Engineering";
            }

            @Override
            public Long getEmployeeCount() {
                return 4L;
            }
        };
        when(employeeRepository.countEmployeesByDepartment()).thenReturn(List.of(projection));

        assertThat(reportingService.employeesByDepartment())
                .containsExactly(new com.example.employeemanagement.dto.response.DepartmentEmployeeCountResponse(
                        3L, "Engineering", 4L));
    }
}
