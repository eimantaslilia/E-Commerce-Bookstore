package com.project.bookstore.service;

import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.PasswordResetToken;
import com.project.bookstore.domain.security.UserRole;

import java.util.Set;

public interface UserService {

    User save(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    void saveUserAndRolesAndCart(User user, Set<UserRole> userRoles) throws Exception;

    void createPasswordResetTokenForUser(User user, String token);

    PasswordResetToken getPasswordResetToken(String token);

    void deleteToken(Long id);
}
