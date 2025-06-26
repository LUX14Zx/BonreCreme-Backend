package com.tlfdt.bonrecreme.utils.bill;

import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.SeatTable;
import com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus;

import java.math.BigDecimal;
import java.util.List;

public class GetBill {

    public static Bill getBill(SeatTable seatTable, List<Order> servedOrders, BigDecimal totalAmount) {
        Bill bill = new Bill();
        bill.setSeatTable(seatTable);
        bill.setOrders(servedOrders);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentMethod("Cash");
        bill.setStatus(BillStatus.PENDING);
        return bill;
    }
}