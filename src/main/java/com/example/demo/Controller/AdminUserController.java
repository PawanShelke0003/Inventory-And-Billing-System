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
    public String addUserForm(Model model,
                              org.springframework.security.core.Authentication auth){
        model.addAttribute("user",new User());
        model.addAttribute("roles", Role.values());
        boolean isOwner = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
        model.addAttribute("isOwner", isOwner);
        return "admin/user-form";
    }

    @GetMapping
    public String listUsers(Model model, org.springframework.security.core.Authentication auth){
        model.addAttribute("users",user.getAllUsers());
        boolean isOwner = auth.getAuthorities().stream().
                anyMatch(a->a.getAuthority().equals("ROLE_OWNER"));
        model.addAttribute("isOwner",isOwner);
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
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id,Model model ,
                           org.springframework.security.core.Authentication auth){
        model.addAttribute("user",user.getUserById(id));
        model.addAttribute("roles",com.example.demo.Models.Role.values());
        boolean isOwner = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
        model.addAttribute("isOwner", isOwner);
        return "admin/user-form";
    }

}
