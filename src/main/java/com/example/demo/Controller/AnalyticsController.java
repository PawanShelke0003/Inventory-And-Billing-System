package com.example.demo.Controller;

import com.example.demo.DTO.AnalyticsDTO;
import com.example.demo.Models.Role;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.interfaces.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    private List<String> toLabels(List<AnalyticsDTO> list) {
        return list.stream()
                .map(AnalyticsDTO::getLabel)
                .toList();
    }


    private List<java.math.BigDecimal> toValues(List<AnalyticsDTO> list) {
        return list.stream()
                .map(AnalyticsDTO::getValue)
                .toList();
    }

    @GetMapping("/admin/analytics")
    public String managerAnalytics(
            @RequestParam(required = false)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate endDate,
            @RequestParam(required = false)Long staffId,
            @RequestParam(required = false)Long categoryId,
            Model model
            ){

        if(startDate==null)startDate=LocalDate.now().withDayOfMonth(1);
        if(endDate==null)endDate=LocalDate.now();

        model.addAttribute("totalRevenue",analyticsService.getTotalRevenue(startDate,endDate));
        model.addAttribute("totalBills",analyticsService.getTotalBillCount(startDate,endDate));
        model.addAttribute("averageOrder",analyticsService.getAverageOrderValue(startDate,endDate));

        if(staffId!=null){
            model.addAttribute("filteredStaffRevenue",analyticsService.getRevenueByStaff(staffId,startDate,endDate));
            model.addAttribute("filteredStaffBills",analyticsService.getBillCountByStaff(staffId,startDate,endDate));
            model.addAttribute("selectedStaffId",staffId);
        }

        List<AnalyticsDTO>staffLeaderboard=analyticsService.getStaffLeaderboard(startDate,endDate);
        List<AnalyticsDTO>dailyTrend=analyticsService.getDailyRevenueTrend(startDate,endDate);
        List<AnalyticsDTO>topProducts=analyticsService.geTopSellingProduct(startDate,endDate);
        List<AnalyticsDTO>categoryRevenue=categoryId!=null?analyticsService.getTopSellingProductsByCategory(startDate,endDate,categoryId)
                :analyticsService.getRevenueByCategory(startDate,endDate);


        model.addAttribute("staffLabels", toLabels(staffLeaderboard));
        model.addAttribute("staffValues",toValues(staffLeaderboard));
        model.addAttribute("trendLabels",toLabels(dailyTrend));
        model.addAttribute("trendValues",toValues(dailyTrend));
        model.addAttribute("productLabels",toLabels(topProducts));
        model.addAttribute("productValues",toValues(topProducts));
        model.addAttribute("categoryLabels",toLabels(categoryRevenue));
        model.addAttribute("categoryValues",toValues(categoryRevenue));

        model.addAttribute("allStaff",userRepository.findByRole(Role.ROLE_STAFF));
        model.addAttribute("allCategories",categoryRepository.findAll());

        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);
        model.addAttribute("categoryId",categoryId);

        return "admin/analytics";
    }

    @GetMapping("/owner/analytics")
    public String ownerAnalytics(
            @RequestParam(required = false)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false)@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)LocalDate endDate,
            Model model
    ){

        if(startDate==null)startDate=LocalDate.now().minusMonths(11).withDayOfMonth(1);
        if(endDate==null)endDate=LocalDate.now();

        LocalDate now = LocalDate.now();

        model.addAttribute("totalRevenue",analyticsService.getTotalRevenue(startDate,endDate));
        model.addAttribute("totalBills",analyticsService.getTotalBillCount(startDate,endDate));
        model.addAttribute("averageOrder",analyticsService.getAverageOrderValue(startDate,endDate));
        model.addAttribute("thisMonthRevenue",analyticsService.getMonthRevenue(now.getYear(), now.getMonthValue()));
        model.addAttribute("lastMonthRevenue",analyticsService.getMonthRevenue(now.minusMonths(1).getYear(),now.minusMonths(1).getMonthValue()));
        model.addAttribute("momGrowth",analyticsService.getMonthOverMonthGrowth());
        model.addAttribute("inventoryValue",analyticsService.getTotalInventoryValue());

        List<AnalyticsDTO>monthlyTrend=analyticsService.getMonthlyRevenueTrend(startDate,endDate);
        List<AnalyticsDTO>staffLeaderboard=analyticsService.getStaffLeaderboard(startDate,endDate);
        List<AnalyticsDTO>categoryRevenue=analyticsService.getRevenueByCategory(startDate,endDate);
        List<AnalyticsDTO>topProducts=analyticsService.geTopSellingProduct(startDate,endDate);

        model.addAttribute("trendLabels",toLabels(monthlyTrend));
        model.addAttribute("trendValues",toValues(monthlyTrend));
        model.addAttribute("staffLabels",toLabels(staffLeaderboard));
        model.addAttribute("staffValues",toValues(staffLeaderboard));
        model.addAttribute("categoryLabels",toLabels(categoryRevenue));
        model.addAttribute("categoryValues",toValues(categoryRevenue));
        model.addAttribute("productLabels",toLabels(topProducts));
        model.addAttribute("productValues",toValues(topProducts));

        model.addAttribute("startDate",startDate);
        model.addAttribute("endDate",endDate);

        return "owner/analytics";
    }
}
