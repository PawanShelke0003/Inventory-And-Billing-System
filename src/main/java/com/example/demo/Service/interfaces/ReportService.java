package com.example.demo.Service.interfaces;

import com.example.demo.DTO.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<SalesReportRow> getSalesReport(LocalDate start, LocalDate end, Long staffId);
    List<ItemizedReportRow> getItemizedReport(LocalDate start, LocalDate end, Long staffId, Long categoryId);
    List<TaxReportRow> getTaxReport(LocalDate start, LocalDate end);
    List<InventoryReportRow> getInventoryReport();
    List<StaffPerformanceRow> getStaffPerformanceReport(LocalDate start, LocalDate end, Long categoryId);
    List<CategorySummaryRow> getCategorySummaryReport(LocalDate start, LocalDate end, Long staffId, Long categoryId);

    void exportCsv(HttpServletResponse response, String type,
                   LocalDate start, LocalDate end,
                   Long staffId, Long categoryId) throws IOException;

    void exportPdf(HttpServletResponse response, String type,
                   LocalDate start, LocalDate end,
                   Long staffId, Long categoryId) throws IOException;
}
