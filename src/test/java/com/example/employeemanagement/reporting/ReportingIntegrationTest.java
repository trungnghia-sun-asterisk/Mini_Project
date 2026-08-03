package com.example.employeemanagement.reporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employeemanagement.dto.request.EmployeeRequest;
import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.security.JwtService;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private Department department;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        appUserRepository.deleteAll();
        department = departmentRepository.save(new Department("Engineering", "Product engineering"));
        appUserRepository.save(new AppUser("report-user", passwordEncoder.encode("ignored-password"), Role.USER));
        Cache cache = cacheManager.getCache("employeeTotal");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void reportEndpointsReturnTotalAndDepartmentCounts() throws Exception {
        employeeRepository.save(new Employee("Ada", "ada-report@example.com", department));
        String token = jwtService.generateToken(User.withUsername("report-user")
                .password("ignored")
                .roles("USER")
                .build());

        mockMvc.perform(get("/api/reports/employees/total")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(1));

        mockMvc.perform(get("/api/reports/employees/by-department")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$[0].employeeCount").value(1));
    }

    @Test
    void employeeMutationsEvictTotalEmployeeCache() {
        Cache cache = cacheManager.getCache("employeeTotal");
        assertThat(cache).isNotNull();

        employeeService.create(new EmployeeRequest("Ada", "ada-cache@example.com", department.getId()));
        assertThat(reportingService.totalEmployees()).isEqualTo(1);
        assertThat(cache.get(SimpleKey.EMPTY).get()).isEqualTo(1L);

        employeeService.create(new EmployeeRequest("Grace", "grace-cache@example.com", department.getId()));

        assertThat(cache.get(SimpleKey.EMPTY)).isNull();
        assertThat(reportingService.totalEmployees()).isEqualTo(2);
    }
}
