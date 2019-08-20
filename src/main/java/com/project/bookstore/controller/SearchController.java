package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private BookService bookService;


    @PostMapping("/search")
    public ModelAndView searchBook(@ModelAttribute("keyword") String keyword, Principal principal) {

        List<Book> allBooks = new ArrayList<>();

        List<Book> titleBooks = bookService.blurrySearch(keyword);
        for (Book titleBook : titleBooks) {
            allBooks.add(titleBook);
        }

        List<Book> authorBooks = bookService.findByAuthor(keyword);
        for (Book authorBook : authorBooks) {
            allBooks.add(authorBook);
        }

        ModelAndView mav = new ModelAndView("browse");
        mav.addObject("classActiveBrowse", "active");

        mav.addObject("allBooks", allBooks);
        return mav;
    }

    @GetMapping("/searchByCategory")
    public ModelAndView searchByCategory(@RequestParam("category") String category) {

        String classActiveCategory = "active" + category;
        classActiveCategory = classActiveCategory.replaceAll("\\s+", "");
        classActiveCategory = classActiveCategory.replaceAll("&", "");

        ModelAndView mav = new ModelAndView("browse");
        mav.addObject(classActiveCategory, true);

        List<Book> allBooks = bookService.findByGenre(category);
        mav.addObject("allBooks", allBooks);
        mav.addObject("classActiveBrowse", "active");

        return mav;
    }
}
