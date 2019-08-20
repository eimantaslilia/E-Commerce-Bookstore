package com.project.bookstore.controller;

import com.project.bookstore.domain.Order;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
public class AccountController {

    @Autowired
    private UserService userService;


    @GetMapping("/account")
    public ModelAndView account(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView mav = new ModelAndView("profile");

        mav.addObject("user", user);
        mav.addObject("userPaymentList", user.getPaymentList());
        mav.addObject("userAddressList", user.getAddressList());
        mav.addObject("classActiveAccount", "active");

        List<Order> orderList = user.getOrderList();
        orderList.sort(Comparator.comparing(Order::getId));
        Collections.reverse(orderList);
        mav.addObject("userOrderList", orderList);

        return mav;

    }

    @GetMapping("/profile")
    public ModelAndView ordersTab() {

        ModelAndView mav = new ModelAndView("forward:/account");
        mav.addObject("ordersTabOpen", true);
        mav.addObject("classActiveOrders", "active");
        return mav;
    }
}
