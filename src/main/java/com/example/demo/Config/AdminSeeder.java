package com.example.demo.Config;

import com.example.demo.Models.Role;
import com.example.demo.Models.User;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.admin.password:owner123 }")
    private String defaultadminpassword;


    @Override
    public void run(String... args) throws Exception {

        Optional<User>adminOptional=userRepository.findByUsername("owner");

        if(adminOptional.isEmpty()){
            User adminUser = User.builder().username("owner").
                    password(passwordEncoder.encode("owner123")).
                    role(Role.ROLE_OWNER).active(true).build();

            userRepository.save(adminUser);
            log.info("ADMIN CREATED SUCCESSFULLY");
        }else {
            log.info("ADMIN ALREADY EXIST");
        }

    }
}
