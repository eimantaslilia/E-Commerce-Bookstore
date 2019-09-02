package com.project.bookstore.service.impl;

import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;
import com.project.bookstore.repository.AddressRepository;
import com.project.bookstore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {


    @Autowired
    private AddressRepository addressRepository;


    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public Address getOne(Long id) {
        return addressRepository.getOne(id);
    }

    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public void addNewAddress(User user, Address address) {

        address.setUser(user);

        setToDefaultIfFirstAddress(user, address);

        user.getAddressList().add(address);
        addressRepository.save(address);
    }

    private void setToDefaultIfFirstAddress(User user, Address address) {

        if (user.getAddressList().isEmpty()) {
            address.setDefaultAddress(true);
        }
    }

    @Override
    public void setAsDefaultAddress(User user, Long defaultAddressId) {

        List<Address> userAddressList = user.getAddressList();
        for (Address address : userAddressList) {
            if (address.getId().equals(defaultAddressId)) {
                address.setDefaultAddress(true);
                addressRepository.save(address);
            } else {
                address.setDefaultAddress(false);
                addressRepository.save(address);
            }
        }
    }


}
