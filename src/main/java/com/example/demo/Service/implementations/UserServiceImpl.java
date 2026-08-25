package com.example.demo.Service.implementations;

import com.example.demo.Models.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    @Override
    public User createUsers(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void toggleUserStatus(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();
    user.setActive(!user.getActive());
    userRepository.save(user);
    }

    @Override
    public User findByUsername(String name) {


        return userRepository.findByUsername(name).orElseThrow(()->new RuntimeException("USER NOT FOUND "+name));
    }

    @Override
    public List<User> getUserByActiveStatus() {
        return userRepository.findByActiveTrue();
    }
}
