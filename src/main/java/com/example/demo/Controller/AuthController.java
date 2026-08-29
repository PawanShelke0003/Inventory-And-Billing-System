package com.example.demo.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(){
        return "auth/login";
    }
@GetMapping("/redirect")
    public String redirectAfterLogin(Authentication auth){

    if(auth.getAuthorities().stream()
            .anyMatch(a->
                    a.getAuthority().equals("ROLE_OWNER"))){
        return "redirect:/owner/dashboard";
    }

        if(auth.getAuthorities().stream()
                .anyMatch(a->
                        a.getAuthority().equals("ROLE_MANAGER"))){
            return "redirect:/admin/dashboard";
        }
        return "redirect:/staff/dashboard";

    }
}
