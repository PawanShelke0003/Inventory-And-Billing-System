package com.example.demo.Repository;

import com.example.demo.DTO.AnalyticsDTO;
import com.example.demo.Models.Bill;
import com.example.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByCreatedByOrderByBillDateDesc(User user);
    List<Bill> findTop5ByCreatedByOrderByBillDateDesc(User staff);
    List<Bill> findAllByOrderByBillDateDesc();

    @Query("SELECT b FROM Bill b WHERE b.createdBy.id = :staffId " +
           "AND (:phone IS NULL OR :phone = '' OR b.customer.phone = :phone) " +
           "AND (:startDate IS NULL OR b.billDate >= :startDate) " +
           "AND (:endDate IS NULL OR b.billDate <= :endDate) " +
           "ORDER BY b.billDate DESC")
    List<Bill> findStaffBillsWithFilters(
            @Param("staffId") Long staffId,
            @Param("phone") String phone,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COALESCE(SUM(b.grandTotal), 0) FROM Bill b " +
           "WHERE b.billDate >= :start AND b.billDate <= :end")
    BigDecimal findTotalRevenue(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(b) FROM Bill b " +
           "WHERE b.billDate >= :start AND b.billDate <= :end")
    long countBillsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT new com.example.demo.DTO.AnalyticsDTO(b.createdBy.username, SUM(b.grandTotal)) " +
           "FROM Bill b " +
           "WHERE b.billDate >= :start AND b.billDate <= :end " +
           "GROUP BY b.createdBy.username " +
           "ORDER BY SUM(b.grandTotal) DESC")
    List<AnalyticsDTO> findRevenuePerStaff(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = "SELECT DATE_FORMAT(bill_date, '%d %b') as label, SUM(grand_total) as value " +
                   "FROM bills WHERE bill_date >= :start AND bill_date <= :end " +
                   "AND (:staffId IS NULL OR created_by = :staffId) " +
                   "GROUP BY DATE_FORMAT(bill_date, '%d %b'), DATE(bill_date) " +
                   "ORDER BY DATE(bill_date) ASC", nativeQuery = true)
    List<Object[]> findDailyRevenueTrend(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId
    );

    @Query(value = "SELECT DATE_FORMAT(bill_date, '%b %Y') as label, SUM(grand_total) as value " +
                   "FROM bills WHERE bill_date >= :start AND bill_date <= :end " +
                   "GROUP BY DATE_FORMAT(bill_date, '%b %Y'), YEAR(bill_date), MONTH(bill_date) " +
                   "ORDER BY YEAR(bill_date) ASC, MONTH(bill_date) ASC", nativeQuery = true)
    List<Object[]> findMonthlyRevenueTrend(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(b) FROM Bill b " +
           "WHERE b.createdBy.id = :staffId " +
           "AND b.billDate >= :start AND b.billDate <= :end")
    long countBillsByStaffBetween(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(b.grandTotal), 0) FROM Bill b " +
           "WHERE b.createdBy.id = :staffId " +
           "AND b.billDate >= :start AND b.billDate <= :end")
    BigDecimal findRevenueByStaffBetween(
            @Param("staffId") Long staffId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COALESCE(SUM(b.grandTotal), 0) FROM Bill b " +
           "WHERE year(b.billDate) = :year AND month(b.billDate) = :month")
    BigDecimal findRevenueByYearMonth(
            @Param("year") int year,
            @Param("month") int month
    );
    @Query("SELECT COUNT(DISTINCT b) FROM Bill b JOIN b.items bi " +
           "WHERE b.billDate >= :start AND b.billDate <= :end " +
           "AND (:staffId IS NULL OR b.createdBy.id = :staffId) " +
           "AND bi.product.category.id = :categoryId")
    long countBillsByCategoryFilter(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId,
            @Param("categoryId") Long categoryId
    );

    @Query(value = "SELECT DATE_FORMAT(b.bill_date, '%d %b') as label, SUM(bi.total) as value " +
                   "FROM bills b " +
                   "JOIN bill_items bi ON b.id = bi.bill_id " +
                   "JOIN products p ON bi.product_id = p.id " +
                   "WHERE b.bill_date >= :start AND b.bill_date <= :end " +
                   "AND (:staffId IS NULL OR b.created_by = :staffId) " +
                   "AND p.category_id = :categoryId " +
                   "GROUP BY DATE_FORMAT(b.bill_date, '%d %b'), DATE(b.bill_date) " +
                   "ORDER BY DATE(b.bill_date) ASC", nativeQuery = true)
    List<Object[]> findDailyRevenueTrendByCategory(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId,
            @Param("categoryId") Long categoryId
    );
}
