package com.example.demo.Repository;

import com.example.demo.DTO.AnalyticsDTO;
import com.example.demo.Models.Bill;
import com.example.demo.Models.User;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill,Long> {


    List<Bill>findByCreatedByOrderByBillDateDesc(User user);
    List<Bill>findTop5ByCreatedByOrderByBillDateDesc(User staff);
    List<Bill>findAllByOrderByBillDateDesc();

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

@Query("SELECT COALESCE(sum(b.grandTotal),0) FROM Bill b " +
        "WHERE b.billDate >= :start AND b.billDate <= :end")
    BigDecimal findTotalRevenue(
            @Param("start")LocalDateTime start,
            @Param("end")LocalDateTime end
    );
@Query("SELECT count(b) FROM Bill b "
        + "WHERE b.billDate >= :start AND b.billDate <= :end")
long countBillsBetween(
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end
);

@Query("SELECT new com.example.demo.DTO.AnalyticsDTO(b.createdBy.username , SUM(b.grandTotal)) " + "FROM Bill b " +
        "WHERE b.billDate >= :start AND b.billDate <= :end "+"GROUP BY b.createdBy.username " +
        "ORDER By SUM(b.grandTotal) DESC")
List<AnalyticsDTO>findRevenuePerStaff(
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end

);

@Query("SELECT new com.example.demo.DTO.AnalyticsDTO("+
        "FUNCTION('DATE_FORMAT',b.billDate,'%d %b'),sum(b.grandTotal)) "+
        "FROM Bill b "+"WHERE b.billDate >= :start AND b.billDate <= :end "+
        "GROUP BY FUNCTION ('DATE_FORMAT',b.billDate,'%d %b'),CAST(b.billDate AS date) "+
        "ORDER BY CAST(b.billDate AS date)ASC")
List<AnalyticsDTO>findDailyRevenueTrend(
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end
);

@Query("SELECT new com.example.demo.DTO.AnalyticsDTO("+"FUNCTION('DATE_FORMAT',b.billDate,'%b %Y'),sum(b.grandTotal))"+
        " FROM Bill b "+"WHERE b.billDate >= :start AND b.billDate <= :end "+
        "GROUP BY FUNCTION('DATE_FORMAT',b.billDate,'%b %Y'), "+
        "FUNCTION ('YEAR',b.billDate),FUNCTION('MONTH',b.billDate) "+
        "ORDER BY FUNCTION('YEAR',b.billDate)ASC,FUNCTION('MONTH',b.billDate)ASC")
List<AnalyticsDTO>findMonthlyRevenueTrend(
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end
);

@Query("SELECT COUNT(b) FROM Bill b "+
        "WHERE b.createdBy.id = :staffId "+
        "AND b.billDate >= :start AND b.billDate<=:end")
long countBillsByStaffBetween(
        @Param("staffId")Long staffId,
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end
        );

@Query("SELECT COALESCE (sum(b.grandTotal),0) FROM Bill b "+
        "WHERE b.createdBy.id=:staffId "+
        "AND b.billDate >= :start AND b.billDate <= :end")
BigDecimal findRevenueByStaffBetween(
        @Param("staffId")Long staffId,
        @Param("start")LocalDateTime start,
        @Param("end")LocalDateTime end
);

@Query("SELECT COALESCE(sum(b.grandTotal),0) FROM Bill b "+"WHERE FUNCTION('YEAR',b.billDate)=:year " +
        "AND FUNCTION('MONTH',b.billDate) =:month")
BigDecimal findRevenueByYearMonth(
        @Param("year")int year,
        @Param("month")int month
);


}
