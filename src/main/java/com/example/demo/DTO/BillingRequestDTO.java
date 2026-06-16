package com.example.demo.DTO;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.processing.Pattern;

import java.util.List;

@Data
public class BillingRequestDTO {

    private String customerName;

    private String phone;
    private List<CartItemDTO> items;


}
