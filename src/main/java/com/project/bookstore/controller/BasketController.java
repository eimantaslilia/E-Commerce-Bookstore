package com.project.bookstore.controller;

import com.project.bookstore.domain.BasketItem;
import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.ShoppingCart;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

import java.util.List;


@RestController
@RequestMapping("/basket")
public class BasketController {


    @Autowired
    private UserService userService;

    @Autowired
    private BasketService basketService;

    @Autowired
    private BookService bookService;

    @GetMapping("/items")
    public ModelAndView basketItems(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ShoppingCart shoppingCart = user.getShoppingCart();
        List<BasketItem> itemList = basketService.findByShoppingCart(shoppingCart);
        double totalPrice = 0;
        int totalQty = 0;

        if (!itemList.isEmpty()) {
            for (BasketItem item : itemList) {
                totalPrice += item.getBook().getOurPrice() * item.getQty();
                totalQty += item.getQty();
            }
        }
        ModelAndView mav = new ModelAndView("basket");

        mav.addObject("user", user);
        mav.addObject("basketItemList", itemList);
        mav.addObject("totalQty", totalQty);
        mav.addObject("totalPrice", totalPrice);
        mav.addObject("classActiveBasket", "active");

        return mav;
    }

    @PostMapping("/add")
    public RedirectView addBookToBasket(@ModelAttribute("book") Book book,
                                        @ModelAttribute("qty") int qty,
                                        Principal principal,
                                        RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        basketService.addBasketItem(user, book, qty);

        RedirectView rv = new RedirectView("/basket/items");
        ra.addFlashAttribute("successAdded", "The book has been added to your basket");
        return rv;
    }

    @RequestMapping("/addFromBrowse")
    public RedirectView addFromBrowse(@RequestParam("bookId") Long bookId, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Book book = bookService.getOne(bookId);

        basketService.addBasketItem(user, book, 1);

        ra.addFlashAttribute("successAdded", "The book has been added to your basket");
        return new RedirectView("/basket/items");
    }

    @GetMapping("/delete")
    public RedirectView deleteItem(@RequestParam("basketItemId") Long id, RedirectAttributes ra) {

        basketService.deleteById(id);

        ra.addFlashAttribute("successDeleted", "The book has been removed from your basket");
        return new RedirectView("/basket/items");
    }

    @GetMapping("/deleteFromCheckout")
    public RedirectView deleteItemFromCheckout(@RequestParam("basketItemId") Long id, RedirectAttributes ra) {

        basketService.deleteById(id);

        RedirectView rv = new RedirectView("/checkout");
        ra.addFlashAttribute("reviewChanged", true);
        return rv;
    }
}
