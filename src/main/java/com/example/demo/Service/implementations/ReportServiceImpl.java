package com.example.demo.Service.implementations;

import com.example.demo.DTO.*;
import com.example.demo.Repository.BillItemRepository;
import com.example.demo.Repository.BillRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.interfaces.ReportService;
import com.opencsv.CSVWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final com.example.demo.Repository.UserRepository userRepository;
    private final com.example.demo.Repository.CategoryRepository categoryRepository;
    private final com.example.demo.Service.interfaces.SettingsService settingsService;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
    private static final DateTimeFormatter D_FMT  = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private LocalDateTime startOf(LocalDate d) { return d.atStartOfDay(); }
    private LocalDateTime endOf(LocalDate d)   { return d.atTime(LocalTime.MAX); }

    // ── DATA ─────────────────────────────────────────────────────────────────

    @Override
    public List<SalesReportRow> getSalesReport(LocalDate start, LocalDate end, Long staffId) {
        return billRepository.findSalesReport(startOf(start), endOf(end), staffId);
    }

    @Override
    public List<ItemizedReportRow> getItemizedReport(LocalDate start, LocalDate end, Long staffId, Long categoryId) {
        return billItemRepository.findItemizedReport(startOf(start), endOf(end), staffId, categoryId);
    }

    @Override
    public List<TaxReportRow> getTaxReport(LocalDate start, LocalDate end) {
        List<Object[]> rows = billRepository.findTaxReportRaw(startOf(start), endOf(end));
        return rows.stream().map(r -> new TaxReportRow(
                (String) r[0],
                ((Number) r[1]).longValue(),
                r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO,
                r[3] != null ? new BigDecimal(r[3].toString()) : BigDecimal.ZERO,
                r[4] != null ? new BigDecimal(r[4].toString()) : BigDecimal.ZERO
        )).collect(Collectors.toList());
    }

    @Override
    public List<InventoryReportRow> getInventoryReport() {
        return productRepository.findByActiveTrueOrderByCategoryNameAscNameAsc()
                .stream()
                .map(p -> {
                    String status = p.getQuantity() == 0 ? "OUT"
                            : p.getQuantity() <= p.getMinStock() ? "LOW" : "OK";
                    BigDecimal stockValue = p.getPrice()
                            .multiply(BigDecimal.valueOf(p.getQuantity()));
                    return new InventoryReportRow(
                            p.getName(),
                            p.getCategory().getName(),
                            p.getQuantity(),
                            p.getMinStock(),
                            p.getPrice(),
                            stockValue,
                            status
                    );
                }).collect(Collectors.toList());
    }

    @Override
    public List<StaffPerformanceRow> getStaffPerformanceReport(LocalDate start, LocalDate end, Long categoryId) {
        List<Object[]> rows;
        if (categoryId == null) {
            rows = billRepository.findStaffPerformanceRaw(startOf(start), endOf(end));
        } else {
            rows = billRepository.findStaffPerformanceRawByCategory(startOf(start), endOf(end), categoryId);
        }
        return rows.stream().map(r -> new StaffPerformanceRow(
                (String) r[0],
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                r[3] != null ? new BigDecimal(r[3].toString()) : BigDecimal.ZERO
        )).collect(Collectors.toList());
    }

    @Override
    public List<CategorySummaryRow> getCategorySummaryReport(LocalDate start, LocalDate end, Long staffId, Long categoryId) {
        List<Object[]> rows;
        Double gstRate = settingsService.getSettings().getGstRate();
        if (staffId == null && categoryId == null) {
            rows = billRepository.findCategorySummaryRaw(startOf(start), endOf(end), gstRate);
        } else if (staffId != null && categoryId == null) {
            rows = billRepository.findCategorySummaryRawByStaff(startOf(start), endOf(end), staffId, gstRate);
        } else if (staffId == null && categoryId != null) {
            rows = billRepository.findCategorySummaryRawByCategory(startOf(start), endOf(end), categoryId, gstRate);
        } else {
            rows = billRepository.findCategorySummaryRawByStaffAndCategory(startOf(start), endOf(end), staffId, categoryId, gstRate);
        }
        return rows.stream().map(r -> new CategorySummaryRow(
                (String) r[0],
                ((Number) r[1]).longValue(),
                r[2] != null ? new BigDecimal(r[2].toString()) : BigDecimal.ZERO,
                r[3] != null ? new BigDecimal(r[3].toString()) : BigDecimal.ZERO
        )).collect(Collectors.toList());
    }

    // ── CSV ───────────────────────────────────────────────────────────────────

    @Override
    public void exportCsv(HttpServletResponse response, String type,
                          LocalDate start, LocalDate end,
                          Long staffId, Long categoryId) throws IOException {
        response.setContentType("text/csv");
        switch (type) {
            case "sales" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"sales_report.csv\"");
                List<SalesReportRow> data = getSalesReport(start, end, staffId);
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{"Bill ID", "Date", "Staff", "Customer Phone",
                                            "Sub-Total", "GST", "Grand Total"});
                    data.forEach(r -> w.writeNext(new String[]{
                            "#INV-" + r.getBillId(),
                            r.getBillDate().format(DT_FMT),
                            r.getStaffName(),
                            r.getCustomerPhone(),
                            r.getTotalAmount().toString(),
                            r.getGstAmount().toString(),
                            r.getGrandTotal().toString()
                    }));
                }
            }
            case "itemized" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"itemized_report.csv\"");
                List<ItemizedReportRow> data = getItemizedReport(start, end, staffId, categoryId);
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{"Bill ID", "Date", "Staff", "Product",
                                            "Category", "Qty", "Unit Price", "Item Total"});
                    data.forEach(r -> w.writeNext(new String[]{
                            "#INV-" + r.getBillId(),
                            r.getBillDate().format(DT_FMT),
                            r.getStaffName(),
                            r.getProductName(),
                            r.getCategoryName(),
                            String.valueOf(r.getQuantity()),
                            r.getUnitPrice().toString(),
                            r.getItemTotal().toString()
                    }));
                }
            }
            case "tax" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"tax_report.csv\"");
                List<TaxReportRow> data = getTaxReport(start, end);
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{"Date", "Bills", "Sub-Total", "GST Collected", "Grand Total"});
                    data.forEach(r -> w.writeNext(new String[]{
                            r.getPeriod(),
                            String.valueOf(r.getBillCount()),
                            r.getTotalAmount().toString(),
                            r.getGstAmount().toString(),
                            r.getGrandTotal().toString()
                    }));
                }
            }
            case "inventory" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"inventory_report.csv\"");
                List<InventoryReportRow> data = getInventoryReport();
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{"Product", "Category", "Stock", "Min Stock",
                                            "Unit Price", "Stock Value", "Status"});
                    data.forEach(r -> w.writeNext(new String[]{
                            r.getProductName(),
                            r.getCategoryName(),
                            String.valueOf(r.getCurrentStock()),
                            String.valueOf(r.getMinStock()),
                            r.getUnitPrice().toString(),
                            r.getTotalStockValue().toString(),
                            r.getStockStatus()
                    }));
                }
            }
            case "staff_performance" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"staff_performance_report.csv\"");
                List<StaffPerformanceRow> data = getStaffPerformanceReport(start, end, categoryId);
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{"Staff Name", "Bills Generated", "Items Sold", "Total Revenue"});
                    data.forEach(r -> w.writeNext(new String[]{
                            r.getStaffName(),
                            String.valueOf(r.getBillCount()),
                            String.valueOf(r.getItemsSold()),
                            r.getTotalRevenue().toString()
                    }));
                }
            }
            case "category_summary" -> {
                String reportTitle = categoryId != null ? "product_wise_sales_report.csv" : "category_summary_report.csv";
                String firstColumn = categoryId != null ? "Product Name" : "Category Name";
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportTitle + "\"");
                List<CategorySummaryRow> data = getCategorySummaryReport(start, end, staffId, categoryId);
                try (CSVWriter w = new CSVWriter(response.getWriter())) {
                    w.writeNext(new String[]{firstColumn, "Quantity Sold", "Total Revenue", "GST Collected"});
                    data.forEach(r -> w.writeNext(new String[]{
                            r.getCategoryName(),
                            String.valueOf(r.getQuantitySold()),
                            r.getTotalRevenue().toString(),
                            r.getGstCollected().toString()
                    }));
                }
            }
        }
    }

    // ── PDF ───────────────────────────────────────────────────────────────────

    @Override
    public void exportPdf(HttpServletResponse response, String type,
                          LocalDate start, LocalDate end,
                          Long staffId, Long categoryId) throws IOException {
        response.setContentType("application/pdf");
        String html = switch (type) {
            case "itemized" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"itemized_report.pdf\"");
                yield buildItemizedPdfHtml(getItemizedReport(start, end, staffId, categoryId), start, end);
            }
            case "tax" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"tax_report.pdf\"");
                yield buildTaxPdfHtml(getTaxReport(start, end), start, end);
            }
            case "inventory" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"inventory_report.pdf\"");
                yield buildInventoryPdfHtml(getInventoryReport());
            }
            case "staff_performance" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"staff_performance_report.pdf\"");
                String catName = null;
                if (categoryId != null) {
                    com.example.demo.Models.Category c = categoryRepository.findById(categoryId).orElse(null);
                    if (c != null) catName = c.getName();
                }
                yield buildStaffPerformancePdfHtml(getStaffPerformanceReport(start, end, categoryId), start, end, catName);
            }
            case "category_summary" -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"category_summary_report.pdf\"");
                String staffName = null;
                String catName = null;
                if (staffId != null) {
                    com.example.demo.Models.User staff = userRepository.findById(staffId).orElse(null);
                    if (staff != null) staffName = staff.getUsername();
                }
                if (categoryId != null) {
                    com.example.demo.Models.Category c = categoryRepository.findById(categoryId).orElse(null);
                    if (c != null) catName = c.getName();
                }
                yield buildCategorySummaryPdfHtml(getCategorySummaryReport(start, end, staffId, categoryId), start, end, staffName, catName);
            }
            default -> {
                response.setHeader("Content-Disposition", "attachment; filename=\"sales_report.pdf\"");
                yield buildSalesPdfHtml(getSalesReport(start, end, staffId), start, end);
            }
        };
        response.getOutputStream().write(renderPdf(html));
    }

    // ── PDF HELPERS ───────────────────────────────────────────────────────────

    private byte[] renderPdf(String html) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }

    private String pdfBase(String title, String subtitle, String tableHeaders) {
        return "<!DOCTYPE html><html><head><style>" +
               "body{font-family:Arial,sans-serif;font-size:11px;color:#1f2937;margin:24px}" +
               "h1{font-size:18px;color:#111827;margin:0 0 4px}" +
               "p.sub{font-size:11px;color:#6b7280;margin:0 0 16px}" +
               "table{width:100%;border-collapse:collapse;margin-top:8px}" +
               "th{background:#1e40af;color:#fff;padding:7px 10px;text-align:left;font-size:10px}" +
               "td{padding:6px 10px;border-bottom:1px solid #e5e7eb}" +
               "tr:nth-child(even){background:#f8fafc}" +
               ".ok{color:#059669;font-weight:700}" +
               ".low{color:#d97706;font-weight:700}" +
               ".out{color:#dc2626;font-weight:700}" +
               ".footer{margin-top:24px;font-size:9px;color:#9ca3af;text-align:right}" +
               "</style></head><body>" +
               "<h1>" + title + "</h1>" +
               "<p class=\"sub\">" + subtitle + "</p>" +
               "<table>" + tableHeaders;
    }

    private String pdfClose() {
        return "</table><p class=\"footer\">Generated on " +
               LocalDateTime.now().format(DT_FMT) + "</p></body></html>";
    }

    private String buildSalesPdfHtml(List<SalesReportRow> data, LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder(pdfBase(
                "Sales Report",
                "Period: " + start.format(D_FMT) + " — " + end.format(D_FMT),
                "<tr><th>#</th><th>Bill ID</th><th>Date</th><th>Staff</th>" +
                "<th>Customer</th><th>Sub-Total</th><th>GST</th><th>Grand Total</th></tr>"));
        int i = 1;
        for (SalesReportRow r : data) {
            sb.append("<tr>")
              .append("<td>").append(i++).append("</td>")
              .append("<td>#INV-").append(r.getBillId()).append("</td>")
              .append("<td>").append(r.getBillDate().format(DT_FMT)).append("</td>")
              .append("<td>").append(r.getStaffName()).append("</td>")
              .append("<td>").append(r.getCustomerPhone()).append("</td>")
              .append("<td>Rs.").append(r.getTotalAmount()).append("</td>")
              .append("<td>Rs.").append(r.getGstAmount()).append("</td>")
              .append("<td>Rs.").append(r.getGrandTotal()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }

    private String buildItemizedPdfHtml(List<ItemizedReportRow> data, LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder(pdfBase(
                "Itemized Sales Report",
                "Period: " + start.format(D_FMT) + " — " + end.format(D_FMT),
                "<tr><th>Bill ID</th><th>Date</th><th>Staff</th><th>Product</th>" +
                "<th>Category</th><th>Qty</th><th>Unit Price</th><th>Total</th></tr>"));
        for (ItemizedReportRow r : data) {
            sb.append("<tr>")
              .append("<td>#INV-").append(r.getBillId()).append("</td>")
              .append("<td>").append(r.getBillDate().format(DT_FMT)).append("</td>")
              .append("<td>").append(r.getStaffName()).append("</td>")
              .append("<td>").append(r.getProductName()).append("</td>")
              .append("<td>").append(r.getCategoryName()).append("</td>")
              .append("<td>").append(r.getQuantity()).append("</td>")
              .append("<td>Rs.").append(r.getUnitPrice()).append("</td>")
              .append("<td>Rs.").append(r.getItemTotal()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }

    private String buildTaxPdfHtml(List<TaxReportRow> data, LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder(pdfBase(
                "GST / Tax Summary Report",
                "Period: " + start.format(D_FMT) + " — " + end.format(D_FMT),
                "<tr><th>Date</th><th>Bills</th><th>Sub-Total</th>" +
                "<th>GST Collected</th><th>Grand Total</th></tr>"));
        for (TaxReportRow r : data) {
            sb.append("<tr>")
              .append("<td>").append(r.getPeriod()).append("</td>")
              .append("<td>").append(r.getBillCount()).append("</td>")
              .append("<td>Rs.").append(r.getTotalAmount()).append("</td>")
              .append("<td>Rs.").append(r.getGstAmount()).append("</td>")
              .append("<td>Rs.").append(r.getGrandTotal()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }

    private String buildInventoryPdfHtml(List<InventoryReportRow> data) {
        StringBuilder sb = new StringBuilder(pdfBase(
                "Inventory Value Report",
                "Snapshot as of " + LocalDate.now().format(D_FMT),
                "<tr><th>Product</th><th>Category</th><th>Stock</th>" +
                "<th>Min Stock</th><th>Unit Price</th><th>Stock Value</th><th>Status</th></tr>"));
        for (InventoryReportRow r : data) {
            String css = switch (r.getStockStatus()) {
                case "LOW" -> "low";
                case "OUT" -> "out";
                default    -> "ok";
            };
            sb.append("<tr>")
              .append("<td>").append(r.getProductName()).append("</td>")
              .append("<td>").append(r.getCategoryName()).append("</td>")
              .append("<td>").append(r.getCurrentStock()).append("</td>")
              .append("<td>").append(r.getMinStock()).append("</td>")
              .append("<td>Rs. ").append(r.getUnitPrice()).append("</td>")
              .append("<td>Rs. ").append(r.getTotalStockValue()).append("</td>")
              .append("<td class=\"").append(css).append("\">").append(r.getStockStatus()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }

    private String buildStaffPerformancePdfHtml(List<StaffPerformanceRow> data, LocalDate start, LocalDate end, String catName) {
        String subtitle = "Period: " + start.format(D_FMT) + " — " + end.format(D_FMT);
        if (catName != null) {
            subtitle += " | Category: " + catName;
        }
        StringBuilder sb = new StringBuilder(pdfBase(
                "Staff Performance Report",
                subtitle,
                "<tr><th>Staff Name</th><th>Bills Generated</th><th>Items Sold</th><th>Total Revenue</th></tr>"));
        for (StaffPerformanceRow r : data) {
            sb.append("<tr>")
              .append("<td>").append(r.getStaffName()).append("</td>")
              .append("<td>").append(r.getBillCount()).append("</td>")
              .append("<td>").append(r.getItemsSold()).append("</td>")
              .append("<td>Rs.").append(r.getTotalRevenue()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }

    private String buildCategorySummaryPdfHtml(List<CategorySummaryRow> data, LocalDate start, LocalDate end, String staffName, String catName) {
        String subtitle = "Period: " + start.format(D_FMT) + " — " + end.format(D_FMT);
        if (staffName != null) {
            subtitle += " | Staff: " + staffName;
        }
        if (catName != null) {
            subtitle += " | Category: " + catName;
        }
        String reportTitle = catName != null ? "Product-Wise Sales Report" : "Category-Wise Sales Report";
        String firstColumn = catName != null ? "Product Name" : "Category Name";
        
        StringBuilder sb = new StringBuilder(pdfBase(
                reportTitle,
                subtitle,
                "<tr><th>" + firstColumn + "</th><th>Quantity Sold</th><th>Total Revenue</th><th>GST Collected</th></tr>"));
        for (CategorySummaryRow r : data) {
            sb.append("<tr>")
              .append("<td>").append(r.getCategoryName()).append("</td>")
              .append("<td>").append(r.getQuantitySold()).append("</td>")
              .append("<td>Rs.").append(r.getTotalRevenue()).append("</td>")
              .append("<td>Rs.").append(r.getGstCollected()).append("</td></tr>");
        }
        return sb.append(pdfClose()).toString();
    }
}
