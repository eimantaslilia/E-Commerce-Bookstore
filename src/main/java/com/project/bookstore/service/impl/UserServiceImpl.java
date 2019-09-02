package com.project.bookstore.service.impl;

import com.project.bookstore.domain.ShoppingCart;
import com.project.bookstore.domain.User;
import com.project.bookstore.domain.security.PasswordResetToken;
import com.project.bookstore.domain.security.UserRole;
import com.project.bookstore.repository.PasswordTokenRepository;
import com.project.bookstore.repository.RoleRepository;
import com.project.bookstore.repository.UserRepository;
import com.project.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserAndRolesAndCart(User user, Set<UserRole> userRoles) {

        for (UserRole userRole : userRoles) {
            roleRepository.save(userRole.getRole());
        }
        user.getUserRoles().addAll(userRoles);

        ShoppingCart shoppingCart = new ShoppingCart();
        user.setShoppingCart(shoppingCart);
        shoppingCart.setUser(user);

        userRepository.save(user);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordTokenRepository.findByToken(token);
    }

    @Override
    public void deleteToken(Long id) {
        passwordTokenRepository.deleteById(id);
    }

}
