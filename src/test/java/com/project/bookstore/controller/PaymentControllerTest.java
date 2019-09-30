package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.PaymentService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class PaymentControllerTest {

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private UserService userService;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private Payment payment;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        payment = mock(Payment.class);

        given(paymentService.getOne(anyLong())).willReturn(payment);
        given(userService.findByUsername(anyString())).willReturn(user);
    }

    @Test
    @DisplayName("Add new Credit Card - Payment Already Exists")
    void addNewCreditCardPostExistingCard() throws Exception {

        List<Payment> userPaymentList = new ArrayList<>();
        Payment paymentThatExists = new Payment();
        paymentThatExists.setCardNumber("986588");
        userPaymentList.add(paymentThatExists);
        given(user.getPaymentList()).willReturn(userPaymentList);

        mockMvc.perform(post("/addNewCreditCard")
                .flashAttr("payment", paymentThatExists))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("paymentTabOpen", true))
                .andExpect(flash().attributeExists("creditNumberExists"))
                .andExpect(view().name("redirect:/account"));

        verify(paymentService, never()).addNewCreditCard(any(User.class), any(Payment.class));
    }

    @Test
    @DisplayName("Add new Credit Card - Successful")
    void addNewCreditCardPostNewCard() throws Exception {

        mockMvc.perform(post("/addNewCreditCard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("paymentTabOpen", true))
                .andExpect(view().name("redirect:/account"));

        verify(paymentService, only()).addNewCreditCard(any(User.class), any(Payment.class));
    }

    @Test
    @DisplayName("Set Payment as Default and go to Checkout")
    void setAsDefaultPaymentAndCheckout() throws Exception {

        mockMvc.perform(get("/setAsDefaultPayment?checkout=" + true)
                .flashAttr("paymentId", 50L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("checkoutPaymentChanged", true))
                .andExpect(view().name("redirect:/checkout"));
    }

    @Test
    @DisplayName("Set Payment as Default and go to Account/Payment Methods")
    void setAsDefaultPaymentAndAccount() throws Exception {

        mockMvc.perform(get("/setAsDefaultPayment?checkout=" + false)
                .flashAttr("paymentId", 50L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("defaultPaymentChanged"))
                .andExpect(view().name("redirect:/account"));
    }

    @Test
    @DisplayName("Remove Default Credit Card, Set New Default and Go to Checkout")
    void removeCreditCardAndCheckout() throws Exception {

        Payment paymentToBeMadeDefault = new Payment();
        List<Payment> userPaymentList = Collections.singletonList(paymentToBeMadeDefault);

        given(user.getPaymentList()).willReturn(userPaymentList);
        given(payment.isDefaultCard()).willReturn(true); //assume deleted card was default

        mockMvc.perform(get("/removeCreditCard?id=" + anyLong() + "&checkout=" + true))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("checkoutPaymentChanged"))
                .andExpect(view().name("redirect:/checkout"));

        verify(paymentService, times(1)).deleteById(anyLong());
        assertTrue(user.getPaymentList().get(0).isDefaultCard()); //so the non-default card in our list becomes default
    }

    @Test
    @DisplayName("Remove Credit Card That was not Default and Go to Account/Payment Methods")
    void removeCreditCardAndAccount() throws Exception {

        mockMvc.perform(get("/removeCreditCard?id=" + anyLong() + "&checkout=" + false))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("paymentTabOpen"))
                .andExpect(view().name("redirect:/account"));
    }

    @Test
    @DisplayName("Button to Add a new Payment in Checkout")
    void paymentMethodsInAccount() throws Exception {
        mockMvc.perform(get("/paymentFromCheckout"))
                .andExpect(flash().attribute("paymentTabOpen", true))
                .andExpect(redirectedUrl("/account"));
    }

    @Test
    @DisplayName("Button to Checkout in Payment Methods ")
    void linkToCheckoutFromPaymentMethodsInAccount() throws Exception {
        mockMvc.perform(get("/checkoutPaymentFromAccount"))
                .andExpect(flash().attribute("checkoutPaymentChanged", true))
                .andExpect(redirectedUrl("/checkout"));
    }
}