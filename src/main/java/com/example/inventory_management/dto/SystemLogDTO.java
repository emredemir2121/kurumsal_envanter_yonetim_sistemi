package com.example.inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemLogDTO {
    private int id;
    private String zamanStr;
    private String seviye;
    private String kaynak;
    private String mesaj;
}

