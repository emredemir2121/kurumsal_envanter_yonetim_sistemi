package com.example.inventory_management.service;

import com.example.inventory_management.model.Category;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.repository.CategoryRepository;
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

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository; // Sahte ürün tablosu

    @Mock
    private CategoryRepository categoryRepository; // Sahte kategori tablosu

    @Mock
    private StockMovementRepository stockMovementRepository; // Sahte hareket geçmişi tablosu

    @Mock
    private LogService logService; // Sahte loglama servisi

    @InjectMocks
    private ProductService productService; // Test edeceğimiz asıl servis sınıfı

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDecreaseStock_Success() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        int sampleProductId = 10;

        Product mockProduct = new Product();
        mockProduct.setId(sampleProductId);
        mockProduct.setBrand("Lenovo");
        mockProduct.setModelName("ThinkPad");
        mockProduct.setStockQuantity(5); // Başlangıç stoğumuz: 5

        when(productRepository.findById(sampleProductId)).thenReturn(Optional.of(mockProduct));

        // =============== 2. METODU TETİKLE (Act) ===============
        productService.decreaseStock(sampleProductId);

        // =============== 3. DOĞRULAMA (Assert) ===============
        assertEquals(4, mockProduct.getStockQuantity(), "Zimmet sürecinde stok 1 adet azalmalıdır!");
        verify(productRepository, times(1)).save(mockProduct);
    }

    // 🚨 YENİ - NEGATİF TEST SENARYOSU (Failure Test)
    @Test
    void testDecreaseStock_Failure_WhenStockIsZero() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        int sampleProductId = 20;

        Product mockProduct = new Product();
        mockProduct.setId(sampleProductId);
        mockProduct.setBrand("Apple");
        mockProduct.setModelName("iPhone");
        mockProduct.setStockQuantity(0); // 🚨 Kritik Durum: Stok zaten 0!

        when(productRepository.findById(sampleProductId)).thenReturn(Optional.of(mockProduct));

        // =============== 2. METODU TETİKLE (Act) ===============
        // Stok zaten 0 iken azaltma metodunu çağırıyoruz
        productService.decreaseStock(sampleProductId);

        // =============== 3. DOĞRULAMA (Assert) ===============
        // İş Kuralı: Stok 0'ın altına (-1'e) düşmemeli, 0 olarak kalmalı!
        assertEquals(0, mockProduct.getStockQuantity(), "Stok miktarı 0 ise daha fazla azaltılamaz, 0 kalmalıdır!");

        // Kurumsal Kontrol: Stok değişmediği için veritabanına KAYDETME (save) işlemi ASLA tetiklenmemeli!
        verify(productRepository, never()).save(mockProduct); // 🚨 never() kullanarak hiç çağrılmadığını ispatlıyoruz
    }

    @Test
    void testAddProduct_ShouldAssignCategoryAndSave() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        int categoryId = 2;

        Category mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Bilgisayar Ögeleri");

        Product newProduct = new Product();
        newProduct.setBrand("HP");
        newProduct.setModelName("ProBook");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(newProduct)).thenReturn(newProduct);
        doNothing().when(logService).log(anyString(), anyString(), anyString());

        // =============== 2. METODU TETİKLE (Act) ===============
        productService.addProduct(newProduct, categoryId);

        // =============== 3. DOĞRULAMA (Assert) ===============
        assertNotNull(newProduct.getCategory(), "Ürünün kategorisi null kalmamalı!");
        assertEquals("Bilgisayar Ögeleri", newProduct.getCategory().getName(), "Kategori ismi eşleşmeli!");
        verify(productRepository, times(1)).save(newProduct);
        verify(logService, times(1)).log(eq("INFO"), eq("Envanter"), anyString());
    }
}