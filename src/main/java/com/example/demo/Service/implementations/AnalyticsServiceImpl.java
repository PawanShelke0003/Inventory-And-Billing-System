package com.example.demo.Service.implementations;

import com.example.demo.DTO.AnalyticsDTO;
import com.example.demo.Repository.BillItemRepository;
import com.example.demo.Repository.BillRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Service.interfaces.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;

    private LocalDateTime startOf(LocalDate date){
        return date.atStartOfDay();
    }
    private LocalDateTime endOf(LocalDate date){
        return date.atTime(LocalTime.MAX);
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDate start, LocalDate end) {
        return billRepository.findTotalRevenue(startOf(start),endOf(end));
    }

    @Override
    public long getTotalBillCount(LocalDate start, LocalDate end) {
        return billRepository.countBillsBetween(startOf(start),endOf(end));
    }

    @Override
    public BigDecimal getAverageOrderValue(LocalDate start, LocalDate end) {
        long count = getTotalBillCount(start,end);
        if(count==0)return BigDecimal.ZERO;

        return getTotalRevenue(start,end)
                .divide(BigDecimal.valueOf(count),2, RoundingMode.HALF_UP);
    }

    @Override
    public List<AnalyticsDTO> getStaffLeaderboard(LocalDate start, LocalDate end) {
        return billRepository.findRevenuePerStaff(startOf(start),endOf(end));
    }

    @Override
    public List<AnalyticsDTO> getDailyRevenueTrend(LocalDate start, LocalDate end) {
        return billRepository.findDailyRevenueTrend(startOf(start),endOf(end));
    }

    @Override
    public List<AnalyticsDTO> geTopSellingProduct(LocalDate start, LocalDate end) {
        return billItemRepository.findTopSellingProducts(startOf(start),endOf(end));
    }

    @Override
    public List<AnalyticsDTO> getRevenueByCategory(LocalDate start, LocalDate end) {
        return billItemRepository.findRevenueByCategory(startOf(start),endOf(end));
    }

    @Override
    public List<AnalyticsDTO> getTopSellingProductsByCategory(LocalDate start, LocalDate end, Long categoryId) {
        return billItemRepository.findTopSellingProductByCategory(startOf(start),endOf(end),categoryId);
    }

    @Override
    public BigDecimal getRevenueByStaff(Long staffId, LocalDate start, LocalDate end) {
        return billRepository.findRevenueByStaffBetween(staffId,startOf(start),endOf(end));
    }

    @Override
    public long getBillCountByStaff(Long staffId, LocalDate start, LocalDate end) {
        return billRepository.countBillsByStaffBetween(staffId,startOf(start),endOf(end));
    }

    @Override
    public BigDecimal getMonthRevenue(int year, int month) {
        return billRepository.findRevenueByYearMonth(year, month);
    }

    @Override
    public BigDecimal getMonthOverMonthGrowth() {
        LocalDate now = LocalDate.now();
        BigDecimal thisMonth = getMonthRevenue(now.getYear(),now.getMonthValue());
        BigDecimal lastMonth = getMonthRevenue(now.minusMonths(1)
                .getYear(),now.minusMonths(1).getMonthValue());

        if(lastMonth.compareTo(BigDecimal.ZERO)==0)return BigDecimal.ZERO;

        return thisMonth.subtract(lastMonth).divide(lastMonth,4,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).
                setScale(2,RoundingMode.HALF_UP);
    }

    @Override
    public List<AnalyticsDTO> getMonthlyRevenueTrend(LocalDate start, LocalDate end) {
        return billRepository.findMonthlyRevenueTrend(startOf(start),endOf(end));
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        return productRepository.findTotalInventoryValue();
    }
}
