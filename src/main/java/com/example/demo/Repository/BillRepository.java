package com.example.demo.Repository;

import com.example.demo.Models.Bill;
import com.example.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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




}
