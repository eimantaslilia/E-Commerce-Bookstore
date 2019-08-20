package com.project.bookstore.repository;

import com.project.bookstore.domain.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishListItem, Long> {
}
