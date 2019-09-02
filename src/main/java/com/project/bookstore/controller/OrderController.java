package com.project.bookstore.controller;

import com.project.bookstore.domain.*;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.OrderService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.utility.CartStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
public class OrderController {


    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BasketService basketService;

    @Autowired
    private CartStats cartStats;


    @GetMapping("/createOrder")
    public ModelAndView createOrder(Principal principal, ModelMap model, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        List<BasketItem> basketItemList = cartStats.basketItemList(user);

        if (user.getAddressList().isEmpty()) {
            model.addAttribute("noAddress", true);
            model.addAttribute("checkoutAddressChanged", true);
            return new ModelAndView("forward:/checkout", model);
        }

        if (user.getPaymentList().isEmpty()) {
            model.addAttribute("noPayment", true);
            model.addAttribute("checkoutPaymentChanged", true);
            return new ModelAndView("forward:/checkout", model);
        }

        if (basketItemList.isEmpty()) {
            ra.addFlashAttribute("emptyOrderList", true);
            return new ModelAndView("redirect:/basket/items");
        }

        OrderAddress orderAddress = createOrderAddressFromDefaultAddress(user);
        OrderPayment orderPayment = createOrderPaymentFromDefaultPayment(user);

        ModelAndView mav = new ModelAndView("afterOrdering");
        mav.addObject("itemList", basketItemList);
        mav.addObject("address", orderAddress);
        mav.addObject("payment", orderPayment);
        mav.addObject("today", LocalDate.now());
        mav.addObject("totalPrice", cartStats.totalPrice(user));
        mav.addObject("last4Digits", last4DigitsOfCardNumber(orderPayment.getCardNumber()));

        orderService.createOrder(user, basketItemList, orderAddress, orderPayment);
        basketService.clearShoppingCart(user.getShoppingCart());

        return mav;
    }

    private OrderAddress createOrderAddressFromDefaultAddress(User user) {

        List<Address> addressList = user.getAddressList();
        OrderAddress orderAddress = new OrderAddress();

        for (Address address : addressList) {
            if (address.isDefaultAddress()) {
                orderAddress.setFullName(address.getFullName());
                orderAddress.setPhoneNumber(address.getPhoneNumber());
                orderAddress.setCountryOrRegion(address.getCountryOrRegion());
                orderAddress.setPostCode(address.getPostCode());
                orderAddress.setStreetAddress1(address.getStreetAddress1());
                orderAddress.setCity(address.getCity());
            }
        }
        return orderAddress;
    }

    private OrderPayment createOrderPaymentFromDefaultPayment(User user) {

        List<Payment> paymentList = user.getPaymentList();
        OrderPayment orderPayment = new OrderPayment();
        for (Payment payment : paymentList) {
            if (payment.isDefaultCard()) {
                orderPayment.setNameOnCard(payment.getNameOnCard());
                orderPayment.setCardNumber(payment.getCardNumber());
                orderPayment.setExpiryMonth(payment.getExpiryMonth());
                orderPayment.setExpiryYear(payment.getExpiryYear());
                orderPayment.setCvc(payment.getCvc());
            }
        }
        return orderPayment;
    }

    public String last4DigitsOfCardNumber(String cardNumber) {

        String last4Digits = "";

        for (int i = 1; i < 5; i++) {
            last4Digits += cardNumber.charAt(cardNumber.length() - i);
        }
        return last4Digits;
    }
}