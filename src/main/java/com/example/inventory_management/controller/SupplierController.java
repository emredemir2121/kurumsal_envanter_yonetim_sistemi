package com.example.inventory_management.controller;

import com.example.inventory_management.dto.SupplierRequestDTO;
import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.service.SupplierService;
import com.example.inventory_management.service.LogService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid; // 🚨 Hafta 13: JSR-380 doğrulama tetikleyicisi
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 🚨 Hafta 13: Hata yakalayıcı
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SupplierController {

    private final SupplierService supplierService;
    private final LogService logService;

    // Bellekte tutulan bağımsız talep geçmişi listesi (Yeni DTO yapısına geçirildi)
    private static final List<SupplierRequestDTO> requestHistoryList = new ArrayList<>();
    private static int requestIdCounter = 1;

    @Autowired
    public SupplierController(SupplierService supplierService, LogService logService) {
        this.supplierService = supplierService;
        this.logService = logService;
    }

    @GetMapping("/suppliers")
    public String showSuppliersPage(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("requests", requestHistoryList);

        // Thymeleaf veri bağlama kilitlenmemesi için boş nesne gönderiyoruz
        if (!model.containsAttribute("supplierForm")) {
            model.addAttribute("supplierForm", new Supplier());
        }
        return "suppliers";
    }

    @PostMapping("/suppliers/add")
    public String addSupplier(@Valid @ModelAttribute("supplierForm") Supplier supplier,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // 🚨 HAFTA 13 & 14 VALIDATION: Formda geçersiz veri varsa (örn: yanlış mail veya boş isim) yakalar
        if (result.hasErrors()) {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("requests", requestHistoryList);
            model.addAttribute("errorMessage", "Lütfen tedarikçi bilgilerindeki doğrulama hatalarını düzeltiniz!");
            return "suppliers"; // Fail-Fast: Veritabanına gitmeden sayfaya hatalarla geri döner
        }

        try {
            supplierService.saveSupplier(supplier);
            logService.log("INFO", "Tedarikçi", "Yeni tedarikçi eklendi: " + supplier.getCompanyName());
            redirectAttributes.addFlashAttribute("successMessage", "Tedarikçi başarıyla sisteme eklendi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ekleme hatası: " + e.getMessage());
        }
        return "redirect:/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            Supplier supplier = supplierService.getSupplierById(id);
            if (supplier != null) {
                String name = supplier.getCompanyName();
                supplierService.deleteSupplierById(id);
                logService.log("WARNING", "Tedarikçi", "Tedarikçi sistemden silindi: " + name);
                redirectAttributes.addFlashAttribute("successMessage", "Tedarikçi sistemden kaldırıldı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Silme hatası: " + e.getMessage());
        }
        return "redirect:/suppliers";
    }

    @PostMapping("/suppliers/request-product")
    public String requestProduct(@RequestParam("supplierId") int supplierId,
                                 @RequestParam("productDetails") String productDetails,
                                 @RequestParam("quantity") int quantity,
                                 RedirectAttributes redirectAttributes) {
        try {
            Supplier supplier = supplierService.getSupplierById(supplierId);

            if (supplier != null && productDetails != null && !productDetails.trim().isEmpty()) {
                SupplierRequestDTO newRequest = new SupplierRequestDTO();
                newRequest.setId(requestIdCounter++);
                newRequest.setSupplierName(supplier.getCompanyName());
                newRequest.setProductName(productDetails.trim());
                newRequest.setQuantity(quantity);
                newRequest.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                requestHistoryList.add(0, newRequest);

                logService.log("INFO", "Tedarik", supplier.getCompanyName() + " firmasından " + quantity + " adet [" + productDetails + "] talebi listeye işlendi.");
                redirectAttributes.addFlashAttribute("successMessage", "Ürün tedarik talebi başarıyla listeye eklendi.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tedarikçi bilgisi veya ürün detayı geçersiz.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Talep işleme hatası: " + e.getMessage());
        }
        return "redirect:/suppliers";
    }

    @PostMapping("/suppliers/request/delete/{id}")
    public String deleteRequest(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean removed = requestHistoryList.removeIf(req -> req.getId() == id);
            if (removed) {
                logService.log("WARNING", "Tedarik", "ID: " + id + " olan ürün tedarik talebi listeden kaldırıldı.");
                redirectAttributes.addFlashAttribute("successMessage", "Tedarik talebi listeden başarıyla çıkarıldı.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Kaldırılmak istenen talep bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Talep silme hatası: " + e.getMessage());
        }
        return "redirect:/suppliers";
    }

    @GetMapping("/suppliers/export-csv")
    public void exportRequestsToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=tedarik_urun_talepleri.csv");

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');

        writer.println("Talep ID;Tedarikçi Firma;Talep Edilen Ürün;Miktar;İşlem Tarihi;Durum");

        for (SupplierRequestDTO req : requestHistoryList) {
            writer.println(req.getId() + ";" +
                    req.getSupplierName() + ";" +
                    req.getProductName() + ";" +
                    req.getQuantity() + " Adet;" +
                    req.getCreatedAt() + ";BEKLEMEDE");
        }
        writer.flush();
        writer.close();
    }
}