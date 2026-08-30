package com.example.demo.Service.interfaces;

import com.example.demo.Models.Customer;

import java.util.Optional;

public interface CustomerService {

    Customer findOrCreateCustomer(String name , String phone);
    Optional<Customer>getCustomerByPhone(String phone);
    Customer updateCustomer(Long id,String newName,String newPhone);
}
