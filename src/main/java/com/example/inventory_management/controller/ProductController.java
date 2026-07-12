package com.example.inventory_management.controller;

import com.example.inventory_management.dto.ProductDTO;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid; // 🚨 Hafta 13: Doğrulama tetikleyicisi
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 🚨 Hafta 13: Hata yakalayıcı nesne
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/inventory")
    public String showInventory(@RequestParam(required = false) Integer fetchId, Model model) {
        // UI için DTO listesini gönderiyoruz
        List<ProductDTO> products = productService.getAllProductsForUI();
        model.addAttribute("products", products);

        // 🚨 GÜNCELLEME: Thymeleaf formunun kilitlenmemesi için nesne bağlamayı garantiye alıyoruz
        if (!model.containsAttribute("productForm")) {
            model.addAttribute("productForm", new Product());
        }

        model.addAttribute("stockEvaluator", (Function<String, Integer>) productService::getCriticalStockLimit);

        if (fetchId != null) {
            Product existingProduct = productService.getProductById(fetchId);
            if (existingProduct != null) {
                model.addAttribute("productForm", existingProduct); // Form verisini doldur
                model.addAttribute("fetchedProduct", existingProduct);
            } else {
                model.addAttribute("errorMessage", "HATA: Girilen ID'ye ait bir ürün bulunamadı!");
            }
        }
        return "inventory";
    }


    @PostMapping("/inventory/save")
    public String saveOrUpdateProduct(@Valid @ModelAttribute("productForm") Product product,
                                      BindingResult result,
                                      @RequestParam int categoryId,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {

        // 🚨 HAFTA 13 & 14 VALIDATION: Kirli veri algılandığı an veritabanı durdurulur (Fail-Fast)
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProductsForUI());
            model.addAttribute("stockEvaluator", (Function<String, Integer>) productService::getCriticalStockLimit);
            model.addAttribute("errorMessage", "Lütfen formdaki doğrulama hatalarını düzeltiniz!");
            return "inventory"; // Veritabanına sızma engellendi, sayfaya hata mesajlarıyla geri dönüldü
        }

        try {
            if (product.getId() > 0) {
                productService.updateProduct(product, categoryId);
                redirectAttributes.addFlashAttribute("successMessage", "Ürün (ID: " + product.getId() + ") başarıyla güncellendi.");
            } else {
                productService.addProduct(product, categoryId);
                redirectAttributes.addFlashAttribute("successMessage", "Yeni ürün başarıyla envantere eklendi.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "İşlem sırasında beklenmedik hata: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/delete")
    public String deleteProduct(@RequestParam int deleteProductId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(deleteProductId);
            redirectAttributes.addFlashAttribute("successMessage", "ID: " + deleteProductId + " olan ürün silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ürün silinemedi: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @GetMapping("/inventory/export-csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=envanter_raporu.csv");

        PrintWriter writer = response.getWriter();
        writer.write('\ufeff'); // UTF-8 BOM Excel uyumluluğu için

        writer.println("ID;Marka;Model;Tip;Fiyat;Stok;Özellikler;Kategori ID");

        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            String desc = (p.getSpecifications() != null) ? p.getSpecifications().replace(";", ",") : "-";
            int catId = (p.getCategory() != null) ? p.getCategory().getId() : 1;

            writer.println(String.format("%d;%s;%s;%s;%.2f;%d;%s;%d",
                    p.getId(),
                    p.getBrand(),
                    p.getModelName(),
                    p.getProductType(),
                    p.getPrice(),
                    p.getStockQuantity(),
                    desc,
                    catId));
        }
        writer.flush();
        writer.close();
    }
}