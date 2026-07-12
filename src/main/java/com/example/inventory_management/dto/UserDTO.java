package com.example.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;
    private String username;
    private int roleId;
    private String firstName;
    private String lastName;
    private String fullName; // Arayüzde kolay gösterim için birleştirilmiş isim
    private String phone;
    private String roomNumber;
    private String department;
}