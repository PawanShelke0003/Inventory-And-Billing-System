package com.example.demo.Controller;

import com.example.demo.Models.Bill;
import com.example.demo.Service.BillingService;
import com.example.demo.Service.ProductService;
import com.example.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashBoardController {
    private final BillingService billingService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model){
        List<Bill>bills=billingService.getAllBills();
        model.addAttribute("bills",bills);
        model.addAttribute("billCount",bills.size());

        model.addAttribute("totalRevenue",bills.stream()
                .map(Bill::getGrandTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add));
        model.addAttribute("productCount",productService.getActiveProduct().size());
        model.addAttribute("userCount",userService.getUserByActiveStatus().size());


        return "admin/dashboard";
    }
}
