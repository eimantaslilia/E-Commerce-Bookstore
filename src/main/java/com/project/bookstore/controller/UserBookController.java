package com.project.bookstore.controller;


import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/books")
public class UserBookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/bookInfo")
    public ModelAndView bookInfo(@RequestParam("bookId") Long id) {

        Book book = bookService.getOne(id);

        ModelAndView mav = new ModelAndView("bookInfoUser");
        mav.addObject("book", book);

        List<Integer> qtyList = new ArrayList<>();
        int qty = book.getStock();
        for (int i = 1; i <= qty; i++) {
            qtyList.add(i);
        }

        mav.addObject("qtyList", qtyList);
        mav.addObject("qty", 1);

        return mav;
    }


}
