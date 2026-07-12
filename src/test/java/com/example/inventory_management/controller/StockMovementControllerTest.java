package com.example.inventory_management.controller;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.service.AssignmentService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockMovementControllerTest {

    @Mock
    private AssignmentService assignmentService; // 🚨 Servis katmanlarını taklit ediyoruz (Mock)

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StockMovementController stockMovementController; // 🚨 Test edilecek asıl API Kontrolörümüz

    @BeforeEach
    void setUp() {
        // Mockito düzeneğini her testten önce hazır hale getirir
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetActiveProductsForUser_ApiSuccess() {
        // 1. Veri Hazırlığı (Arrange)
        int sampleUserId = 1;

        // Ürün listemizi hazırlıyoruz
        List<Product> mockProductList = new ArrayList<>();
        Product p1 = new Product();
        p1.setId(101);
        p1.setBrand("Apple");
        p1.setModelName("MacBook Pro");
        mockProductList.add(p1);

        // 🚨 REVIZE: Hatalı .clear() yerine Mockito standartlarına uygun sahte boş listeler dönüyoruz
        List<StockMovement> mockZimmetListesi = new ArrayList<>();

        // Davranış Belirleme: Servis çağrıldığında sahte verilerimizi dön diyoruz
        when(productService.getAllProducts()).thenReturn(mockProductList);
        when(assignmentService.getZimmetListesi()).thenReturn(mockZimmetListesi);

        // 2. API Metodunu Doğrudan Çalıştır (Act)
        List<Product> result = stockMovementController.getActiveProductsForUser(sampleUserId);

        // 3. Sonuçları ve Liste Bütünlüğünü Doğrula (Assert)
        assertNotNull(result, "API sonucu null dönmemeli!");

        // Kurumsal Doğrulama Kontrolü: getAllProducts metoduna gerçekten 1 kere istek gitti mi?
        verify(productService, times(1)).getAllProducts();
    }
}