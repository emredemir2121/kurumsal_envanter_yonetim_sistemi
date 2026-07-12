package com.example.inventory_management.controller;

import com.example.inventory_management.dto.AssignmentDTO;
import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.service.AssignmentService;
import com.example.inventory_management.service.UserService;
import com.example.inventory_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StockMovementController {

    private final AssignmentService assignmentService;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public StockMovementController(AssignmentService assignmentService,
                                   UserService userService,
                                   ProductService productService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/assignments")
    public String showAllAssignments(Model model) {
        List<AssignmentDTO> completedMovements = assignmentService.getCompletedAssignmentsForUI();
        model.addAttribute("movements", completedMovements);

        List<UserDTO> eligibleUsers = userService.getAllPersonnelForUI();
        model.addAttribute("users", eligibleUsers);

        model.addAttribute("products", productService.getAllProducts());

        return "assignments";
    }

    @PostMapping("/assignments/assign-request")
    public String requestAssignment(@RequestParam("userId") int userId,
                                    @RequestParam("productId") int productId,
                                    RedirectAttributes redirectAttributes) {
        try {
            assignmentService.createRequestWithStatus(userId, productId, "ZIMMET", "PENDING_BY_ADMIN");
            redirectAttributes.addFlashAttribute("successMessage", "Zimmet atama talebi başarıyla oluşturuldu, onay havuzuna gönderildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Talep oluşturulurken hata: " + e.getMessage());
        }
        return "redirect:/assignments";
    }

    @PostMapping("/assignments/return-request")
    public String requestReturn(@RequestParam("userId") int userId,
                                @RequestParam("productId") int productId,
                                RedirectAttributes redirectAttributes) {
        try {
            assignmentService.createRequestWithStatus(userId, productId, "IADE", "PENDING_BY_ADMIN");
            redirectAttributes.addFlashAttribute("successMessage", "Cihaz iade talebi başarıyla oluşturuldu, onay havuzuna gönderildi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "İade talebi oluşturulurken hata: " + e.getMessage());
        }
        return "redirect:/assignments";
    }

    // 🚨 REVIZE: Admin panelindeki kullanıcı envanter listesi süzgeci esnetildi (APPROV yapıldı)
    @GetMapping("/assignments/user-products/{userId}")
    @ResponseBody
    public List<Product> getActiveProductsForUser(@PathVariable("userId") int userId) {
        List<Product> allProducts = productService.getAllProducts();

        // 🚨 BURASI GÜNCELLENDİ: "APPROVED" yerine "APPROV" köküne bakarak tüm onay türevlerini yakalıyoruz
        List<StockMovement> userMovements = assignmentService.getZimmetListesi().stream()
                .filter(m -> m.getUserId() == userId && m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                .collect(Collectors.toList());

        Map<Integer, Integer> productBalances = userMovements.stream()
                .collect(Collectors.groupingBy(
                        StockMovement::getProductId,
                        Collectors.summingInt(m -> {
                            if (m.getMovementType() != null && "ZIMMET".equalsIgnoreCase(m.getMovementType().trim())) {
                                return 1;
                            } else if (m.getMovementType() != null && "IADE".equalsIgnoreCase(m.getMovementType().trim())) {
                                return -1;
                            }
                            return 0;
                        })
                ));

        List<Integer> activeProductIds = productBalances.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return allProducts.stream()
                .filter(p -> activeProductIds.contains(p.getId()))
                .collect(Collectors.toList());
    }
    @GetMapping("/requests")
    public String showPendingRequests(Model model) {
        List<AssignmentDTO> pendingRequests = assignmentService.getPendingRequestsForUI();
        model.addAttribute("pendingRequests", pendingRequests);
        return "requests";
    }

    @PostMapping("/requests/approve/{id}")
    public String approveRequest(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            if (assignmentService.approveRequest(id)) {
                redirectAttributes.addFlashAttribute("successMessage", "Talep başarıyla onaylandı ve stok hareket akışına işlendi.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Onaylanacak talep bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Onaylama hatası: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject/{id}")
    public String rejectRequest(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            if (assignmentService.rejectRequest(id)) {
                redirectAttributes.addFlashAttribute("successMessage", "Talep reddedildi, akış geçmişine eklendi.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Reddedilecek talep bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Reddetme hatası: " + e.getMessage());
        }
        return "redirect:/requests";
    }
}