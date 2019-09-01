package com.project.bookstore.service;

import com.project.bookstore.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {

    Book save(Book book);

//    void update(Book book) throws IOException;

    Book getOne(Long id);

    void deleteById(Long id);

    List<Book> findAll();

    List<Book> blurrySearch(String title);

    List<Book> findByAuthor(String title);

    List<Book> findByGenre(String category);

    Page<Book> findAllByPage(Pageable pageable);

    void uploadBookImage(Book book) throws IOException;

    void deleteImageFromS3(Long id);

}
