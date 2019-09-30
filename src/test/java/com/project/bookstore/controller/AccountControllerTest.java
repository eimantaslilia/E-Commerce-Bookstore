package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class AccountControllerTest {

    @MockBean
    UserSecurityService userSecurityService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testAccountPage() throws Exception {
        User user = Mockito.mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userPaymentList", user.getPaymentList()))
                .andExpect(model().attribute("userAddressList", user.getAddressList()))
                .andExpect(model().attribute("classActiveAccount", "active"))
                .andExpect(model().attributeExists("userOrderList"))
                .andExpect(view().name("profile"));
    }

    @Test
    void testOrdersTabWithMockUser() throws Exception {

        mockMvc.perform(get("/profile"))
                .andExpect(model().attribute("ordersTabOpen", true))
                .andExpect(model().attribute("classActiveOrders", "active"))
                .andExpect(view().name("forward:/account"));
    }
}