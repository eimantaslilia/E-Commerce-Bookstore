package com.project.bookstore.controller;

import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.Role;
import com.project.bookstore.domain.security.UserRole;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


import java.util.HashSet;
import java.util.Set;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;


    @PostMapping("/registration")
    public ModelAndView registerNewAccount(@ModelAttribute("username") String username,
                                           @ModelAttribute("email") String email,
                                           @ModelAttribute("password") String password) throws Exception {

        ModelAndView mav = new ModelAndView("signup");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (userService.findByUsername(username) != null) {
            mav.addObject("usernameExists", true);
            return mav;
        }
        if (userService.findByEmail(email) != null) {
            mav.addObject("emailExists", true);
            return mav;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);

        ModelAndView loginPage = new ModelAndView("login");
        loginPage.addObject("registrationSuccessful", true);

        return loginPage;
    }
}
