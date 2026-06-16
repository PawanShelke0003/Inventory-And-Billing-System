package com.example.demo.Service;

import com.example.demo.Models.Customer;
import com.example.demo.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements  CustomerService{
    private final CustomerRepository repository;
    @Override
    public Customer findOrCreateCustomer(String name, String phone) {

        return repository.findByPhone(phone)
                .orElseGet(()->repository.save(
                        Customer.builder()
                                .name(name)
                                .phone(phone)
                                .build()
                ));
    }
}
