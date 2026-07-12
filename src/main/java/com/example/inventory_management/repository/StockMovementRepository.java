package com.example.inventory_management.repository;

import com.example.inventory_management.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM StockMovement s WHERE s.productId = :productId")
    void deleteByProductId(int productId); // Ürüne bağlı tüm zimmet geçmişini temizleyen stratejik metot
}