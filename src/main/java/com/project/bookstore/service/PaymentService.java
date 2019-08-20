package com.project.bookstore.service;

import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;

public interface PaymentService {

    void addNewCreditCard(User user, Payment payment);

    void setAsDefaultPayment(User user, Long defaultPaymendId);

    void deleteById(Long id);

    Payment getOne(Long id);

    void save(Payment payment);

    String last4OfCardNumber(String cardNumber);
}
