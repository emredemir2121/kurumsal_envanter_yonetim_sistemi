package com.example.inventory_management.repository;

import com.example.inventory_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Giriş (Login) işlemlerinde arayüzden gelen kullanıcı adını sorgulamak için özel metot:
    Optional<User> findByUsername(String username);
}