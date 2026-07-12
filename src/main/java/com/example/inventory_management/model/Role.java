package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // 🚨 Hafta 13: Kirli veriyi engelleme kalkanı
import jakarta.validation.constraints.Size;
import lombok.Data; // 🚨 Hafta 14: Boilerplate kod azaltımı ve Clean Code zırhı
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Data // Getter, Setter, toString metotlarını arka planda otomatik üretir
@NoArgsConstructor // Parametresiz boş constructor üretir
@AllArgsConstructor // Tüm alanları içeren kurumsal constructor üretir
@EqualsAndHashCode(callSuper = true) // Üst sınıf olan BaseEntity alanlarını da mimariye dahil eder
public class Role extends BaseEntity {

    // 🚨 VALIDATION & MAPPING: MySQL şemasındaki 'role_name' kolonuna bağlandı ve zırhlandırıldı
    @NotBlank(message = "Rol adı alanı boş bırakılamaz!")
    @Size(min = 3, max = 20, message = "Rol adı en az 3, en fazla 20 karakter olmalıdır!")
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
}