package com.example.demo.Service.interfaces;

import com.example.demo.Models.Product;

import java.util.List;

public interface ProductService {

   public Product createProduct(Product product,Long categoryId);
    List<Product>getAllProducts();
    List<Product>getActiveProduct();
    void toggleActiveStatus(Long id);
    void updateStock(Long productId,Integer quantity);
    Product getProductById(Long id);
    List<Product>getLowStockProducts();

}
