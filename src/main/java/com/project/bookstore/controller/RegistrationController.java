package com.project.bookstore.controller;

import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.Role;
import com.project.bookstore.domain.security.UserRole;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.HashSet;
import java.util.Set;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/registration")
    public ModelAndView registerNewAccount(@ModelAttribute("username") String username,
                                           @ModelAttribute("email") String email,
                                           @ModelAttribute("password") String password,
                                           RedirectAttributes ra) throws Exception {

        if (userService.findByUsername(username) != null) {
            ra.addFlashAttribute("usernameExists", true);
            return new ModelAndView("redirect:/signup");
        }
        if (userService.findByEmail(email) != null) {
            ra.addFlashAttribute("emailExists", true);
            return new ModelAndView("redirect:/signup");
        }

        createUserAndRoles(username, email, password);

        ra.addFlashAttribute("registrationSuccessful", true);
        return new ModelAndView("redirect:/login");
    }

    private void createUserAndRoles(String username, String email, String password) throws Exception {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));

        userService.saveUserAndRolesAndCart(user, userRoles);
    }
}
