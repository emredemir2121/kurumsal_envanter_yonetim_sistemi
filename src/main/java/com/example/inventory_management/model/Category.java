package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // 🚨 Hafta 13: Doğrulama kalkanı
import jakarta.validation.constraints.Size;
import lombok.Data; // 🚨 Hafta 14: Clean Code için Lombok entegrasyonu
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "categories")
@Data // Bütün el yazımı Getter/Setter/toString kalabalığını ortadan kaldırır
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Parametreli constructor üretir
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 🚨 REVIZE & VALIDATION: MySQL görselindeki 'category_name' kolonuna bağlandı ve zırhlandırıldı
    @NotBlank(message = "Kategori adı boş bırakılamaz!")
    @Size(min = 2, max = 50, message = "Kategori adı 2 ile 50 karakter arasında olmalıdır!")
    @Column(name = "category_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}