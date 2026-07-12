package com.example.inventory_management.repository;

import com.example.inventory_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Kurumsal altyapı Spring Data JPA tarafından otomatik yönetilir
}