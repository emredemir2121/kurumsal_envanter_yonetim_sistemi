package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email; // 🚨 Hafta 13: E-posta format denetimi için
import jakarta.validation.constraints.NotBlank; // 🚨 Hafta 13: Boş veri engelleme kalkanı
import jakarta.validation.constraints.Size;
import lombok.Data; // 🚨 Hafta 14: Boilerplate kod arındırma zırhı
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "suppliers")
@Data // El yazımı tüm Getter/Setter/toString kalabalığını temizler
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Tam parametreli kurumsal constructor üretir
@EqualsAndHashCode(callSuper = true) // Üst sınıf olan BaseEntity alanlarını da mimariye dahil eder
public class Supplier extends BaseEntity { // 🚨 REVIZE: created_at ve updated_at ortak ata sınıfa devredildi

    // 🚨 VALIDATION: Şirket adı boş bırakılamaz ve veritabanı kolonuyla tam eşleşir
    @NotBlank(message = "Tedarikçi şirket adı boş bırakılamaz!")
    @Size(min = 2, max = 100, message = "Şirket adı 2 ile 100 karakter arasında olmalıdır!")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Vergi numarası alanı boş bırakılamaz!")
    @Column(name = "tax_number")
    private String taxNumber;

    @NotBlank(message = "Yetkili kişi bilgisi boş bırakılamaz!")
    @Column(name = "contact_person")
    private String contactPerson;

    // 🚨 VALIDATION: Ders notundaki @Email standardı uygulandı
    @NotBlank(message = "E-posta adresi boş bırakılamaz!")
    @Email(message = "Lütfen geçerli bir kurumsal e-posta formatı giriniz!")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Firma adresi boş bırakılamaz!")
    @Column(name = "address", length = 500)
    private String address;
}