package com.project.bookstore.controller;

import com.project.bookstore.domain.*;
import com.project.bookstore.service.UserService;
import com.project.bookstore.utility.CartStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartStats cartStats;

    @GetMapping("/checkout")
    public ModelAndView basketItems(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView mav = new ModelAndView("checkout");

        mav.addObject("user", user);
        mav.addObject("basketItemList", cartStats.basketItemList(user));
        mav.addObject("totalQty", cartStats.totalQty(user));
        mav.addObject("totalPrice", cartStats.totalPrice(user));
        mav.addObject("userAddressList", user.getAddressList());
        mav.addObject("userPaymentList", user.getPaymentList());
        mav.addObject("estimatedDeliveryDate", LocalDateTime.now().plusDays(3));

        return mav;
    }

}
