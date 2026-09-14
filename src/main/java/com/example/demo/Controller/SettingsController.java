package com.example.demo.Controller;

import com.example.demo.Models.StoreSettings;
import com.example.demo.Service.interfaces.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public String settingsPage(Model model, java.security.Principal principal) {
        model.addAttribute("activeMenu", "settings");
        model.addAttribute("settings", settingsService.getSettings());
        model.addAttribute("staffList", settingsService.getAllStaff());
        model.addAttribute("currentUsername", principal != null ? principal.getName() : "");
        return "admin/settings";
    }

    @PostMapping("/store")
    public String updateStoreSettings(@ModelAttribute StoreSettings settings) {
        settingsService.saveSettings(settings);
        return "redirect:/admin/settings?success=store";
    }

    @PostMapping("/staff/{id}")
    public String updateStaff(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam(required = false) String password) {
        settingsService.updateStaff(id, username, password);
        return "redirect:/admin/settings?success=staff";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String newUsername,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            settingsService.updateMyProfile(userDetails.getUsername(), newUsername, oldPassword, newPassword);
            return "redirect:/login?logout"; // Force re-login on profile change
        } catch (Exception e) {
            return "redirect:/admin/settings?error=" + e.getMessage();
        }
    }
}
