package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.MenuItem;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link OrderItem} entities.
 *
 * Provides optimized and secure data access methods for order items. It includes
 * standard CRUD operations, pagination support, and advanced queries for reporting
 * and performance-critical operations.
 *
 * @see OrderItem
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Finds all order items associated with a specific order, with pagination.
     * Eagerly fetches the associated MenuItem to prevent N+1 queries.
     *
     * @param order The {@link Order} entity to find items for.
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link OrderItem}s.
     */
    @EntityGraph(attributePaths = {"menuItem"})
    Page<OrderItem> findByOrder(Order order, Pageable pageable);

    /**
     * Finds all order items for a specific menu item, with pagination.
     * This is useful for analyzing the sales of a particular item.
     *
     * @param menuItem The {@link MenuItem} to find orders for.
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link OrderItem}s.
     */
    Page<OrderItem> findByMenuItem(MenuItem menuItem, Pageable pageable);

    /**
     * Finds all order items where the quantity is greater than a specified value.
     *
     * @param quantity The minimum quantity (exclusive).
     * @return A list of {@link OrderItem}s matching the criteria.
     */
    List<OrderItem> findByQuantityGreaterThan(Integer quantity);

    /**
     * Finds a specific order item by its composite key of order and menu item.
     *
     * @param order    The {@link Order} of the item.
     * @param menuItem The {@link MenuItem} of the item.
     * @return An {@link Optional} containing the {@link OrderItem} if found.
     */
    Optional<OrderItem> findByOrderAndMenuItem(Order order, MenuItem menuItem);

    /**
     * Retrieves the top N best-selling menu items within a given date range.
     * This query aggregates the quantity of each menu item sold and orders the
     * results to find the most popular ones.
     *
     * @param startDate The start of the date range (inclusive).
     * @param endDate   The end of the date range (exclusive).
     * @param pageable  Pagination to limit the results (e.g., to get the top 10).
     * @return A {@link Page} containing an array of Objects, where each array
     * contains the {@link MenuItem} and the total quantity sold (Long).
     */
    @Query("SELECT oi.menuItem, SUM(oi.quantity) as totalQuantity " +
            "FROM OrderItem oi " +
            "WHERE oi.order.createdAt >= :startDate AND oi.order.createdAt < :endDate " +
            "GROUP BY oi.menuItem " +
            "ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingMenuItems(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}