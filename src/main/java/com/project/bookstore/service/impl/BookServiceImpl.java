package com.project.bookstore.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.bookstore.domain.Book;
import com.project.bookstore.repository.BookRepository;
import com.project.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {


    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.endpointUrl}")
    private String awsUrl;

    @Value("${aws.s3.bucket}")
    private String bucketName;


    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book getOne(Long id) {
        return bookRepository.getOne(id);
    }

    public void deleteById(Long id) {
        deleteImageFromS3(id);
        bookRepository.deleteById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByIdDesc();
    }

    public Page<Book> findAllByPage(Pageable pageable) {
        return bookRepository.findAllByOrderByIdDesc(pageable);
    }

    public List<Book> findByGenre(String category) {
        List<Book> booksByGenre = bookRepository.findByGenre(category);
        booksByGenre.sort(Comparator.comparing(Book::getId));
        Collections.reverse(booksByGenre);
        return booksByGenre;
    }

    public List<Book> bookSearchByTitleAndAuthor(String keyword) {

        List<Book> allBooks = new ArrayList<>();

        List<Book> titleBooks = bookRepository.findByTitleContaining(keyword);
        for (Book titleBook : titleBooks) {
            allBooks.add(titleBook);
        }

        List<Book> authorBooks = bookRepository.findByAuthorContaining(keyword);
        for (Book authorBook : authorBooks) {
            allBooks.add(authorBook);
        }
        return allBooks;
    }

    public void uploadBookImage(Book book) throws IOException {

        String bookName = book.getId() + ".jpg";

        if (s3Client.doesObjectExist(bucketName, bookName)) {
            deleteImageFromS3(book.getId());
        }

        File convertedFile = convertMultipartToFile(book.getBookImage(), bookName);

        String imagePath = awsUrl + "/" + bucketName + "/" + bookName;

        s3Client.putObject(new PutObjectRequest(bucketName, bookName, convertedFile).withCannedAcl(CannedAccessControlList.PublicRead));

        convertedFile.delete();
        book.setImagePath(imagePath);
        bookRepository.save(book);
    }

    private File convertMultipartToFile(MultipartFile file, String name) throws IOException {

        File convertedFile = new File(name);
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public void deleteImageFromS3(Long id) {
        Book book = getOne(id);
        String bookName = book.getId() + ".jpg";

        s3Client.deleteObject(bucketName, bookName);
    }
}
