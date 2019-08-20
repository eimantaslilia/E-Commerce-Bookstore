package com.project.bookstore.service;

import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.WishListItem;

public interface WishListService {

    void addToWishList(User user, Book book);

    void removeFromWishList(Long id);

    WishListItem getOne(Long id);
}
