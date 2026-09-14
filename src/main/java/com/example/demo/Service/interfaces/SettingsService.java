package com.example.demo.Service.interfaces;

import com.example.demo.Models.StoreSettings;
import com.example.demo.Models.User;
import java.util.List;

public interface SettingsService {
    StoreSettings getSettings();
    StoreSettings saveSettings(StoreSettings settings);
    
    List<User> getAllStaff();
    User updateStaff(Long id, String username, String newPassword);
    User updateMyProfile(String currentUsername, String newUsername, String oldPassword, String newPassword);
}
