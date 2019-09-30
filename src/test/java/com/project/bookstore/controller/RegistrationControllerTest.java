package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Registration - User with this username already exists, back to Sign Up")
    void registerNewAccountUsernameExists() throws Exception {

        User user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);

        mockMvc.perform(post("/registration"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("usernameExists", true))
                .andExpect(view().name("redirect:/signup"));

        verify(userService, never()).saveUserAndRolesAndCart(any(User.class), anySet());
    }

    @Test
    @DisplayName("Registration - User with this Email address already exists, back to Sign Up")
    void registerNewAccountEmailExists() throws Exception {

        User user = mock(User.class);
        given(userService.findByEmail(anyString())).willReturn(user);

        mockMvc.perform(post("/registration"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("emailExists", true))
                .andExpect(view().name("redirect:/signup"));

        verify(userService, never()).saveUserAndRolesAndCart(any(User.class), anySet());
    }

    @Test
    @DisplayName("Registration Success - Username and Email is Unique")
    void registerNewAccountSuccess() throws Exception {


        mockMvc.perform(post("/registration"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("registrationSuccessful", true))
                .andExpect(view().name("redirect:/login"));

        verify(userService, times(1)).saveUserAndRolesAndCart(any(User.class), anySet());
    }
}