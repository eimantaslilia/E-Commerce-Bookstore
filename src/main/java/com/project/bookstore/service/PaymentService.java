package com.project.bookstore.service;

import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;

public interface PaymentService {

    Payment getOne(Long id);

    void save(Payment payment);

    void deleteById(Long id);

    void addNewCreditCard(User user, Payment payment);

    void setAsDefaultPayment(User user, Long defaultPaymentId);
}
