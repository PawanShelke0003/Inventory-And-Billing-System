package com.example.demo.Controller;

import com.example.demo.Models.Product;
import com.example.demo.Service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    @GetMapping("/active")
    public List<Product> getActiveProducts() {
        return productService.getActiveProduct();
    }

    @GetMapping("/category/{id}")
    public List<Product>getProductByCategory(@PathVariable Long id){
        return productService.getActiveProductsByCategory(id);
    }
}
