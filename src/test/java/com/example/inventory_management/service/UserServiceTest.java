package com.example.inventory_management.service;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Sahte veritabanı tablosu

    @Mock
    private LogService logService; // 🚨 HATAYI ÇÖZEN EKLEME: Sahte log servisi

    @Mock
    private PasswordEncoder passwordEncoder; // 🚨 HATAYI ÇÖZEN EKLEME: Sahte şifreleyici servisi

    @InjectMocks
    private UserService userService; // Test edilecek asıl servis sınıfı

    @BeforeEach
    void setUp() {
        // Mockito kalkanını devreye alır ve yukarıdaki tüm @Mock nesnelerini hazır eder
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPersonnel_ShouldHashPasswordAndSave() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        User newUser = new User();
        newUser.setUsername("yeniKullanici");
        newUser.setPassword("duzMetinSifre");
        newUser.setFirstName("Ahmet");
        newUser.setLastName("Yılmaz");

        // Şifreleyici çağrıldığında sahte bir hashlenmiş şifre dönmesini söylüyoruz
        when(passwordEncoder.encode("duzMetinSifre")).thenReturn("$2a$10$sahteHashlenmisSifre");
        when(userRepository.save(newUser)).thenReturn(newUser);
        doNothing().when(logService).log(anyString(), anyString(), anyString());

        // =============== 2. METODU TETİKLE (Act) ===============
        userService.addPersonnel(newUser);

        // =============== 3. DOĞRULAMA (Assert) ===============
        // save metodu tam 1 kere tetiklendi mi?
        verify(userRepository, times(1)).save(newUser);

        // logService.log metodu kurumsal parametrelerle tam 1 kere tetiklendi mi?
        verify(logService, times(1)).log(eq("INFO"), eq("Personel Yönetimi"), anyString());
    }

    @Test
    void testFindUserByUsername_Success() {
        // 1. Veri Hazırlığı (Arrange)
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("cememre");
        mockUser.setFirstName("Cem Emre");
        mockUser.setLastName("Demir");

        when(userRepository.findByUsername("cememre")).thenReturn(Optional.of(mockUser));

        // 2. Metodu Çalıştır (Act)
        Optional<User> foundUserOpt = userRepository.findByUsername("cememre");

        // 3. Sonuçları Doğrula (Assert)
        assertTrue(foundUserOpt.isPresent(), "Kullanıcı nesnesi mevcut olmalı!");
        assertEquals("Cem Emre", foundUserOpt.get().getFirstName(), "İsimler birbiriyle eşleşmeli!");
        assertEquals("Demir", foundUserOpt.get().getLastName(), "Soyisimler birbiriyle eşleşmeli!");

        verify(userRepository, times(1)).findByUsername("cememre");
    }

    @Test
    void testFindUserByUsername_Failure() {
        // 1. Veri Hazırlığı (Arrange)
        when(userRepository.findByUsername("gecersizKullanici")).thenReturn(Optional.empty());

        // 2. Metodu Çalıştır (Act)
        Optional<User> foundUserOpt = userRepository.findByUsername("gecersizKullanici");

        // 3. Sonuçları Doğrula (Assert)
        assertFalse(foundUserOpt.isPresent(), "Geçersiz kullanıcı adı durumunda sonuç boş dönmeli!");

        verify(userRepository, times(1)).findByUsername("gecersizKullanici");
    }
}