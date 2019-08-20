package com.project.bookstore.controller;

import com.project.bookstore.domain.Book;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminBookController {

    @Autowired
    private BookService bookService;


    @GetMapping("/add")
    public ModelAndView addBook() {
        Book book = new Book();
        ModelAndView mav = new ModelAndView("addBook");
        mav.addObject("book", book);
        return mav;
    }

    @PostMapping("/add")
    public RedirectView addBookPost(@ModelAttribute("book") Book book, RedirectAttributes ra) throws IOException {

        bookService.save(book);

        MultipartFile bookImage = book.getBookImage();
        String name = book.getId() + ".jpg";
        bookService.uploadBookImage(book, bookImage, name);


        RedirectView rv = new RedirectView("/admin/add");
        ra.addFlashAttribute("message", "The book has been added successfully");
        return rv;
    }

    @GetMapping("/all")
    public ModelAndView viewAllBooks(Model model) {
        List<Book> allBooks = bookService.findAllByOrder();
        model.addAttribute("allBooks", allBooks);
        ModelAndView mav = new ModelAndView("allBooks");
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
        Book book = bookService.getOne(id);
        ModelAndView mav = new ModelAndView("updateBook");
        mav.addObject("book", book);
        return mav;
    }

    @PostMapping("/updateBook")
    public RedirectView updateBookPost(@ModelAttribute("book") Book book, RedirectAttributes ra) throws IOException {

        bookService.save(book);

        MultipartFile bookImage = book.getBookImage();

        String name = book.getId() + ".jpg";
        bookService.deleteImageFromS3(book.getId());

        bookService.uploadBookImage(book, bookImage, name);

        RedirectView rv = new RedirectView("/admin/all");
        ra.addFlashAttribute("updateMessage", "The book has been updated");
        return rv;
    }

    @GetMapping("/bookInfo")
    public ModelAndView bookInfo(@RequestParam("bookId") Long id) {
        Book book = bookService.getOne(id);
        ModelAndView mav = new ModelAndView("bookInfoAdmin");
        mav.addObject("book", book);
        return mav;
    }
}


