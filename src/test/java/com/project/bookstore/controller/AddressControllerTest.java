package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.AddressService;
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

@WebMvcTest(AddressController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class AddressControllerTest {

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private Address address;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        address = mock(Address.class);

        given(addressService.getOne(anyLong())).willReturn(address);
        given(userService.findByUsername(anyString())).willReturn(user);
    }

    @Test
    @DisplayName("Unsuccessful - address already exists")
    void testAddingNewAddressPostMethodAlreadyExists() throws Exception {

        List<Address> userAddressList = new ArrayList<>();
        Address addressThatExists = new Address();
        addressThatExists.setStreetAddress1("Lancaster st. 122");
        userAddressList.add(addressThatExists);
        given(user.getAddressList()).willReturn(userAddressList);

        mockMvc.perform(post("/addNewAddress")
                .flashAttr("address", addressThatExists))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("addressTabOpen", true))
                .andExpect(flash().attributeExists("streetAddressExists"))
                .andExpect(view().name("redirect:/account"));

        verify(addressService, never()).addNewAddress(any(User.class), any(Address.class));
    }

    @Test
    @DisplayName("Successful - address added")
    void testAddingNewAddressPostMethodSuccess() throws Exception {


        mockMvc.perform(post("/addNewAddress"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("addressTabOpen", true))
                .andExpect(view().name("redirect:/account"));

        verify(addressService, only()).addNewAddress(any(User.class), any(Address.class));
    }

    @Test
    @DisplayName("Remove Address that was NOT DEFAULT and go to Account Page")
    void removeAddressAccount() throws Exception {

        mockMvc.perform(get("/removeAddress?id=" + anyLong() + "&checkout=" + false))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("addressTabOpen", true))
                .andExpect(view().name("redirect:/account"));

        verify(addressService, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Remove Address that was DEFAULT and go to Checkout Page")
    void removeAddressCheckout() throws Exception {

        Address addressToBeMadeDefault = new Address();
        List<Address> addressList = new ArrayList<>(Collections.singletonList(addressToBeMadeDefault));

        given(user.getAddressList()).willReturn(addressList);
        given(address.isDefaultAddress()).willReturn(true);

        mockMvc.perform(get("/removeAddress?id=" + anyLong() + "&checkout=" + true))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/checkout"));

        assertTrue(user.getAddressList().get(0).isDefaultAddress());
        verify(addressService, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Set as Default Address and go to Account Page")
    void setAsDefaultAddressAndAccount() throws Exception {

        mockMvc.perform(get("/setAsDefaultAddress?addressId=" + anyLong() + "&checkout=" + false))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("addressTabOpen", true))
                .andExpect(flash().attributeExists("defaultAddressChanged"))
                .andExpect(view().name("redirect:/account"));

        verify(addressService, only()).setAsDefaultAddress(any(User.class), anyLong());
    }

    @Test
    @DisplayName("Set as Default Address and go to Account Page")
    void setAsDefaultAddressAndCheckout() throws Exception {

        mockMvc.perform(get("/setAsDefaultAddress?addressId=" + anyLong() + "&checkout=" + true))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("checkoutAddressChanged", true))
                .andExpect(view().name("redirect:/checkout"));

        verify(addressService, only()).setAsDefaultAddress(any(User.class), anyLong());
    }

    @Test
    @DisplayName("Add a new Address Button in Checkout Points to Account/Address Tab")
    void deliveryAddressesInAccount() throws Exception {
        mockMvc.perform(get("/addressFromCheckout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("addressTabOpen", true))
                .andExpect(redirectedUrl("/account"));
    }

    @Test
    @DisplayName("Checkout button in Account Page - Payment and Delivery tabs points to Checkout")
    void linkToCheckoutFromDeliveryAddressesInAccount() throws Exception {
        mockMvc.perform(get("/checkoutAddressFromAccount"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("checkoutAddressChanged", true))
                .andExpect(redirectedUrl("/checkout"));
    }
}