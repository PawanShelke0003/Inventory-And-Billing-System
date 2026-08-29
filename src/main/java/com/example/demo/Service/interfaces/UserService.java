package com.example.demo.Service.interfaces;


import com.example.demo.Models.User;

import java.util.List;

public interface UserService {

    User createUsers(User user);
    List<User>getAllUsers();
    void toggleUserStatus(Long userId);
    User findByUsername(String name);
    List<User>getUserByActiveStatus();
    User getUserById(Long id);
}
