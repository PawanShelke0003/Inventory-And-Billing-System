package com.example.demo.Controller;

import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.interfaces.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    
}
