package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryRow {
    private String categoryName;
    private Long quantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal gstCollected;
}
