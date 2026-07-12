package com.example.inventory_management.service;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LogService logService;
    private final PasswordEncoder passwordEncoder; // 🚨 YENİ: BCrypt Şifreleyici Enjekte Edildi

    @Autowired
    public UserService(UserRepository userRepository, LogService logService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.logService = logService;
        this.passwordEncoder = passwordEncoder;
    }

    // 🚨 REVIZE: Klasik login metodu devredışı bırakıldı, kimlik doğrulamayı artık CustomUserDetailsService yürütüyor.
    public User login(String username, String password) {
        throw new UnsupportedOperationException("Giriş işlemleri Spring Security katmanına devredilmiştir.");
    }

    public List<User> getAllSystemUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllPersonnelForUI() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoleId() == 2)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 🚨 REVIZE: Yeni eklenen personellerin şifresi artık veritabanına BCrypt ile hash'lenerek kaydedilir
    public void addPersonnel(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        }
        userRepository.save(user);
        logService.log("INFO", "Personel Yönetimi", "Yeni personel eklendi ve şifresi güvenli biçimde hash'lendi: " + user.getFirstName() + " " + user.getLastName());
    }

    public void removeUser(int id) {
        userRepository.deleteById(id);
        logService.log("WARNING", "Personel Yönetimi", "ID: " + id + " olan personel sistemden silindi.");
    }

    public UserDTO convertToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRoleId(user.getRoleId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFirstName() + " " + user.getLastName());
        dto.setPhone(user.getPhone() != null ? user.getPhone() : "-");
        dto.setRoomNumber(user.getRoomNumber() != null ? user.getRoomNumber() : "-");
        dto.setDepartment(user.getDepartment() != null ? user.getDepartment() : "-");
        return dto;
    }
}