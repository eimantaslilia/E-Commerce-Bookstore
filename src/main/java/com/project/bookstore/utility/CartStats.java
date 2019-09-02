package com.project.bookstore.utility;

import com.project.bookstore.domain.BasketItem;
import com.project.bookstore.domain.ShoppingCart;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartStats {

    @Autowired
    private BasketService basketService;

    public List<BasketItem> basketItemList(User user){
        ShoppingCart shoppingCart = user.getShoppingCart();
        return basketService.findByShoppingCart(shoppingCart);
    }

    public int totalQty(User user){
        int totalQty = 0;

        for(BasketItem item : basketItemList(user)){
            totalQty += item.getQty();
        }
        return totalQty;
    }

    public double totalPrice(User user) {

        double totalPrice = 0;

        for (BasketItem item : basketItemList(user)) {
            totalPrice += item.getBook().getOurPrice() * item.getQty();
        }
        return totalPrice;
    }
}
