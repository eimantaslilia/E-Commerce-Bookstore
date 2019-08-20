package com.project.bookstore.repository;

import com.project.bookstore.domain.security.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
