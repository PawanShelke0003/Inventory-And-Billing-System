package com.example.demo.Config;

import com.example.demo.Service.CustomUserServiceDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserServiceDetails serviceDetails;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
                .authorizeHttpRequests(auth->
                                auth.requestMatchers("/login","/css/**")
                                        .permitAll()
                                        .requestMatchers("/admin/**").hasRole("ADMIN")
                                        .requestMatchers("/staff/**").hasRole("STAFF")
                                        .anyRequest().authenticated()


                        )
                .formLogin(form->form.loginPage("/login")
                        .defaultSuccessUrl("/redirect",true)
                        .permitAll())

                .logout(out->out.logoutSuccessUrl("/login?logout")
                        .permitAll())

                ;
                return http.build();
    }

}
