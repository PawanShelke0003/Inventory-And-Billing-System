package com.example.demo.Service.interfaces;


import com.example.demo.DTO.AnalyticsDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {

    BigDecimal getTotalRevenue(LocalDate start,LocalDate end);
    long getTotalBillCount(LocalDate start,LocalDate end);
    BigDecimal getAverageOrderValue(LocalDate start,LocalDate end);
    List<AnalyticsDTO>getStaffLeaderboard(LocalDate start,LocalDate end);
    List<AnalyticsDTO>getDailyRevenueTrend(LocalDate start,LocalDate end);
    List<AnalyticsDTO>geTopSellingProduct(LocalDate start,LocalDate end);
    List<AnalyticsDTO>getRevenueByCategory(LocalDate start,LocalDate end);
    List<AnalyticsDTO>getTopSellingProductsByCategory(LocalDate start,LocalDate end,Long categoryId);

    BigDecimal getRevenueByStaff(Long staffId,LocalDate start,LocalDate end);
    long getBillCountByStaff(Long staffId,LocalDate start,LocalDate end);

    BigDecimal getMonthRevenue(int year,int month);
    BigDecimal getMonthOverMonthGrowth();
    List<AnalyticsDTO>getMonthlyRevenueTrend(LocalDate start,LocalDate end);
    BigDecimal getTotalInventoryValue();
}
