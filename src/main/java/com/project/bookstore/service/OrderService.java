package com.project.bookstore.service;

import com.project.bookstore.domain.*;

import java.util.List;

public interface OrderService {

    void createOrder(User user, List<BasketItem> basketItemList, OrderAddress orderAddress, OrderPayment orderPayment);
}
