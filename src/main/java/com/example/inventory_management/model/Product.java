package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // 🚨 Hafta 13: Kirli veriyi engelleyen anotasyonlar [cite: 2316, 2318]
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Data // 🚨 Hafta 14: Getter, Setter, toString metotlarını otomatik bağlar [cite: 3250]
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Tam parametreli constructor üretir
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Marka / Üretici alanı boş bırakılamaz! [cite: 2428]")
    @Size(min = 2, max = 50, message = "Marka adı en az 2, en fazla 50 karakter olmalıdır! [cite: 2341]")
    private String brand;

    @NotBlank(message = "Model adı boş bırakılamaz! [cite: 2428]")
    @Column(name = "model_name") // 🚨 Eski veritabanı şemanla tam uyum için korundu
    private String modelName;

    @NotBlank(message = "Ürün tipi boş bırakılamaz! [cite: 2428]")
    @Column(name = "product_type") // 🚨 Eski veritabanı şemanla tam uyum için korundu
    private String productType;

    @NotNull(message = "Birim fiyat girilmesi zorunludur! [cite: 2335]")
    @DecimalMin(value = "0.0", inclusive = false, message = "Birim fiyat 0'dan büyük olmalıdır!")
    private double price;

    @Min(value = 0, message = "Stok miktarı negatif bir değer alamaz! [cite: 2430]")
    @Column(name = "stock_quantity") // 🚨 Eski veritabanı şemanla tam uyum için korundu
    private int stockQuantity;

    @Column(name = "specifications") // 🚨 Eski veritabanı şemanla tam uyum için korundu
    private String specifications;

    @ManyToOne
    @JoinColumn(name = "category_id") // 🚨 Eski veritabanı şemanla tam uyum için korundu
    private Category category;
}