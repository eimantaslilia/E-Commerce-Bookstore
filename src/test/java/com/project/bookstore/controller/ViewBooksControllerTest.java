package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ViewBooksController.class)
@Import(SecurityConfig.class)
class ViewBooksControllerTest {

    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=UTF-8";

    @MockBean
    UserSecurityService userSecurityService;

    @MockBean
    BookService bookService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void testAllGenresInCategoriesPage() throws Exception {

        List<Book> allBooks = new ArrayList<>();
        given(bookService.findAll()).willReturn(allBooks);

        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(model().attribute("classActiveBrowse", "active"))
                .andExpect(model().attributeExists("activeAll"))
                .andExpect(model().attribute("allBooks", allBooks))
                .andExpect(view().name("browse"));
    }

    @Test
    void testHomePagePagination() throws Exception {

        List<Book> bookList = new ArrayList<>();
        Page<Book> pagedBookList = new PageImpl<>(bookList); // Has 1 Page, so (pagedBookList.getTotalPages() > 0) will return Page Numbers
        given(bookService.findAllByPage(any())).willReturn(pagedBookList);

        String uri = "/?size=" + 20 + "&page=" + 3;
        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(model().attribute("classActiveHome", "active"))
                .andExpect(model().attributeExists("bookList"))
                .andExpect(view().name("pagedHome"));
    }

    @Test
    void testIndividualBookInformation() throws Exception {
        Book book = new Book();
        given(bookService.getOne(anyLong())).willReturn(book);

        mockMvc.perform(get("/books/bookInfo?bookId=" + anyLong()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TEXT_HTML_CHARSET_UTF_8))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attributeExists("qtyList"))
                .andExpect(model().attributeExists("qty"))
                .andExpect(view().name("bookInfoUser"));
    }
}