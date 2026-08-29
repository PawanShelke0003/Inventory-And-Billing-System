package com.example.demo.Controller;

import com.example.demo.Models.Category;
import com.example.demo.Service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService service;

    @GetMapping
    public String listCategories(Model model){
        model.addAttribute("categories",service.getAllCategories());
        return "admin/category-list";
    }

    @GetMapping("/add")
    public String addCategoryForm(Model model){
        model.addAttribute("category",new Category());
        return "admin/category-form";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category){
        service.createCategory(category);

        return "redirect:/admin/categories";

    }
    @GetMapping("/toggle/{id}")
    public String toggleCategory(@PathVariable Long id){
        service.toggleCategoryStatus(id);
        return "redirect:/admin/categories";
    }
    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id , Model model){
       Category existingCategory = service.getCategoryById(id);
       model.addAttribute("category",existingCategory);
       return "admin/category-form";
    }
}
