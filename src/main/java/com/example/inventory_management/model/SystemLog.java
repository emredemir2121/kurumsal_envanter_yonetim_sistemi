package com.example.inventory_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // 🚨 Hafta 13: Validation kalkanı
import jakarta.validation.constraints.NotNull;
import lombok.Data; // 🚨 Hafta 14: Boilerplate kod arındırma zırhı
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data // Getter, Setter, toString metotlarını otomatik yönetir
@NoArgsConstructor // Boş constructor
@AllArgsConstructor // Dolu constructor
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 🚨 REVIZE: MySQL görselinde içi dolu olan 'created_at' kolonuna bağlanarak veri kaybı önlendi
    @NotNull(message = "Log zaman damgası boş olamaz!")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime date;

    // 🚨 REVIZE: MySQL görselinde içi dolu olan 'log_level' kolonuna bağlanarak çökme pürüzü giderildi
    @NotBlank(message = "Log seviyesi belirtilmelidir!")
    @Column(name = "log_level")
    private String level;

    @NotBlank(message = "Log kaynağı boş bırakılamaz!")
    @Column(name = "source")
    private String source;

    @NotBlank(message = "Log mesaj detayı boş bırakılamaz!")
    @Column(name = "message", length = 1000)
    private String message;
}