package com.project.bookstore.repository;

import com.project.bookstore.domain.BasketItem;
import com.project.bookstore.domain.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketRepository extends JpaRepository<BasketItem, Long> {

    List<BasketItem> findByShoppingCart(ShoppingCart shoppingCart);
}
