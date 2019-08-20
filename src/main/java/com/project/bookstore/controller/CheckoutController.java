package com.project.bookstore.controller;

import com.project.bookstore.domain.*;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private BasketService basketService;

    @GetMapping("/checkout")
    public ModelAndView basketItems(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        List<BasketItem> itemList = basketService.findByShoppingCart(user.getShoppingCart());
        double totalPrice = 0;
        int totalQty = 0;

        for (BasketItem item : itemList) {
            totalPrice += item.getBook().getOurPrice() * item.getQty();
            totalQty += item.getQty();
        }

        Address defaultAddress = null;

        for (Address address : user.getAddressList()) {
            if (address.isDefaultAddress()) {
                defaultAddress = address;
            }
        }

        ModelAndView mav = new ModelAndView("checkout");

        mav.addObject("user", user);
        mav.addObject("basketItemList", itemList);
        mav.addObject("totalQty", totalQty);
        mav.addObject("totalPrice", totalPrice);
        mav.addObject("userAddressList", user.getAddressList());
        mav.addObject("userPaymentList", user.getPaymentList());
        mav.addObject("defaultAddress", defaultAddress);
        mav.addObject("localDateTime", LocalDateTime.now().plusDays(3));

        return mav;
    }

}
