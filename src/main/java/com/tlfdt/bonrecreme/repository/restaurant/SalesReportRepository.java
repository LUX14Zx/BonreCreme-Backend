package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.SalesReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesReportRepository extends JpaRepository<SalesReport, Long> {

    @NonNull
    Optional<SalesReport> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    Optional<SalesReport> findByBill(Bill bill);
    
    List<SalesReport> findByReportYear(Integer reportYear);
    
    List<SalesReport> findByReportYearAndReportMonth(Integer reportYear, Integer reportMonth);
    
    List<SalesReport> findByTotalRevenueGreaterThan(BigDecimal totalRevenue);
    
    List<SalesReport> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}