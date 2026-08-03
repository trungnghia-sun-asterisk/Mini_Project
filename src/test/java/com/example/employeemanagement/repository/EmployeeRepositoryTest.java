package com.example.employeemanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.example.employeemanagement.dto.response.DepartmentEmployeeCountProjection;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department engineering;
    private Department people;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        engineering = departmentRepository.save(new Department("Engineering", "Product engineering"));
        people = departmentRepository.save(new Department("People", "People operations"));
        employeeRepository.save(new Employee("Ada Lovelace", "ada@example.com", engineering));
        employeeRepository.save(new Employee("Grace Hopper", "grace@example.com", engineering));
        employeeRepository.save(new Employee("Mary Jackson", "mary@example.com", people));
    }

    @Test
    void searchesByNameIgnoringCase() {
        List<Employee> result = employeeRepository.findByNameContainingIgnoreCaseOrderByNameAsc("hopper");

        assertThat(result).extracting(Employee::getName).containsExactly("Grace Hopper");
    }

    @Test
    void searchesByDepartment() {
        List<Employee> result = employeeRepository.findByDepartmentIdOrderByNameAsc(engineering.getId());

        assertThat(result).extracting(Employee::getName)
                .containsExactly("Ada Lovelace", "Grace Hopper");
    }

    @Test
    void countsTotalAndByDepartment() {
        assertThat(employeeRepository.countTotalEmployees()).isEqualTo(3);
        assertThat(employeeRepository.countByDepartmentId(engineering.getId())).isEqualTo(2);

        List<DepartmentEmployeeCountProjection> report = employeeRepository.countEmployeesByDepartment();
        assertThat(report).hasSize(2);
        assertThat(report.get(0).getDepartmentName()).isEqualTo("Engineering");
        assertThat(report.get(0).getEmployeeCount()).isEqualTo(2);
    }
}
