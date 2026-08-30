package com.example.demo.Service.implementations;

import com.example.demo.Models.Customer;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.interfaces.CustomerService;
import com.example.demo.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
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

    @Override
    public Optional<Customer> getCustomerByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    @Override
    public Customer updateCustomer(Long id, String newName, String newPhone) {
        Optional<Customer>existingCustomer=repository.findByPhone(newPhone);

        if(existingCustomer.isPresent()&&!existingCustomer.get().getId().equals(id)){
            throw new BusinessException
                    ("Update Failed : Phone Number - "
                            +newPhone+" is already registered to "+
                            existingCustomer.get().getName());
        }

        Customer customerToUpdate = repository.findById(id).orElseThrow(()->new BusinessException("Customer not found "));
        customerToUpdate.setName(newName);
        customerToUpdate.setPhone(newPhone);

        return repository.save(customerToUpdate);
    }
}
