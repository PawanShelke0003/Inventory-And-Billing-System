package com.example.demo.Controller;

import com.example.demo.Models.Bill;
import com.example.demo.Models.User;
import com.example.demo.Service.interfaces.BillingService;
import com.example.demo.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/bills")
@RequiredArgsConstructor
public class BillManagementController {
    private final UserService userService;
    private final BillingService billingService;

    @GetMapping
    public String showStaffDirectory(Model model){
        model.addAttribute("users",userService.getAllUsers());
        return "admin/bill-management";

    }
    @GetMapping("staff/{id}")
    public String showStaffDashboard(
            @PathVariable Long id,
            @PathVariable(required = false) String phone,
            @PathVariable(required = false)LocalDate startDate,
            @PathVariable(required = false)LocalDate endDate,
            Model model
            ){

        User staff = userService.getUserById(id);
        List<Bill>bills=billingService.getStaffsBillsWithFilters(id,phone,startDate,endDate);
        BigDecimal totalRevenue = billingService.calculateTotalRevenue(bills);

        model.addAttribute("staff",staff);
        model.addAttribute("bills",bills);
        model.addAttribute("totalRevenue",totalRevenue);

        return "admin/staff-billing-dashboard";

    }

    @GetMapping("/search")
    public String quickPhoneSearch(@RequestParam String phone){

        return "redirect:/admin/bills";
    }

}
