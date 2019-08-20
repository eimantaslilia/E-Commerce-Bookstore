package com.project.bookstore.service.impl;

import com.project.bookstore.domain.*;
import com.project.bookstore.repository.BasketRepository;
import com.project.bookstore.repository.ShoppingCartRepository;
import com.project.bookstore.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasketServiceImpl implements BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private BasketService basketService;

    public void addBasketItem(User user, Book book, int qty) {

        ShoppingCart shoppingCart = user.getShoppingCart();
        List<BasketItem> basketItemList = basketService.findByShoppingCart(shoppingCart);

        BasketItem newBasketItem = new BasketItem(book, qty);
        newBasketItem.setShoppingCart(user.getShoppingCart());

        if (basketItemList.isEmpty()) {
            basketRepository.save(newBasketItem);
        } else {
            for (BasketItem item : basketItemList) {
                if (book.getId() == item.getBook().getId()) {
                    item.setQty(item.getQty() + qty);
                    basketRepository.save(item);
                    return;
                }
            }
            basketRepository.save(newBasketItem);
        }
    }

    public Optional<BasketItem> findById(Long id) {
        return basketRepository.findById(id);
    }

    public void deleteById(Long id) {
        basketRepository.deleteById(id);
    }

    public List<BasketItem> findByShoppingCart(ShoppingCart shoppingCart) {
        return basketRepository.findByShoppingCart(shoppingCart);
    }

    public void clearShoppingCart(ShoppingCart shoppingCart) {
        List<BasketItem> basketItemList = basketService.findByShoppingCart(shoppingCart);

        for (BasketItem item : basketItemList) {
            item.setShoppingCart(null);
            basketRepository.save(item);
        }
        shoppingCartRepository.save(shoppingCart);
    }
}
