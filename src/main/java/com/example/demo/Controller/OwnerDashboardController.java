package com.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerDashboardController {

    @GetMapping("/dashboard")
    public String ownerDashboard(Model model) {
        // We will add the heavy analytics math here later!
        return "owner/dashboard"; 
    }
}
