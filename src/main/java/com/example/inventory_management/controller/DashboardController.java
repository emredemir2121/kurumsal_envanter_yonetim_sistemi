package com.example.inventory_management.controller;

import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.AssignmentService;
import com.example.inventory_management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final AssignmentService assignmentService;
    private final UserService userService;
    private final UserRepository userRepository; // 🚨 YENİ: Aktif kullanıcıyı veritabanından çekmek için eklendi

    @Autowired
    public DashboardController(ProductService productService,
                               AssignmentService assignmentService,
                               UserService userService,
                               UserRepository userRepository) {
        this.productService = productService;
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        // 🚨 REVIZE: Session yerine güvenli Principal mimarisiyle aktif kullanıcıyı veritabanından buluyoruz
        String username = principal.getName();
        User loggedInUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        String fullName = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
        model.addAttribute("adminName", fullName);

        model.addAttribute("totalStock", productService.getTotalStockCount());
        model.addAttribute("totalValue", productService.getTotalInventoryValue());

        long pendingRequestsCount = assignmentService.getZimmetListesi().stream()
                .filter(m -> "PENDING".equalsIgnoreCase(m.getStatus()))
                .count();
        model.addAttribute("pendingRequests", pendingRequestsCount);

        long personnelCount = userService.getAllSystemUsers().stream()
                .filter(u -> u.getRoleId() == 2)
                .count();
        model.addAttribute("personnelCount", personnelCount);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        model.addAttribute("currentDate", LocalDate.now().format(formatter));

        return "dashboard";
    }
}