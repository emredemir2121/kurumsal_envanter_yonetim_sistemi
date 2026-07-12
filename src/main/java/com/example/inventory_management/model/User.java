package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // 🚨 Hafta 13: JSR-380 doğrulama kalkanları
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data // 🚨 Hafta 14: Getter, Setter, toString metotlarını otomatik bağlar (Clean Code)
@NoArgsConstructor // Boş constructor üretir
@AllArgsConstructor // Tam parametreli constructor üretir
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "E-posta adresi boş bırakılamaz!")
    @Email(message = "Lütfen kurumsal standartlara uygun, geçerli bir e-posta adresi giriniz!") // 🚨 Ders Notu: @Email kontrolü
    private String username;

    @NotBlank(message = "Şifre alanı boş bırakılamaz!")
    @Size(min = 6, message = "Güvenlik nedeniyle şifre en az 6 karakter olmalıdır!")
    private String password;

    @Column(name = "role_id")
    @NotNull(message = "Rol ID belirtilmesi zorunludur!")
    private int roleId;

    @NotBlank(message = "İsim alanı boş bırakılamaz!")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Soyisim alanı boş bırakılamaz!")
    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Telefon numarası boş bırakılamaz!")
    @Column(name = "phone_number")
    private String phone;

    @Column(name = "room_number")
    private String roomNumber;

    @NotBlank(message = "Departman bilgisi boş bırakılamaz!")
    private String department;
}