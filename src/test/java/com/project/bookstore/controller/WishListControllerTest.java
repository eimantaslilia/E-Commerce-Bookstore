package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.WishListItem;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.WishListService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishListController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
class WishListControllerTest {

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private UserService userService;

    @MockBean
    private WishListService wishListService;

    @MockBean
    private BookService bookService;

    @MockBean
    private BasketService basketService;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);
    }

    @Test
    void wishList() throws Exception {

        List<WishListItem> wishList = Collections.emptyList();
        given(user.getWishList()).willReturn(wishList);

        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("classActiveWishList"))
                .andExpect(model().attribute("wishList", wishList))
                .andExpect(view().name("wishList"));
    }

    @DisplayName("The book is already in Wish List")
    @Test
    void addToWishListAlreadyInWishList() throws Exception {

        Book book = new Book();
        book.setId(10L);
        WishListItem wishListItem = new WishListItem();
        wishListItem.setBook(book);
        List<WishListItem> wishList = Collections.singletonList(wishListItem);

        given(user.getWishList()).willReturn(wishList);
        given(bookService.getOne(10L)).willReturn(book);

        mockMvc.perform(get("/wishlist/add?bookId=" + 10L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("alreadyInWishList"))
                .andExpect(view().name("redirect:/books/bookInfo?bookId=" + 10L));

        verify(wishListService, never()).addToWishList(any(User.class), any(Book.class));

    }

    @DisplayName("The book has been added to your Wish List")
    @Test
    void addToWishListSuccess() throws Exception {

        List<WishListItem> wishList = Collections.emptyList();
        given(user.getWishList()).willReturn(wishList);

        given(bookService.getOne(10L)).willReturn(mock(Book.class)); //user wishList doesn't contain this book

        mockMvc.perform(get("/wishlist/add?bookId=" + 10L))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("addedToWishList"))
                .andExpect(view().name("redirect:/books/bookInfo?bookId=" + 10L));

        verify(wishListService, only()).addToWishList(any(User.class), any(Book.class));
    }

    @Test
    void deleteFromWishList() throws Exception {

        mockMvc.perform(get("/wishlist/delete?itemId=" + 50))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("removedFromWishList"))
                .andExpect(view().name("redirect:/wishlist"));

        verify(wishListService, only()).removeFromWishList(50L);
    }

    @Test
    @DisplayName("If an Item in Wish List has at least 1 copy available - then it's moved to basket for purchase")
    void moveToBasketSuccess() throws Exception {

        WishListItem wishListItem = new WishListItem();
        Book book = new Book();
        book.setStock(1);
        wishListItem.setBook(book);
        given(wishListService.getOne(anyLong())).willReturn(wishListItem);

        mockMvc.perform(get("/wishlist/moveToBasket?itemId=" + anyLong()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successAdded"))
                .andExpect(view().name("redirect:/basket/items"));

        verify(basketService, only()).addBasketItem(any(User.class), any(Book.class), anyInt());
        verify(wishListService, times(1)).removeFromWishList(anyLong());
    }

    @Test
    @DisplayName("If an Item in Wish List isn't available for purchase - redirect to Wish List")
    void moveToBasketFailure() throws Exception {

        WishListItem wishListItem = new WishListItem();
        Book book = new Book();
        book.setStock(0);
        wishListItem.setBook(book);
        given(wishListService.getOne(anyLong())).willReturn(wishListItem);

        mockMvc.perform(get("/wishlist/moveToBasket?itemId=" + anyLong()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("notEnoughStock"))
                .andExpect(view().name("redirect:/wishlist"));

        verify(basketService, never()).addBasketItem(any(User.class), any(Book.class), anyInt());
        verify(wishListService, never()).removeFromWishList(anyLong());
    }
}