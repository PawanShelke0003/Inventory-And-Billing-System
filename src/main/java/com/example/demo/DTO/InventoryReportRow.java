package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportRow {
    private String productName;
    private String categoryName;
    private Integer currentStock;
    private Integer minStock;
    private BigDecimal unitPrice;
    private BigDecimal totalStockValue;
    private String stockStatus;
}
