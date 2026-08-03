package com.example.employeemanagement.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.employeemanagement.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
        departmentRepository.save(new Department("Engineering", "Product engineering"));
        departmentRepository.save(new Department("People", "People operations"));
    }

    @Test
    void findsDepartmentIgnoringCase() {
        assertThat(departmentRepository.findByNameIgnoreCase("engineering"))
                .isPresent()
                .get()
                .extracting(Department::getName)
                .isEqualTo("Engineering");
    }

    @Test
    void detectsDuplicateNamesIgnoringCase() {
        assertThat(departmentRepository.existsByNameIgnoreCase("ENGINEERING")).isTrue();
        assertThat(departmentRepository.existsByNameIgnoreCase("Finance")).isFalse();
    }
}
