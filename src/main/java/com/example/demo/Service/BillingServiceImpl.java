package com.example.demo.Service;

import com.example.demo.Models.*;
import com.example.demo.Repository.BillItemRepository;
import com.example.demo.Repository.BillRepository;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingServiceImpl implements BillingService {
    private final BillRepository billRepo;
    private final BillItemRepository billItemRepo;
    private final ProductRepository productRepo;

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    @Override
    public Bill createBill(Customer customer, User staff, List<BillItem> items) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BillItem item : items) {
            Product product = productRepo.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("PRODUCT NOT FOUND "));
            if (product.getQuantity() < item.getQuantity()) {
                throw new BusinessException("INSUFFICIENT STOCK FOR " + product.getName());
            }

            BigDecimal itemTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setPrice(product.getPrice());
            item.setTotal(itemTotal);
            item.setProduct(product);

            totalAmount = totalAmount.add(itemTotal);

        }

        BigDecimal gstAmount = totalAmount.multiply(GST_RATE).
                setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = totalAmount.add(gstAmount);

        Bill bill = Bill.builder()
                .billDate(LocalDateTime.now())
                .customer(customer)
                .createdBy(staff)
                .totalAmount(totalAmount)
                .gstAmount(gstAmount)
                .grandTotal(grandTotal)
                .build();

        bill.setItems(items);
        items.forEach(i -> i.setBill(bill));
        bill.setItems(items);

        Bill savedbill = billRepo.save(bill);


        for (BillItem item : items) {
            Product product = item.getProduct();

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepo.save(product);


        }
        return savedbill;
    }

    @Override
    public List<Bill> getBillByStaff(User staff) {
        return billRepo.findByCreatedByOrderByBillDateDesc(staff);
    }

    @Override
    public Bill getBillById(Long id) {
        return billRepo.findById(id).orElseThrow(() -> new RuntimeException("Bill Not Found"));
    }

    @Override
    public List<Bill> getRecentBills(User user) {
        return billRepo.findTop5ByCreatedByOrderByBillDateDesc(user);
    }

    @Override
    public List<Bill> getAllBills() {
        return billRepo.findAllByOrderByBillDateDesc();
    }

}
