package com.example.inventory_management.strategy;

import org.springframework.stereotype.Component;

@Component
public class AdminRoleStrategy implements UserRoleStrategy {
    @Override
    public boolean isApplicable(int roleId) {
        return roleId == 1; // Yönetici (Admin) Rolü
    }

    @Override
    public String getRedirectPath() {
        return "redirect:/dashboard";
    }
}