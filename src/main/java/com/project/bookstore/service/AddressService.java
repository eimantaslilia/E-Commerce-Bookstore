package com.project.bookstore.service;

import com.project.bookstore.domain.Address;
import com.project.bookstore.domain.User;

public interface AddressService {

    void save(Address address);

    Address getOne(Long id);

    void deleteById(Long id);

    void addNewAddress(User user, Address address);

    void setAsDefaultAddress(User user, Long defaultAddressId);

}
