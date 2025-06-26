package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @NonNull
    Optional<Order> findById(@NonNull Long id);

    boolean existsById(@NonNull Long id);
    
    List<Order> findByStatus(OrderStatus status);

    List<Order> findBySeatTableIdAndStatus(Long tableId, OrderStatus status);

    /**
     * Finds orders for a specific table with a given status that have not yet been assigned to a bill.
     *
     * @param tableId The ID of the seat table.
     * @param status The status of the order.
     * @return A list of orders that match the criteria.
     */
    List<Order> findBySeatTableIdAndStatusAndBillIsNull(Long tableId, OrderStatus status);
}