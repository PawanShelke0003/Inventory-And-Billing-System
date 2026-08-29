package com.example.demo.Service.implementations;

import com.example.demo.Models.Category;
import com.example.demo.Models.Product;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository repository;
  private final CategoryRepository categoryRepository;


    @Override
    public Product createProduct(Product product, Long categoryId) {


        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new RuntimeException("CATEGORY NOT FOUND"));
        product.setCategory(category);
        if(product.getId()==null){
            product.setActive(true);
        }

        return repository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @Override
    public List<Product> getActiveProduct() {
        return repository.findByActiveTrue();
    }

    @Override
    public void toggleActiveStatus(Long id) {
        Product product = repository.findById(id).orElseThrow(()->  new RuntimeException("PRODUCT NOT FOUND"));
        product.setActive(!product.getActive());
        repository.save(product);

    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = repository.findById(productId).orElseThrow(()->new RuntimeException("PRODUCT NOT FOUND"));
        product.setQuantity(quantity);
        repository.save(product);
    }

    @Override
    public Product getProductById(Long id) {

        
        return repository.findById(id).orElseThrow(()->new RuntimeException("PRODUCT NOT FOUND"));
    }

    @Override
    public List<Product> getLowStockProducts() {
        return repository.findByQuantityLessThan(5);
    }

    @Override
    public List<Product> getActiveProductsByCategory(Long categoryId) {
        return repository.findByCategoryIdAndActiveTrue(categoryId);
    }
}
