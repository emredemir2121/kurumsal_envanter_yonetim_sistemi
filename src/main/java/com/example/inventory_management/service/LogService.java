package com.example.inventory_management.service;

import com.example.inventory_management.model.SystemLog;
import com.example.inventory_management.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {

    private final SystemLogRepository systemLogRepository;

    @Autowired
    public LogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    // 🚨 HATA KÖKTEN ÇÖZÜLDÜ: Yeni model field isimlerine göre (level, source, message, date) loglama metodu
    public void log(String level, String source, String message) {
        try {
            SystemLog systemLog = new SystemLog();

            // Buradaki metot çağrıları senin güncel modelindeki (level, source, message, date) alanlarıyla birebir eşitlendi
            systemLog.setLevel(level);
            systemLog.setSource(source);
            systemLog.setMessage(message);
            systemLog.setDate(LocalDateTime.now());

            systemLogRepository.save(systemLog);
        } catch (Exception e) {
            System.err.println("Sistem logu kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
}