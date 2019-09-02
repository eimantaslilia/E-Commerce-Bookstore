package com.project.bookstore.service.impl;

import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;
import com.project.bookstore.repository.PaymentRepository;
import com.project.bookstore.repository.UserRepository;
import com.project.bookstore.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Payment getOne(Long id) {
        return paymentRepository.getOne(id);
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public void deleteById(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Override
    public void addNewCreditCard(User user, Payment payment) {

        payment.setUser(user);

        setToDefaultIfFirstAddress(user, payment);

        user.getPaymentList().add(payment);
        userRepository.save(user);
    }

    private void setToDefaultIfFirstAddress(User user, Payment payment) {
        if (user.getPaymentList().isEmpty()) {
            payment.setDefaultCard(true);
        }
    }

    @Override
    public void setAsDefaultPayment(User user, Long userPaymentId) {
        List<Payment> paymentList = paymentRepository.findAll();

        for (Payment payment : paymentList) {
            if (payment.getId().equals(userPaymentId)) {
                payment.setDefaultCard(true);
                paymentRepository.save(payment);
            } else {
                payment.setDefaultCard(false);
                paymentRepository.save(payment);
            }
        }
    }


}
