package com.example.demo.Controller;

import com.example.demo.Models.Customer;
import com.example.demo.Service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/phone/{phone}")
    public ResponseEntity<Customer>getCustomerByPhone(@PathVariable String phone){
        return customerService.
                getCustomerByPhone(phone).
                map(ResponseEntity::ok).
                orElse(ResponseEntity.notFound().build());
    }
}
