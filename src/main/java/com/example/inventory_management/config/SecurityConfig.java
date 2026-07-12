package com.example.inventory_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 🚨 Spring Security koruma kalkanını devreye alır
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Sayfa Bazlı Yetkilendirme Kuralları (Authorization)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/css/**", "/js/**", "/images/**").permitAll() // Herkese açık alanlar
                        .requestMatchers("/user/**").hasRole("USER") // Personel sayfaları koruması
                        .requestMatchers("/dashboard", "/personnel/**", "/inventory/**", "/suppliers/**", "/assignments/**", "/requests/**", "/logs/**").hasRole("ADMIN") // Admin sayfaları koruması
                        .anyRequest().authenticated() // Kalan tüm isteklere giriş şartı koy
                )
                // 2. Kurumsal Giriş Sayfası Ayarları
                .formLogin(form -> form
                        .loginPage("/login") // Bizim kendi login sayfamızın adresi
                        .loginProcessingUrl("/login") // Spring Security'nin form verilerini yakalayacağı arka plan post adresi
                        .usernameParameter("username") // Formdaki kullanıcı adı alanının "name" özniteliği
                        .passwordParameter("password") // Formdaki şifre alanının "name" özniteliği
                        // 🚨 REVIZE: Sabit yönlendirme yerine dinamik rol kontrolü yapan başarı yönlendiricisi eklendi
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities();
                            boolean isAdmin = roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            if (isAdmin) {
                                response.sendRedirect("/dashboard");
                            } else {
                                response.sendRedirect("/user/dashboard");
                            }
                        })
                        .permitAll()
                )
                // 3. Güvenli Çıkış Ayarları
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) // Oturumu temizle
                        .clearAuthentication(true)  // Kimlik bilgilerini temizle
                        .permitAll()
                );

        return http.build();
    }









    // 🚨 REVIZE: Veritabanındaki düz metin şifrelerin doğrudan çalışabilmesi için NoOp kurgulandı
    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }
}