package com.example.employeemanagement.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employeemanagement.entity.AppUser;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        appUserRepository.deleteAll();
        appUserRepository.save(new AppUser("user", passwordEncoder.encode("user-password"), Role.USER));
        appUserRepository.save(new AppUser("admin", passwordEncoder.encode("admin-password"), Role.ADMIN));
    }

    @Test
    void helloAndHealthArePublic() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee Management System is running successfully"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));

        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/caches"))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedApiRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void userCanReadButCannotCreateEmployee() throws Exception {
        String token = tokenFor("user", Role.USER);

        mockMvc.perform(get("/api/employees").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("{\"name\":\"Ada\",\"email\":\"ada@example.com\",\"departmentId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminCanCreateEmployee() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering", ""));
        String token = tokenFor("admin", Role.ADMIN);

        mockMvc.perform(post("/api/employees")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.createObjectNode()
                                .put("name", "Ada Lovelace")
                                .put("email", "ada@example.com")
                                .put("departmentId", department.getId())
                                .toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));
    }

    @Test
    void registerAndLoginReturnJwt() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"new-user\",\"password\":\"strong-password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"new-user\",\"password\":\"strong-password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode response = objectMapper.readTree(loginResponse);
        org.assertj.core.api.Assertions.assertThat(response.path("accessToken").asText()).isNotBlank();

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"new-user\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void webLoginIsPublicAndEmployeesRequireAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sign in")));

        mockMvc.perform(get("/employees/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/login")));

        mockMvc.perform(get("/employees/list").with(user(User.withUsername("user").password("ignored").roles("USER").build())))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Employees")));
    }

    private String tokenFor(String username, Role role) {
        UserDetails user = User.withUsername(username).password("ignored").roles(role.name()).build();
        return jwtService.generateToken(user);
    }
}
