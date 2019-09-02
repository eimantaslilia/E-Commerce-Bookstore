package com.project.bookstore.repository;

import com.project.bookstore.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByIdDesc();

    Page<Book> findAllByOrderByIdDesc(Pageable pageable);

    List<Book> findByTitleContaining(String title);

    List<Book> findByAuthorContaining(String title);

    List<Book> findByGenre(String category);

}
