package com.project.bookstore.service;

import com.project.bookstore.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BookService {

    Book save(Book book);

    Book getOne(Long id);

    void deleteById(Long id);

    List<Book> findAll();

    Page<Book> findAllByPage(Pageable pageable);

    List<Book> findByGenre(String category);

    List<Book> bookSearchByTitleAndAuthor(String keyword);

    void uploadBookImage(Book book) throws IOException;

    void deleteImageFromS3(Long id);

}
