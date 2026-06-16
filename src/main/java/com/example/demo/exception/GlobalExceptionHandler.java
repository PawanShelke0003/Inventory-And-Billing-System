package com.example.demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, Model model){
        model.addAttribute("errorTitle","OperationFailed");
        model.addAttribute("errorMessage",ex.getMessage());

        return "error/costom-error";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException exception,Model model){
    model.addAttribute("errorTitle","Not Found");
    model.addAttribute("errorMessage",exception.getMessage());
        return "error/custom-error";
    }


    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex,Model model){

        model.addAttribute("errorTitle","somethingWentWrong");
        model.addAttribute("errorMessage","please contact admin or try again later");

        return "error/custom-error";
    }
}
