package com.project.bookstore.controller;

import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.AddressService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;


    @PostMapping("/addNewAddress")
    public RedirectView addNewAddressPost(@ModelAttribute("address") Address address, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        RedirectView rv = new RedirectView("/account");
        ra.addFlashAttribute("addressTabOpen", true);

        List<Address> userAddressList = user.getAddressList();
        for (Address street : userAddressList) {
            if (street.getStreetAddress1().equals(address.getStreetAddress1())) {
                ra.addFlashAttribute("streetAddressExists", "Delivery option with this street address already exists");
                return rv;
            }
        }

        addressService.addNewAddress(user, address);

        return rv;
    }


    @GetMapping("/removeAddress")
    public RedirectView removeAddress(@RequestParam("id") Long addressId, @RequestParam("checkout") boolean checkout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Address addressToDelete = addressService.getOne(addressId);

        boolean defaultAddress = addressToDelete.isDefaultAddress();

        addressService.deleteById(addressId);

        List<Address> userAddressList = user.getAddressList();

        if (defaultAddress & !userAddressList.isEmpty()) {
            userAddressList.get(0).setDefaultAddress(true);
        }

        for (Address address : userAddressList) {
            addressService.save(address);
        }
        if (checkout) {
            return new RedirectView("/checkout");
        }

        ra.addFlashAttribute("addressTabOpen", true);
        return new RedirectView("/account");
    }


    @GetMapping("/setAsDefaultAddress")
    public RedirectView setAsDefaultAddress(@ModelAttribute("addressId") Long addressId, @RequestParam("checkout") boolean checkout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        addressService.setAsDefaultAddress(user, addressId);

        if (checkout) {
            RedirectView checkoutPage = new RedirectView("/checkout");
            ra.addFlashAttribute("checkoutAddressChanged", true);
            return checkoutPage;
        }

        RedirectView rv = new RedirectView("/account");
        ra.addFlashAttribute("addressTabOpen", true);
        ra.addFlashAttribute("defaultAddressChanged", "Your default address has been updated");
        return rv;
    }


    @GetMapping("/setAsDefaultAddressFromCheckout")
    public RedirectView setAsDefaultFromCheckout(@RequestParam("addressId") Long addressId, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        addressService.setAsDefaultAddress(user, addressId);

        RedirectView rv = new RedirectView("/checkout");
        ra.addFlashAttribute("checkoutAddressChanged", true);
        return rv;
    }


    @GetMapping("/addressFromCheckout")
    public RedirectView addressFromCheckout(RedirectAttributes ra) {
        RedirectView rv = new RedirectView("/account");
        ra.addFlashAttribute("addressTabOpen", true);
        return rv;
    }
    @GetMapping("/checkoutAddressFromAccount")
    public RedirectView checkoutAddressFromAccount(RedirectAttributes ra) {
        RedirectView rv = new RedirectView("/checkout");
        ra.addFlashAttribute("checkoutAddressChanged", true);
        return rv;
    }
}
