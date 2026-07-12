package com.example.inventory_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data; // 🚨 Hafta 14: Boilerplate kod azaltımı için entegre edildi
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@MappedSuperclass
@Data // 🚨 Tüm getter, setter, toString ve equals metotlarını arkada otomatik üretir
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Tüm alanları içeren parametreli constructor üretir
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}