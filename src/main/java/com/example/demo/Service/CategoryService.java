package com.example.demo.Service;

import com.example.demo.Models.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);
    List<Category>getAllCategories();
    void toggleCategoryStatus(Long id);

}
