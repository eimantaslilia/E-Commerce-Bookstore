package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.*;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.OrderService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import com.project.bookstore.utility.CartStats;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserSecurityService userSecurityService;
    @MockBean
    private UserService userService;
    @MockBean
    private OrderService orderService;
    @MockBean
    private BasketService basketService;
    @MockBean
    private CartStats cartStats;
    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);
    }

    @Test
    @DisplayName("Create Order Failure - Address List is Empty")
    void createOrderEmptyAddressList() throws Exception {

        mockMvc.perform(get("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("noAddress", true))
                .andExpect(model().attribute("checkoutAddressChanged", true))
                .andExpect(model().attributeDoesNotExist("checkoutPaymentChanged"))
                .andExpect(model().attributeDoesNotExist("last4Digits"))
                .andExpect(view().name("forward:/checkout"));

        assertThat(user.getAddressList()).isEmpty();
        verify(orderService, never()).createOrder(any(User.class), anyList(), any(), any());
    }

    @Test
    @DisplayName("Create Order Failure - Payment List is Empty")
    void createOrderEmptyPaymentList() throws Exception {

        List<Address> userAddressList = Collections.singletonList(new Address());
        given(user.getAddressList()).willReturn(userAddressList);

        mockMvc.perform(get("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("noPayment", true))
                .andExpect(model().attribute("checkoutPaymentChanged", true))
                .andExpect(model().attributeDoesNotExist("checkoutAddressChanged"))
                .andExpect(model().attributeDoesNotExist("last4Digits"))
                .andExpect(view().name("forward:/checkout"));

        assertThat(user.getPaymentList()).isEmpty();
        verify(orderService, never()).createOrder(any(User.class), anyList(), any(), any());
    }

    @Test
    @DisplayName("Create Order Failure - Basket Item List is Empty")
    void createOrderEmptyBasketItemList() throws Exception {

        List<Address> userAddressList = Collections.singletonList(new Address());
        given(user.getAddressList()).willReturn(userAddressList);

        List<Payment> userPaymentList = Collections.singletonList(new Payment());
        given(user.getPaymentList()).willReturn(userPaymentList);

        mockMvc.perform(get("/createOrder"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("emptyOrderList", true))
                .andExpect(model().attributeDoesNotExist("checkoutAddressChanged"))
                .andExpect(model().attributeDoesNotExist("checkoutPaymentChanged"))
                .andExpect(model().attributeDoesNotExist("itemList"))
                .andExpect(view().name("redirect:/basket/items"));

        verify(orderService, never()).createOrder(any(User.class), anyList(), any(), any());
    }

    @Test
    @DisplayName("Create Order Success - All Objects Present")
    void createOrderAllGood() throws Exception {

        List<Address> userAddressList = new ArrayList<>();
        Address address = new Address();
        address.setId(50L);
        address.setFullName("Don");
        address.setPhoneNumber(5646413L);
        address.setCountryOrRegion("Malaysia");
        address.setPostCode("S701LW");
        address.setStreetAddress1("Hell's Kitchen");
        address.setDefaultAddress(true);
        userAddressList.add(address);
        given(user.getAddressList()).willReturn(userAddressList);

        List<Payment> userPaymentList = new ArrayList<>();
        Payment payment = new Payment();
        payment.setId(75L);
        payment.setDefaultCard(true);
        payment.setCardNumber("5987654123");
        payment.setExpiryMonth(10);
        payment.setExpiryYear(1999);
        payment.setCvc(365);
        payment.setNameOnCard("Aladdin");
        userPaymentList.add(payment);
        given(user.getPaymentList()).willReturn(userPaymentList);

        List<BasketItem> basketItemList = new ArrayList<>();
        Book book = new Book();
        book.setId(20L);
        BasketItem basketItem = new BasketItem();
        basketItem.setBook(book);
        basketItemList.add(basketItem);
        given(cartStats.basketItemList(user)).willReturn(basketItemList);

        mockMvc.perform(get("/createOrder"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("noAddress"))
                .andExpect(model().attributeDoesNotExist("noPayment"))
                .andExpect(model().attribute("itemList", basketItemList))
                .andExpect(model().attributeExists("address"))
                .andExpect(model().attributeExists("today"))
                .andExpect(model().attribute("totalPrice", cartStats.totalPrice(user)))
                .andExpect(model().attributeExists("last4Digits"))
                .andExpect(view().name("afterOrdering"));

        verify(orderService, only()).createOrder(any(User.class), anyList(), any(), any());
        verify(basketService, only()).clearShoppingCart(any());
    }

    @DisplayName("Return the last 4 digits of the card number")
    @Test
    void test4DigitReturn() {

        OrderController returnTest = mock(OrderController.class);
        when(returnTest.last4DigitsOfCardNumber(anyString())).thenCallRealMethod();

        String digits = returnTest.last4DigitsOfCardNumber("654321");
        assertEquals("4321", digits);
    }
}