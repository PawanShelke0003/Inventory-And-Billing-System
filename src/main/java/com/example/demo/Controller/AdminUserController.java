package com.example.demo.Controller;

import com.example.demo.Models.Role;
import com.example.demo.Models.User;
import com.example.demo.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService user;

    @GetMapping("/add")
    public String addUserForm(Model model){
        model.addAttribute("user",new User());
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @GetMapping
    public String listUsers(Model model){
        model.addAttribute("users",user.getAllUsers());
        return "admin/user-list";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User users){
    user.createUsers(users);
        return "redirect:/admin/users";
    }

    @GetMapping("/toggle/{Id}")
    public String toggleUsers(@PathVariable Long Id){
        user.toggleUserStatus(Id);
        return "redirect:/admin/users";
    }

}
