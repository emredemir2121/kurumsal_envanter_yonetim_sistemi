package com.example.inventory_management.service;

import com.example.inventory_management.model.Supplier;
import com.example.inventory_management.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository; // Sahte tedarikçi tablomuz (Mock)

    @InjectMocks
    private SupplierService supplierService; // Test edilecek asıl servis sınıfı

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllSuppliers_ShouldReturnListCorrectly() {
        // 1. Veri Hazırlığı (Arrange)
        List<Supplier> mockSuppliers = new ArrayList<>();

        Supplier s1 = new Supplier();
        Supplier s2 = new Supplier();

        mockSuppliers.add(s1);
        mockSuppliers.add(s2);

        // Taklit Kuralı: Repo içindeki findAll çağrılırsa bizim sahte listemizi dön
        when(supplierRepository.findAll()).thenReturn(mockSuppliers);

        // 2. Metodu Çalıştır (Act)
        List<Supplier> result = supplierService.getAllSuppliers();

        // 3. Doğrulama (Assert)
        assertNotNull(result, "Dönen tedarikçi listesi null olamaz!");
        assertEquals(2, result.size(), "Tedarikçi listesinin boyutu 2 olmalı!");

        // Kurumsal Kontrol: Veritabanına gerçekten 1 kere gidildi mi?
        verify(supplierRepository, times(1)).findAll();
    }
}