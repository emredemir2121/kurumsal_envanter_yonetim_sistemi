package com.example.inventory_management.service;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AssignmentServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository; // Sahte hareket tablosu

    @Mock
    private ProductRepository productRepository; // Sahte ürün tablosu

    @Mock
    private ProductService productService; // Sahte ürün servisi

    // 🚨 YENİ: NullPointerException logService hatasını çözmek için eklenen mock tanımı
    @Mock
    private LogService logService;

    @InjectMocks
    private AssignmentService assignmentService; // Test edilecek asıl iş mantığı sınıfı

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApproveZimmetRequest_ShouldDecreaseStock_AndSetStatusToApproved() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        int requestId = 55;
        int productId = 101;

        StockMovement pendingMovement = new StockMovement();
        pendingMovement.setId(requestId);
        pendingMovement.setProductId(productId);
        pendingMovement.setMovementType("ZIMMET");
        pendingMovement.setStatus("PENDING");
        pendingMovement.setQuantity(1);

        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setBrand("Asus");
        mockProduct.setModelName("ZenBook");
        mockProduct.setStockQuantity(10);

        // Mockito ile taklit kuralları
        when(stockMovementRepository.findById(requestId)).thenReturn(Optional.of(pendingMovement));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        // Metotlar çağrıldığında hiçbir hata fırlatmamaları için konulan kurallar (doNothing)
        doNothing().when(productService).decreaseStock(productId);
        doNothing().when(logService).log(anyString(), anyString(), anyString());

        // =============== 2. SENARYOYU TETİKLE (Act) ===============
        boolean isApproved = assignmentService.approveRequest(requestId);

        // =============== 3. İŞ KURALLARINI DOĞRULA (Assert) ===============
        assertTrue(isApproved, "Zimmet onaylama işlemi true dönmeli!");

        // Hareket durumunun onaylandı kelimesini içerdiğini doğrula
        assertTrue(pendingMovement.getStatus().toUpperCase().contains("APPROV"), "Hareket durumu onaylandı olmalı!");

        // Kurumsal Doğrulama Kontrolü: Bağımlılıkların çağrılıp çağrılmadığını kontrol ediyoruz
        verify(productService, times(1)).decreaseStock(productId);
        verify(stockMovementRepository, times(1)).save(pendingMovement);
        verify(logService, times(1)).log(anyString(), anyString(), anyString());
    }
}