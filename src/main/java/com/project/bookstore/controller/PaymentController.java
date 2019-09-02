package com.project.bookstore.controller;

import com.project.bookstore.domain.Payment;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.PaymentService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView addNewCreditCardPost(@ModelAttribute("payment") Payment payment, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        if (creditCardAlreadyExists(user, payment)) {
            ra.addFlashAttribute("creditNumberExists", "Payment option with this credit card number already exists");
        } else {
            paymentService.addNewCreditCard(user, payment);
        }

        ra.addFlashAttribute("paymentTabOpen", true);
        return new ModelAndView("redirect:/account");
    }

    private boolean creditCardAlreadyExists(User user, Payment payment) {

        List<Payment> userPaymentList = user.getPaymentList();
        for (Payment card : userPaymentList) {
            if (payment.getCardNumber().equals(card.getCardNumber())) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/setAsDefaultPayment")
    public ModelAndView setAsDefaultPayment(@ModelAttribute("paymentId") Long paymentId, @RequestParam("checkout") boolean backToCheckout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        paymentService.setAsDefaultPayment(user, paymentId);

        if (backToCheckout) {
            ra.addFlashAttribute("checkoutPaymentChanged", true);
            return new ModelAndView("redirect:/checkout");
        }

        ra.addFlashAttribute("defaultPaymentChanged", "Your default payment has been updated");
        ra.addFlashAttribute("paymentTabOpen", true);
        return new ModelAndView("redirect:/account");
    }

    @GetMapping("/removeCreditCard")
    public ModelAndView removeCreditCard(@RequestParam("id") Long paymentId, @RequestParam("checkout") boolean backToCheckout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Payment paymentToDelete = paymentService.getOne(paymentId);

        boolean paymentWasDefault = paymentToDelete.isDefaultCard();

        paymentService.deleteById(paymentId);

        if (paymentWasDefault) {
            setNewDefaultPaymentAfterRemoval(user);
        }
        if (backToCheckout) {
            ra.addFlashAttribute("checkoutPaymentChanged", true);
            return new ModelAndView("redirect:/checkout");
        }
        ra.addFlashAttribute("paymentTabOpen", true);
        return new ModelAndView("redirect:/account");
    }

    private void setNewDefaultPaymentAfterRemoval(User user) {

        List<Payment> userPaymentList = user.getPaymentList();

        if (!userPaymentList.isEmpty()) {
            userPaymentList.get(0).setDefaultCard(true);
        }
        for (Payment payment : userPaymentList) {
            paymentService.save(payment);
        }
    }

    @GetMapping("/paymentFromCheckout")
    public RedirectView paymentMethodsInAccount(RedirectAttributes ra) {

        ra.addFlashAttribute("paymentTabOpen", true);
        return new RedirectView("/account");
    }

    @GetMapping("/checkoutPaymentFromAccount")
    public RedirectView linkToCheckoutFromPaymentMethodsInAccount(RedirectAttributes ra) {

        ra.addFlashAttribute("checkoutPaymentChanged", true);
        return new RedirectView("/checkout");
    }
}
