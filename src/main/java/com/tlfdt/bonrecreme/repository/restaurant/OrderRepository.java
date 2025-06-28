package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link Order} entities.
 *
 * Provides optimized data access methods for orders, focusing on performance-critical
 * operations like fetching orders for billing and supporting pagination for large datasets.
 * It uses EntityGraphs and custom JPQL queries to prevent N+1 problems.
 *
 * @see Order
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders with a specific status, with support for pagination.
     * It uses an EntityGraph to eagerly fetch the associated seatTable, preventing
     * an N+1 query issue when accessing table information from the order.
     *
     * @param status   The {@link OrderStatus} to filter by.
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link Order}s matching the status.
     */
    @EntityGraph(attributePaths = {"seatTable"})
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Finds all unbilled orders for a specific table with a given status.
     * This query is highly optimized using {@code LEFT JOIN FETCH} to retrieve the
     * {@code Order}, its {@code OrderItem}s, and each {@code OrderItem}'s {@code MenuItem}
     * in a single database query. This completely avoids the N+1 problem that would
     * otherwise occur when calculating the bill total, making the checkout process
     * significantly more performant.
     *
     * @param tableId The ID of the seat table.
     * @param status  The status of the orders to find (e.g., SERVED).
     * @return A List of fully initialized {@link Order} objects ready for processing.
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.menuItem " +
            "WHERE o.seatTable.id = :tableId AND o.status = :status AND o.bill IS NULL")
    List<Order> findUnbilledOrdersForTableWithDetails(
            @Param("tableId") Long tableId,
            @Param("status") OrderStatus status);

    /**
     * Finds all orders created within a given time range, with pagination.
     *
     * @param start    The start of the date range (inclusive).
     * @param end      The end of the date range (exclusive).
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link Order}s created within the specified timeframe.
     */
    Page<Order> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}