package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.utility.CartStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@RestController
@RequestMapping("/basket")
public class BasketController {


    @Autowired
    private UserService userService;

    @Autowired
    private BasketService basketService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CartStats cartStats;

    @GetMapping("/items")
    public ModelAndView basketItems(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView mav = new ModelAndView("basket");
        mav.addObject("user", user);
        mav.addObject("basketItemList", cartStats.basketItemList(user));
        mav.addObject("totalQty", cartStats.totalQty(user));
        mav.addObject("totalPrice", cartStats.totalPrice(user));
        mav.addObject("classActiveBasket", "active");

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView addBookToBasket(@ModelAttribute("book") Book book,
                                        @ModelAttribute("qty") int qty,
                                        Principal principal,
                                        RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        basketService.addBasketItem(user, book, qty);

        ra.addFlashAttribute("successAdded", "The book has been added to your basket");
        return new ModelAndView("redirect:/basket/items");
    }

    @RequestMapping("/addFromBrowse")
    public ModelAndView addFromBrowse(@RequestParam("bookId") Long bookId, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Book book = bookService.getOne(bookId);

        basketService.addBasketItem(user, book, 1);

        ra.addFlashAttribute("successAdded", "The book has been added to your basket");
        return new ModelAndView("redirect:/basket/items");
    }


    @GetMapping("/delete")
    public ModelAndView deleteItemFromBasket(@RequestParam("basketItemId") Long id,
                                             @RequestParam("checkout") boolean backToCheckout,
                                             RedirectAttributes ra) {

        basketService.deleteById(id);

        if (backToCheckout) {
            ra.addFlashAttribute("reviewChanged", true);
            return new ModelAndView("redirect:/checkout");
        }

        ra.addFlashAttribute("successDeleted", "The book has been removed from your basket");
        return new ModelAndView("redirect:/basket/items");
    }
}
