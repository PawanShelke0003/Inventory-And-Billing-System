package com.example.demo.Service.implementations;

import com.example.demo.Models.Role;
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
        if(user.getId()==null){
            user.setPassword(encoder.encode(user.getPassword()));
            user.setActive(true);
        }else{
            User existing = getUserById(user.getId());

            if(existing.getRole()== Role.ROLE_OWNER){
                String loggedInUserName=
                        org.springframework.security.core.
                                context.SecurityContextHolder.
                                getContext().getAuthentication().getName();
                if(!existing.getUsername().equals(loggedInUserName)){
                    throw new RuntimeException("UNAUTHORIZED : YOU CANNOT MODIFY ANOTHER ADMINISTRATOR'S ACCOUNT");
                }
            }

            if(user.getPassword()==null||user.getPassword().trim().isEmpty()){
                user.setPassword(existing.getPassword());
            }else{
                user.setPassword(encoder.encode(user.getPassword()));
            }

            user.setActive(existing.getActive());
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void toggleUserStatus(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();

    if(user.getRole()== Role.ROLE_OWNER){
        String loggedInUserName =
                org.springframework.security.core.
                        context.SecurityContextHolder.getContext().
                        getAuthentication().getName();

        if(!user.getUsername().equals(loggedInUserName)){
            throw new RuntimeException("UNAUTHORIZED YOU CANNOT MODIFY ANOTHER MANAGERS ACCOUNT ");
        }
    }

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

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->
                new RuntimeException("USER NOT FOUND"));
    }
}
