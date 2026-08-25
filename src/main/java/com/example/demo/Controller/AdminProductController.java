package com.example.demo.Controller;

import com.example.demo.Models.Product;
import com.example.demo.Service.interfaces.CategoryService;
import com.example.demo.Service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model){
        model.addAttribute("products",productService.getAllProducts());

        return "admin/product-list";
    }

    @GetMapping("/add")
    public String addProductForm(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("categories",categoryService.getAllCategories());
        return "admin/product-form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,@RequestParam Long categoryId){
        productService.createProduct(product,categoryId);
        return "redirect:/admin/products";
    }

    @GetMapping("/toggle/{id}")
    public String toggleProducts(@PathVariable Long id){
        productService.toggleActiveStatus(id);
        return "redirect:/admin/products";
    }


    @GetMapping("/stock/{id}")
    public String updateStockForm(@PathVariable Long id , Model model){

        model.addAttribute("product",productService.getProductById(id));
        return "admin/product-stock";
    }


    @PostMapping("/stock/update")
    public String updateStock(@RequestParam Long productId,
                                @RequestParam Integer quantity) {
    productService.updateStock(productId,quantity);
        return "redirect:/admin/products";
    }


    @GetMapping("/stock")
    public String stockPage(Model model){
        model.addAttribute("products",productService.getAllProducts());
        return "admin/stock";
    }


}
