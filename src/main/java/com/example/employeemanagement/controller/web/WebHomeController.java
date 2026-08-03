package com.example.employeemanagement.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebHomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/employees/list";
    }
}
