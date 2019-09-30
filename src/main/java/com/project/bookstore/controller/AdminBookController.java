package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminBookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/add")
    public ModelAndView addBookEmptyForm() {

        ModelAndView mav = new ModelAndView("addBook");
        mav.addObject("book", new Book());

        return mav;
    }

    @PostMapping("/add")
    public ModelAndView addBookPost(@ModelAttribute("book") Book book, RedirectAttributes ra) throws IOException {

        bookService.save(book);
        bookService.uploadBookImage(book);

        ra.addFlashAttribute("message", "The book has been added successfully");
        return new ModelAndView("redirect:/admin/add");
    }

    @GetMapping("/all")
    public ModelAndView viewAllBooks() {

        ModelAndView mav = new ModelAndView("allBooks");
        mav.addObject("allBooks", bookService.findAll());

        return mav;
    }

    @GetMapping("/deleteBook")
    public RedirectView deleteBook(@RequestParam("bookId") Long bookId, RedirectAttributes ra) {

        bookService.deleteById(bookId);

        ra.addFlashAttribute("deleteMessage", "The book has been deleted");
        return new RedirectView("/admin/all");
    }

    @GetMapping("/updateBook")
    public ModelAndView updateBook(@RequestParam("bookId") Long id) {

        ModelAndView mav = new ModelAndView("updateBook");
        mav.addObject("book", bookService.getOne(id));
        return mav;
    }

    @PostMapping("/updateBook")
    public ModelAndView updateBookPost(@ModelAttribute("book") Book book, RedirectAttributes ra) throws IOException {

        bookService.save(book);
        bookService.uploadBookImage(book);

        ra.addFlashAttribute("updateMessage", "The book has been updated");
        return new ModelAndView("redirect:/admin/all");
    }


    @GetMapping("/bookInfo")
    public ModelAndView bookInfo(@RequestParam("bookId") Long id) {

        ModelAndView mav = new ModelAndView("bookInfoAdmin");
        mav.addObject("book", bookService.getOne(id));
        return mav;
    }
}


