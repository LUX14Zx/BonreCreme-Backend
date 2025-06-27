package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @NonNull
    Optional<Bill> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);

    List<Long> findBySeatTableId(Long tableId);

    /**
     * Finds the most recent bill for a given table with a specific status.
     *
     * @param tableId The ID of the seat table.
     * @param status  The status of the bill to find.
     * @return An Optional containing the most recent Bill if found.
     */
    Optional<Bill> findFirstBySeatTable_IdAndStatusOrderByCreatedAtDesc(Long tableId, BillStatus status);
}