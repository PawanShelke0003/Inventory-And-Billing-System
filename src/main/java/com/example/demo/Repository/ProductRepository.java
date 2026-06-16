package com.example.demo.Repository;

import com.example.demo.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product>findByName(String name);
    List<Product>findByActiveTrue();
    List<Product>findByQuantityLessThan(Integer quantity);


}
