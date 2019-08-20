package com.project.bookstore.service.impl;

import com.project.bookstore.domain.Book;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.WishListItem;
import com.project.bookstore.repository.WishListRepository;
import com.project.bookstore.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WishListServiceImpl implements WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    public void addToWishList(User user, Book book) {

        WishListItem wishListItem = new WishListItem();
        wishListItem.setBook(book);
        wishListItem.setUser(user);

        wishListRepository.save(wishListItem);
    }

    public void removeFromWishList(Long id) {
        wishListRepository.deleteById(id);
    }

    public WishListItem getOne(Long id) {
        return wishListRepository.getOne(id);
    }
}
