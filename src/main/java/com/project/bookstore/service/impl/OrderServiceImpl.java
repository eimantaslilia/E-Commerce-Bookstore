package com.project.bookstore.service.impl;

import com.project.bookstore.domain.*;
import com.project.bookstore.repository.OrderRepository;
import com.project.bookstore.service.OrderService;
import com.project.bookstore.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private MailConstructor mailConstructor;

    public void createOrder(User user, List<BasketItem> basketItemList, OrderAddress orderAddress, OrderPayment orderPayment) {

        Order order = new Order();

        order.setUser(user);
        order.setAddress(orderAddress);
        order.setPayment(orderPayment);

        orderAddress.setOrder(order);
        orderPayment.setOrder(order);

        double totalPrice = 0;
        for (BasketItem item : basketItemList) {
            item.setOrder(order);
            totalPrice += item.getBook().getOurPrice() * item.getQty();
            item.getBook().setStock(item.getBook().getStock() - item.getQty());
        }
        order.setOrderedItems(basketItemList);

        totalPrice = totalPrice + totalPrice * 0.06;
        totalPrice = Double.parseDouble(new DecimalFormat("##.##").format(totalPrice));
        order.setTotalPrice(totalPrice);

        order.setCompleted(false);

        LocalDate today = LocalDate.now();
        order.setOrderDate(today);

        LocalDate estimatedDeliveryDate = today.plusDays(3);
        order.setShippingDate(estimatedDeliveryDate);

        orderRepository.save(order);

        mailSender.send(mailConstructor.constructOrderConfirmationEmail(order, user));
    }
}
