package com.example.demo.Service.implementations;

import com.example.demo.Models.Role;
import com.example.demo.Models.StoreSettings;
import com.example.demo.Models.User;
import com.example.demo.Repository.StoreSettingsRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.interfaces.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private final StoreSettingsRepository storeSettingsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public StoreSettings getSettings() {
        return storeSettingsRepository.findById(1L).orElseGet(() -> {
            StoreSettings defaultSettings = StoreSettings.builder()
                    .storeName("My Store")
                    .address("123 Main St")
                    .phone("1234567890")
                    .gstin("GSTIN123456789")
                    .gstRate(0.18)
                    .invoicePrefix("INV-")
                    .receiptFooter("Thank you for your business!")
                    .build();
            return storeSettingsRepository.save(defaultSettings);
        });
    }

    @Override
    public StoreSettings saveSettings(StoreSettings settings) {
        StoreSettings existing = getSettings();
        existing.setStoreName(settings.getStoreName());
        existing.setAddress(settings.getAddress());
        existing.setPhone(settings.getPhone());
        existing.setGstin(settings.getGstin());
        existing.setGstRate(settings.getGstRate());
        existing.setInvoicePrefix(settings.getInvoicePrefix());
        existing.setReceiptFooter(settings.getReceiptFooter());
        return storeSettingsRepository.save(existing);
    }

    @Override
    public List<User> getAllStaff() {
        return userRepository.findByRole(Role.ROLE_STAFF);
    }

    @Override
    public User updateStaff(Long id, String username, String newPassword) {
        User staff = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        staff.setUsername(username);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(staff);
    }

    @Override
    public User updateMyProfile(String currentUsername, String newUsername, String oldPassword, String newPassword) {
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new RuntimeException("User not found"));
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            currentUser.setUsername(newUsername);
        }
        if (oldPassword != null && newPassword != null && !newPassword.trim().isEmpty()) {
            if (passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                throw new RuntimeException("Incorrect old password");
            }
        }
        return userRepository.save(currentUser);
    }
}
