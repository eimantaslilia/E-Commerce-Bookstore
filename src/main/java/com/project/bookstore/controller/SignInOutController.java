package com.project.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignInOutController {


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("classActiveLogin", "active");
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("classActiveLogin", "active");
        return "signup";
    }
    @GetMapping("/deliveryInfo")
    public String deliveryInfo(Model model) {
        return "deliveryInformation";
    }
}
