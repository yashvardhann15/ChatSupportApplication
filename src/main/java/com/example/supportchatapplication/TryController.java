package com.example.supportchatapplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TryController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello! The application is running... Bht dimag lagaya ";
    }
}
