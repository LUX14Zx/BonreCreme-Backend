package com.tlfdt.bonrecreme.utils.bill.mapper;

import com.tlfdt.bonrecreme.controller.api.v1.cashier.dto.bill.BillResponseDTO;
import com.tlfdt.bonrecreme.model.restaurant.Bill;
import com.tlfdt.bonrecreme.model.restaurant.Order;
import com.tlfdt.bonrecreme.model.restaurant.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A component responsible for mapping {@link Bill} entities to {@link BillResponseDTO} data transfer objects.
 * This ensures a clean separation between the domain model and the API representation.
 */
@Component
public class BillMapper {

    public BillResponseDTO toBillResponseDTO(Bill bill) {
        if (bill == null) {
            return null;
        }
        List<BillResponseDTO.OrderDTO> orderDTOs = bill.getOrders().stream()
                .map(this::toOrderDTO)
                .collect(Collectors.toList());

        return BillResponseDTO.builder()
                .billId(bill.getId())
                .tableNumber(Long.valueOf(bill.getSeatTable().getTableNumber()))
                .totalAmount(bill.getTotalAmount())
                .billTime(bill.getCreatedAt())
                .isPaid(bill.getStatus() == com.tlfdt.bonrecreme.model.restaurant.enums.BillStatus.PAID)
                .orders(orderDTOs)
                .build();
    }

    private BillResponseDTO.OrderDTO toOrderDTO(Order order) {
        List<BillResponseDTO.OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::toOrderItemDTO)
                .collect(Collectors.toList());

        BigDecimal orderTotal = orderItemDTOs.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BillResponseDTO.OrderDTO.builder()
                .orderId(order.getId())
                .totalPrice(orderTotal)
                .items(orderItemDTOs)
                .build();
    }

    private BillResponseDTO.OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        return BillResponseDTO.OrderItemDTO.builder()
                .name(orderItem.getMenuItem().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPriceAtTime()) // Use the price at the time of the order
                .build();
    }
}