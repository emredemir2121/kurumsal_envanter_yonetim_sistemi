package com.example.inventory_management.controller;

import com.example.inventory_management.dto.UserDTO;
import com.example.inventory_management.model.User;
import com.example.inventory_management.model.StockMovement;
import com.example.inventory_management.service.UserService;
import com.example.inventory_management.service.AssignmentService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.repository.StockMovementRepository;
import com.example.inventory_management.service.LogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PersonnelController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final ProductService productService;
    private final StockMovementRepository stockMovementRepository;
    private final LogService logService;

    @Autowired
    public PersonnelController(UserService userService,
                               AssignmentService assignmentService,
                               ProductService productService,
                               StockMovementRepository stockMovementRepository,
                               LogService logService) {
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.productService = productService;
        this.stockMovementRepository = stockMovementRepository;
        this.logService = logService;
    }

    @GetMapping("/personnel")
    public String listPersonnel(@RequestParam(required = false) Integer selectedUserId, Model model) {
        List<UserDTO> personnelList = userService.getAllPersonnelForUI();
        model.addAttribute("personnelList", personnelList);

        if (!model.containsAttribute("personnelForm")) {
            model.addAttribute("personnelForm", new User());
        }

        if (selectedUserId != null) {
            UserDTO selectedUser = personnelList.stream()
                    .filter(u -> u.getId() == selectedUserId)
                    .findFirst().orElse(null);
            model.addAttribute("selectedUser", selectedUser);

            List<StockMovement> allMovements = assignmentService.getZimmetListesi().stream()
                    .filter(m -> m.getUserId() == selectedUserId)
                    .collect(Collectors.toList());

            // 🚨 REVIZE: Durum kontrolü büyük/küçük harf ve Spring Security türevleri uyuşmazlığını engellemek için "APPROV" olarak esnetildi
            Map<Integer, Integer> productBalances = allMovements.stream()
                    .filter(m -> m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                    .collect(Collectors.groupingBy(
                            StockMovement::getProductId,
                            Collectors.summingInt(m -> "ZIMMET".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") ? 1 : -1)
                    ));

            List<Integer> activeProductIds = productBalances.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            model.addAttribute("activeProductIds", activeProductIds);

            // 🚨 REVIZE: Tamamlanmış iadeler listelenirken de "APPROV" filtresi uygulandı
            List<StockMovement> approvedReturns = allMovements.stream()
                    .filter(m -> "IADE".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") && m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                    .collect(Collectors.toList());
            model.addAttribute("approvedReturns", approvedReturns);
            model.addAttribute("productService", productService);
        }

        return "personnel";
    }

    @PostMapping("/personnel/save")
    public String savePersonnel(@Valid @ModelAttribute("personnelForm") User user,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("personnelList", userService.getAllPersonnelForUI());
            model.addAttribute("errorMessage", "Lütfen personel bilgilerindeki doğrulama hatalarını düzeltiniz!");
            return "personnel";
        }

        userService.addPersonnel(user);
        String fullName = user.getFirstName() + " " + user.getLastName();
        redirectAttributes.addFlashAttribute("successMessage", "Yeni personel \"" + fullName + "\" başarıyla eklendi.");
        return "redirect:/personnel";
    }

    @GetMapping("/personnel/delete/{id}")
    public String deletePersonnel(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            List<User> allUsers = userService.getAllSystemUsers();
            User userToDelete = allUsers.stream()
                    .filter(u -> u.getId() == id)
                    .findFirst().orElse(null);

            List<StockMovement> userMovements = assignmentService.getZimmetListesi().stream()
                    .filter(m -> m.getUserId() == id)
                    .collect(Collectors.toList());

            // 🚨 REVIZE: Silme koruması için aktif zimmet bakiyesi hesaplanırken durum kontrolü esnetildi
            Map<Integer, Integer> productBalances = userMovements.stream()
                    .filter(m -> m.getStatus() != null && m.getStatus().toUpperCase().contains("APPROV"))
                    .collect(Collectors.groupingBy(
                            StockMovement::getProductId,
                            Collectors.summingInt(m -> "ZIMMET".equalsIgnoreCase(m.getMovementType() != null ? m.getMovementType().trim() : "") ? 1 : -1)
                    ));

            long activeZimmetCount = productBalances.values().stream().filter(val -> val > 0).count();

            if (activeZimmetCount > 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "HATA: Personel üzerinde aktif zimmetli cihaz bulunduğu için silme işlemi gerçekleştirilemez!");
                return "redirect:/personnel";
            }

            if (!userMovements.isEmpty()) {
                stockMovementRepository.deleteAll(userMovements);
            }

            if (userToDelete != null) {
                userService.removeUser(id);
                String fullName = userToDelete.getFirstName() + " " + userToDelete.getLastName();
                logService.log("WARNING", "Personel", "ID: " + id + " olan " + fullName + " personeli ve bağlı stok geçmişi silindi.");
                redirectAttributes.addFlashAttribute("successMessage", fullName + " ve tüm geçmiş hareket kayıtları başarıyla sistemden silindi.");
            } else {
                userService.removeUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Personel başarıyla sistemden silindi.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Personel silinirken hata oluştu: " + e.getMessage());
        }

        return "redirect:/personnel";
    }
}