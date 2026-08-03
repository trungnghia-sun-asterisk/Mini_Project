package com.example.employeemanagement.controller.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebEmployeeTemplateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Department department;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        department = departmentRepository.save(new Department("Engineering", "Product engineering"));
    }

    @Test
    void rendersAddFormForAdmin() throws Exception {
        mockMvc.perform(get("/employees/add")
                        .with(user(User.withUsername("admin").password("ignored").roles("ADMIN").build())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Add employee")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Engineering")));
    }

    @Test
    void rendersEditAndStatisticsPages() throws Exception {
        Employee employee = employeeRepository.save(new Employee("Ada", "ada@example.com", department));
        var admin = user(User.withUsername("admin").password("ignored").roles("ADMIN").build());

        mockMvc.perform(get("/employees/{id}/edit", employee.getId()).with(admin))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Edit employee")));

        mockMvc.perform(get("/employees/statistics").with(admin))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Total employees")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Engineering")));
    }

    @Test
    void displaysValidationErrorsOnAddForm() throws Exception {
        mockMvc.perform(post("/employees/add")
                        .with(user(User.withUsername("admin").password("ignored").roles("ADMIN").build()))
                        .with(csrf())
                        .param("name", "")
                        .param("email", "invalid")
                        .param("departmentId", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Name is required")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Email must be valid")));
    }
}
