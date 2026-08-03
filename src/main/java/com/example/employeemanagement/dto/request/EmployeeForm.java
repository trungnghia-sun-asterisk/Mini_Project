package com.example.employeemanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class EmployeeForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotNull(message = "Department is required")
    @Positive(message = "Department id must be positive")
    private Long departmentId;

    public EmployeeForm() {
    }

    public EmployeeForm(String name, String email, Long departmentId) {
        this.name = name;
        this.email = email;
        this.departmentId = departmentId;
    }

    public static EmployeeForm from(com.example.employeemanagement.dto.response.EmployeeResponse employee) {
        return new EmployeeForm(employee.name(), employee.email(), employee.departmentId());
    }

    public EmployeeRequest toRequest() {
        return new EmployeeRequest(name, email, departmentId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
