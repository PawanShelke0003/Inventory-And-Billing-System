package com.example.demo.Service.interfaces;

import com.example.demo.Models.Bill;
import com.example.demo.Models.BillItem;
import com.example.demo.Models.Customer;
import com.example.demo.Models.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BillingService {

    Bill createBill(Customer customer, User staff, List<BillItem>items);
    List<Bill>getBillByStaff(User staff);
    Bill getBillById(Long id);
    List<Bill>getRecentBills(User user);
    List<Bill>getAllBills();
    long getTodaysBillCount(User staff);
    BigDecimal getTodaysSales(User staff);
    List<Bill>getStaffsBillsWithFilters(Long staffId, String phone, LocalDate startDate, LocalDate endDate);
    BigDecimal calculateTotalRevenue(List<Bill> bills);
}
