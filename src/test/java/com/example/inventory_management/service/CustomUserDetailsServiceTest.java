package com.example.inventory_management.service;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository; // Sahte kullanıcı tablosu (Mock)

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService; // Test edilecek Spring Security servisimiz

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        User mockUser = new User();
        mockUser.setUsername("cememre");
        mockUser.setPassword("12345");
        // 🚨 REVIZE: Setter uyuşmazlığı yaratabilecek katı setRole satırı tamamen kaldırıldı.
        // Mockito doğrudan findByUsername metodunun çalışmasını ölçecek.

        when(userRepository.findByUsername("cememre")).thenReturn(Optional.of(mockUser));

        // =============== 2. METODU TETİKLE (Act) ===============
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("cememre");

        // =============== 3. DOĞRULAMA (Assert) ===============
        assertNotNull(userDetails, "UserDetails nesnesi null dönmemeli!");
        assertEquals("cememre", userDetails.getUsername(), "Kullanıcı adı eşleşmeli!");
        assertEquals("12345", userDetails.getPassword(), "Şifre eşleşmeli!");

        verify(userRepository, times(1)).findByUsername("cememre");
    }

    @Test
    void testLoadUserByUsername_UserNotFound_ShouldThrowException() {
        // =============== 1. SENARYO HAZIRLIĞI (Arrange) ===============
        when(userRepository.findByUsername("olmayanKullanici")).thenReturn(Optional.empty());

        // =============== 2 & 3. TETİKLE VE DOĞRULA (Act & Assert) ===============
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("olmayanKullanici");
        }, "Kullanıcı bulunamadığında UsernameNotFoundException fırlatılmalıdır!");

        verify(userRepository, times(1)).findByUsername("olmayanKullanici");
    }
}