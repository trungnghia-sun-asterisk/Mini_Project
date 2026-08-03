package com.example.employeemanagement.controller.web;

import com.example.employeemanagement.dto.request.RegistrationForm;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthWebController {

    private final AuthenticationService authenticationService;

    public AuthWebController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registration", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") RegistrationForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            authenticationService.register(form.toRequest());
            return "redirect:/login?registered";
        } catch (DuplicateResourceException exception) {
            bindingResult.reject("registration.failed", exception.getMessage());
            return "auth/register";
        }
    }
}
