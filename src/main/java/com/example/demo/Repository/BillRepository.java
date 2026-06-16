package com.example.demo.Repository;

import com.example.demo.Models.Bill;
import com.example.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill,Long> {


    List<Bill>findByCreatedByOrderByBillDateDesc(User user);



    List<Bill>findTop5ByCreatedByOrderByBillDateDesc(User staff);

    List<Bill>findAllByOrderByBillDateDesc();




}
