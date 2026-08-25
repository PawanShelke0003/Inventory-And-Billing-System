package com.example.demo.Service.interfaces;

import com.example.demo.Models.Customer;

public interface CustomerService {

    Customer findOrCreateCustomer(String name , String phone);
}
