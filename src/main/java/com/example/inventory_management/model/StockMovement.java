package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // 🚨 Hafta 13: JSR-380 doğrulama kuralları için
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "stock_movements")
@Data // 🚨 Hafta 14: Getter, Setter, toString metotlarını otomatik bağlar (Clean Code)
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Tam parametreli constructor üretir
@EqualsAndHashCode(callSuper = true) // Üst sınıf olan BaseEntity alanlarını da eşitlik kontrollerine dahil eder
public class StockMovement extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    @Min(value = 1, message = "Geçerli bir ürün seçilmesi zorunludur!") // 🚨 Kirli veriyi engelleme
    private int productId;

    @Column(name = "user_id")
    @Min(value = 1, message = "Zimmet/İade işleminin yapılacağı personel seçilmelidir!")
    private int userId;

    @Column(name = "warehouse_id")
    @Min(value = 1, message = "Geçerli bir depo/lokasyon seçilmelidir!")
    private int warehouseId;

    @Column(name = "quantity")
    @Min(value = 1, message = "Zimmet veya iade miktarı en az 1 adet olmalıdır!") // 🚨 Negatif/Sıfır miktarı engeller
    private int quantity;

    @NotBlank(message = "Hareket türü (ZIMMET/IADE) boş bırakılamaz!")
    @Column(name = "movement_type")
    private String movementType; // ZIMMET veya IADE

    @NotBlank(message = "İşlem durumu boş bırakılamaz!")
    @Column(name = "status")
    private String status; // PENDING, APPROVED, REJECTED
}