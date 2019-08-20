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

    public List<Book> findAllByOrder() {
        List<Book> books = bookRepository.findAllByOrderByIdDesc();
        return books;
    }
    public Page<Book> findAllByPage(Pageable pageable) {
        Page<Book> imagePage = bookRepository.findAllByOrderByIdDesc(pageable);
        return imagePage;
    }

    public Book getOne(Long id) {
        return bookRepository.getOne(id);
    }

    public void deleteById(Long id) {

        deleteImageFromS3(id);
        bookRepository.deleteById(id);
    }

    public void deleteImageFromS3(Long id) {
        Book book = getOne(id);
        String name = book.getId() + ".jpg";

        s3Client.deleteObject(bucketName, name);
    }

    public List<Book> blurrySearch(String title) {
        List<Book> bookList = bookRepository.findByTitleContaining(title);
        return bookList;
    }

    public List<Book> findByAuthor(String title) {
        List<Book> bookList = bookRepository.findByAuthorContaining(title);
        return bookList;
    }


    public List<Book> findByGenre(String category) {
        List<Book> bookList = bookRepository.findByGenre(category);
        return bookList;
    }

    public void uploadBookImage(Book book, MultipartFile bookImage, String name) throws IOException {

        File convertedFile = convertMultipartToFile(bookImage, name);

        String imagePath = "";

        imagePath = awsUrl + "/" + bucketName + "/" + name;

        s3Client.putObject(new PutObjectRequest(bucketName, name, convertedFile).withCannedAcl(CannedAccessControlList.PublicRead));

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
}
