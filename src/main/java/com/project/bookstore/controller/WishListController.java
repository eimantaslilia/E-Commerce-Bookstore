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
import org.springframework.web.servlet.view.RedirectView;

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
    public ModelAndView wishlist(Principal principal) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView wishlistPage = new ModelAndView("wishList");
        wishlistPage.addObject("classActiveWishList", "active");
        wishlistPage.addObject("wishList", user.getWishList());
        return wishlistPage;
    }

    @GetMapping("/add")
    public RedirectView addToWishlist(@RequestParam("bookId") Long bookId, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Book book = bookService.getOne(bookId);

        List<WishListItem> wishList = user.getWishList();

        for (WishListItem item : wishList) {
            if (item.getBook().getId() == book.getId()) {

                RedirectView rv = new RedirectView("/books/bookInfo?bookId=" + bookId);
                ra.addFlashAttribute("alreadyInWishList", "The book is already in your Wish List");
                return rv;
            }
        }

        wishListService.addToWishList(user, book);

        RedirectView rv = new RedirectView("/books/bookInfo?bookId=" + bookId);
        ra.addFlashAttribute("addedToWishList", "The book has been added to your Wish List");
        return rv;
    }

    @GetMapping("/delete")
    public RedirectView deleteFromWishList(@RequestParam("itemId") Long id, Principal principal, RedirectAttributes ra) {

        wishListService.removeFromWishList(id);

        ra.addFlashAttribute("removedFromWishList", "The book has been removed from your Wish List");
        return new RedirectView("/wishlist");
    }

    @GetMapping("/moveToBasket")
    public RedirectView moveToBasket(@RequestParam("itemId") Long id, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        WishListItem itemToMove = wishListService.getOne(id);

        Book book = itemToMove.getBook();

        if (book.getStock() > 0) {
            basketService.addBasketItem(user, book, 1);
            wishListService.removeFromWishList(id);
            RedirectView rv = new RedirectView("/basket/items");
            ra.addFlashAttribute("successAdded", "The book has been added to your basket");
            return rv;
        }
        RedirectView noStock = new RedirectView("/wishlist");
        ra.addFlashAttribute("notEnoughStock", "The item is currently unavailable for purchase. We'll keep it in your Wish List!");
        return noStock;


    }
}
