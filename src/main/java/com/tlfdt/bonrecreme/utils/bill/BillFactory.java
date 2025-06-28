package com.tlfdt.bonrecreme.utils.bill;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A factory component responsible for creating {@link Bill} instances.
 * <p>
 * This class encapsulates the logic for constructing a new bill, promoting
 * separation of concerns and making the creation process more testable and
 * maintainable than a static utility method.
 */
@Component
public class BillFactory {

    /**
     * Creates a new Bill instance with a PENDING status.
     * <p>
     * The newly created bill is initialized with the provided table, orders, and total amount.
     * It sets a default payment method which can be updated later in the payment process.
     *
     * @param seatTable    The table to which the bill belongs.
     * @param servedOrders The list of served orders to be included in the bill.
     * @param totalAmount  The calculated total amount for all orders.
     * @return A new, unsaved {@link Bill} entity.
     */
    /**
     * Creates a new Bill instance with a PENDING status.
     */
    public Bill createPendingBill(SeatTable seatTable, Set<Order> servedOrders, BigDecimal totalAmount) {
        return Bill.builder()
                .seatTable(seatTable)
                .orders(new HashSet<>(servedOrders)) // Convert the list to a HashSet
                .totalAmount(totalAmount)
                .status(BillStatus.PENDING)
                .paymentMethod("Cash")
                .build();
    }
}
