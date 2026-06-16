package com.example.demo.Service;

import com.example.demo.Models.Category;
import com.example.demo.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository repository;

    @Override
    public Category createCategory(Category category) {

        repository.findByName(category.getName())
                .ifPresent(c->{
                        throw new RuntimeException("CATEGORY ALREADY EXIST");
                }
                );
        category.setActive(true);


        return repository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public void toggleCategoryStatus(Long id) {
        Category category = repository.findById(id).orElseThrow(()->new RuntimeException("CATEGORY NOT FOUND"));
        category.setActive(!category.getActive());
        repository.save(category);

    }
}
