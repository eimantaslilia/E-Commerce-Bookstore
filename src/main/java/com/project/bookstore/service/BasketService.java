package com.project.bookstore.service;

import com.project.bookstore.domain.BasketItem;
import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.ShoppingCart;
import com.project.bookstore.domain.User;

import java.util.List;

public interface BasketService {

    void deleteById(Long id);

    List<BasketItem> findByShoppingCart(ShoppingCart shoppingCart);

    void addBasketItem(User user, Book book, int qty);

    void clearShoppingCart(ShoppingCart shoppingCart);
}
