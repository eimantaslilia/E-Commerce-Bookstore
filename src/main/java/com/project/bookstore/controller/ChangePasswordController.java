package com.project.bookstore.controller;

import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.PasswordResetToken;
import com.project.bookstore.service.UserService;
import com.project.bookstore.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

@RestController
public class ChangePasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private JavaMailSenderImpl mailSender;


    @PostMapping("/changePassword")
    public RedirectView changePassword(@ModelAttribute("oldPassword") String oldPassword,
                                       @ModelAttribute("newPassword") String newPassword,
                                       Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        RedirectView rv = new RedirectView("/account");


        ra.addFlashAttribute("profileTabOpen", true);

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            ra.addFlashAttribute("passwordChanged", "Your password has been changed successfully.");
            userService.save(user);
        } else {

            ra.addFlashAttribute("incorrectCurrentPassword", "The old password you entered was incorrect");
            return rv;
        }

        return rv;
    }

    @GetMapping("/forgottenPassword")
    public ModelAndView forgottenPassword() {

        ModelAndView mav = new ModelAndView("forgottenPassword");
        return mav;
    }

    @PostMapping("/forgottenPassword")
    public RedirectView forgottenPasswordPost(@ModelAttribute("email") String email, RedirectAttributes ra, HttpServletRequest request) {

        User user = userService.findByEmail(email);

        if (user == null) {

            ra.addFlashAttribute("userDoesNotExist", true);
            return new RedirectView("/forgottenPassword");
        }

        String token = UUID.randomUUID().toString();

        String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        userService.createPasswordResetTokenForUser(user, token);

        mailSender.send(mailConstructor.constructPasswordResetEmail(appUrl, token, user));

        return new RedirectView("/passwordEmailSent?emailSent=true");
    }

    @GetMapping("/passwordEmailSent")
    public ModelAndView passwordEmailSent(@RequestParam("emailSent") boolean emailSent) {

        ModelAndView mav = new ModelAndView("passwordEmailSent");
        if (emailSent) {
            mav.addObject("newPasswordSent", true);
            return mav;
        }

        mav.addObject("areYouLost", true);
        return mav;
    }

    @RequestMapping("/resetPassword")
    public ModelAndView resetPassword(@RequestParam("token") String token, RedirectAttributes ra) {

        PasswordResetToken passToken = userService.getPasswordResetToken(token);

        ModelAndView mav = new ModelAndView("redirect:/forgottenPassword");

        if (passToken == null) {
            ra.addFlashAttribute("passTokenInvalid", true);
            return mav;
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0)) {
            ra.addFlashAttribute("tokenExpired", true);
            return mav;
        }

        User user = passToken.getUser();
        userService.deleteToken(passToken.getId());


        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));

        SecurityContextHolder.getContext().setAuthentication(auth);
        return new ModelAndView("updatePassword");
    }

    @PostMapping("/saveNewPassword")
    public RedirectView saveNewPassword(@ModelAttribute("newPassword") String password, RedirectAttributes ra) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);

        SecurityContextHolder.getContext().setAuthentication(null);

        RedirectView rv = new RedirectView("/login");
        ra.addFlashAttribute("passwordUpdatedAfterToken", true);
        return rv;
    }


}
