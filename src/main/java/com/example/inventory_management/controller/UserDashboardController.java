package com.example.inventory_management.controller;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.dto.ProductDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.service.UserService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.AssignmentService;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    private final UserService userService;
    private final ProductService productService;
    private final AssignmentService assignmentService;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserDashboardController(UserService userService,
                                   ProductService productService,
                                   AssignmentService assignmentService,
                                   StockMovementRepository stockMovementRepository,
                                   UserRepository userRepository) {
        this.userService = userService;
        this.productService = productService;
        this.assignmentService = assignmentService;
        this.stockMovementRepository = stockMovementRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showUserDashboard(Principal principal, Model model) {
        String username = principal.getName();
        User activeUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        UserDTO userDTO = userService.convertToDTO(activeUser);

        List<StockMovement> myMovements = assignmentService.getZimmetListesi().stream()
                .filter(m -> m.getUserId() == userDTO.getId())
                .collect(Collectors.toList());

        // 🚨 REVIZE: Durum kontrolü büyük/küçük harf uyuşmazlığını engellemek için "APPROV" köküne göre esnetildi
        Map<Integer, Integer> productBalances = myMovements.stream()
                .filter(m -> m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                .collect(Collectors.groupingBy(
                        StockMovement::getProductId,
                        Collectors.summingInt(m -> "ZIMMET".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") ? 1 : -1)
                ));

        List<Integer> activeProductIds = productBalances.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<ProductDTO> myActiveProducts = productService.getAllProductsForUI().stream()
                .filter(p -> activeProductIds.contains(p.getId()))
                .map(p -> {
                    String lastApprovedStatus = myMovements.stream()
                            .filter(m -> m.getProductId() == p.getId() && m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                            .map(StockMovement::getStatus)
                            .reduce((first, second) -> second)
                            .orElse("APPROVED");

                    p.setStockStatus(lastApprovedStatus);
                    return p;
                })
                .collect(Collectors.toList());

        List<ProductDTO> availableProducts = productService.getAllProductsForUI().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .collect(Collectors.toList());

        List<StockMovement> approvedReturns = myMovements.stream()
                .filter(m -> "IADE".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") && m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                .collect(Collectors.toList());

        model.addAttribute("user", userDTO);
        model.addAttribute("myActiveProducts", myActiveProducts);
        model.addAttribute("availableProducts", availableProducts);
        model.addAttribute("approvedReturns", approvedReturns);
        model.addAttribute("productService", productService);

        return "user_dashboard";
    }

    @GetMapping("/request")
    public String showRequestPage(Principal principal, Model model) {
        String username = principal.getName();
        User activeUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        UserDTO userDTO = userService.convertToDTO(activeUser);
        List<ProductDTO> availableProducts = productService.getAllProductsForUI().stream()
                .filter(p -> p.getStockQuantity() > 0)
                .collect(Collectors.toList());

        model.addAttribute("user", userDTO);
        model.addAttribute("availableProducts", availableProducts);
        return "user_request";
    }

    @GetMapping("/history")
    public String showUserHistory(Principal principal, Model model) {
        String username = principal.getName();
        User activeUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        UserDTO userDTO = userService.convertToDTO(activeUser);
        List<StockMovement> myMovements = assignmentService.getZimmetListesi().stream()
                .filter(m -> m.getUserId() == userDTO.getId())
                .collect(Collectors.toList());

        List<StockMovement> approvedReturns = myMovements.stream()
                .filter(m -> "IADE".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") && m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                .collect(Collectors.toList());

        model.addAttribute("user", userDTO);
        model.addAttribute("approvedReturns", approvedReturns);
        model.addAttribute("productService", productService);

        return "user_history";
    }

    @PostMapping("/request-item")
    public String sendAssignmentRequest(@RequestParam("productId") int productId,
                                        @RequestParam(value = "movementType", defaultValue = "ZIMMET") String movementType,
                                        Principal principal,
                                        RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            User activeUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

            ProductDTO product = productService.getAllProductsForUI().stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst().orElse(null);

            if (product != null) {
                StockMovement requestMovement = new StockMovement();
                requestMovement.setUserId(activeUser.getId());
                requestMovement.setProductId(productId);
                requestMovement.setWarehouseId(1);
                requestMovement.setQuantity(1);
                requestMovement.setMovementType(movementType);
                requestMovement.setStatus("PENDING");
                requestMovement.setCreatedAt(LocalDateTime.now());

                stockMovementRepository.save(requestMovement);

                String typeStr = "ZIMMET".equalsIgnoreCase(movementType) ? "Zimmet" : "İade";
                redirectAttributes.addFlashAttribute("successMessage",
                        product.getBrand() + " " + product.getModelName() + " için " + typeStr + " talebiniz başarıyla iletildi.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Talep oluşturulurken hata: " + e.getMessage());
        }

        if ("IADE".equalsIgnoreCase(movementType)) {
            return "redirect:/user/dashboard";
        } else {
            return "redirect:/user/request";
        }
    }
}