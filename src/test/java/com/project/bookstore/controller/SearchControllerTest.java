package com.project.bookstore.controller;

import com.project.bookstore.config.SecurityConfig;
import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import com.project.bookstore.service.impl.UserSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@Import(SecurityConfig.class)
class SearchControllerTest {

    @MockBean
    private UserSecurityService userSecurityService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void searchBook() throws Exception {

        List<Book> books = new ArrayList<>();
        given(bookService.bookSearchByTitleAndAuthor(anyString())).willReturn(books);

        mockMvc.perform(post("/search")
                .flashAttr("keyword", "anyKeyword"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("allBooks", books))
                .andExpect(model().attributeExists("classActiveBrowse"))
                .andExpect(view().name("browse"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Classics", "Crime %26 Mystery", "Fantasy",
            "Fiction", "Nonfiction", "Romance",
            "Science Fiction", "Thriller"})
    void searchByCategory(String category) throws Exception {

        String classActiveCategory = "active" + category;
        classActiveCategory = classActiveCategory.replaceAll("\\s+", "");
        classActiveCategory = classActiveCategory.replaceAll("&", "");

        mockMvc.perform(get("/searchByCategory?category=" + category))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("allBooks"))
                .andExpect(model().attributeExists(classActiveCategory))
                .andExpect(model().attributeExists("classActiveBrowse"))
                .andExpect(view().name("browse"));
    }
}