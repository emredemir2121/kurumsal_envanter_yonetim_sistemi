package com.example.inventory_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Kök dizine gelindiğinde kullanıcıyı login mekanizmasına yönlendirir
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    // 🚨 REVIZE: Sadece kurumsal login sayfasını render eder.
    // POST /login ve GET /logout işlemlerini Spring Security arka planda otomatik olarak devralır.
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}