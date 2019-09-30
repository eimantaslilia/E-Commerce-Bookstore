package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.impl.UserSecurityService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminBookController.class)
@WithMockUser(username = "test", password = "test", roles = "ADMIN")
@Import(SecurityConfig.class)
class AdminBookControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserSecurityService userSecurityService;
    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Empty form for adding a book")
    void addBookEmptyForm() throws Exception {

        mockMvc.perform(get("/admin/add"))
                .andExpect(model().attributeExists("book"))
                .andExpect(view().name("addBook"));
    }

    @Test
    @DisplayName("Add a book to the database")
    void addBookPost() throws Exception {

        mockMvc.perform(post("/admin/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(view().name("redirect:/admin/add"));

        verify(bookService, times(1)).save(any(Book.class));
        verify(bookService, times(1)).uploadBookImage(any(Book.class));
    }

    @Test
    @DisplayName("View All Books in Admin Panel as a Table")
    void viewAllBooks() throws Exception {
        List<Book> allBooks = Collections.emptyList();
        given(bookService.findAll()).willReturn(allBooks);

        mockMvc.perform(get("/admin/all"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("allBooks", allBooks))
                .andExpect(view().name("allBooks"));

    }

    @Test
    void deleteBook() throws Exception {

        mockMvc.perform(get("/admin/deleteBook?bookId=" + anyLong()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("deleteMessage"))
                .andExpect(redirectedUrl("/admin/all"));

        verify(bookService, only()).deleteById(anyLong());

    }

    @ParameterizedTest
    @ValueSource(longs = {20, 655, 8888})
    @DisplayName("Book form filled with existing Book Information for updating")
    void updateBook(long idFromValueSource) throws Exception {
        Book book = new Book();
        given(bookService.getOne(idFromValueSource)).willReturn(book);

        mockMvc.perform(get("/admin/updateBook?bookId=" + idFromValueSource))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", book))
                .andExpect(view().name("updateBook"));
    }

    @Test
    @DisplayName("Update Book Submit to database")
    void updateBookPost() throws Exception {

        mockMvc.perform(post("/admin/updateBook"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(view().name("redirect:/admin/all"));

        verify(bookService, times(1)).save(any(Book.class));
        verify(bookService, times(1)).uploadBookImage(any(Book.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 354, 6574})
    @DisplayName("Test if bookId param returns the correct book")
    void bookInfo(long idFromValueSource) throws Exception {
        Book book = new Book();
        given(bookService.getOne(idFromValueSource)).willReturn(book);

        mockMvc.perform(get("/admin/bookInfo?bookId=" + idFromValueSource))
                .andExpect(status().isOk())
                .andExpect(model().attribute("book", book))
                .andExpect(view().name("bookInfoAdmin"));
    }
}