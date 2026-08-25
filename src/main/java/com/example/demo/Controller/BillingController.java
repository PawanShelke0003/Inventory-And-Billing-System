package com.example.demo.Controller;

import com.example.demo.DTO.BillingRequestDTO;
import com.example.demo.Models.Bill;
import com.example.demo.Models.BillItem;
import com.example.demo.Models.Customer;
import com.example.demo.Models.User;
import com.example.demo.Service.interfaces.BillingService;
import com.example.demo.Service.interfaces.CustomerService;
import com.example.demo.Service.interfaces.ProductService;
import com.example.demo.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff/billing")
public class BillingController {

    private final ProductService productService;
    private final CustomerService customerService;
    private final BillingService billingService;
    private final UserService userService;
@GetMapping
    public String billingForm(Model  model){
        model.addAttribute("products",productService.getActiveProduct());
        return "staff/billing-form";
    }

    @PostMapping("/save")
    public String saveBill(
            @ModelAttribute BillingRequestDTO request,
            Principal principal,Model model) {

        Customer customer = customerService
                .findOrCreateCustomer(request.getCustomerName(), request.getPhone());

        User staff = userService.findByUsername(principal.getName());

        List<BillItem> items = request.getItems().stream()
                .filter(i -> i.getQuantity() != null && i.getQuantity() > 0)
                .map(i -> {
                    BillItem item = new BillItem();
                    item.setProduct(productService.getProductById(i.getProductId()));
                    item.setQuantity(i.getQuantity());
                    return item;
                })
                .toList();
        if(items.isEmpty()){
            model.addAttribute("errors","please select at least 1 product");
            model.addAttribute("products",productService.getActiveProduct());
            return "staff/billing-form";
        }
        Bill bill = billingService.createBill(customer, staff, items);

        return "redirect:/staff/billing/view/" + bill.getId();
    }


    @GetMapping("view/{id}")
    public  String viewBill(@PathVariable Long id , Model model){
    var bill = billingService.getBillById(id);

    model.addAttribute("bill",bill);
    model.addAttribute("items",bill.getItems());

    return "staff/bill-reciept";
    }

    @GetMapping("/history")
    public String billingHistory(Model model,Principal principal){
    User staff = userService.findByUsername(principal.getName());
    model.addAttribute("bills",billingService.getBillByStaff(staff));
    return "staff/bill-history";
    }


}
