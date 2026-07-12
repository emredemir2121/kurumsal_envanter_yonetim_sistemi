package com.example.inventory_management.service;

import com.example.inventory_management.dto.AssignmentDTO;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.model.User;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.repository.StockMovementRepository;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final LogService logService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public AssignmentService(StockMovementRepository stockMovementRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository,
                             ProductService productService,
                             LogService logService) {
        this.stockMovementRepository = stockMovementRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.logService = logService;
    }

    public List<StockMovement> getZimmetListesi() {
        return stockMovementRepository.findAll();
    }

    // 🚨 REVIZE: Onaylanmış/Reddedilmiş veya admin tarafından tamamlanmış kayıtları DTO listesi olarak UI'a döner
    public List<AssignmentDTO> getCompletedAssignmentsForUI() {
        List<User> allUsers = userRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getStatus() != null &&
                        !"PENDING".equalsIgnoreCase(m.getStatus().trim()) &&
                        !"PENDING_BY_ADMIN".equalsIgnoreCase(m.getStatus().trim()))
                .map(m -> convertToDTO(m, allUsers, allProducts))
                .collect(Collectors.toList());
    }

    // 🚨 REVIZE: Hem normal personelden hem de adminden gelen onay bekleyen talepleri DTO listesi olarak UI'a döner
    public List<AssignmentDTO> getPendingRequestsForUI() {
        List<User> allUsers = userRepository.findAll();
        List<Product> allProducts = productRepository.findAll();

        return stockMovementRepository.findAll().stream()
                .filter(m -> m.getStatus() != null &&
                        ("PENDING".equalsIgnoreCase(m.getStatus().trim()) || "PENDING_BY_ADMIN".equalsIgnoreCase(m.getStatus().trim())))
                .map(m -> convertToDTO(m, allUsers, allProducts))
                .collect(Collectors.toList());
    }

    // Personelden gelen standart talep motoru (Bozulmadı)
    public void createRequest(int userId, int productId, String movementType) {
        createRequestWithStatus(userId, productId, movementType, "PENDING");
    }

    // 🚨 YENİ: Admin kökenli talepleri özel durum koduyla mühürleyen Overloaded metot
    public void createRequestWithStatus(int userId, int productId, String movementType, String customStatus) {
        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setUserId(userId);
        movement.setWarehouseId(1);
        movement.setQuantity(1);
        movement.setMovementType(movementType.toUpperCase().trim());
        movement.setStatus(customStatus);
        movement.setCreatedAt(LocalDateTime.now());

        stockMovementRepository.save(movement);
        logService.log("INFO", "Zimmet", "Ürün ID: " + productId + " için " + movementType + " talebi oluşturuldu. Durum: " + customStatus);
    }

    // 🚨 REVIZE: Onay anında admin mühürlü isteklerin kökenini koruyarak APPROVED_BY_ADMIN yapar
    public boolean approveRequest(int id) {
        StockMovement movement = stockMovementRepository.findById(id).orElse(null);
        if (movement != null) {
            if ("PENDING_BY_ADMIN".equalsIgnoreCase(movement.getStatus().trim())) {
                movement.setStatus("APPROVED_BY_ADMIN");
            } else {
                movement.setStatus("APPROVED");
            }

            movement.setUpdatedAt(LocalDateTime.now());
            stockMovementRepository.save(movement);

            if (movement.getMovementType() != null && "ZIMMET".equalsIgnoreCase(movement.getMovementType().trim())) {
                productService.decreaseStock(movement.getProductId());
            } else if (movement.getMovementType() != null && "IADE".equalsIgnoreCase(movement.getMovementType().trim())) {
                productService.increaseStock(movement.getProductId());
            }

            logService.log("INFO", "Zimmet Yönetimi", "Talep ID: " + id + " ONAYLANDI. Durum: " + movement.getStatus());
            return true;
        }
        return false;
    }

    public boolean rejectRequest(int id) {
        StockMovement movement = stockMovementRepository.findById(id).orElse(null);
        if (movement != null) {
            movement.setStatus("REJECTED");
            movement.setUpdatedAt(LocalDateTime.now());
            stockMovementRepository.save(movement);
            logService.log("WARNING", "Zimmet Yönetimi", "Talep ID: " + id + " REDDEDİLDİ.");
            return true;
        }
        return false;
    }

    private AssignmentDTO convertToDTO(StockMovement m, List<User> allUsers, List<Product> allProducts) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(m.getId());
        dto.setUserId(m.getUserId());
        dto.setProductId(m.getProductId());
        dto.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt().format(formatter) : "");
        dto.setMovementType(m.getMovementType());
        dto.setStatus(m.getStatus());

        User u = allUsers.stream().filter(user -> user.getId() == m.getUserId()).findFirst().orElse(null);
        if (u != null) {
            dto.setPersonelInfo("[ID: " + m.getUserId() + "] " + u.getFirstName() + " " + u.getLastName());
        } else {
            dto.setPersonelInfo("[ID: " + m.getUserId() + "] Bilinmeyen Personel");
        }

        Product p = allProducts.stream().filter(prod -> prod.getId() == m.getProductId()).findFirst().orElse(null);
        if (p != null) {
            dto.setUrunInfo("[ID: " + m.getProductId() + "] " + p.getBrand() + " " + p.getModelName());
        } else {
            dto.setUrunInfo("[ID: " + m.getProductId() + "] Bilinmeyen Ürün");
        }

        return dto;
    }


}