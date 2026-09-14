package com.example.demo.Controller;

import com.example.demo.Models.Role;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.interfaces.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String reportsPage(Model model) {
        model.addAttribute("activeMenu", "reports");
        model.addAttribute("allStaff", userRepository.findByRole(Role.ROLE_STAFF));
        model.addAttribute("allCategories", categoryRepository.findAll());
        return "admin/reports";
    }

    @GetMapping("/generate")
    public String generateReport(
            @RequestParam(defaultValue = "sales") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) Long categoryId,
            Model model) {

        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null)   endDate   = LocalDate.now();

        switch (type) {
            case "sales"     -> model.addAttribute("rows", reportService.getSalesReport(startDate, endDate, staffId));
            case "itemized"  -> model.addAttribute("rows", reportService.getItemizedReport(startDate, endDate, staffId, categoryId));
            case "tax"       -> model.addAttribute("rows", reportService.getTaxReport(startDate, endDate));
            case "inventory" -> model.addAttribute("rows", reportService.getInventoryReport());
            case "staff_performance" -> model.addAttribute("rows", reportService.getStaffPerformanceReport(startDate, endDate, categoryId));
            case "category_summary"  -> model.addAttribute("rows", reportService.getCategorySummaryReport(startDate, endDate, staffId, categoryId));
        }

        model.addAttribute("activeMenu", "reports");
        model.addAttribute("reportType", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("staffId", staffId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("allStaff", userRepository.findByRole(Role.ROLE_STAFF));
        model.addAttribute("allCategories", categoryRepository.findAll());
        return "admin/reports";
    }

    @GetMapping("/export")
    public void exportReport(
            @RequestParam String format,
            @RequestParam(defaultValue = "sales") String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long staffId,
            @RequestParam(required = false) Long categoryId,
            HttpServletResponse response) throws IOException {

        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null)   endDate   = LocalDate.now();

        if ("pdf".equals(format)) {
            reportService.exportPdf(response, type, startDate, endDate, staffId, categoryId);
        } else {
            reportService.exportCsv(response, type, startDate, endDate, staffId, categoryId);
        }
    }
}
