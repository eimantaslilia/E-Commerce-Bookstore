package com.project.bookstore.service;

import com.project.bookstore.domain.BasketItem;
import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.ShoppingCart;
import com.project.bookstore.domain.User;

import java.util.List;
import java.util.Optional;

public interface BasketService {

    void addBasketItem(User user, Book book, int qty);

    Optional<BasketItem> findById(Long id);

    void deleteById(Long id);

    List<BasketItem> findByShoppingCart(ShoppingCart shoppingCart);

    void clearShoppingCart(ShoppingCart shoppingCart);
}
