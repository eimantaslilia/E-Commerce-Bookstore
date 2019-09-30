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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/changePassword")
    public ModelAndView changePasswordFromMyAccount(@ModelAttribute("oldPassword") String oldPassword,
                                                    @ModelAttribute("newPassword") String newPassword,
                                                    Principal principal, RedirectAttributes ra) {

        if (passwordChangeSuccessful(principal, oldPassword, newPassword)) {
            ra.addFlashAttribute("passwordChanged", "Your password has been changed successfully");
        } else {
            ra.addFlashAttribute("incorrectCurrentPassword", "The current password you entered was incorrect");
        }

        ra.addFlashAttribute("profileTabOpen", true);
        return new ModelAndView("redirect:/account");
    }

    private boolean passwordChangeSuccessful(Principal principal, String oldPassword, String newPassword) {

        User user = userService.findByUsername(principal.getName());

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            savePassword(user, newPassword);
            return true;
        }
        return false;
    }

    private void savePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
    }

    @GetMapping("/forgottenPassword")
    public ModelAndView forgottenPasswordPage() {
        return new ModelAndView("forgottenPassword");
    }

    @PostMapping("/forgottenPassword")
    public ModelAndView sendingPasswordRetrievalEmailForm(@ModelAttribute("email") String email, RedirectAttributes ra, HttpServletRequest request) {

        User user = userService.findByEmail(email);

        if (user == null) {
            ra.addFlashAttribute("userDoesNotExist", true);
            return new ModelAndView("redirect:/forgottenPassword");
        }

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String changePassUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        mailSender.send(mailConstructor.constructPasswordResetEmail(changePassUrl, token, user));

        return new ModelAndView("redirect:/passwordEmailSent?emailSent=true");
    }

    @GetMapping("/passwordEmailSent")
    public ModelAndView passwordRetrievalEmailSent(@RequestParam("emailSent") boolean emailSent) {

        ModelAndView mav = new ModelAndView("passwordEmailSent");

        if (emailSent) {
            mav.addObject("newPasswordSent", true);
            return mav;
        }
        mav.addObject("areYouLost", true);
        return mav;
    }

    @RequestMapping("/resetPassword")
    public ModelAndView resetPasswordWithEmail(@RequestParam("token") String token, RedirectAttributes ra) {

        PasswordResetToken passToken = userService.getPasswordResetToken(token);

        ModelAndView mav = new ModelAndView("redirect:/forgottenPassword");

        if (passToken == null) {
            ra.addFlashAttribute("passTokenInvalid", true);
            return mav;
        }
        if (tokenHasExpired(passToken)) {
            ra.addFlashAttribute("tokenExpired", true);
            return mav;
        }

        User user = passToken.getUser();
        userService.deleteToken(passToken.getId());

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));

        SecurityContextHolder.getContext().setAuthentication(auth);
        return new ModelAndView("updatePassword");
    }

    private boolean tokenHasExpired(PasswordResetToken passToken) {

        Calendar cal = Calendar.getInstance();
        if (passToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
            return true;
        }
        return false;
    }

    @PostMapping("/saveNewPassword")
    public ModelAndView saveNewPasswordFromForgottenForm(@ModelAttribute("newPassword") String password, RedirectAttributes ra) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        savePassword(user, password);

        SecurityContextHolder.getContext().setAuthentication(null);

        ra.addFlashAttribute("passwordUpdatedAfterToken", true);
        return new ModelAndView("redirect:/login");
    }

}
