package com.project.bookstore.controller;

import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.PaymentService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;


    @PostMapping("/addNewCreditCard")
    public RedirectView addNewCreditCardPost(@ModelAttribute("payment") Payment payment, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        RedirectView rv = new RedirectView("/account");
        ra.addFlashAttribute("paymentTabOpen", true);

        List<Payment> userPaymentList = user.getPaymentList();
        for (Payment card : userPaymentList) {
            if (payment.getCardNumber().equals(card.getCardNumber())) {
                ra.addFlashAttribute("creditNumberExists", "Payment with this credit card number already exists");
                return rv;
            }
        }

        paymentService.addNewCreditCard(user, payment);

        return rv;
    }

    @GetMapping("/setAsDefaultPayment")
    public RedirectView setAsDefaultPayment(@ModelAttribute("paymentId") Long paymentId, @RequestParam("checkout") boolean checkout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        paymentService.setAsDefaultPayment(user, paymentId);

        if (checkout) {
            RedirectView checkoutPage = new RedirectView("/checkout");
            ra.addFlashAttribute("checkoutPaymentChanged", true);
            return checkoutPage;
        }

        RedirectView profilePage = new RedirectView("/account");
        ra.addFlashAttribute("defaultPaymentChanged", "Your default payment has been updated");

        ra.addFlashAttribute("paymentTabOpen", true);
        return profilePage;
    }

    @GetMapping("/removeCreditCard")
    public RedirectView removeCreditCard(@RequestParam("id") Long paymentId, @RequestParam("checkout") boolean checkout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Payment paymentToDelete = paymentService.getOne(paymentId);

        boolean defaultPayment = paymentToDelete.isDefaultCard();

        paymentService.deleteById(paymentId);

        List<Payment> userPaymentList = user.getPaymentList();

        if (defaultPayment & !userPaymentList.isEmpty()) {
            userPaymentList.get(0).setDefaultCard(true);
        }
        for (Payment payment : userPaymentList) {
            paymentService.save(payment);
        }
        if (checkout) {
            RedirectView checkoutPage = new RedirectView("checkout");
            ra.addFlashAttribute("checkoutPaymentChanged", true);
            return checkoutPage;
        }

        RedirectView profilePage = new RedirectView("/account");
        ra.addFlashAttribute("paymentTabOpen", true);
        return profilePage;
    }

    @GetMapping("/paymentFromCheckout")
    public RedirectView paymentTabFromCheckout(RedirectAttributes ra) {
        RedirectView rv = new RedirectView("/account");
        ra.addFlashAttribute("paymentTabOpen", true);
        return rv;
    }
    @GetMapping("/checkoutPaymentFromAccount")
    public RedirectView checkoutPaymentFromAccount(RedirectAttributes ra) {
        RedirectView rv = new RedirectView("/checkout");
        ra.addFlashAttribute("checkoutPaymentChanged", true);
        return rv;
    }
}
