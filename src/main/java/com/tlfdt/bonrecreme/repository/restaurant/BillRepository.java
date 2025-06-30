// src/main/java/com/tlfdt/bonrecreme/repository/restaurant/BillRepository.java

package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("SELECT b FROM Bill b LEFT JOIN FETCH b.orders o LEFT JOIN FETCH o.orderItems WHERE b.seatTable.id = :tableId AND b.status = :status ORDER BY b.createdAt DESC")
    List<Bill> findMostRecentBillWithDetailsByTableIdAndStatus(@Param("tableId") Long tableId, @Param("status") BillStatus status);

    /**
     * Finds all bills with a specific status created within a given date range.
     * This query is optimized to fetch all associated data (SeatTable)
     * in a single database call to prevent N+1 issues when accessing table numbers.
     *
     * @param status    The status of the bills to find (e.g., PAID).
     * @param startDate The start of the date range (inclusive).
     * @param endDate   The end of the date range (exclusive).
     * @return A list of {@link Bill} objects with their associated SeatTable eagerly fetched.
     */
    @Query("SELECT b FROM Bill b JOIN FETCH b.seatTable WHERE b.status = :status AND b.createdAt >= :startDate AND b.createdAt < :endDate")
    List<Bill> findBillsWithDetailsByStatusAndDateRange(
            @Param("status") BillStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    /**
     * Finds all bills associated with a specific seat table.
     *
     * @param tableId The ID of the seat table.
     * @return A list of bills for the given table.
     */
    @Query("SELECT b FROM Bill b WHERE b.seatTable.id = :tableId")
    List<Bill> findAllBySeatTableId(@Param("tableId") Long tableId);
}