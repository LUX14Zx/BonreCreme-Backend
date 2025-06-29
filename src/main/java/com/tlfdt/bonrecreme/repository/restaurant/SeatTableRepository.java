package com.tlfdt.bonrecreme.repository.restaurant;

import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
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
 * Repository for {@link SeatTable} entities.
 *
 * Provides optimized data access methods for restaurant tables, including
 * support for pagination and advanced queries to fetch related data efficiently.
 *
 * @see SeatTable
 */
@Repository
public interface SeatTableRepository extends JpaRepository<SeatTable, Long> {

    /**
     * Finds all tables with a specific status, with support for pagination.
     *
     * @param status   The {@link TableStatus} to filter by (e.g., AVAILABLE, OCCUPIED).
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link SeatTable}s matching the specified status.
     */
    Page<SeatTable> findByStatus(TableStatus status, Pageable pageable);

    /**
     * Finds all tables and eagerly fetches their associated orders using an EntityGraph.
     * This is useful for screens that need to display tables and their order counts
     * without causing an N+1 query problem.
     *
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link SeatTable}s with their orders initialized.
     */
    @EntityGraph(attributePaths = {"orders"})
    @Query("SELECT st FROM SeatTable st")
    Page<SeatTable> findAllWithOrders(Pageable pageable);

    /**
     * Finds tables that are currently available but have had orders that were marked as
     * PAID within the specified time window. This is useful for identifying tables
     * that may need cleaning or inspection after guests have left.
     *
     * @param sinceDateTime The cutoff time to check for recent payments.
     * @return A list of {@link SeatTable}s that are available and were recently occupied.
     */
    @Query("SELECT DISTINCT b.seatTable FROM Bill b " +
            "WHERE b.seatTable.status = :status AND b.status = com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus.PAID " +
            "AND b.createdAt >= :sinceDateTime")
    List<SeatTable> findAvailableTablesWithRecentPayments(
            @Param("status") TableStatus status,
            @Param("sinceDateTime") LocalDateTime sinceDateTime);
}