package com.example.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierRequestDTO {
    private int id;
    private String supplierName;
    private String productName;
    private int quantity;
    private String createdAt;
}