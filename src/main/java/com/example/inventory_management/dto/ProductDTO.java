package com.example.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private int id;
    private String brand;
    private String modelName;
    private String productType;
    private double price;
    private String formattedPrice; // Arayüzde kurumsal görünmesi için işlenmiş fiyat (Örn: 6.500,00 TL)
    private int stockQuantity;
    private String specifications;
    private int categoryId;
    private String categoryName;
    private String stockStatus;    // Strategy/Kritik sınır kontrolü sonucu: "KRİTİK STOK" veya "STOKTA VAR"
}