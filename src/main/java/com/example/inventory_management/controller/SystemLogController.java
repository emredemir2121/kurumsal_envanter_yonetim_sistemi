package com.example.inventory_management.controller;

import com.example.inventory_management.dto.SystemLogDTO;
import com.example.inventory_management.model.SystemLog;
import com.example.inventory_management.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class SystemLogController {

    private final SystemLogRepository systemLogRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Locale TR_LOCALE = Locale.forLanguageTag("tr-TR");

    @Autowired
    public SystemLogController(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    @GetMapping("/logs")
    public String showSystemLogs(@RequestParam(value = "level", required = false) String level,
                                 @RequestParam(value = "source", required = false) String source,
                                 @RequestParam(value = "search", required = false) String search,
                                 Model model) {

        List<SystemLog> allLogs = systemLogRepository.findAllByOrderByDateDesc();

        List<SystemLogDTO> filteredLogs = allLogs.stream()
                .filter(log -> {
                    if (level == null || level.trim().isEmpty()) return true;
                    String dbLevel = log.getLevel() != null ? log.getLevel() : "INFO";
                    return dbLevel.toLowerCase(TR_LOCALE).contains(level.toLowerCase(TR_LOCALE).trim());
                })
                .filter(log -> {
                    if (source == null || source.trim().isEmpty()) return true;
                    String dbSource = log.getSource() != null ? log.getSource() : "Genel Bilgi";

                    String targetSource = source.toLowerCase(TR_LOCALE).trim();
                    String currentDbSource = dbSource.toLowerCase(TR_LOCALE).trim();

                    if ("info".equals(targetSource)) {
                        return currentDbSource.equals("info") || currentDbSource.contains("genel") || currentDbSource.contains("bilgi");
                    }
                    return currentDbSource.contains(targetSource);
                })
                .filter(log -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String keyword = search.toLowerCase(TR_LOCALE).trim();

                    String msg = log.getMessage() != null ? log.getMessage().toLowerCase(TR_LOCALE) : "";
                    String src = log.getSource() != null ? log.getSource().toLowerCase(TR_LOCALE) : "";

                    return msg.contains(keyword) || src.contains(keyword);
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        model.addAttribute("logs", filteredLogs);
        model.addAttribute("selectedLevel", level);
        model.addAttribute("selectedSource", source);
        model.addAttribute("selectedSearch", search);

        return "logs";
    }

    // 🚨 HELPER METHOD: Entity -> DTO Dönüşüm Motoru (Hafta 14)
    private SystemLogDTO convertToDTO(SystemLog log) {
        SystemLogDTO dto = new SystemLogDTO();
        dto.setId(log.getId());
        dto.setSeviye(log.getLevel() != null ? log.getLevel() : "INFO");
        dto.setKaynak(log.getSource() != null ? log.getSource() : "Genel Bilgi");
        dto.setMesaj(log.getMessage() != null ? log.getMessage() : "-");

        if (log.getDate() != null) {
            dto.setZamanStr(log.getDate().format(formatter));
        } else {
            dto.setZamanStr("2026-05-21 20:00:00"); // Boş kalıp tasarımı bozmasın diye akış zaman damgası
        }
        return dto;
    }
}