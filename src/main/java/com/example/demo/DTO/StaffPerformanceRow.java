package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffPerformanceRow {
    private String staffName;
    private Long billCount;
    private Long itemsSold;
    private BigDecimal totalRevenue;
}
