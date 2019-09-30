package com.project.bookstore.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SignInRegisterController {


    @GetMapping("/login")
    public ModelAndView login(Model model) {
        model.addAttribute("classActiveLogin", "active");
        return new ModelAndView("login");
    }

    @GetMapping("/signup")
    public ModelAndView signup(Model model) {

        model.addAttribute("classActiveLogin", "active");
        return new ModelAndView("signup");

    }
    @GetMapping("/deliveryInfo")
    public ModelAndView deliveryInfo() {
        return new ModelAndView("deliveryInformation");
    }
}
