package com.project.bookstore.service;

import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;

public interface AddressService {

    void addNewAddress(User user, Address address);

    void deleteById(Long id);

    void setAsDefaultAddress(User user, Long defaultAddressId);

    void save(Address address);

    Address getOne(Long id);
}
