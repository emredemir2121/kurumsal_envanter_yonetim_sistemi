package com.example.inventory_management.service;

import com.example.inventory_management.model.SystemLog;
import com.example.inventory_management.repository.SystemLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogServiceTest {

    @Mock
    private SystemLogRepository systemLogRepository; // Sahte log tablomuz (Mock)

    @InjectMocks
    private LogService logService; // Test edilecek asıl servis sınıfı

    @BeforeEach
    void setUp() {
        // Her testten önce düzeneği hazırlar
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLog_Success() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        String expectedLevel = "INFO";
        String expectedSource = "Envanter";
        String expectedMessage = "Yeni ürün başarıyla eklendi.";

        // 🚨 KURUMSAL TEKNİK (ArgumentCaptor): Servisin içeride gizlice 'new' anahtar kelimesiyle
        // ürettiği SystemLog nesnesini yakalayıp içindeki verileri kontrol etmemizi sağlar.
        ArgumentCaptor<SystemLog> logCaptor = ArgumentCaptor.forClass(SystemLog.class);

        // =============== 2. METODU TETİKLE (Act) ===============
        logService.log(expectedLevel, expectedSource, expectedMessage);

        // =============== 3. DOĞRULAMA (Assert) ===============
        // Kural A: Veritabanına kaydetme metodunun tam 1 kere tetiklendiğini doğrula ve nesneyi yakala
        verify(systemLogRepository, times(1)).save(logCaptor.capture());

        // Kural B: Yakalanan log nesnesinin içindeki verilerin doğruluğunu kontrol et
        SystemLog capturedLog = logCaptor.getValue();
        assertNotNull(capturedLog, "Oluşturulan log nesnesi null olmamalı!");
        assertEquals(expectedLevel, capturedLog.getLevel(), "Log seviyesi eşleşmeli!");
        assertEquals(expectedSource, capturedLog.getSource(), "Log kaynağı eşleşmeli!");
        assertEquals(expectedMessage, capturedLog.getMessage(), "Log mesajı eşleşmeli!");
        assertNotNull(capturedLog.getDate(), "Log tarihi sistem tarafından otomatik atanmış olmalı!");
    }

    @Test
    void testLog_ExceptionHandled() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        // 🚨 Negatif Senaryo: Veritabanı o an çökerse veya bir hata fırlatırsa ne olacak?
        // any() kullanarak save metoduna ne gelirse gelsin bir RuntimeException fırlat diyoruz.
        when(systemLogRepository.save(any(SystemLog.class))).thenThrow(new RuntimeException("Veritabanı bağlantı hatası"));

        // =============== 2. METODU TETİKLE VE DOĞRULA (Act & Assert) ===============
        // İş Kuralı: Metodun içinde try-catch olduğu için bu hata yukarı fırlamamalı,
        // yani bu metot çağrıldığında uygulama ÇÖKMEMELİ, hata fırlatmadan (doesNotThrow) pürüzsüz bitmeli.
        assertDoesNotThrow(() -> logService.log("ERROR", "Sistem", "Kritik Hata Denemesi"),
                "Log servisi içerisindeki catch bloğu hatayı yutmalı ve uygulamayı patlatmamalıdır!");

        // Kurumsal Kontrol: Hata fırlatılsa bile save metoduna girilmeye çalışıldığını doğrula
        verify(systemLogRepository, times(1)).save(any(SystemLog.class));
    }
}