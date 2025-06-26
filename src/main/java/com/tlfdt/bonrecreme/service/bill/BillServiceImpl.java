package com.tlfdt.bonrecreme.service.bill;

import com.tlfdt.bonrecreme.exception.custom.CustomExceptionHandler;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import com.tlfdt.bonrecreme.model.restaurant.enums.OrderStatus;
import com.tlfdt.bonrecreme.model.restaurant.enums.TableStatus;
import com.tlfdt.bonrecreme.repository.restaurant.BillRepository;
import com.tlfdt.bonrecreme.repository.restaurant.OrderRepository;
import com.tlfdt.bonrecreme.repository.restaurant.SeatTableRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for managing bills.
 * This class handles the logic for bill generation and calculation.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Creates a constructor with all final fields for dependency injection.
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final SeatTableRepository seatTableRepository;

    /**
     * Generates a bill for a specific table following these steps:
     * 1. Retrieves the table.
     * 2. Collects all SERVED orders for the table that have not yet been billed.
     * 3. Calculates the total amount from the order items of these orders.
     * 4. Creates and saves a new Bill, associating the orders with it.
     *
     * @param tableId The ID of the table for which to generate the bill.
     * @return The newly created and persisted Bill object.
     * @throws EntityNotFoundException if no table is found for the provided tableId.
     * @throws CustomExceptionHandler  if there are no new 'SERVED' orders to bill for the table.
     */
    @Transactional
    @Override
    public Bill generateBillForTable(Long tableId) {
        log.info("Starting bill generation process for table ID: {}", tableId);

        // Step 1: Retrieve the Table entity.
        SeatTable seatTable = seatTableRepository.findById(tableId)
                .orElseThrow(() -> {
                    log.error("Failed to find table with ID: {}", tableId);
                    return new EntityNotFoundException("Table not found with id: " + tableId);
                });
        log.info("Successfully retrieved table '{}' with ID: {}", seatTable.getTableNumber(), tableId);

        // Step 2: Collect the list of orders to be billed.
        // This fetches only orders with SERVED status for the given table that do not have a bill assigned yet (bill is null).
        // NOTE: This requires a custom query method in OrderRepository for efficiency.
        // Example for OrderRepository:
        // @Query("SELECT o FROM Order o WHERE o.seatTable.id = :tableId AND o.status = 'SERVED' AND o.bill IS NULL")
        // List<Order> findUnbilledServedOrdersByTableId(@Param("tableId") Long tableId);
        List<Order> ordersToBill = orderRepository.findBySeatTableIdAndStatusAndBillIsNull(tableId, OrderStatus.SERVED);


        if (ordersToBill.isEmpty()) {
            log.warn("No new SERVED orders to bill for table ID: {}", tableId);
            throw new CustomExceptionHandler("No new orders to bill for table " + tableId);
        }
        log.info("Found {} new orders to include in the bill for table ID: {}", ordersToBill.size(), tableId);

        // Step 3 & 4: Calculate total price from OrderItems and save the new Bill.
        BigDecimal totalAmount = calculateTotalAmount(ordersToBill);
        log.info("Calculated total amount: {} for table ID: {}", totalAmount, tableId);

        Bill newBill = createAndSaveBill(seatTable, ordersToBill, totalAmount);

        // Update the table status to indicate billing is in process.
        seatTable.setStatus(TableStatus.BILLING);
        seatTableRepository.save(seatTable);
        log.info("Updated status to BILLING for table ID: {}. Bill generation complete.", tableId);

        return newBill;
    }

    /**
     * Calculates the total amount for a list of orders.
     *
     * @param orders A list of Order objects.
     * @return The total cost as a BigDecimal.
     */
    private BigDecimal calculateTotalAmount(List<Order> orders) {
        // FlatMap combines all OrderItems from all Orders into a single stream.
        // Map calculates the subtotal for each OrderItem (price * quantity).
        // Reduce sums up all the subtotals.
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getMenuItem().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Creates a new Bill entity, associates it with the orders, and saves it to the repository.
     *
     * @param table        The table the bill is for.
     * @param orders       The list of orders included in the bill.
     * @param totalAmount  The total calculated amount for the bill.
     * @return The saved Bill entity.
     */
    private Bill createAndSaveBill(SeatTable table, List<Order> orders, BigDecimal totalAmount) {
        Bill bill = Bill.builder()
                .seatTable(table)
                .orders(orders)
                .totalAmount(totalAmount)
                .status(BillStatus.PENDING)
                .build();

        // Associate each order with this new bill.
        orders.forEach(order -> order.setBill(bill));

        Bill savedBill = billRepository.save(bill);
        log.info("Successfully created and saved new bill with ID: {} for table ID: {}", savedBill.getId(), table.getId());
        return savedBill;
    }
}
