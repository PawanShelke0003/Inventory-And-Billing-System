package com.example.demo.Controller;

import com.example.demo.Models.User;
import com.example.demo.Service.interfaces.BillingService;
import com.example.demo.Service.interfaces.ProductService;
import com.example.demo.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffDashBoardController {

    private final BillingService billingService;
    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(Model model,Principal principal){
    User staff = userService.findByUsername(principal.getName());
        model.addAttribute("staff",staff);
        model.addAttribute("recentBills",
                billingService.getBillByStaff(staff));
        model.addAttribute("lowStockProducts",productService.getLowStockProducts());
        return "staff/dashboard";
    }
}
