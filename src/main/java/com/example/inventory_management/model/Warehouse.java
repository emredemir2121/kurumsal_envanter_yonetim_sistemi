package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min; // 🚨 Hafta 13: Kirli veri engelleme kalkanı
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // 🚨 Hafta 14: Boilerplate kod arındırma ve Clean Code zırhı
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "warehouses")
@Data // Tüm el yazımı Getter/Setter/toString kalabalığını arkada otomatik üretir
@NoArgsConstructor // Parametresiz boş constructor üretir
@AllArgsConstructor // Tüm alanları içeren kurumsal constructor üretir
@EqualsAndHashCode(callSuper = true) // Üst sınıf olan BaseEntity alanlarını da kontrol mekanizmalarına dahil eder
public class Warehouse extends BaseEntity {

    // 🚨 VALIDATION: Depo adı boş bırakılamaz ve veritabanı kolonuyla tam eşleşir
    @NotBlank(message = "Depo/Teslimat noktası adı boş bırakılamaz!")
    @Size(min = 2, max = 100, message = "Depo adı 2 ile 100 karakter arasında olmalıdır!")
    @Column(name = "warehouse_name", nullable = false)
    private String warehouseName;

    @NotBlank(message = "Lokasyon / Konum bilgisi boş bırakılamaz!")
    @Column(name = "location")
    private String location;

    // 🚨 VALIDATION: Kapasitenin negatif değer almasını engelliyoruz
    @Min(value = 0, message = "Depo kapasitesi negatif bir değer olamaz!")
    @Column(name = "capacity")
    private int capacity;
}