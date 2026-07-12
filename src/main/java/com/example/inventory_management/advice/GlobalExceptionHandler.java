package com.example.inventory_management.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice // 🚨 Tüm controller'ların üzerinde bir koruma kalkanı oluşturur
public class GlobalExceptionHandler {

    // 🚨 1. YOL: Geçersiz Argüman Hataları (Veri doğrulama veya mantık hataları için)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Sistemsel Mantık Hatası: " + ex.getMessage());
        return "redirect:/dashboard"; // Güvenli ana panele fırlatır, beyaz sayfa engellenir
    }

    // 🚨 2. YOL: Null Pointer Hataları (Veritabanından null dönen nesne pürüzleri için)
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointer(NullPointerException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "HATA: Aradığınız nesneye veya veriye şu anda ulaşılamıyor!");
        return "redirect:/dashboard";
    }

    // 🚨 3. YOL: GLOBAL KORUMA SÜZGECİ (Gözden kaçan tüm diğer beklenmedik runtime hataları için)
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Beklenmedik bir sistem hatası meydana geldi: " + ex.getMessage());
        return "dashboard"; // Çökmek yerine admin panelinde şık bir toast mesajı yakar
    }
}