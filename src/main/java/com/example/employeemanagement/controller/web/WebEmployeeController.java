package com.example.employeemanagement.controller.web;

import com.example.employeemanagement.dto.request.EmployeeForm;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.ReportingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class WebEmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final ReportingService reportingService;

    public WebEmployeeController(EmployeeService employeeService,
                                 DepartmentService departmentService,
                                 ReportingService reportingService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.reportingService = reportingService;
    }

    @GetMapping({"/list", "/search"})
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) Long departmentId,
                       Model model) {
        model.addAttribute("employees", employeeService.findAll(name, departmentId));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("searchName", name == null ? "" : name);
        model.addAttribute("searchDepartmentId", departmentId);
        return "employees/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new EmployeeForm());
        addDepartments(model);
        return "employees/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("employee") EmployeeForm form,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDepartments(model);
            return "employees/add";
        }
        try {
            employeeService.create(form.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully");
            return "redirect:/employees/list";
        } catch (DuplicateResourceException | ResourceNotFoundException exception) {
            bindingResult.reject("employee.save.failed", exception.getMessage());
            addDepartments(model);
            return "employees/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("employeeId", id);
            model.addAttribute("employee", EmployeeForm.from(employeeService.findById(id)));
            addDepartments(model);
            return "employees/edit";
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/employees/list";
        }
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("employee") EmployeeForm form,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDepartments(model);
            return "employees/edit";
        }
        try {
            employeeService.update(id, form.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully");
        } catch (DuplicateResourceException | ResourceNotFoundException exception) {
            bindingResult.reject("employee.save.failed", exception.getMessage());
            addDepartments(model);
            return "employees/edit";
        }
        return "redirect:/employees/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/employees/list";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("totalEmployees", reportingService.totalEmployees());
        model.addAttribute("departmentReports", reportingService.employeesByDepartment());
        return "employees/statistics";
    }

    private void addDepartments(Model model) {
        model.addAttribute("departments", departmentService.findAll());
    }
}
