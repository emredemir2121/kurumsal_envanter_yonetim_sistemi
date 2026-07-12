package com.example.inventory_management.service;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Kullanıcıyı veritabanında ara
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2. Veritabanındaki roleId değerini Spring Security rol biçimine (ROLE_...) dönüştür
        String roleName = (user.getRoleId() == 1) ? "ROLE_ADMIN" : "ROLE_USER";
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // 3. Spring Security'nin anlayacağı UserDetails nesnesini oluştur ve dön
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), //🚨 DİKKAT: Veritabanındaki şifre hash'li olmalı, bir sonraki adımda çözeceğiz
                Collections.singletonList(authority)
        );
    }
}