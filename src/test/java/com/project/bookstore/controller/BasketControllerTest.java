package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.BasketService;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.UserService;
import com.project.bookstore.service.impl.UserSecurityService;
import com.project.bookstore.utility.CartStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BasketController.class)
@WithMockUser(username = "test", password = "test", roles = "USER")
@Import(SecurityConfig.class)
class BasketControllerTest {

    @MockBean
    UserSecurityService userSecurityService;

    @MockBean
    UserService userService;

    @MockBean
    private BasketService basketService;

    @MockBean
    private BookService bookService;

    @MockBean
    private CartStats cartStats;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    private Book book;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        given(userService.findByUsername(anyString())).willReturn(user);

        book = mock(Book.class);
        given(bookService.getOne(anyLong())).willReturn(book);
    }

    @Test
    @DisplayName("View all of user's basket items")
    void basketItems() throws Exception {

        mockMvc.perform(get("/basket/items"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("basketItemList", cartStats.basketItemList(user)))
                .andExpect(model().attribute("totalQty", cartStats.totalQty(user)))
                .andExpect(model().attribute("totalPrice", cartStats.totalPrice(user)))
                .andExpect(view().name("basket"));
    }

    @Test
    void addBookToBasket() throws Exception {

        mockMvc.perform(post("/basket/add")
                .flashAttr("book", book)
                .flashAttr("qty", 10))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successAdded"))
                .andExpect(view().name("redirect:/basket/items"));

        verify(basketService, times(1)).addBasketItem(user, book, 10);
    }

    @Test
    @DisplayName("Add to Basket with Shopping Cart icon next to the price in Categories")
    void addFromBrowse() throws Exception {

        mockMvc.perform(post("/basket/addFromBrowse?bookId=" + anyLong()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successAdded"))
                .andExpect(view().name("redirect:/basket/items"));

        verify(basketService, times(1)).addBasketItem(user, book, 1);
    }

    @ParameterizedTest
    @ValueSource(longs = {42, 666, 1337})
    @DisplayName("Delete Item from Basket and Go to Checkout")
    void deleteItemFromBasketCheckout(long basketItemIdFromSource) throws Exception {

        mockMvc.perform(get("/basket/delete?basketItemId=" + basketItemIdFromSource + "&checkout=" + true))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("reviewChanged", true))
                .andExpect(view().name("redirect:/checkout"));

        verify(basketService, times(1)).deleteById(basketItemIdFromSource);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 852, 9998})
    @DisplayName("Delete Item with Correct Id from Basket and Go to Account")
    void deleteItemFromBasketAccount(long basketItemIdFromSource) throws Exception {

        mockMvc.perform(get("/basket/delete?basketItemId=" + basketItemIdFromSource + "&checkout=" + false))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successDeleted"))
                .andExpect(view().name("redirect:/basket/items"));

        verify(basketService, times(1)).deleteById(basketItemIdFromSource);
    }
}