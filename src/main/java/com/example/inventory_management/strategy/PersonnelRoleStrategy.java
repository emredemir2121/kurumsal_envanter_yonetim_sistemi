package com.example.inventory_management.strategy;

import org.springframework.stereotype.Component;

@Component
public class PersonnelRoleStrategy implements UserRoleStrategy {
    @Override
    public boolean isApplicable(int roleId) {
        return roleId == 2; // Normal Çalışan (Personel) Rolü
    }

    @Override
    public String getRedirectPath() {
        return "redirect:/user/dashboard";
    }
}