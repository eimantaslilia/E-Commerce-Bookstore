package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.WishListItem;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishListController {


    @Autowired
    private UserService userService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BasketService basketService;

    @GetMapping
    public ModelAndView wishList(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView mav = new ModelAndView("wishList");
        mav.addObject("classActiveWishList", "active");
        mav.addObject("wishList", user.getWishList());
        return mav;
    }

    @GetMapping("/add")
    public ModelAndView addToWishList(@RequestParam("bookId") Long bookId, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        Book book = bookService.getOne(bookId);

        if (itemAlreadyInWishList(user, book)) {
            ra.addFlashAttribute("alreadyInWishList", "The book is already in your Wish List");
            return new ModelAndView("redirect:/books/bookInfo?bookId=" + bookId);
        }

        wishListService.addToWishList(user, book);
        ra.addFlashAttribute("addedToWishList", "The book has been added to your Wish List");
        return new ModelAndView("redirect:/books/bookInfo?bookId=" + bookId);
    }

    private boolean itemAlreadyInWishList(User user, Book book) {

        List<WishListItem> wishList = user.getWishList();

        for (WishListItem item : wishList) {
            if (item.getBook().getId().equals(book.getId())) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/delete")
    public ModelAndView deleteFromWishList(@RequestParam("itemId") Long id, RedirectAttributes ra) {

        wishListService.removeFromWishList(id);

        ra.addFlashAttribute("removedFromWishList", "The book has been removed from your Wish List");
        return new ModelAndView("redirect:/wishlist");
    }

    @GetMapping("/moveToBasket")
    public ModelAndView moveToBasket(@RequestParam("itemId") Long id, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        WishListItem itemToMove = wishListService.getOne(id);
        Book book = itemToMove.getBook();

        if (book.getStock() > 0) {
            moveToBasketDeleteFromWishList(user, book, id);
            ra.addFlashAttribute("successAdded", "The book has been added to your basket");
            return new ModelAndView("redirect:/basket/items");
        }

        ra.addFlashAttribute("notEnoughStock", "The item is currently unavailable for purchase. We'll keep it in your Wish List!");
        return new ModelAndView("redirect:/wishlist");

    }
    private void moveToBasketDeleteFromWishList(User user, Book book, Long id){
        basketService.addBasketItem(user, book, 1);
        wishListService.removeFromWishList(id);
    }
}
