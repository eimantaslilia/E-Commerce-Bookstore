package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.PasswordResetToken;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import com.project.bookstore.utility.MailConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChangePasswordController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class ChangePasswordControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserSecurityService userSecurityService;
    @MockBean
    private UserService userService;
    @MockBean
    private MailConstructor mailConstructor;
    @MockBean
    private JavaMailSenderImpl mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        User user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);
    }

    @Test
    @DisplayName("My Account/Change Password - Passwords Dont Match")
    void changePasswordFromMyAccountDontMatch() throws Exception {

        mockMvc.perform(post("/changePassword")
                .flashAttr("oldPassword", "this pass will not match db pass cause not encoded")
                .flashAttr("newPassword", "random"))
                .andExpect(flash().attribute("profileTabOpen", true))
                .andExpect(flash().attributeExists("incorrectCurrentPassword"))
                .andExpect(view().name("redirect:/account"));
    }

    @Test
    @DisplayName("My Account/Change Password - Password Changed Successfully")
    void changePasswordFromMyAccountMatch() throws Exception {

        //first value here has to match with flashAttr oldPassword. Second value has to be encoded cause pass in db is encoded.
        given(passwordEncoder.matches("correctOldPass", passwordEncoder.encode("willMatchForTest"))).willReturn(true);
        //once true the savePassword() method runs so change is successful and passwordChanged attribute is added.

        mockMvc.perform(post("/changePassword")
                .flashAttr("oldPassword", "correctOldPass")
                .flashAttr("newPassword", "newPass"))
                .andExpect(flash().attribute("profileTabOpen", true))
                .andExpect(flash().attributeExists("passwordChanged"))
                .andExpect(view().name("redirect:/account"));

        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void forgottenPasswordPage() throws Exception {
        mockMvc.perform(get("/forgottenPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgottenPassword"));
    }

    @Test
    @DisplayName("Forgotten Password Retrieval - User does not exist")
    void sendingPasswordRetrievalEmailFormUserIsNull() throws Exception {

        given(userService.findByEmail(anyString())).willReturn(null);
        mockMvc.perform(post("/forgottenPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("userDoesNotExist", true))
                .andExpect(view().name("redirect:/forgottenPassword"));

        verify(userService, never()).createPasswordResetTokenForUser(any(User.class), anyString());
    }

    @Test
    @DisplayName("Forgotten Password Retrieval - User exists")
    void sendingPasswordRetrievalEmailFormUserIsFound() throws Exception {

        User userForEmailForm = mock(User.class);

        given(userService.findByEmail(anyString())).willReturn(userForEmailForm);
        mockMvc.perform(post("/forgottenPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/passwordEmailSent?emailSent=true"));

        verify(userService, times(1)).createPasswordResetTokenForUser(any(User.class), anyString());
    }

    @Test
    @DisplayName("Password Retrieval Email Sent")
    void passwordRetrievalEmailSent() throws Exception {

        mockMvc.perform(get("/passwordEmailSent?emailSent=" + true))
                .andExpect(status().isOk())
                .andExpect(model().attribute("newPasswordSent", true))
                .andExpect(view().name("passwordEmailSent"));
    }

    @Test
    @DisplayName("Password Retrieval Email Has Not Been Sent")
    void passwordRetrievalEmailNotSent() throws Exception {

        mockMvc.perform(get("/passwordEmailSent?emailSent=" + false))
                .andExpect(status().isOk())
                .andExpect(model().attribute("areYouLost", true))
                .andExpect(model().attributeDoesNotExist("newPasswordSent"))
                .andExpect(view().name("passwordEmailSent"));
    }

    @Test
    @DisplayName("Reset Password - Token Is Null")
    void resetPasswordWithEmailNullToken() throws Exception {

        given(userService.getPasswordResetToken(anyString())).willReturn(null);

        mockMvc.perform(get("/resetPassword?token=" + anyString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("passTokenInvalid", true))
                .andExpect(view().name("redirect:/forgottenPassword"));

        verify(userService, never()).deleteToken(anyLong());
    }

    @Disabled("How to mock an expired token if the expiry date is set automatically in constructor?" +
            "and token expiration date is checked against current time. If we create token in test and check it - it won't be expired")
    @Test
    @DisplayName("Reset Password - Token Is Expired")
    void resetPasswordWithEmailExpiredToken() throws Exception {

        PasswordResetToken myToken = mock(PasswordResetToken.class);

        given(userService.getPasswordResetToken(anyString())).willReturn(myToken);

        mockMvc.perform(get("/resetPassword?token=" + anyString()))
                .andExpect(status().isOk())
                .andExpect(flash().attribute("tokenExpired", true))
                .andExpect(view().name("redirect:/forgottenPassword"));
    }
}