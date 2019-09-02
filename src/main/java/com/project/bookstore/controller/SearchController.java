package com.project.bookstore.controller;

import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@RestController
public class SearchController {

    @Autowired
    private BookService bookService;


    @PostMapping("/search")
    public ModelAndView searchBook(@ModelAttribute("keyword") String keyword) {

        ModelAndView mav = new ModelAndView("browse");

        mav.addObject("allBooks", bookService.bookSearchByTitleAndAuthor(keyword));
        mav.addObject("classActiveBrowse", "active");
        return mav;
    }

    @GetMapping("/searchByCategory")
    public ModelAndView searchByCategory(@RequestParam("category") String category) {

        ModelAndView mav = new ModelAndView("browse");

        mav.addObject(activeCategoryTab(category), true);
        mav.addObject("allBooks", bookService.findByGenre(category));
        mav.addObject("classActiveBrowse", "active");

        return mav;
    }

    private String activeCategoryTab(String category) {
        String classActiveCategory = "active" + category;
        classActiveCategory = classActiveCategory.replaceAll("\\s+", "");
        classActiveCategory = classActiveCategory.replaceAll("&", "");
        return classActiveCategory;
    }
}
