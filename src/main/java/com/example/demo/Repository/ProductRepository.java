package com.example.demo.Repository;

import com.example.demo.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product>findByName(String name);
    List<Product>findByActiveTrue();
    List<Product>findByQuantityLessThan(Integer quantity);
    List<Product>findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity),0) FROM Product p WHERE p.active = true")
    BigDecimal findTotalInventoryValue();

}
