package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class ViewBooksController {

    @Autowired
    private BookService bookService;


    @GetMapping("/browse")
    public ModelAndView homepage() {

        ModelAndView mav = new ModelAndView("browse");

        mav.addObject("allBooks", bookService.findAll());
        mav.addObject("classActiveBrowse", "active");
        mav.addObject("activeAll", true);

        return mav;
    }

    @GetMapping("/")
    public ModelAndView paginatedHome(@RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);
        Page<Book> bookList = bookService.findAllByPage(PageRequest.of(currentPage - 1, pageSize));

        ModelAndView mav = new ModelAndView("pagedHome");
        mav.addObject("classActiveHome", "active");
        mav.addObject("bookList", bookList);

        if (bookList.getTotalPages() > 0) {
            mav.addObject("pageNumbers", addPagination(bookList.getTotalPages()));
        }
        return mav;
    }

    private List<Integer> addPagination(int totalPages) {
        List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed().collect(Collectors.toList());
        return pageNumbers;
    }


    @GetMapping("/books/bookInfo")
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
