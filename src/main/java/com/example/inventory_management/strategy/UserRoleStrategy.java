package com.example.inventory_management.strategy;

public interface UserRoleStrategy {
    boolean isApplicable(int roleId);
    String getRedirectPath();
}