package com.example.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDTO {
    private int id;
    private int userId;
    private int productId;
    private String personelInfo; // [ID: X] Ad Soyad
    private String urunInfo;     // [ID: Y] Marka Model
    private String createdAt;
    private String movementType; // ZIMMET veya IADE
    private String status;       // PENDING, APPROVED, REJECTED
}