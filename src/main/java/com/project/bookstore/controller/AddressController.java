package com.project.bookstore.controller;

import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;
import com.project.bookstore.service.AddressService;
import com.project.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView addNewAddressPost(@ModelAttribute("address") Address address, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        ModelAndView mav = new ModelAndView("redirect:/account");
        ra.addFlashAttribute("addressTabOpen", true);

        if(userAddressAlreadyExists(user, address)){
            ra.addFlashAttribute("streetAddressExists", "Delivery option with this street address already exists");
            return mav;
        }
        addressService.addNewAddress(user, address);

        return mav;
    }

    private boolean userAddressAlreadyExists(User user, Address address){
        List<Address> userAddressList = user.getAddressList();
        for (Address street : userAddressList) {
            if (street.getStreetAddress1().equals(address.getStreetAddress1())) {
                return true;
            }
        }
        return false;
    }


    @GetMapping("/removeAddress")
    public ModelAndView removeAddress(@RequestParam("id") Long addressId, @RequestParam("checkout") boolean backToCheckout,
                                      Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());

        Address addressToDelete = addressService.getOne(addressId);

        boolean addressWasDefaultAddress = addressToDelete.isDefaultAddress();

        addressService.deleteById(addressId);

        if(addressWasDefaultAddress) {
            setNewDefaultAddress(user);
        }
        if (backToCheckout) {
            return new ModelAndView("redirect:/checkout");
        }
        ra.addFlashAttribute("addressTabOpen", true);
        return new ModelAndView("redirect:/account");
    }

    private void setNewDefaultAddress(User user){

        List<Address> userAddressList = user.getAddressList();

        if (!userAddressList.isEmpty()) {
            userAddressList.get(0).setDefaultAddress(true);
        }
        for (Address address : userAddressList) {
            addressService.save(address);
        }
    }


    @GetMapping("/setAsDefaultAddress")
    public ModelAndView setAsDefaultAddress(@ModelAttribute("addressId") Long addressId, @RequestParam("checkout") boolean backToCheckout, Principal principal, RedirectAttributes ra) {

        User user = userService.findByUsername(principal.getName());
        addressService.setAsDefaultAddress(user, addressId);

        if (backToCheckout) {
            ra.addFlashAttribute("checkoutAddressChanged", true);
            return new ModelAndView("redirect:/checkout");
        }

        ra.addFlashAttribute("addressTabOpen", true);
        ra.addFlashAttribute("defaultAddressChanged", "Your default address has been updated");
        return new ModelAndView("redirect:/account");
    }


    @GetMapping("/addressFromCheckout")
    public RedirectView DeliveryAddressesInAccount(RedirectAttributes ra) {

        ra.addFlashAttribute("addressTabOpen", true);
        return new RedirectView("/account");
    }


    @GetMapping("/checkoutAddressFromAccount")
    public RedirectView linkToCheckoutFromDeliveryAddressesInAccount(RedirectAttributes ra) {

        ra.addFlashAttribute("checkoutAddressChanged", true);
        return new RedirectView("/checkout");
    }
}
