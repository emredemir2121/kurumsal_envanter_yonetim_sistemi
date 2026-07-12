package com.example.inventory_management.service;

import com.example.inventory_management.dto.ProductDTO;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.Category;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.CategoryRepository;
import com.example.inventory_management.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final LogService logService;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          StockMovementRepository stockMovementRepository,
                          LogService logService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.logService = logService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 🚨 REVIZE: UI Katmanı için veritabanı nesnelerini DTO'ya dönüştürerek servis ediyoruz (Hafta 14)
    public List<ProductDTO> getAllProductsForUI() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    // Zimmet Akışı Mantığı: Değiştirilmeden Korundu
    public void decreaseStock(int productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null && product.getStockQuantity() > 0) {
            product.setStockQuantity(product.getStockQuantity() - 1);
            productRepository.save(product);
        }
    }

    // İade Alma Mantığı: Değiştirilmeden Korundu
    public void increaseStock(int productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() + 1);
            productRepository.save(product);
        }
    }

    public boolean deleteProduct(int id) {
        return removeProduct(id);
    }

    public boolean removeProduct(int id) {
        try {
            stockMovementRepository.deleteByProductId(id);
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                logService.log("WARNING", "Envanter", "ID: " + id + " olan ürün ve tüm bağlı hareketleri silindi.");
                return true;
            }
            return false;
        } catch (Exception e) {
            logService.log("ERROR", "Envanter", "Ürün silinirken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    public void addProduct(Product product, int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        product.setCategory(category);
        productRepository.save(product);
        logService.log("INFO", "Envanter", "Yeni ürün eklendi: " + product.getBrand() + " " + product.getModelName());
    }

    public void updateProduct(Product product, int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        product.setCategory(category);
        productRepository.save(product);
        logService.log("INFO", "Envanter", "Ürün güncellendi. ID: " + product.getId());
    }

    public double getTotalInventoryValue() {
        return productRepository.findAll().stream()
                .mapToDouble(p -> p.getPrice() * p.getStockQuantity())
                .sum();
    }

    public int getTotalStockCount() {
        return productRepository.findAll().stream()
                .mapToInt(Product::getStockQuantity)
                .sum();
    }

    // 🚨 REVIZE: Ürün tipine göre dinamik kritik stok algoritması (Limitler 15-30 bandına yükseltildi)
    public int getCriticalStockLimit(String productType) {
        if (productType == null) return 15; // Varsayılan genel limitimiz artık 15
        switch (productType.toUpperCase().trim()) {
            case "LAPTOP":
            case "PHONE":
                return 15; // Değerli/Hızlı dağıtılan cihazlar için kritik limit: 15

            case "MONITOR":
            case "ROUTER":
            case "SWITCH":
                return 20; // Ağ ve çevre birimleri için kritik limit: 20

            case "MOUSE":
            case "KEYBOARD":
                return 30; // Sarf malzemeleri (Çok hızlı tükenenler) için kritik limit: 30

            default: return 15;
        }
    }

    // 🚨 HELPER METHOD: Refactoring Mapping Logic (Entity -> DTO)
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setBrand(product.getBrand());
        dto.setModelName(product.getModelName());
        dto.setProductType(product.getProductType());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSpecifications(product.getSpecifications() != null ? product.getSpecifications() : "-");
        dto.setFormattedPrice(String.format("%.2f TL", product.getPrice()));

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        } else {
            dto.setCategoryId(1);
            dto.setCategoryName("Genel");
        }

        // Dinamik kritik eşik kontrolü burada işletiliyor
        int limit = getCriticalStockLimit(product.getProductType());
        if (product.getStockQuantity() <= limit) {
            dto.setStockStatus("KRİTİK STOK");
        } else {
            dto.setStockStatus("STOKTA VAR");
        }
        return dto;
    }
}