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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class HomeBrowseController {

    @Autowired
    private BookService bookService;


    @GetMapping("/browse")
    public ModelAndView homepage() {

        ModelAndView mav = new ModelAndView("browse");

        List<Book> allBooks = bookService.findAllByOrder();
        mav.addObject("allBooks", allBooks);
        mav.addObject("classActiveBrowse", "active");
        mav.addObject("activeAll", true);

        return mav;
    }

    @GetMapping("/")
    public ModelAndView paginatedHome(@RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size) {


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(20);

        Page<Book> bookPage = bookService.findAllByPage(PageRequest.of(currentPage - 1, pageSize));

        ModelAndView mav = new ModelAndView("pagedHome");
        mav.addObject("bookPage", bookPage);

        int totalPages = bookPage.getTotalPages();

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed().collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }

        mav.addObject("classActiveHome", "active");
        return mav;
    }


}
