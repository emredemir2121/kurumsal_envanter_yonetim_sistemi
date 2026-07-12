package com.example.inventory_management.repository;

import com.example.inventory_management.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Integer> {
    // Yeni kolon adına (date) göre tersten sıralama metodu
    List<SystemLog> findAllByOrderByDateDesc();
}