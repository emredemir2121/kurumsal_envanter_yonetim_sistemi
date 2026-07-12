package com.example.inventory_management.repository;

import com.example.inventory_management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Tüm CRUD (Ekle, Sil, Güncelle, Listele) metotları JpaRepository ile otomatik hazır gelir!
}