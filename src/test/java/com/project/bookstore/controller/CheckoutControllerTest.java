package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import com.project.bookstore.utility.CartStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class CheckoutControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserSecurityService userSecurityService;
    @MockBean
    private UserService userService;
    @MockBean
    private CartStats cartStats;

    @Test
    void basketItems() throws Exception {

        User user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("basketItemList", cartStats.basketItemList(user)))
                .andExpect(model().attribute("totalQty", cartStats.totalQty(user)))
                .andExpect(model().attribute("totalPrice", cartStats.totalPrice(user)))
                .andExpect(model().attribute("userAddressList", user.getAddressList()))
                .andExpect(model().attribute("userPaymentList", user.getPaymentList()))
                .andExpect(model().attributeExists("estimatedDeliveryDate"))
                .andExpect(view().name("checkout"));
    }
}