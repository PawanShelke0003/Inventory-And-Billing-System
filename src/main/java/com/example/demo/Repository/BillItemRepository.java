package com.example.demo.Repository;

import com.example.demo.DTO.AnalyticsDTO;
import com.example.demo.Models.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    @Query("SELECT new com.example.demo.DTO.AnalyticsDTO(bi.product.name, SUM(bi.quantity)) " +
           "FROM BillItem bi " +
           "WHERE bi.bill.billDate >= :start AND bi.bill.billDate <= :end " +
           "AND (:staffId IS NULL OR bi.bill.createdBy.id = :staffId) " +
           "GROUP BY bi.product.name " +
           "ORDER BY SUM(bi.quantity) DESC")
    List<AnalyticsDTO> findTopSellingProducts(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId
    );

    @Query("SELECT new com.example.demo.DTO.AnalyticsDTO(bi.product.category.name, SUM(bi.total)) " +
           "FROM BillItem bi " +
           "WHERE bi.bill.billDate >= :start AND bi.bill.billDate <= :end " +
           "AND (:staffId IS NULL OR bi.bill.createdBy.id = :staffId) " +
           "GROUP BY bi.product.category.name " +
           "ORDER BY SUM(bi.total) DESC")
    List<AnalyticsDTO> findRevenueByCategory(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId
    );

    @Query("SELECT new com.example.demo.DTO.AnalyticsDTO(bi.product.name, SUM(bi.quantity)) " +
           "FROM BillItem bi " +
           "WHERE bi.bill.billDate >= :start AND bi.bill.billDate <= :end " +
           "AND bi.product.category.id = :categoryId " +
           "AND (:staffId IS NULL OR bi.bill.createdBy.id = :staffId) " +
           "GROUP BY bi.product.name " +
           "ORDER BY SUM(bi.quantity) DESC")
    List<AnalyticsDTO> findTopSellingProductByCategory(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("categoryId") Long categoryId,
            @Param("staffId") Long staffId
    );

    @Query("SELECT COALESCE(SUM(bi.total), 0) FROM BillItem bi " +
           "WHERE bi.bill.billDate >= :start AND bi.bill.billDate <= :end " +
           "AND (:staffId IS NULL OR bi.bill.createdBy.id = :staffId) " +
           "AND bi.product.category.id = :categoryId")
    java.math.BigDecimal findTotalRevenueByCategoryFilter(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("staffId") Long staffId,
            @Param("categoryId") Long categoryId
    );
}
