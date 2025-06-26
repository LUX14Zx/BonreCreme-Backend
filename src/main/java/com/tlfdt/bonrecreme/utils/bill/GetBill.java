package com.tlfdt.bonrecreme.utils.bill;

import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;

import java.math.BigDecimal;
import java.util.List;

public class GetBill {

    public static com.tlfdt.bonrecreme.model.restaurant.Bill getBill(List<Order> servedOrders, BigDecimal totalAmount) {
        com.tlfdt.bonrecreme.model.restaurant.Bill bill = new com.tlfdt.bonrecreme.model.restaurant.Bill();
        // For a single bill covering multiple orders, you might associate it with the first order,
        // or create a separate linking mechanism if the GetBill model only allows one order.
        // For simplicity, we'll associate it with the first order found, but the amount covers all.
        // A more robust solution might involve a many-to-many relationship or a dedicated GetBill-Order linking table.
        bill.setOrders(servedOrders); // Associate with the first served order
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod("Cash");
        bill.setStatus(BillStatus.PENDING);
        return bill;
    }
}
